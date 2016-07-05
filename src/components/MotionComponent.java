package components;

import renderEngine.Display;
import vector.Quaternion;
import vector.Vector3f;

public class MotionComponent implements Component{
	public Vector3f velocity;
	public Quaternion angularVelocity;
	private TransformComponent transformComponent;
	
	public MotionComponent(TransformComponent tComponent, Vector3f velocity, Quaternion rotV){
		this.transformComponent = tComponent;
		this.velocity = velocity;
		this.angularVelocity = rotV;	
	}
	
	public MotionComponent(TransformComponent tComponent){
		this.transformComponent = tComponent;
		velocity = new Vector3f();
		angularVelocity = new Quaternion();
	}

	@Override
	public void update() {
		float delta = Display.getFrameTimeSeconds();
		Vector3f newV = new Vector3f(velocity);
		Quaternion newRV = new Quaternion(angularVelocity);
		newV.scale(delta);
		newRV.scale(delta);
		transformComponent.transform.addPosition(newV);
		transformComponent.transform.rotate(newRV);
	}
	@Override
	public ComponentType getType() {
		return ComponentType.MOTION;
	}
}
