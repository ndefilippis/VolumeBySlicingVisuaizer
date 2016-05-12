package entities;

import input.CameraPivot;
import input.InputHandler;
import models.TexturedModel;
import renderEngine.Display;
import terrain.Terrain;
import util.Utils;
import vector.Quaternion;
import vector.Vector3f;
import vector.Vector4f;

public class Airplane extends Entity{
	private Vector3f myPosition;
	private CameraPivot look;
	private float height = 5f;
	private float speed = 100f;
	
	public Airplane(TexturedModel model, float x, float z){
		super(model, new Vector3f(x, 0, z), 270f, 0f, 0f, 1f);
		look = new CameraPivot(0, 0, 0, 150, 10, 10);
		myPosition = super.getPosition();
	}
	
	public void update(Terrain[] terrains){
		look.update();
		Vector3f move = look.getLookDirection();
		move.scale(speed*Display.getFrameTimeSeconds());
		Vector3f.add(myPosition, move, myPosition);
		float terrainHeight = Utils.getTerrainHeight(terrains, myPosition.x, myPosition.z);
		if(myPosition.y < terrainHeight){
			myPosition.y = (terrainHeight);
		}
		setPosition(myPosition);
		Vector3f lookD = look.getLookDirection();
		Vector3f upD = look.getUpDirection();
		Vector3f rightD = look.getRightDirection();
		Quaternion q1 = new Quaternion();
		Quaternion q2 = new Quaternion();
		Quaternion q3 = new Quaternion();
		q1.setFromAxisAngle(new Vector4f(lookD.x, lookD.y, lookD.z, (float)Math.toRadians(look.getRoll())));
		q2.setFromAxisAngle(new Vector4f(0, 1, 0, (float)Math.toRadians(-look.getYaw())));
		q3.setFromAxisAngle(new Vector4f(rightD.x, rightD.y, rightD.z, (float)Math.toRadians(270 - look.getPitch())));
		Quaternion result = Quaternion.mul(q1, q2, null);
		result = Quaternion.mul(q3, result, null);
		result.normalise();
		setOrientation(result);
	}
	
	@Override
	public float getHeight() {
		return height;
	}
	
	public CameraPivot getPivot(){
		return look;
	}
}
