package entities;

import input.CameraPivot;
import input.CameraRig;
import vector.Quaternion;
import vector.Vector3f;

public class FreeFormCamera extends Camera{
	private CameraRig rig;
	private CameraPivot pivot;
	
	public FreeFormCamera(){
		rig = new CameraRig(0, 0, 0, 1, 5);
		pivot = new CameraPivot(0, 0, 0);
	}
	
	public void update(){
		pivot.update();
		rig.update(pivot.getOrientation());
	}
	
	public Vector3f getPosition(){
		return rig.getPosition();
	}

	@Override
	public Quaternion getOrientation() {
		return pivot.getOrientation();
	}

}
