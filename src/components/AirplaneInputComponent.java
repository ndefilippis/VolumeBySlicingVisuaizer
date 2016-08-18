package components;

import input.CameraPivot;
import input.InputContext;
import renderEngine.Display;
import vector.Quaternion;
import vector.Vector3f;

public class AirplaneInputComponent extends HumanInputComponent{
	public CameraPivot pivot;
	public float maxSpeed = 300f;
	public float ratio = 0.5f;
	public boolean turbo = false;
	private float turboTime = 0.0f;
	private float maxTurboTime = 2.0f;
	private float maxTurboCooldownTime = 5.0f;
	private float turboCooldownTime = 0.0f;
	
	public AirplaneInputComponent(InputContext context) {
		super(context);
		pivot = new CameraPivot(context, 0, 0, 0, 20, 5, 20);
		pivot.disableLimits();
	}
	
	public void enableTurbo(){
		if(turboCooldownTime >= maxTurboCooldownTime){
			this.turbo = true;
			turboCooldownTime = 0.0f;
		}
	}
	public void updateTurboTime(float dt){
		if(turbo){
			turboTime += dt;
			if(turboTime >= maxTurboTime){
				turbo = false;
				turboTime = 0.0f;
			}
		}
		else{
			turboCooldownTime += dt;
		}
	}
}
