package input;

import org.lwjgl.glfw.GLFW;

import renderEngine.Display;
import vector.Vector3f;

public class Impulse{
	private Vector3f impulse;
	private float walkSpeed;
	private float sprintSpeed;
	private float speed;
	private boolean isSprinting;
	
	public Impulse(){
		impulse = new Vector3f();
		walkSpeed = 20f;
		sprintSpeed = 100f;
		this.speed = walkSpeed;
	}
	
	
	public Vector3f getImpulse(){
		return new Vector3f(impulse);
	}
	
	public Impulse(float walkSpeed, float sprintSpeed){
		impulse = new Vector3f();
		this.walkSpeed = walkSpeed;
		this.sprintSpeed = sprintSpeed;
		this.speed = walkSpeed;
	}

	public void update(CameraPivot directions){
		update(directions.getLookDirection(), directions.getUpDirection(), directions.getRightDirection());
	}

	public void update(Vector3f lookDirection, Vector3f upDirection, Vector3f rightDirection){
		impulse = new Vector3f();
		Vector3f move = ((Vector3f) lookDirection.normalise().scale(speed*Display.getFrameTimeSeconds()));
		Vector3f sideMove = ((Vector3f) rightDirection.normalise().scale(speed*Display.getFrameTimeSeconds()));
		Vector3f upMove = ((Vector3f) upDirection.normalise().scale(speed*Display.getFrameTimeSeconds()));
		if(InputHandler.isKeyPressed(GLFW.GLFW_KEY_W)){
			Vector3f.add(impulse, move, impulse);
		}
		if(InputHandler.isKeyPressed(GLFW.GLFW_KEY_S)){
			Vector3f.sub(impulse, move, impulse);
		}
		if(InputHandler.isKeyPressed(GLFW.GLFW_KEY_A)){
			Vector3f.sub(impulse, sideMove, impulse);
		}
		if(InputHandler.isKeyPressed(GLFW.GLFW_KEY_D)){
			Vector3f.add(impulse, sideMove, impulse);
		}
		if(InputHandler.isKeyPressed(GLFW.GLFW_KEY_SPACE)){
			Vector3f.add(impulse, upMove, impulse);
		}
		if(InputHandler.isKeyPressed(GLFW.GLFW_KEY_LEFT_CONTROL)){
			Vector3f.sub(impulse, upMove, impulse);
		}
		if(InputHandler.isKeyPressed(GLFW.GLFW_KEY_LEFT_SHIFT)){
			speed = sprintSpeed;
			isSprinting = true;
		}
		else{
			speed = walkSpeed;
			isSprinting = false;
		}
	}

	public boolean isSprinting() {
		return isSprinting;
	}

}
