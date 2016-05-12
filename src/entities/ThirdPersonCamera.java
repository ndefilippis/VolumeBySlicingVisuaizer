package entities;

import input.CameraPivot;
import input.InputHandler;
import terrain.Terrain;
import util.Utils;
import vector.Vector3f;

public class ThirdPersonCamera extends Camera{
	private float distanceFromFocus = 50f;
	private float angleAroundFocus = 0f;
	private CameraPivot pivot;
	private Terrain[] t;
	
	private Entity focusOn;
	private Vector3f offset;

	public ThirdPersonCamera(Entity entity, CameraPivot pivot, Terrain[] t){
		this.focusOn = entity;
		offset = new Vector3f();
		this.pivot = pivot;
		this.t = t;
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
	
	public void move(){
		calculateZoom();
		float horizontalDistance = (float)(distanceFromFocus * Math.cos(Math.toRadians(-pivot.getPitch())));
		float verticalDistance = (float)(distanceFromFocus * Math.sin(Math.toRadians(-pivot.getPitch())));
		calculateCameraPosition(horizontalDistance, verticalDistance);
	}
	
	private void calculateCameraPosition(float horiz, float vert){
		float angle = -pivot.getYaw();
		offset.x = (float)(horiz * Math.sin(Math.toRadians(angle)));
		offset.z = (float)(horiz * Math.cos(Math.toRadians(angle)));
		offset.y = -vert; //+ focusOn.getHeight();
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
	
	private void calculateZoom(){
		float zoomLevel = InputHandler.scrollDistance;
		distanceFromFocus -= zoomLevel;
		distanceFromFocus = Math.max(distanceFromFocus, 1.0f);
	}
}
