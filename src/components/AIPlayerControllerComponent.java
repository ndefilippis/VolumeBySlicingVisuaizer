package components;

import renderEngine.Display;
import util.Utils;
import vector.Quaternion;
import vector.Vector3f;

public class AIPlayerControllerComponent implements Component {
	private TransformComponent transform;
	private MotionComponent motion;
	private PhysicsComponent physics;
	private Vector3f goalLocation;
	private float speed = 20f;

	public AIPlayerControllerComponent(TransformComponent transform, MotionComponent motion, PhysicsComponent physics){
		this.transform = transform;
		this.motion = motion;
		this.goalLocation = Utils.randomVector(0, 1600, 0, 1600, 0, 1600);
		this.physics = physics;
	}

	@Override
	public void update() {
		if(Utils.random(0, 1) < Display.getFrameTimeSeconds()*0.1f){
			goalLocation = Utils.randomVector(1000, 2000, 1000, 2000, 1000, 2000);
			Vector3f toGoal = Vector3f.sub(goalLocation, transform.transform.getPosition(), null);
			if(toGoal.lengthSquared() != 0.0f){
				toGoal.normalise();
			};
			motion.velocity = toGoal;
			motion.velocity.scale(speed);
			transform.transform.setOrientation(Quaternion.AxisAngle(toGoal, 0f));
		}
	}

	@Override
	public ComponentType getType() {
		return ComponentType.AIPLAYER;
	}

}
