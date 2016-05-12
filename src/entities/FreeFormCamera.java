package entities;

import input.CameraPivot;
import input.CameraRig;
import vector.Vector3f;

public class FreeFormCamera extends Camera{
	private CameraRig rig;
	private CameraPivot pivot;
	
	public FreeFormCamera(){
		rig = new CameraRig(0, 0, 0, 1, 5);
		pivot = new CameraPivot(0, 0, 0);
	}
	
	public void move(){
		pivot.update();
		Vector3f lookDirection = pivot.getLookDirection();
		Vector3f upDirection = pivot.getUpDirection();
		Vector3f rightDirection = pivot.getRightDirection();
		rig.update(lookDirection, upDirection, rightDirection);
	}
	
	public float getRoll(){
		return pivot.getRoll();
	}
	
	public float getYaw(){
		return pivot.getYaw();
	}
	
	public float getPitch(){
		return pivot.getPitch();
	}
	
	public Vector3f getPosition(){
		return rig.getPosition();
	}

}
