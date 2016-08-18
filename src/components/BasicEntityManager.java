package components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Basic implementation of an entity manager.
 * 
 * @author <a href="http://www.cs.bsu.edu/~pvg">Paul Gestwicki</a>
 * @author Andrew DePersio (removeComponent() )
 * @author David Rickey (hasAllComponents())
 */
public class BasicEntityManager implements EntityManager {

	private final Map<Class<?>, Map<Entity, ? extends Component>> entityStore = new HashMap<Class<?>, Map<Entity, ? extends Component>>();

	public Entity createEntity() {
		return new Entity(this);
	}

	public void clear() {
		entityStore.clear();
	}

	@SuppressWarnings("unchecked")
	public <T extends Component> Entity addComponent(Entity e, T component) {
		Map<Entity, T> map = (Map<Entity, T>) entityStore.get(component.getClass());
		if (map == null) {
			map = new HashMap<Entity, T>();
			entityStore.put(component.getClass(), map);
		}
		assert map != null;
		map.put(e, component);
		return e;
	}

	@SuppressWarnings("unchecked")
	public <T extends Component> Entity removeComponent(Entity e, T component) {
		Map<Entity, T> map = (Map<Entity, T>) entityStore.get(component.getClass());
		assert map != null;
		map.remove(e);
		if (entityStore.get(component.getClass()).isEmpty()) {
			entityStore.remove(component.getClass());
		}
		return e;
	}

	@SuppressWarnings("unchecked")
	public <T extends Component> T getComponent(Entity e, Class<T> type) {
		Map<Entity, T> map = (Map<Entity, T>) entityStore.get(type);
		if (map != null)
			return map.get(e);
		throw new IllegalArgumentException("No such " + type + "Component for entity " + e);
	}

	// @Override
	public <T extends Component> boolean hasComponent(Entity e, Class<T> type) {
		Map<Entity, ? extends Component> map = entityStore.get(type);
		if (map == null)
			return false;
		else
			return map.containsKey(e);
	}

	public <T extends Component> boolean hasAllComponents(Entity e, Class<? extends Component>... types) {
		if (types.length == 0)
			return false;
		for (Class<? extends Component> c : types) {
			if (!hasComponent(e, c))
				return false;
		}
		return true;
	}

	public <T extends Component> List<Entity> getAll(Class<T> type) {
		List<Entity> result = new ArrayList<Entity>();
		Map<Entity, ? extends Component> map = entityStore.get(type);
		if (map != null)
			result.addAll(map.keySet());
		return result;
	}

	public List<Entity> getAll(Class<? extends Component>... types) {
		if (types.length == 0)
			return new ArrayList<Entity>();

		List<Entity> result = getAll(types[0]);
		for (int i = 1; i < types.length; i++)
			result.retainAll(getAll(types[i]));

		return new ArrayList<Entity>(result);
	}

	public void remove(Entity e) {
		// This is very inefficient, but works.
		// Optimize it if it becomes a bottleneck.
		for (Map<Entity, ? extends Component> map : entityStore.values()) {
			map.remove(e);
		}
	}

	@Override
	public String toString() {
		return "entityStore" + entityStore.toString();
	}
}