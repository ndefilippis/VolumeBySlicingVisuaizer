 package components;

import java.util.List;

import input.InputContext;
import renderEngine.Display;
import util.Transform;
import vector.Vector3f;

public class PlayerController {

	public void update(EntityManager mgr){
		List<Entity> entities = mgr.getAll(Transform.class, MotionComponent.class, PlayerInputComponent.class, CollisionComponent.class, CameraFocusComponent.class);
		for(Entity entity : entities){
			entity.as(PlayerInputComponent.class).updateMovement();
			Vector3f velocity = entity.as(PlayerInputComponent.class).impulse.getImpulse();
			float scale = velocity.length();
			velocity.y = 0;
			if(velocity.length() != 0){
				velocity.normalise();
			}
			velocity.scale(scale);
			InputContext context = entity.as(PlayerInputComponent.class).context;
			if(context.getState("jumping")){
				if(!entity.as(PlayerInputComponent.class).isJumping()){
					if(entity.as(PlayerInputComponent.class).impulse.isSprinting()){
						velocity.y = 100.0f;
					}
					else{
						velocity.y = 30.0f;
					}
					entity.as(PlayerInputComponent.class).setJumping(true);
					entity.as(MotionComponent.class).velocity.y = velocity.y;
				}
			}
			entity.as(MotionComponent.class).velocity.x = velocity.x;
			entity.as(MotionComponent.class).velocity.z = velocity.z;
			
			Vector3f position = entity.as(CameraFocusComponent.class).cameraPosition;
			if(context.getState("croutch")){
				position.y = Math.max(1.0f, position.y - 25.0f*Display.getFrameTimeSeconds());
			}
			else{
				position.y = Math.min(5f, position.y+25.0f*Display.getFrameTimeSeconds());
			}
			if(entity.as(CollisionComponent.class).foundCollision){
				entity.as(PlayerInputComponent.class).setJumping(false);
			}
			entity.as(Transform.class).setOrientation(entity.as(PlayerInputComponent.class).pivot.getOrientation().negate(null));
		}
		
	}
}
