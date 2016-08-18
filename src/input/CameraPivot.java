package input;

import org.lwjgl.glfw.GLFW;

import renderEngine.Display;
import vector.Quaternion;
import vector.Vector2f;
import vector.Vector3f;
import vector.Vector4f;

public class CameraPivot {
	private InputContext input;
	private Quaternion orientation;
	private float lookSensitivity = 5f;
	private Quaternion orig_orientation;

	private float yawSpeed, pitchSpeed, rollSpeed;
	
	private float totalRotX;
	private float minX = -(float) Math.PI / 2f, maxX = (float) Math.PI / 2f;

	private boolean limits = true;

	private static Vector3f up = new Vector3f(0, 1, 0);
	private static Vector3f right = new Vector3f(1, 0, 0);
	private static Vector3f forward = new Vector3f(0, 0, -1);

	public CameraPivot(InputContext context) {
		orig_orientation = new Quaternion();
		setSpeed(20, 20, 20);
		setAngle(0f, 0f, 0f);
		this.input = context;
	}

	private void setAngle(float roll, float yaw, float pitch) {
		orig_orientation = Quaternion.setFromEulerAngles(yaw, pitch, roll);
		orientation = new Quaternion(orig_orientation);
	}

	private void setSpeed(float rollSpeed, float yawSpeed, float pitchSpeed) {
		this.rollSpeed = (float) Math.toRadians(rollSpeed);
		this.yawSpeed = (float) Math.toRadians(yawSpeed);
		this.pitchSpeed = (float) Math.toRadians(pitchSpeed);
	}

	public CameraPivot(InputContext context, float initialRoll, float initialYaw, float initialPitch, float rollSpeed, float yawSpeed,
			float pitchSpeed) {
		setAngle(initialRoll, initialYaw, initialPitch);
		setSpeed(rollSpeed, yawSpeed, pitchSpeed);
		this.input = context;
	}

	public CameraPivot(InputContext context, float initialRoll, float initialYaw, float initialPitch) {
		setAngle(initialRoll, initialYaw, initialPitch);
		setSpeed(20, 20, 20);
		this.input = context;
	}

	public CameraPivot(float rollSpeed, float yawSpeed, float pitchSpeed) {
		registerInput();
		setSpeed(rollSpeed, yawSpeed, pitchSpeed);
		setAngle(0f, 0f, 0f);
	}

	public void registerInput() {
		input = new InputContext();
		input.addKeyState(GLFW.GLFW_KEY_Q, "roll_left");
		input.addKeyState(GLFW.GLFW_KEY_E, "roll_right");
		input.addMouseRange(Axis.HORIZONTAL, "yaw");
		input.addMouseRange(Axis.VERTICAL, "pitch");
		input.addJoystickRange(Axis.VERTICAL, "pitch");
		input.addJoystickRange(Axis.TWIST, "yaw");
		input.addJoystickRange(Axis.HORIZONTAL, "roll");
	}


	private float rotX = 0;
	private float rotY = 0;
	private float rotZ = 0;
	
	public void update() {
		float time = Display.getFrameTimeSeconds();
		if (input.getState("roll_left")) {
			rotZ -= time * rollSpeed;
		}
		if (input.getState("roll_right")) {
			rotZ += time * rollSpeed;
		}
		rotZ += input.getRange("roll") * lookSensitivity * rollSpeed * time;
		rotX += input.getRange("pitch") * lookSensitivity * pitchSpeed * time;
		rotY += input.getRange("yaw") * lookSensitivity * yawSpeed * time;
		
		if (limits) {
			if(rotX >= maxX){
				rotX = maxX;
			}
			if(rotX <= minX){
				rotX = minX;
			}
		}
		Quaternion axis1 = Quaternion.AxisAngle(forward, rotZ);
		axis1.normalise();
		Quaternion.mul(axis1, new Quaternion(), orientation);
		
		Quaternion axis3 = Quaternion.AxisAngle(up, rotY);
		axis3.normalise();
		Quaternion.mul(axis3, orientation, orientation);
		
		Quaternion axis2 = Quaternion.AxisAngle(right, rotX);
		axis2.normalise();
		Quaternion.mul(axis2, orientation, orientation);
		
		

	}

	public Vector3f getUpDirection() {
		return orientation.rotate(up).normalise(null);
	}

	public Vector3f getRightDirection() {
		return orientation.rotate(right).normalise(null);
	}

	public Vector3f getLookDirection() {
		return orientation.rotate(forward).normalise(null);
	}

	public Quaternion getOrientation() {
		return orientation;
	}

	public void disableLimits() {
		limits = false;
	}

	public void setOrientation(Quaternion q) {
		this.orientation = q;
	}
}
