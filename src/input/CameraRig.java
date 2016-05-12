package input;

import org.lwjgl.glfw.GLFW;

import vector.Vector3f;

public class CameraRig{
	private Vector3f position;
	private Impulse impulse;
	
	public CameraRig(){
		position = new Vector3f();
		impulse = new Impulse();
	}
	
	public CameraRig(float x, float y, float z){
		position = new Vector3f(x, y, z);
		impulse = new Impulse();
	}
	
	public Vector3f getPosition(){
		return new Vector3f(position);
	}
	
	public CameraRig(float x, float y, float z, float walkSpeed, float sprintSpeed){
		position = new Vector3f(x, y, z);
		impulse = new Impulse(walkSpeed, sprintSpeed);
	}

	public void update(CameraPivot directions){
		update(directions.getLookDirection(), directions.getUpDirection(), directions.getRightDirection());
	}
	public void setPosition(Vector3f pos){
		position = pos;
	}

	public void update(Vector3f lookDirection, Vector3f upDirection, Vector3f rightDirection){
		impulse.update(lookDirection, upDirection, rightDirection);
		Vector3f.add(position, impulse.getImpulse(), position);
	}

}
