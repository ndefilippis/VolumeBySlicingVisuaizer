package entities;

import org.lwjgl.glfw.GLFW;

import input.CameraPivot;
import input.Impulse;
import input.InputHandler;
import models.TexturedModel;
import renderEngine.Display;
import terrain.Terrain;
import util.Utils;
import vector.Quaternion;
import vector.Vector3f;
import vector.Vector4f;

public class Player extends Entity{
	private Impulse impulse;
	private Vector3f myPosition;
	private CameraPivot look;
	private float vy;
	private boolean isJumping;
	private float height = 1f;
	private static float gravity = 50f;
	
	public Player(TexturedModel model, float x, float z){
		super(model, new Vector3f(x, 0, z), 0, 180, 0, 1);
		impulse = new Impulse(20, 50);
		myPosition = super.getPosition();
		look = new CameraPivot(0, 0, 0);
	}
	
	public void update(Terrain[] terrains){
		look.update();
		impulse.update(look);
		myPosition.x += impulse.getImpulse().x;
		myPosition.z += impulse.getImpulse().z;
		if(InputHandler.isKeyPressed(GLFW.GLFW_KEY_SPACE)){
			if(!isJumping){
				if(impulse.isSprinting()){
					vy = 50.0f;
				}
				else{
					vy = 15.0f;
				}
				isJumping = true;
			}
		}
		if(InputHandler.isKeyPressed(GLFW.GLFW_KEY_LEFT_CONTROL)){
			height = Math.max(1.0f, height - 25.0f*Display.getFrameTimeSeconds());
		}
		else{
			height = Math.min(5f, height+25.0f*Display.getFrameTimeSeconds());
		}
		vy -= gravity*Display.getFrameTimeSeconds();
		myPosition.y += vy*Display.getFrameTimeSeconds();
		float terrainHeight = Utils.getTerrainHeight(terrains, myPosition.x, myPosition.z);
		if(myPosition.y < terrainHeight+1.0f){
			myPosition.y = (terrainHeight)+1.0f;
			vy = 0;
			isJumping = false;
		}
		setPosition(myPosition);
		Vector3f lookD = look.getLookDirection();
		Vector3f upD = look.getUpDirection();
		Vector3f rightD = look.getRightDirection();
		//Quaternion result = Quaternion.mul(q1, q2, null);
		//result = Quaternion.mul(q3, result, null);
		//result.normalise();
		Quaternion q1 = new Quaternion();
		Quaternion q2 = new Quaternion();
		Quaternion q3 = new Quaternion();
		q1.setFromAxisAngle(new Vector4f(upD.x, upD.y, upD.z,(float)Math.toRadians( 180 - look.getYaw())));
		q2.setFromAxisAngle(new Vector4f(rightD.x, rightD.y, rightD.z,(float)Math.toRadians( - look.getPitch() )));
		q3.setFromAxisAngle(new Vector4f(lookD.x, lookD.y, lookD.z, (float)Math.toRadians(look.getRoll())));
		setOrientation(q3);
	}
	
	
	
	public float getHeight(){
		return height;
	}
	
	public CameraPivot getPivot(){
		return look;
	}
}
