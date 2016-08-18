package input;

import org.lwjgl.glfw.GLFW;

import renderEngine.Display;
import vector.Quaternion;
import vector.Vector3f;

public class Impulse {
	private Vector3f impulse;
	private float walkSpeed;
	private float sprintSpeed;
	private float speed;
	private boolean isSprinting;
	private InputContext input;

	private static Vector3f up = new Vector3f(0, 1, 0);
	private static Vector3f right = new Vector3f(1, 0, 0);
	private static Vector3f forward = new Vector3f(0, 0, -1);

	public Impulse(InputContext context) {
		impulse = new Vector3f();
		walkSpeed = 20f;
		sprintSpeed = 100f;
		this.speed = walkSpeed;
		this.input = context;
	}

	private void configureInput() {
		input = new InputContext();
		input.addKeyState(GLFW.GLFW_KEY_W, "forward");
		input.addKeyState(GLFW.GLFW_KEY_A, "left");
		input.addKeyState(GLFW.GLFW_KEY_D, "right");
		input.addKeyState(GLFW.GLFW_KEY_S, "back");
		input.addKeyState(GLFW.GLFW_KEY_SPACE, "jump");
		input.addKeyState(GLFW.GLFW_KEY_LEFT_SHIFT, "sprint");
		input.addKeyState(GLFW.GLFW_KEY_LEFT_CONTROL, "croutch");

	}

	public Vector3f getImpulse() {
		return new Vector3f(impulse);
	}

	public Impulse(InputContext context, float walkSpeed, float sprintSpeed) {
		this.input = context;
		impulse = new Vector3f();
		this.walkSpeed = walkSpeed;
		this.sprintSpeed = sprintSpeed;
		this.speed = walkSpeed;
	}
	
	public Impulse(){
		this.walkSpeed = 20f;
		this.sprintSpeed = 100f;
		this.speed = walkSpeed;
		configureInput();
	}

	public Impulse(float walkSpeed, float sprintSpeed) {
		this.walkSpeed = walkSpeed;
		this.sprintSpeed = sprintSpeed;
		this.speed = walkSpeed;
		configureInput();
	}

	public void update(CameraPivot pivot) {
		update(pivot.getOrientation().negate(null));
	}

	public void update(Quaternion orientation) {
		impulse = new Vector3f();
		Vector3f move = ((Vector3f) orientation.rotate(forward).scale(speed));
		Vector3f sideMove = ((Vector3f) orientation.rotate(right).scale(speed));
		Vector3f upMove = ((Vector3f) orientation.rotate(up).scale(speed));
		if (input.getState("forward")) {
			Vector3f.add(impulse, move, impulse);
		}
		if (input.getState("back")) {
			Vector3f.sub(impulse, move, impulse);
		}
		if (input.getState("left")) {
			Vector3f.sub(impulse, sideMove, impulse);
		}
		if (input.getState("right")) {
			Vector3f.add(impulse, sideMove, impulse);
		}
		if (input.getState("up")) {
			Vector3f.add(impulse, upMove, impulse);
		}
		if (input.getState("down")) {
			Vector3f.sub(impulse, upMove, impulse);
		}
		if (input.getState("sprint")) {
			speed = sprintSpeed;
			isSprinting = true;
		} else {
			speed = walkSpeed;
			isSprinting = false;
		}
	}

	public boolean isSprinting() {
		return isSprinting;
	}

}
