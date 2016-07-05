package components;

import input.CameraPivot;
import input.InputContext;
import renderEngine.Display;
import vector.Vector3f;

public class AirplaneInputComponent extends HumanInputComponent{
	private MotionComponent motion;
	private TransformComponent transform;
	private CameraPivot pivot;
	public float speed = 45f;
	
	public AirplaneInputComponent(InputContext context, TransformComponent transform, MotionComponent motion, CameraFocusComponent height) {
		super(context);
		this.motion = motion;
		this.transform = transform;
		pivot = new CameraPivot(0, 0, 0, 20, 5, 20);
		pivot.disableLimits();
	}

	@Override
	public void update() {
		pivot.update();
		Vector3f move = pivot.getOrientation().negate(null).rotate(new Vector3f(0, 0, -1));
		move.normalise();
		float newSpeed = speed*(-context.getRange("speedScale")+1)/2;
		move.scale(newSpeed*Display.getFrameTimeSeconds());
		motion.velocity = move;
		transform.transform.setOrientation(pivot.getOrientation().negate(null));
	}
	
	@Override
	public ComponentType getType() {
		return ComponentType.AIRPLANE;
	}
}
