package components;

import java.util.ArrayList;
import java.util.List;

import collision.CollisionModel;
import models.TexturedModel;
import renderEngine.Display;
import util.Transform;
import vector.Quaternion;
import vector.Vector3f;

public class ShootyMcTooty implements Component{
	private static final int SHOOTIE_LIMIT = 200;
	private ArrayList<Entity> shooties;
	private TexturedModel projectile;
	private CollisionModel collisionModel;
	float speed;
	float rateOfFire;
	boolean isShooting;
	
	float lastShotTime;
	float timeSinceLastShot;
	
	public ShootyMcTooty(TexturedModel projectile, CollisionModel projectileCollision, float speed, float rateOfFire){
		this.projectile = projectile;
		this.speed = speed;
		this.rateOfFire = rateOfFire;
		this.collisionModel = projectileCollision;
		shooties = new ArrayList<Entity>();
	}
	
	public void shoot(Vector3f fromPosition, Vector3f direction, EntityManager toAdd){
		isShooting = true;
		while(lastShotTime >= 0){
			direction = new Vector3f(direction);
			direction.normalise();
			direction.scale(speed);
			Entity e = createProjectile(toAdd, fromPosition, direction);
			shooties.add(e);
			if(shooties.size() >= SHOOTIE_LIMIT){
				toAdd.remove(shooties.get(0));
				shooties.remove(0);
			}
			lastShotTime -= 1f/rateOfFire;
		}
		timeSinceLastShot = 0f;
		
	}
	
	private Entity createProjectile(EntityManager mgr, Vector3f position, Vector3f velocity){
		Quaternion orientation = Quaternion.lookAt(velocity, new Vector3f(0, 1, 0));
		Entity e = mgr.createEntity();
		e.add(new Transform(position, orientation, 1));
		e.add(new MotionComponent(velocity, new Vector3f()));
		e.add(new CollisionComponent(collisionModel, false));
		e.add(new RenderComponent(projectile));
		e.add(new BulletComponent(5f));
		return e;
	}

	public ArrayList<Entity> getEntities() {
		return shooties;
	}
}
