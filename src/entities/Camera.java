package entities;

import vector.Quaternion;
import vector.Vector3f;

public abstract class Camera{

	public abstract Vector3f getPosition();

	public abstract Quaternion getOrientation();

	public Vector3f getForwardDirection(){
		return getOrientation().rotate(new Vector3f(0, 0, -1));
	}

	public Vector3f getRightDirection(){
		return getOrientation().rotate(new Vector3f(1, 0, 0));
	}

	public Vector3f getUpDirection(){
		return getOrientation().rotate(new Vector3f(0, 1, 0));
	}
	
	public abstract void update();

}