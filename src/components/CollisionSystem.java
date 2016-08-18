package components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import collision.CollisionModel;
import collision.CollisionPacket;
import collision.Plane;
import collision.SAPStructure;
import renderEngine.Display;
import terrains.Terrain;
import util.AABB;
import util.Pair;
import util.Transform;
import util.Utils;
import vector.Vector3f;

public class CollisionSystem {
	private SAPStructure broadphaseStructure;
	private static final float unitsPerMeter = 100.0f;
	public static List<Terrain> terrainColliders = new ArrayList<Terrain>();
	private Map<Entity, List<Entity>> overlapMap;

	public CollisionSystem(SAPStructure broadphaseStructure) {
		this.broadphaseStructure = broadphaseStructure;
	}

	public void update(EntityManager mgr) {
		List<Entity> entities = mgr.getAll(Transform.class, CollisionComponent.class);
		for (Entity e : entities) {
			if (!e.as(CollisionComponent.class).isStatic) {
				e.as(CollisionComponent.class).foundCollision = collideWithTerrain(e);
			}
		}
		broadphaseStructure.run();
		Set<Pair<Entity>> mightBeOverlapping = broadphaseStructure.getOverlappingPairs();
		overlapMap = new HashMap<Entity, List<Entity>>();
		for (Pair<Entity> pair : mightBeOverlapping) {
			if (overlapMap.containsKey(pair.getFirst())) {
				overlapMap.get(pair.getFirst()).add(pair.getSecond());
			} else {
				List<Entity> list = new ArrayList<Entity>();
				list.add(pair.getSecond());
				overlapMap.put(pair.getFirst(), list);
			}
			if (overlapMap.containsKey(pair.getSecond())) {
				overlapMap.get(pair.getSecond()).add(pair.getFirst());
			} else {
				List<Entity> list = new ArrayList<Entity>();
				list.add(pair.getFirst());
				overlapMap.put(pair.getSecond(), list);
			}
		}
		for (Entity entity : overlapMap.keySet()) {
			if(!entity.as(CollisionComponent.class).isStatic){
				collide(entity, overlapMap.get(entity));
			}
		}
	}

	private void collide(Entity collider, List<Entity> collidees) {
		CollisionPacket collisionPackage = new CollisionPacket();
		collisionPackage.R3Position = collider.as(Transform.class).getPosition();
		collisionPackage.R3Velocity = collider.as(MotionComponent.class).scaledVelocity();
		collisionPackage.eRadius = getERadiusVector(collider);
		Vector3f invERadius = new Vector3f(1.0f / collisionPackage.eRadius.x, 1.0f / collisionPackage.eRadius.y,
				1.0f / collisionPackage.eRadius.z);
		Vector3f eSpacePosition = collisionPackage.R3Position.scale(invERadius);
		Vector3f eSpaceVelocity = collisionPackage.R3Velocity.scale(invERadius);

		Vector3f finalPosition = collideWithWorld(collider, collisionPackage, eSpacePosition, eSpaceVelocity, 0);

		if (collider.has(PhysicsComponent.class)) {
			Vector3f gravity = collider.as(PhysicsComponent.class).getScaledGravity();
			collisionPackage.R3Position = finalPosition.scale(collisionPackage.eRadius);
			collisionPackage.R3Velocity = gravity;
			eSpaceVelocity = gravity.scale(invERadius);

			finalPosition = collideWithWorld(collider, collisionPackage, finalPosition, eSpaceVelocity, 0);
		}
		finalPosition = finalPosition.scale(collisionPackage.eRadius);
		collider.as(CollisionComponent.class).foundCollision = collisionPackage.foundCollision;
		collider.as(Transform.class).setPosition(finalPosition);
		Vector3f newVelocity = collisionPackage.velocity.scale(collisionPackage.eRadius);
		float deltaTime = Display.getFrameTimeSeconds();
		if(deltaTime != 0.0f){
			newVelocity.scale(1f/deltaTime);
		}
		collider.as(MotionComponent.class).velocity = newVelocity;
	}

	private boolean collideWithTerrain(Entity collider) {
		boolean ret = false;
		Vector3f position = collider.as(Transform.class).getPosition();
		float terrainHeight = Utils.getTerrainHeight(terrainColliders, position.x, position.z);
		float myHeight = position.y;
		if (myHeight <= terrainHeight) {
			position.y = terrainHeight;
			collider.as(MotionComponent.class).velocity.y = 0;
			ret = true;
		}
		collider.as(Transform.class).setPosition(position);
		return ret;
	}

