package input;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPos;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetMouseButtonCallback;
import static org.lwjgl.glfw.GLFW.glfwSetScrollCallback;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;

import renderEngine.Display;
import vector.Vector2f;

public class InputHandler{
	private GLFWKeyCallback keyCallback;
	private GLFWCursorPosCallback cursorPosCallback;
	private GLFWScrollCallback scrollCallback;
	private GLFWMouseButtonCallback mouseButtonCallback;
	
	private static List<InputContext> contexts = new LinkedList<InputContext>();
	
	private static final int NUMBER_OF_KEYS = 350;
	private static final int NUMBER_OF_MOUSE_BUTTONS = 7;
	private static final int NUMBER_OF_JOYSTICK_BUTTONS = 7;
	private static boolean[] joystickIsPressed = new boolean[20];
	private static float[] lastJoystickLocation = new float[4];
	private static float[] joystickMovedDistance = new float[4];
	private static Vector2f lastMouseLocation = new Vector2f(0, 0);
	private static Vector2f mouseMovedDistance = new Vector2f(0, 0);
	private static float scrollDistance = 0;
	private final int ORIGIN_X, ORIGIN_Y;
	
	public InputHandler(){
		ORIGIN_X = (int)Display.getWidth()/2;
		ORIGIN_Y = (int)Display.getHeight()/2;
		glfwSetKeyCallback(renderEngine.Display.getWindow(), keyCallback = new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {
            	if(action == GLFW_PRESS){
            		for(InputContext context : contexts){
            			if(context.getKeyInputTypeFor(key) == InputType.STATE){
            				context.setStateState(context.getKeyAction(key), true);
            				break;
            			}
            		}
            	}
            	else if(action == GLFW_RELEASE){
            		for(InputContext context : contexts){
            			if(context.getKeyInputTypeFor(key) == InputType.ACTION){
            				context.setActionPerformed(context.getKeyAction(key), true);
            				break;
            			}
            		}
            		for(InputContext context : contexts){
            			if(context.getKeyInputTypeFor(key) == InputType.STATE){
            				context.setStateState(context.getKeyAction(key), false);
            				break;
            			}
            		}
            	}
            }
        });
		glfwSetCursorPos(Display.getWindow(), ORIGIN_X, ORIGIN_Y);
        glfwSetCursorPosCallback(renderEngine.Display.getWindow(), cursorPosCallback = new GLFWCursorPosCallback(){
        	public void invoke(long window, double xpos, double ypos){
        		mouseMovedDistance.x += (float)(xpos - ORIGIN_X);
        		mouseMovedDistance.y += (float)(ypos - ORIGIN_Y);
        		lastMouseLocation.x = (float)xpos;
        		lastMouseLocation.y = (float)ypos;
        		for(InputContext context : contexts){
        			if(context.getMouseMovementInputTypeFor(Axis.HORIZONTAL) == InputType.RANGE){
        				context.setRangeValue(context.getMouseMovementAction(Axis.HORIZONTAL), mouseMovedDistance.x);
        				break;
        			}
        		}
        		for(InputContext context : contexts){
        			if(context.getMouseMovementInputTypeFor(Axis.VERTICAL) == InputType.RANGE){
        				context.setRangeValue(context.getMouseMovementAction(Axis.VERTICAL), mouseMovedDistance.y);
        				break;
        			}
        		}
        	}
        });
        
