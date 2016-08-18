package entities;

import org.lwjgl.glfw.GLFW;

import components.Entity;
import input.Axis;
import input.InputContext;
import renderEngine.Display;
import terrains.Terrain;
import util.Transform;
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

	public ThirdPersonCamera(Entity focusOn, Terrain[] t){
		this.focusOn = focusOn;
		offset = new Vector3f();
		this.t = t;
		
		input = new InputContext();
		input.addScrollRange(Axis.VERTICAL, "zoom");
		input.addKeyState(GLFW.GLFW_KEY_LEFT, "rotate_left");
		input.addKeyState(GLFW.GLFW_KEY_RIGHT, "rotate_right");
	}
	public ThirdPersonCamera(Entity focusOn) {
		this.focusOn = focusOn;
		offset = new Vector3f();
		
		input = new InputContext();
		input.addScrollRange(Axis.VERTICAL, "zoom");
		input.addKeyState(GLFW.GLFW_KEY_LEFT, "rotate_left");
		input.addKeyState(GLFW.GLFW_KEY_RIGHT, "rotate_right");
	}
	@Override
	public Vector3f getPosition() {
		Vector3f position = new Vector3f();
		Vector3f.add(focusOn.as(Transform.class).getPosition(), offset, position);
		if(t != null){
			if(position.y - 1f < Utils.getTerrainHeight(t, position.x, position.z)){
				position.y = Utils.getTerrainHeight(t, position.x, position.z) + 1f;
			}
		}
		return position;
	}
	
	public void update(){
		calculateZoom();
		if(input.getState("rotate_left")){
			angleAroundFocus -= 0.5f*Display.getFrameTimeSeconds();
		}
		if(input.getState("rotate_right")){
			angleAroundFocus += 0.5f*Display.getFrameTimeSeconds();
		}
		Quaternion q = focusOn.as(Transform.class).getOrientation();
		offset = q.rotate(new Vector3f((float)Math.sin(angleAroundFocus), 0, (float)Math.cos(angleAroundFocus)));
		offset.normalise();
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
		Quaternion q = focusOn.as(Transform.class).getOrientation().negate(null);
		Quaternion aroundFocus = Quaternion.AxisAngle(new Vector3f(0, 1, 0), angleAroundFocus);
		return Quaternion.mul(q, aroundFocus.negate(null), null);
	}
}
