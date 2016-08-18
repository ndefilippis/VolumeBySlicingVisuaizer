package components;

import renderEngine.Display;
import terrains.Terrain;
import util.Utils;
import vector.Vector3f;

public class PhysicsComponent implements Component{
	public static final Vector3f GRAVITY = new Vector3f(0, -100f, 0);
	
	public PhysicsComponent(){}
	
	public PhysicsComponent(Vector3f gravity){}
	
	public Vector3f getScaledGravity(){
		Vector3f newGravity = new Vector3f(GRAVITY);
		newGravity.scale(Display.getFrameTimeSeconds());
		return newGravity;
	}
}
