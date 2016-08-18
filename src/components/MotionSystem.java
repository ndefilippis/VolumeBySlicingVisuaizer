package components;

import java.util.List;

import util.Transform;
import vector.Quaternion;
import vector.Vector3f;

public class MotionSystem {

	public void update(EntityManager mgr){
		List<Entity> entities = mgr.getAll(MotionComponent.class, Transform.class);
		for(Entity entity : entities){
			Vector3f scaledVelocity = new Vector3f(entity.as(MotionComponent.class).scaledVelocity());
			Vector3f scaledAVelocity = new Vector3f(entity.as(MotionComponent.class).scaledAngularVelocity());
			entity.as(Transform.class).addPosition(scaledVelocity);
			Quaternion spin = angularVelocityToSpin(scaledAVelocity, entity.as(Transform.class).getOrientation());
			entity.as(Transform.class).addOrientation(spin);
		}
	}
	
	private static Quaternion angularVelocityToSpin(Vector3f angularVelocity, Quaternion orientation){
		float x = angularVelocity.x;
		float y = angularVelocity.y;
		float z = angularVelocity.z;
		return Quaternion.mul(orientation, new Quaternion(x/2.0f, y/2.0f, z/2.0f, 0f), null);
	}
}
