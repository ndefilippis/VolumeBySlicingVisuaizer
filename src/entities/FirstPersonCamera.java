package entities;

import components.CameraFocusComponent;
import components.Entity;
import util.Transform;
import vector.Quaternion;
import vector.Vector3f;

public class FirstPersonCamera extends Camera{
	private Entity focusOn;
	
	public FirstPersonCamera(Entity focus){
		this.focusOn = focus;
	}
	@Override
	public Vector3f getPosition() {
		Vector3f position = focusOn.as(Transform.class).getPosition();
		//Vector3f cameraPosition = focusOn.as(Transform.class).getOrientation().rotate(focusOn.as(CameraFocusComponent.class).cameraPosition);
		Vector3f toReturn = Vector3f.add(position, focusOn.as(CameraFocusComponent.class).cameraPosition, null);
		return toReturn;
	}
	@Override
	public void update() {
		return;
	}
	@Override
	public Quaternion getOrientation() {
		return focusOn.as(Transform.class).getOrientation().negate(null);
	}
}
