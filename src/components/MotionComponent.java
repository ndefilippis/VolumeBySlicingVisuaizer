package components;

import renderEngine.Display;
import vector.Quaternion;
import vector.Vector3f;

public class MotionComponent implements Component{
	public Vector3f velocity;
	public Vector3f angularVelocity;
	
	public MotionComponent(Vector3f velocity, Vector3f rotV){
		this.velocity = velocity;
		this.angularVelocity = rotV;	
	}
	
	public MotionComponent(){
		velocity = new Vector3f();
		angularVelocity = new Vector3f();
	}

	public Vector3f scaledVelocity() {
		Vector3f newVelocity = new Vector3f(velocity);
		newVelocity.scale(Display.getFrameTimeSeconds());
		return newVelocity;
	}
	
	public Vector3f scaledAngularVelocity(){
		Vector3f newRVelocity = new Vector3f(angularVelocity);
		newRVelocity.scale(Display.getFrameTimeSeconds());
		return newRVelocity;
	}

	/*
	@Override
	public void update() {
		float delta = Display.getFrameTimeSeconds();
		Vector3f newV = new Vector3f(velocity);
		Quaternion newRV = new Quaternion(angularVelocity);
		newV.scale(delta);
		newRV.scale(delta);
		transformComponent.transform.addPosition(newV);
		//transformComponent.transform.rotate(newRV);
	}
	}*/
}
