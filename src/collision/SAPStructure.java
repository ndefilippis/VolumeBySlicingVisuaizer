package collision;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import components.CollisionComponent;
import components.Entity;
import util.AABB;
import util.Pair;
import util.Transform;

/**
 * Better Sweep-and-prune implementation, replacing the old SweepAndPrune.java
 */
public class SAPStructure {

	private final class SweepPoint {
		public final Entity entity;
		public final CollisionComponent geo;
		public final boolean begin;
		public final int axis;
		private float lastValue;

		public SweepPoint(Entity entity, CollisionComponent geo, boolean begin, int axis) {
			super();
			this.entity = entity;
			this.geo = geo;
			this.begin = begin;
			this.axis = axis;

			// do an initial check on value
			if (Double.isNaN(value())) {
				throw new IllegalStateException("Geometry has a NaN as bounding value");
			}
		}

		@Override
		public boolean equals(Object other) {
			if (other == this)
				return true;
			if (other == null)
				return false;
			if (other instanceof SweepPoint) {
				SweepPoint otherSweepPoint = (SweepPoint) other;
				return this.entity == otherSweepPoint.entity;
			}
			return false;
		}

		@Override
		public int hashCode() {
			return entity.hashCode() + (begin ? 1 : 0);
		}

		public String toString() {
			return "" + entity.toString() + "," + axis + "," + begin;
		}

		public final float value() {
			AABB box = geo.collisionModel.getBoundingBox();
			Transform t = entity.as(Transform.class);
			AABB newBox = new AABB(box, t);
			if (begin) {
				lastValue = newBox.getMin().get(axis);
			} else {
				lastValue = newBox.getMax().get(axis);
			}
			return lastValue;
		}
	}

	private final class Counter {
		public boolean wasOverlapping = false;
		public int overlaps;

		public String toString() {
			return overlaps + "";
		}
	}

	private ArrayList<Pair<Entity>> overlaps = new ArrayList<Pair<Entity>>();
	private Map<Pair<Entity>, Counter> counters = new HashMap<Pair<Entity>, Counter>();
	private ArrayList<CollisionComponent> geometries = new ArrayList<CollisionComponent>();
	private ArrayList<SweepPoint> axis1 = new ArrayList<SweepPoint>();
	private ArrayList<SweepPoint> axis2 = new ArrayList<SweepPoint>();
	private ArrayList<SweepPoint> axis3 = new ArrayList<SweepPoint>();

	private final void sortAxis(ArrayList<SweepPoint> axis) {
		// insertion sort from Cormen et al, Introduction to Algorithms
		for (int j = 1; j < axis.size(); j++) {
			final SweepPoint keyelement = axis.get(j);
			double key = keyelement.value();

			if (Double.isNaN(key))
				throw new IllegalStateException(
						"Geometry has NaN in its bounding box values" + keyelement.geo.collisionModel.getBoundingBox());

			int i = j - 1;
			while (i >= 0 && axis.get(i).value() > key) {
				// swap
				final SweepPoint swapper = axis.get(i);

				if (keyelement.begin && !swapper.begin) {
					// increment overlap (end before begin)
					final Pair<Entity> pair = new Pair<Entity>(keyelement.entity, swapper.entity);
					if (counters.containsKey(pair)) {
						counters.get(pair).overlaps++;
					} else {
						Counter counter = new Counter();
						counter.overlaps = 1;
						counters.put(pair, counter);
					}
				}

				if (!keyelement.begin && swapper.begin) {
					// decrement overlap (begin before end)
					final Pair<Entity> pair = new Pair<Entity>(keyelement.entity, swapper.entity);

					if (counters.containsKey(pair)) {
						counters.get(pair).overlaps--;

					} else {
						// ignore this case
						System.out.println("hmm?");
					}
				}

				axis.set(i + 1, swapper);
				i = i - 1;
			}
			axis.set(i + 1, keyelement);
		}
	}

	public void add(Entity e) {
		CollisionComponent g = e.as(CollisionComponent.class);
		if (!geometries.contains(g)) {
			geometries.add(g);

			// create new sweep points
			axis1.add(new SweepPoint(e, g, true, 0));
			axis1.add(new SweepPoint(e, g, false, 0));
			axis2.add(new SweepPoint(e, g, true, 1));
			axis2.add(new SweepPoint(e, g, false, 1));
			axis3.add(new SweepPoint(e, g, true, 2));
			axis3.add(new SweepPoint(e, g, false, 2));

		} else {
			throw new IllegalArgumentException("Given geometry already exist");
		}
	}

	public Set<Pair<Entity>> getOverlappingPairs() {
		HashSet<Pair<Entity>> set = new HashSet<Pair<Entity>>();
		for (Pair<Entity> pair : overlaps) {
			set.add(new Pair<Entity>(pair.getFirst(), pair.getSecond()));
		}
		return set;
	}

	public void remove(Entity e, CollisionComponent g) {
		if (geometries.contains(g)) {
			// remove
			geometries.remove(g);

			// remove sweep points
			removeSweepPoint(axis1, g);
			removeSweepPoint(axis2, g);
			removeSweepPoint(axis3, g);

			// go through counters, and delete the ones that involve g. If the
			// counter var
			// an overlapping counter, signal an separation event
			Iterator<Entry<Pair<Entity>, Counter>> iter = counters.entrySet().iterator();
			while (iter.hasNext()) {
				Entry<Pair<Entity>, Counter> entry = iter.next();
				Counter c = entry.getValue();
				Pair<Entity> pair = entry.getKey();
				if (pair.getFirst() == e || pair.getSecond() == e) {
					if (c.wasOverlapping) {
						overlaps.remove(pair);
					}

					// remove counter
					iter.remove();
				}
			}
		} else {
			throw new IllegalArgumentException("Given geometry does not exist");
		}

	}

	private final void removeSweepPoint(List<SweepPoint> list, CollisionComponent g) {
		ListIterator<SweepPoint> iter = list.listIterator();
		while (iter.hasNext()) {
			SweepPoint p = iter.next();
			if (p.geo == g)
				iter.remove();
		}
	}

	public void run() {
		// sort each axis and update counters
		sortAxis(axis1);
		sortAxis(axis2);
		sortAxis(axis3);
		int countingOverlaps = 0;
		// go through all counters
		Iterator<Entry<Pair<Entity>, Counter>> iter = counters.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<Pair<Entity>, Counter> entry = iter.next();

			Counter c = entry.getValue();
			Pair<Entity> pair = entry.getKey();

			if (c.overlaps == 3)
				countingOverlaps++;

			if (c.wasOverlapping) {
				// report separation
				if (c.overlaps < 3) {
					overlaps.remove(pair);
					c.wasOverlapping = false;

					// notify handlers
				}
			} else {
				// report overlap
				if (c.overlaps > 2) {
					overlaps.add(pair);
					c.wasOverlapping = true;

					// notify handlers

				}
			} // if was overlapping

			// if counter is zero at this
			// point, remove it
			if (c.overlaps < 1) {
				iter.remove();
			}
		} // for all counters
	}

}