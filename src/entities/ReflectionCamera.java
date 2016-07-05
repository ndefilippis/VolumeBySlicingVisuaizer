package entities;

import vector.Quaternion;
import vector.Vector3f;
import water.WaterTile;

public class ReflectionCamera extends Camera{

	private Camera camera;
	private WaterTile reflectOver;
	
	public ReflectionCamera(Camera camera, WaterTile tile){
		this.camera = camera;
		this.reflectOver = tile;
	}

	@Override
	public Vector3f getPosition() {
		Vector3f pos = camera.getPosition();
		float distance = 2 * (pos.y - reflectOver.getHeight());
		return new Vector3f(pos.x, pos.y - distance, pos.z);
	}

	@Override
	public void update() {
		return;
	}

	@Override
	public Quaternion getOrientation() {
		Quaternion q = camera.getOrientation();
		return new Quaternion(q.x, -q.y, q.z, -q.w);
	}
	
}
