package entities;

import input.CameraPivot;
import input.InputHandler;
import vector.Quaternion;
import vector.Vector3f;

public class FirstPersonCamera extends Camera{
	private Entity entity;
	
	public FirstPersonCamera(Entity entity){
		this.entity = entity;
	}
	@Override
	public Vector3f getPosition() {
		Vector3f position = entity.getPosition();
		return new Vector3f(position.x, position.y + entity.getHeight(), position.z);
	}
	@Override
	public void update() {
		return;
	}
	@Override
	public Quaternion getOrientation() {
		return entity.getOrientation();
	}
}
