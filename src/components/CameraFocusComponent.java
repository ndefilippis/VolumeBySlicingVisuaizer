package components;

import vector.Quaternion;
import vector.Vector3f;

public class CameraFocusComponent implements Component{
	public Vector3f cameraPosition;
	
	public CameraFocusComponent(Vector3f cameraPosition){
		this.cameraPosition = cameraPosition;
	}
}
