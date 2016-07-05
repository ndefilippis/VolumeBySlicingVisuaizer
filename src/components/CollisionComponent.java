package components;

import java.util.ArrayList;
import java.util.List;

import collision.CollisionModel;
import collision.CollisionPacket;
import collision.Plane;
import util.AABB;
import vector.Vector3f;

public class CollisionComponent implements Component {
	public CollisionModel collisionModel;
	private MotionComponent motionComponent;
	private TransformComponent transformComponent;
	private Vector3f gravity;
	public boolean foundCollision;
	private boolean isStatic;
	private static final float unitsPerMeter = 100.0f;
	public static List<CollisionComponent> colliders = new ArrayList<CollisionComponent>();

	public CollisionComponent(MotionComponent motionComponent, TransformComponent transformComponent,
			CollisionModel collisionModel) {
		this.motionComponent = motionComponent;
		this.transformComponent = transformComponent;
		this.collisionModel = collisionModel;
		gravity = new Vector3f();
		colliders.add(this);
	}
	
	public CollisionComponent(TransformComponent transformComponent, CollisionModel collisionModel) {
		this.transformComponent = transformComponent;
		this.collisionModel = collisionModel;
		this.isStatic = true;
		colliders.add(this);
	}

	@Override
	public void update() {
		if(isStatic){
			return;
		}
		CollisionPacket collisionPackage = new CollisionPacket();
		collisionPackage.R3Position = transformComponent.transform.getPosition();
		collisionPackage.R3Velocity = motionComponent.velocity;
		collisionPackage.eRadius = getERadiusVector();
		Vector3f invERadius = new Vector3f(1.0f / collisionPackage.eRadius.x, 1.0f / collisionPackage.eRadius.y,
				1.0f / collisionPackage.eRadius.z);
		Vector3f eSpacePosition = collisionPackage.R3Position.scale(invERadius);
		Vector3f eSpaceVelocity = collisionPackage.R3Velocity.scale(invERadius);

		Vector3f finalPosition = collideWithWorld(collisionPackage, eSpacePosition, eSpaceVelocity, 0);
		collisionPackage.R3Position = finalPosition.scale(collisionPackage.eRadius);
		collisionPackage.R3Velocity = gravity;
		eSpaceVelocity = gravity.scale(invERadius);

		finalPosition = collideWithWorld(collisionPackage, finalPosition, eSpaceVelocity, 0);
		finalPosition = finalPosition.scale(collisionPackage.eRadius);
		foundCollision = collisionPackage.foundCollision;
	}

	private Vector3f getERadiusVector() {
		AABB box = new AABB(collisionModel.getBoundingBox(), transformComponent.transform);
		Vector3f v = Vector3f.sub(box.getMax(), box.getMin(), null);
		v.scale(0.5f);
		return v;
	}

	public List<CollisionComponent> getMightBeCollidingUsingSAP() {
		List<CollisionComponent> possiblyColliding = new ArrayList<CollisionComponent>();
		AABB box1 = new AABB(this.collisionModel.getBoundingBox(), this.transformComponent.transform);
		for (int j = 0; j < colliders.size(); j++) {
			CollisionComponent cc2 = colliders.get(j);
			AABB box2 = new AABB(cc2.collisionModel.getBoundingBox(), cc2.transformComponent.transform);
			if (box1.isIntersectingWith(box2)) {
				possiblyColliding.add(cc2);
			}
		}

		return possiblyColliding;
	}

	private void checkCollision(CollisionPacket collisionPackage) {
		List<CollisionComponent> possiblyColliding = getMightBeCollidingUsingSAP();
		Vector3f invERadius = new Vector3f(1.0f/collisionPackage.eRadius.x, 1.0f/collisionPackage.eRadius.y, 1.0f/collisionPackage.eRadius.z);
		for(CollisionComponent cc : possiblyColliding){
			CollisionModel collisionModel = cc.collisionModel;
			Vector3f[] points = collisionModel.getPoints();
			for(int i = 0; i < points.length/3; i++){
				Vector3f p1 = new Vector3f(points[3*i + 0]);
				Vector3f p2 = new Vector3f(points[3*i + 1]);
				Vector3f p3 = new Vector3f(points[3*i + 2]);
				p1 = transformComponent.transform.transform(p1);
				p2 = transformComponent.transform.transform(p2);
				p3 = transformComponent.transform.transform(p3);
				p1.scale(invERadius);
				p2.scale(invERadius);
				p3.scale(invERadius);
				CollisionPacket.checkTriangle(collisionPackage, p1, p2, p3);
			}
		}
	}

	private Vector3f collideWithWorld(CollisionPacket collisionPackage, Vector3f pos, Vector3f vel, int collisionRecursionDepth) {
		float unitScale = unitsPerMeter / 100.0f;
		float epsilon = 0.005f * unitScale;

		if (collisionRecursionDepth > 5) {
			return pos;
		}
		collisionPackage.velocity = vel;
		collisionPackage.normalizedVelocity = vel;
		collisionPackage.normalizedVelocity.normalise();
		collisionPackage.basePoint = pos;
		collisionPackage.foundCollision = false;

		checkCollision(collisionPackage);

		if (!collisionPackage.foundCollision) {
			return Vector3f.add(pos, vel, null);
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
		return collideWithWorld(collisionPackage, newBasePoint, newVelocityVector, collisionRecursionDepth++);
	}

	public void setGravity(Vector3f newGravity) {
		this.gravity = newGravity;
	}
	
	@Override
	public ComponentType getType() {
		return ComponentType.COLLSION;
	}
}
