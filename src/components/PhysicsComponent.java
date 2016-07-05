package components;

import renderEngine.Display;
import terrains.Terrain;
import util.Utils;
import vector.Vector3f;

public class PhysicsComponent implements Component{
	public static final Vector3f GRAVITY = new Vector3f(0, -100f, 0);
	private MotionComponent motionComponent;
	CollisionComponent collisionComponent;
	
	public PhysicsComponent(MotionComponent motionComponent, CollisionComponent collisionComponent){
		this.motionComponent = motionComponent;
		this.collisionComponent = collisionComponent;
	}
	
	public PhysicsComponent(MotionComponent motionComponent){
		this.motionComponent = motionComponent;
	}
	
	public void update(){
		Vector3f newGravity = new Vector3f(GRAVITY);
		newGravity.scale(Display.getFrameTimeSeconds());
		if(collisionComponent != null){
			collisionComponent.setGravity(newGravity);
		}
		
		Vector3f.add(motionComponent.velocity, newGravity, motionComponent.velocity);
	}

	public boolean collisionFound() {
		if(collisionComponent == null) return false;
		return collisionComponent.foundCollision;
	}
	
	@Override
	public ComponentType getType() {
		return ComponentType.PHYSICS;
	}
}
