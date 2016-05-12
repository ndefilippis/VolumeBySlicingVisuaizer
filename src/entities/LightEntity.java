package entities;

import models.TexturedModel;
import vector.Vector3f;

public class LightEntity {
	private Entity entity;
	private Light light;
	
	public LightEntity(Entity entity, Light light){
		this.entity = entity;
		this.light = light;
		Vector3f pos = entity.getPosition();
		light.setPosition(new Vector3f(pos.x, pos.y + entity.getHeight(), pos.z));
	}
	
	public LightEntity(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale, float height, Vector3f lightColor, Vector3f lightAttenuation){
		this.entity = new Entity(model, position, rotX, rotY, rotZ, scale, height);
		this.light = new Light(new Vector3f(position.x, position.y + entity.getHeight(), position.z), lightColor, lightAttenuation);
	}
	
	public Light getLight(){
		return light;
	}
	
	public Entity getEntity(){
		return entity;
	}
}
