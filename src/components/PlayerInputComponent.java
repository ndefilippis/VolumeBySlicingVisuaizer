package components;

import input.CameraPivot;
import input.Impulse;
import input.InputContext;
import renderEngine.Display;
import vector.Vector3f;

public class PlayerInputComponent extends HumanInputComponent{
	CameraPivot pivot;
	Impulse impulse;
	private boolean isJumping;
	
	public PlayerInputComponent(InputContext context) {
		super(context);
		pivot = new CameraPivot(context, 0, 0, 0);
		impulse = new Impulse(context, 20, 50);
	}
	
	public boolean isJumping(){
		return isJumping;
	}
	
	public void setJumping(boolean b){
		isJumping = b;
	}

	public void updateMovement() {
		pivot.update();
		impulse.update(pivot);
	}
}
