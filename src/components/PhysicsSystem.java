package components;

import java.util.List;

import vector.Vector3f;

public class PhysicsSystem {
	
	
	public void update(EntityManager mgr){
		List<Entity> entities = mgr.getAll(MotionComponent.class, PhysicsComponent.class);
		for(Entity e : entities){
			Vector3f newGravity = e.as(PhysicsComponent.class).getScaledGravity();
			Vector3f velocity = e.as(MotionComponent.class).velocity;
			Vector3f.add(velocity, newGravity, velocity);
		}
	}
}
