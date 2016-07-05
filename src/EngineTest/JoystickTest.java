package EngineTest;

import input.Axis;
import input.InputContext;
import input.InputHandler;
import renderEngine.Display;

public class JoystickTest {
	private static Display display;

	public static void main(String[] args) {
		display = new Display();
		InputHandler inputHandler = new InputHandler();
		InputContext joystickContext = new InputContext();
		joystickContext.addJoystickButtonAction(0, "shoot!");
		joystickContext.addJoystickRange(Axis.VERTICAL, "vertical");
		joystickContext.addJoystickRange(Axis.HORIZONTAL, "horizontal");
		joystickContext.addJoystickRange(Axis.TWIST, "twist");
		joystickContext.addJoystickRange(Axis.THROTTLE, "throttle");
		
		InputHandler.registerComponent(joystickContext);
		while (display.shouldClose()) {
			inputHandler.update();
			
			if(joystickContext.actionPerformed("shoot!")){
				System.out.println("Pew");
			}
			
			inputHandler.clear();
			
			display.update();
		}
		display.cleanUp();
	}

}
