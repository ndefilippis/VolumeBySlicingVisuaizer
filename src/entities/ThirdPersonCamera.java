package entities;

import input.Axis;
import input.CameraPivot;
import input.InputContext;
import input.InputHandler;
import terrains.Terrain;
import util.Utils;
import vector.Quaternion;
import vector.Vector3f;

public class ThirdPersonCamera extends Camera{
	private float distanceFromFocus = 50f;
	private float angleAroundFocus = 0f;
	private Terrain[] t;
	private InputContext input;
	
	private Entity focusOn;
	private Vector3f offset;

	public ThirdPersonCamera(Entity entity, Terrain[] t){
		this.focusOn = entity;
		offset = new Vector3f();
		this.t = t;
		
		input = new InputContext();
		input.addScrollRange(Axis.VERTICAL, "zoom");
	}
	@Override
	public Vector3f getPosition() {
		Vector3f position = new Vector3f();
		Vector3f.add(focusOn.getPosition(), offset, position);
		if(position.y - 1f < Utils.getTerrainHeight(t, position.x, position.z)){
			position.y = Utils.getTerrainHeight(t, position.x, position.z) + 1f;
		}
		return position;
	}
	
	public void update(){
		calculateZoom();
		Quaternion q = focusOn.getOrientation().negate(null);
		offset = q.rotate(new Vector3f(0, 0, 1));
		offset.scale(distanceFromFocus);
		offset.y += 5.0f;
	}

	
	private void calculateZoom(){
		float zoomLevel = input.getRange("zoom");
		distanceFromFocus -= zoomLevel;
		distanceFromFocus = Math.max(distanceFromFocus, 1.0f);
	}
	@Override
	public Quaternion getOrientation() {
		return pivot.getOrientation();
	}
}
