package entities;

import input.CameraPivot;
import input.InputHandler;
import vector.Vector3f;

public class FirstPersonCamera extends Camera{
	private Entity entity;
	private CameraPivot pivot;
	
	public FirstPersonCamera(Entity entity, CameraPivot pivot){
		this.entity = entity;
		this.pivot = pivot;
	}
	@Override
	public Vector3f getPosition() {
		Vector3f position = entity.getPosition();
		return new Vector3f(position.x, position.y + entity.getHeight(), position.z);
	}

	@Override
	public float getPitch() {
		return pivot.getPitch();
	}

	@Override
	public float getYaw() {
		return pivot.getYaw();
	}

	@Override
	public float getRoll() {
		return pivot.getRoll();
	}
	@Override
	public void move() {
		return;
	}
}
