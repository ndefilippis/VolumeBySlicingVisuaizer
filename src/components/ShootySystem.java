package components;

import java.util.List;

import renderEngine.Display;

public class ShootySystem {
	
	public void update(EntityManager mgr){
		List<Entity> entities = mgr.getAll(ShootyMcTooty.class);
		for(Entity e : entities){
			ShootyMcTooty gun = e.as(ShootyMcTooty.class);
			if(gun.isShooting){
				gun.lastShotTime += Display.getFrameTimeSeconds();
			}
			else if(gun.timeSinceLastShot >= 1f/gun.rateOfFire){
				gun.lastShotTime = 1f/gun.rateOfFire;
			}
			gun.timeSinceLastShot += Display.getFrameTimeSeconds();
			gun.isShooting = false;
			for(int i = gun.getEntities().size() - 1; i >= 0; i--){
				
				BulletComponent bullet = gun.getEntities().get(i).as(BulletComponent.class);
				if(bullet.currentLifetime > bullet.maxLifetime){
					mgr.remove(gun.getEntities().get(i));
					gun.getEntities().remove(i);
				}
				else{
					bullet.currentLifetime += Display.getFrameTimeSeconds();
				}
			}
		}
	}
	
}