	private Vector3f getERadiusVector(Entity entity) {
		AABB box = new AABB(entity.as(CollisionComponent.class).collisionModel.getBoundingBox(),
				entity.as(Transform.class));
		Vector3f v = Vector3f.sub(box.getMax(), box.getMin(), null);
		v.scale(0.5f);
		return v;
	}

	private void checkCollision(CollisionPacket collisionPackage, Entity collider) {
		Vector3f invERadius = new Vector3f(1.0f / collisionPackage.eRadius.x, 1.0f / collisionPackage.eRadius.y,
				1.0f / collisionPackage.eRadius.z);
		Vector3f velocity = new Vector3f();
		for (Entity collidee : overlapMap.get(collider)) {
			CollisionModel collisionModel = collidee.as(CollisionComponent.class).collisionModel;
			Vector3f[] points = collisionModel.getPoints();
			for (int i = 0; i < points.length / 3; i++) {
				Vector3f p1 = new Vector3f(points[3 * i + 0]);
				Vector3f p2 = new Vector3f(points[3 * i + 1]);
				Vector3f p3 = new Vector3f(points[3 * i + 2]);
				Transform transform = collidee.as(Transform.class);
				p1 = transform.transform(p1);
				p2 = transform.transform(p2);
				p3 = transform.transform(p3);

				p1 = p1.scale(invERadius);
				p2 = p2.scale(invERadius);
				p3 = p3.scale(invERadius);
				if(CollisionPacket.checkTriangle(collisionPackage, p1, p2, p3)){
					if(collidee.has(MotionComponent.class)){
						velocity = collidee.as(MotionComponent.class).velocity;
					}
				}
			}
		}
		Vector3f.add(collisionPackage.velocity, velocity.scale(invERadius), collisionPackage.velocity);
	}

	private Vector3f collideWithWorld(Entity collider, CollisionPacket collisionPackage, Vector3f pos, Vector3f vel, int collisionRecursionDepth) {
		float unitScale = unitsPerMeter / 100.0f;
		float epsilon = 0.005f * unitScale;

		if (collisionRecursionDepth > 5) {
			return pos;
		}
		collisionPackage.velocity = vel;
		collisionPackage.normalizedVelocity = new Vector3f(vel);
		if (vel.lengthSquared() != 0) {
			collisionPackage.normalizedVelocity.normalise();
		}
		collisionPackage.basePoint = pos;
		collisionPackage.foundCollision = false;

		checkCollision(collisionPackage, collider);

		if (!collisionPackage.foundCollision) {
			return Vector3f.add(pos, vel, pos);
		}
		Vector3f destinationPoint = Vector3f.add(pos, vel, null);
		Vector3f newBasePoint = pos;
		if (collisionPackage.nearestDistance >= epsilon) {
			Vector3f V = vel;
			V.scale(collisionPackage.nearestDistance - epsilon);
			newBasePoint = Vector3f.add(collisionPackage.basePoint, V, null);
			V.normalise();
			V.scale(epsilon);
			Vector3f.sub(collisionPackage.intersectionPoint, V, collisionPackage.intersectionPoint);
		}
		Vector3f slidePlaneOrigin = collisionPackage.intersectionPoint;
		Vector3f slidePlaneNormal = Vector3f.sub(newBasePoint, collisionPackage.intersectionPoint, null);
		slidePlaneNormal.normalise();
		Plane slidingPlane = new Plane(slidePlaneOrigin, slidePlaneNormal);
		Vector3f rhs = new Vector3f(slidePlaneNormal);
		rhs.scale(slidingPlane.signedDistanceTo(destinationPoint));
		Vector3f newDestinationPoint = Vector3f.sub(destinationPoint, rhs, null);
		Vector3f newVelocityVector = Vector3f.sub(newDestinationPoint, collisionPackage.intersectionPoint, null);
		if (newVelocityVector.length() < epsilon) {
			return newBasePoint;
		}
		return collideWithWorld(collider, collisionPackage, newBasePoint, newVelocityVector, collisionRecursionDepth + 1);
	}
}
