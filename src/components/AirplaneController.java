package components;

import java.util.List;

import renderEngine.Display;
import util.Transform;
import vector.Vector3f;

public class AirplaneController {

	public void update(EntityManager mgr) {
		List<Entity> entities = mgr.getAll(Transform.class, MotionComponent.class, AirplaneInputComponent.class);
		for (Entity entity : entities) {
			AirplaneInputComponent airplane = entity.as(AirplaneInputComponent.class);
			if(airplane.context.actionPerformed("speedUp")){
				airplane.ratio += 0.05f;
			}
			if(airplane.context.actionPerformed("speedDown")){
				airplane.ratio -= 0.05f;
			}
			if(airplane.context.actionPerformed("turbo")){
				airplane.enableTurbo();
			}
			airplane.ratio = Math.max(0, Math.min(1, airplane.ratio));
			float speed = airplane.ratio * airplane.maxSpeed;
			if(airplane.turbo){
				speed = airplane.maxSpeed * 1.5f;
			}
			airplane.updateTurboTime(Display.getFrameTimeSeconds());
			airplane.pivot.update();
			Vector3f move = entity.as(AirplaneInputComponent.class).pivot.getOrientation().negate(null).rotate(new Vector3f(0, 0, -1));
			move.normalise();
			float newSpeed = speed * (-airplane.context.getRange("speedScale") + 1) / 2;
			move.scale(newSpeed);
			entity.as(MotionComponent.class).velocity = move;
			entity.as(Transform.class).setOrientation(entity.as(AirplaneInputComponent.class).pivot.getOrientation().negate(null));
		}
	}

}