        glfwSetScrollCallback(renderEngine.Display.getWindow(), scrollCallback = new GLFWScrollCallback(){

			@Override
			public void invoke(long window, double xoff, double yoff) {
				scrollDistance += yoff;
				for(InputContext context : contexts){
        			if(context.getScrollInputTypeFor(Axis.VERTICAL) == InputType.RANGE){
        				context.setRangeValue(context.getScrollAction(Axis.VERTICAL), scrollDistance);
        				break;
        			}
        		}
			}
        	
        });
        glfwSetMouseButtonCallback(Display.getWindow(), mouseButtonCallback = new GLFWMouseButtonCallback(){

			@Override
            public void invoke(long window, int key, int action, int mods) {
            	if(action == GLFW_PRESS){
            		for(InputContext context : contexts){
            			if(context.getMouseButtonInputTypeFor(key) == InputType.ACTION){
            				context.setActionPerformed(context.getMouseButtonAction(key), true);
            				break;
            			}
            		}
            		for(InputContext context : contexts){
            			if(context.getMouseButtonInputTypeFor(key) == InputType.STATE){
            				context.setStateState(context.getMouseButtonAction(key), true);
            				break;
            			}
            		}
            	}
            	else if(action == GLFW_RELEASE){
            		for(InputContext context : contexts){
            			if(context.getMouseButtonInputTypeFor(key) == InputType.STATE){
            				context.setStateState(context.getMouseButtonAction(key), false);
            				break;
            			}
            		}
            	}
            }
        });

	}
	
	public void update(){
		FloatBuffer axes = GLFW.glfwGetJoystickAxes(GLFW.GLFW_JOYSTICK_1);
		ByteBuffer buttons = GLFW.glfwGetJoystickButtons(GLFW.GLFW_JOYSTICK_1);
		for(int button = 0; button < buttons.capacity(); button++){
			if(buttons.get(button) == 1){
				if(!joystickIsPressed[button]){
					for(InputContext context : contexts){
            			if(context.getJoystickButtonInputTypeFor(button) == InputType.ACTION){
            				context.setActionPerformed(context.getJoystickButtonAction(button), true);
            				break;
            			}
            		}
            		joystickIsPressed[button] = true;
				}
				for(InputContext context : contexts){
        			if(context.getJoystickButtonInputTypeFor(button) == InputType.STATE){
        				context.setStateState(context.getJoystickButtonAction(button), true);
        				break;
        			}
        		}
			}
			else{
				for(InputContext context : contexts){
        			if(context.getJoystickButtonInputTypeFor(button) == InputType.STATE){
        				context.setStateState(context.getJoystickButtonAction(button), false);
        				break;
        			}
        		}
        		joystickIsPressed[button] = false;
			}
		}
		for(int axis = 0; axis < axes.capacity(); axis++){
			for(InputContext context : contexts){
    			if(context.getJoystickMovementInputTypeFor(Axis.getAxis(axis)) == InputType.RANGE){
    				float value = axes.get(axis);
    				if(Axis.getAxis(axis) == Axis.VERTICAL || Axis.getAxis(axis) == Axis.HORIZONTAL){
    					value = -value;
    				}
    				context.setRangeValue(context.getJoystickMovementAction(Axis.getAxis(axis)), value);
    				break;
    			}
    		}
		}
		return;
	}
	
	public synchronized void clear(){
		glfwSetCursorPos(Display.getWindow(), ORIGIN_X, ORIGIN_Y);
		mouseMovedDistance = new Vector2f(0, 0);
		scrollDistance = 0;
		for(InputContext context : contexts){
			if(context.getScrollInputTypeFor(Axis.VERTICAL) == InputType.RANGE){
				context.setRangeValue(context.getScrollAction(Axis.VERTICAL), 0);
			}
		}
		for(InputContext context : contexts){
			if(context.getMouseMovementInputTypeFor(Axis.HORIZONTAL) == InputType.RANGE){
				context.setRangeValue(context.getMouseMovementAction(Axis.HORIZONTAL), 0);
				break;
			}
		}
		for(InputContext context : contexts){
			if(context.getMouseMovementInputTypeFor(Axis.VERTICAL) == InputType.RANGE){
				context.setRangeValue(context.getMouseMovementAction(Axis.VERTICAL), 0);
				break;
			}
		}
		for(InputContext context : contexts){
			if(context.getJoystickMovementInputTypeFor(Axis.HORIZONTAL) == InputType.RANGE){
				context.setRangeValue(context.getMouseMovementAction(Axis.HORIZONTAL), 0);
				break;
			}
		}
		for(InputContext context : contexts){
			if(context.getJoystickMovementInputTypeFor(Axis.VERTICAL) == InputType.RANGE){
				context.setRangeValue(context.getMouseMovementAction(Axis.VERTICAL), 0);
				break;
			}
		}
		for(InputContext context : contexts){
			if(context.getJoystickMovementInputTypeFor(Axis.TWIST) == InputType.RANGE){
				context.setRangeValue(context.getMouseMovementAction(Axis.TWIST), 0);
				break;
			}
		}
		for(InputContext context : contexts){
			if(context.getJoystickMovementInputTypeFor(Axis.THROTTLE) == InputType.RANGE){
				context.setRangeValue(context.getMouseMovementAction(Axis.THROTTLE), 0);
				break;
			}
		}
		for(int i = 0; i < NUMBER_OF_KEYS; i++){
			for(InputContext context : contexts){
    			if(context.getKeyInputTypeFor(i) == InputType.ACTION){
    				context.setActionPerformed(context.getKeyAction(i), false);
    			}
    		}
		}
		for(int i = 0; i < NUMBER_OF_MOUSE_BUTTONS; i++){
			for(InputContext context : contexts){
    			if(context.getMouseButtonInputTypeFor(i) == InputType.ACTION){
    				context.setActionPerformed(context.getMouseButtonAction(i), false);
    			}
    		}
		}
		for(int i = 0; i < NUMBER_OF_JOYSTICK_BUTTONS; i++){
			for(InputContext context : contexts){
    			if(context.getJoystickButtonInputTypeFor(i) == InputType.ACTION){
    				context.setActionPerformed(context.getJoystickButtonAction(i), false);
    			}
    		}
		}
	}
	
	public void unregisterComponent(InputContext context){
		contexts.remove(context);
	}
	
	public static void registerComponent(InputContext context){
		contexts.add(context);
	}
	
	public static void registerComponent(InputContext context, int precedence){
		contexts.add(precedence, context);
	}
}
