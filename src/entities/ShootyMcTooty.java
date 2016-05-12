package entities;

import renderEngine.Display;
import terrain.Terrain;
import util.Utils;
import vector.Vector3f;

public class ShootyMcTooty{
	
	private Entity entity;
	private Vector3f ray;
	private float speed = 100f;
	
	public ShootyMcTooty(Entity entity, Vector3f ray){
		this.entity = entity;
		this.ray = ray;
	}
	
	public void update(){
		Vector3f pos = entity.getPosition();
		Vector3f move = new Vector3f(ray);
		move.scale(Display.getFrameTimeSeconds() * speed);
		Vector3f newPos = new Vector3f();
		Vector3f.add(pos, move, newPos);
		entity.setPosition(newPos);
		
	}

	public Entity getEntity() {
		return entity;
	}
}
