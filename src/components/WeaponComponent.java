package components;

import java.util.ArrayList;
import java.util.List;

import collision.CollisionModel;
import entities.EntityC;
import particles.ParticleSystem;
import renderEngine.Display;
import vector.Quaternion;
import vector.Vector3f;

public class WeaponComponent implements Component{
	private static final int SHOOTIE_LIMIT = 5000;
	private ArrayList<EntityC> shooties;
	private RenderComponent projectile;
	private CollisionModel collisionModel;
	private TransformComponent transform;
	private float speed;
	private float rateOfFire;
	private boolean isShooting;
	
	private float lastShotTime;
	private float timeSinceLastShot;
	
	public WeaponComponent(TransformComponent transform, RenderComponent projectile, CollisionModel projectileCollision, float speed, float rateOfFire){
		this.projectile = projectile;
		this.speed = speed;
		this.rateOfFire = rateOfFire;
		this.transform = transform;
		this.collisionModel = projectileCollision;
		shooties = new ArrayList<EntityC>();
	}
	
	public void update(){
		if(isShooting){
			lastShotTime += Display.getFrameTimeSeconds();
		}
		else if(timeSinceLastShot >= 1f/rateOfFire){
			lastShotTime = 1f/rateOfFire;
		}
		timeSinceLastShot += Display.getFrameTimeSeconds();
		isShooting = false;
	}
	
	public void shoot(Vector3f fromPosition, Vector3f toPosition, List<EntityC> renderables){
		isShooting = true;
		while(lastShotTime >= 0){
			Vector3f direction = Vector3f.sub(toPosition, fromPosition, null);
			direction.normalise();
			direction.scale(speed);
			EntityC e = createProjectile(fromPosition, direction);
			shooties.add(e);
			renderables.add(e);
			if(shooties.size() >= SHOOTIE_LIMIT){
				renderables.remove(shooties.get(0));
				shooties.remove(0);
			}
			lastShotTime -= 1f/rateOfFire;
		}
		timeSinceLastShot = 0f;
	}
	
	private EntityC createProjectile(Vector3f position, Vector3f velocity){
		List<Component> componentList = new ArrayList<Component>();
		TransformComponent transform = new TransformComponent(position, new Quaternion(), 1);
		MotionComponent motion = new MotionComponent(transform, velocity, new Quaternion());
		CollisionComponent collision = new CollisionComponent(motion, transform, collisionModel);
		RenderComponent renderComponent = new RenderComponent(projectile);
		
		
		componentList.add(transform);
		componentList.add(renderComponent);
		componentList.add(motion);
		componentList.add(collision);
		return new EntityC(componentList);
	}

	public ArrayList<EntityC> getEntities() {
		return shooties;
	}
	
	@Override
	public ComponentType getType() {
		return ComponentType.WEAPON;
	}
}
