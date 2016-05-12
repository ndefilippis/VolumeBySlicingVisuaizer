package input;

import org.lwjgl.glfw.GLFW;

import renderEngine.Display;
import vector.Quaternion;
import vector.Vector2f;
import vector.Vector3f;

public class CameraPivot {
	private float roll;
	private float yaw;
	private float pitch;
	private Quaternion orientation;
	private float mouseSensitivity = 0.2f;
	
	private float yawSpeed, pitchSpeed, rollSpeed;
	
	private float rc, rs;
	private float yc, ys;
	private float pc, ps;
	
	public CameraPivot(){
		setSpeed(20, 20, 20);
		setAngle(0f, 0f, 0f);
	}
	
	private void setAngle(float roll, float yaw, float pitch){
		this.roll = roll;
		this.yaw = yaw;
		this.pitch = pitch;
		rc = (float) Math.cos( Math.PI * roll / 180f );
		rs = (float) Math.sin( Math.PI * roll / 180f );
		yc = (float) Math.cos( Math.PI * yaw / 180f );
		ys = (float) Math.sin( Math.PI * yaw / 180f );
		pc = (float) Math.cos( Math.PI * pitch / 180f );
		ps = (float) Math.sin( Math.PI * pitch / 180f );
	}
	
	private void setSpeed(float rollSpeed, float yawSpeed, float pitchSpeed){
		this.rollSpeed = rollSpeed;
		this.yawSpeed = yawSpeed;
		this.pitchSpeed = pitchSpeed;
	}
	
	public CameraPivot(float initialRoll, float initialYaw, float initialPitch, float rollSpeed, float yawSpeed, float pitchSpeed){
		setAngle(initialRoll, initialYaw, initialPitch);
		setSpeed(rollSpeed, yawSpeed, pitchSpeed);		
	}
	
	public CameraPivot(float initialRoll, float initialYaw, float initialPitch){
		setAngle(initialRoll, initialYaw, initialPitch);
		setSpeed(20, 20, 20);
	}
	
	public void update(){
		
		if(InputHandler.isKeyPressed(GLFW.GLFW_KEY_Q)){
			setRoll(roll - rollSpeed*Display.getFrameTimeSeconds());
		}
		if(InputHandler.isKeyPressed(GLFW.GLFW_KEY_E)){
			setRoll(roll + rollSpeed*Display.getFrameTimeSeconds());
		}
		Vector2f mouseD = InputHandler.getMouseMoveDistance();
		mouseD.scale(mouseSensitivity);
		setYaw(yaw + (mouseD.x * rc - mouseD.y * rs)*yawSpeed*Display.getFrameTimeSeconds());
		setPitch(pitch + (mouseD.y * rc + mouseD.x * rs)*pitchSpeed*Display.getFrameTimeSeconds());
		if(pitch > 90f){
			setPitch(90f);
		}
		if(pitch < -90f){
			setPitch(-90f);
		}
	}

	public float getRoll() {
		return roll;
	}

	public void setYaw(float yaw) {
		this.yaw = yaw;
		yc = (float) Math.cos( Math.PI * yaw / 180f );
		ys = (float) Math.sin( Math.PI * yaw / 180f );
	}

	public void setRoll(float roll) {
		this.roll = roll;
		rc = (float) Math.cos( Math.PI * roll / 180f );
		rs = (float) Math.sin( Math.PI * roll / 180f );
	}

	public void setPitch(float pitch) {
		this.pitch = pitch;
		pc = (float) Math.cos( Math.PI * pitch / 180f );
		ps = (float) Math.sin( Math.PI * pitch / 180f );
	}

	public float getYaw() {
		return yaw;
	}

	public float getPitch() {
		return pitch;
	}
	
	public Vector3f getUpDirection(){
		float x = rs * yc;
		float y = rc;
		float z = rs * ys * ps;
		Vector3f ret = new Vector3f(x, y, z);
		ret.normalise();
		return ret;
	}
	
	public Vector3f getRightDirection(){
		float x = yc * rc;
		float y = -rs;
		float z = ys * rc;
		Vector3f ret = new Vector3f(x, y, z);
		ret.normalise();
		return ret;
	}
	
	public Vector3f getLookDirection(){
		float x = ys*pc;
		float y =  -ps;
		float z = -yc*pc;
		Vector3f ret = new Vector3f(x, y, z);
		ret.normalise();
		return ret;
	}
}
