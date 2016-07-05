package components;

import vector.Quaternion;
import vector.Vector3f;

public class CameraFocusComponent implements Component{
	public float height;
	private TransformComponent transform;
	
	public CameraFocusComponent(float height, TransformComponent transform){
		this.height = height;
	}

	@Override
	public void update() {
	}
	
	public Vector3f getPosition(){
		return transform.transform.getPosition();
	}
	
	public Quaternion getOrientation(){
		return transform.transform.getOrientation();
	}
	
	@Override
	public ComponentType getType() {
		return ComponentType.CAMERAFOCUS;
	}
}
