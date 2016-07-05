package water;

import util.Transform;
import vector.Quaternion;
import vector.Vector3f;

public class WaterTile {
	
	private static final float TILE_SIZE = 600;
	private Transform transform;
	
	public WaterTile(float centerX, float centerZ, float height){
		this.transform = new Transform(new Vector3f(centerX, height, centerZ), new Quaternion(), TILE_SIZE);
	}

	public float getHeight() {
		return transform.getPosition().y;
	}

	public float getX() {
		return transform.getPosition().x;
	}

	public float getZ() {
		return transform.getPosition().z;
	}

	public Transform getTransform(){
		return transform;
	}

}
