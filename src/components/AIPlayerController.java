package components;

import java.util.List;

import EngineTest.SpaceShipTest;
import renderEngine.Display;
import util.Transform;
import util.Utils;
import vector.Quaternion;
import vector.Vector3f;

public class AIPlayerController {

	public void update(EntityManager mgr) {
		List<Entity> entities = mgr.getAll(Transform.class, MotionComponent.class, AIPlayerComponent.class,
				ShootyMcTooty.class);
		for (Entity entity : entities) {
			Vector3f toGoal = Vector3f.sub(entity.as(AIPlayerComponent.class).goalLocation,
					entity.as(Transform.class).getPosition(), null);
			if (Utils.random(0, 1) < Display.getFrameTimeSeconds() * 0.1f) {
				entity.as(AIPlayerComponent.class).goalLocation = Utils.randomVector(1000, 2000, 1000, 2000, 1000,
						2000);
				if (toGoal.lengthSquared() != 0.0f) {
					toGoal.normalise();
				}
				;
				entity.as(MotionComponent.class).velocity = toGoal;
				entity.as(MotionComponent.class).velocity.scale(entity.as(AIPlayerComponent.class).speed);
				entity.as(Transform.class).setOrientation(Quaternion.lookAt(toGoal.negate(null), new Vector3f(0, 1, 0)));
			}
			
			//entity.as(ShootyMcTooty.class).shoot(entity.as(Transform.class).getPosition(), toGoal, mgr);
			//entity.as(ShootyMcTooty.class).update();
		}
	}
}
