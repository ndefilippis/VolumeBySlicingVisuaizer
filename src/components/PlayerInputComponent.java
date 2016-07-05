package components;

import input.CameraPivot;
import input.Impulse;
import input.InputContext;
import renderEngine.Display;
import vector.Vector3f;

public class PlayerInputComponent extends HumanInputComponent{
	private MotionComponent motion;
	private TransformComponent transform;
	private CameraFocusComponent height;
	private PhysicsComponent physics;
	private CameraPivot pivot;
	private Impulse impulse;
	private boolean isJumping;
	
	public PlayerInputComponent(InputContext context, 
		TransformComponent transform, 
		MotionComponent motion, 
		CameraFocusComponent heightComponent,
		PhysicsComponent physics) {
		super(context);
		this.motion = motion;
		this.transform = transform;
		this.height = heightComponent;
		this.physics = physics;
		pivot = new CameraPivot(0, 0, 0);
		impulse = new Impulse(20, 50);
	}

	@Override
	public void update() {
		pivot.update();
		impulse.update(pivot);
		Vector3f velocity = impulse.getImpulse();
		float scale = velocity.length();
		velocity.y = 0;
		velocity.normalise();
		velocity.scale(scale);
		motion.velocity = velocity;
		if(context.getState("jumping")){
			if(!isJumping){
				if(impulse.isSprinting()){
					velocity.y = 100.0f;
				}
				else{
					velocity.y = 30.0f;
				}
				isJumping = true;
			}
		}
		if(context.getState("croutch")){
			height.height = Math.max(1.0f, height.height - 25.0f*Display.getFrameTimeSeconds());
		}
		else{
			height.height = Math.min(5f, height.height+25.0f*Display.getFrameTimeSeconds());
		}
		if(physics.collisionFound()){
			isJumping = false;
		}
		transform.transform.setOrientation(pivot.getOrientation());
	}
	
	@Override
	public ComponentType getType() {
		return ComponentType.PLAYER;
	}
}
