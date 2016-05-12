package input;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetScrollCallback;
import static org.lwjgl.glfw.GLFW.glfwSetMouseButtonCallback;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPos;

import java.awt.AWTException;
import java.awt.Robot;

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
	public static int scrollDistance = 0;
	private static boolean[] keyIsPressed = new boolean[350];
	private static boolean[] keyWasPressed = new boolean[350];
	private static boolean[] keyWasReleased = new boolean[350];
	private static boolean[] mouseIsPressed = new boolean[7];
	private static boolean[] mouseWasReleased = new boolean[7];
	private static boolean[] mouseWasPressed = new boolean[7];
	private static Vector2f lastLocation = new Vector2f(0, 0);
	private static Vector2f movedDistance = new Vector2f(0, 0);
	private static Vector2f draggedDistance = new Vector2f(0,0);
	private final int ORIGIN_X, ORIGIN_Y;
	
	//private Robot robot;
	boolean clear;
	
	public InputHandler(){
		ORIGIN_X = (int)Display.getWidth()/2;
		ORIGIN_Y = (int)Display.getHeight()/2;
		glfwSetKeyCallback(renderEngine.Display.getWindow(), keyCallback = new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {
            	if(action == GLFW_PRESS){
            		keyIsPressed[key] = true;
            		keyWasPressed[key] = true;
            	}
            	else if(action == GLFW_RELEASE){
            		keyWasReleased[key] = true;
            		keyIsPressed[key] = false;
            	}
            }
        });
		glfwSetCursorPos(Display.getWindow(), ORIGIN_X, ORIGIN_Y);
        glfwSetCursorPosCallback(renderEngine.Display.getWindow(), cursorPosCallback = new GLFWCursorPosCallback(){
        	public void invoke(long window, double xpos, double ypos){
        		movedDistance.x += (float)(xpos - ORIGIN_X);
        		movedDistance.y += (float)(ypos - ORIGIN_Y);
        		lastLocation.x = (float)xpos;
        		lastLocation.y = (float)ypos;
        	}
        });
        
        glfwSetScrollCallback(renderEngine.Display.getWindow(), scrollCallback = new GLFWScrollCallback(){

			@Override
			public void invoke(long window, double xoff, double yoff) {
				scrollDistance += yoff;
			}
        	
        });
        glfwSetMouseButtonCallback(Display.getWindow(), mouseButtonCallback = new GLFWMouseButtonCallback(){

			@Override
            public void invoke(long window, int key, int action, int mods) {
            	if(action == GLFW_PRESS){
            		mouseIsPressed[key] = true;
            		mouseWasPressed[key] = true;
            	}
            	else if(action == GLFW_RELEASE){
            		mouseWasReleased[key] = true;
            		mouseIsPressed[key] = false;
            	}
            }
        });

	}

	public static boolean isKeyPressed(int key){
		return keyIsPressed[key];
	}
	public static Vector2f getMouseMoveDistance(){
		return new Vector2f(movedDistance);
	}
	
	public synchronized void clear(){
		glfwSetCursorPos(Display.getWindow(), ORIGIN_X, ORIGIN_Y);
		movedDistance = new Vector2f(0, 0);
		scrollDistance = 0;
		for(int i = 0; i < keyWasReleased.length; i++){
			keyWasReleased[i] = false;
		}
		for(int i = 0; i < mouseWasReleased.length; i++){
			mouseWasReleased[i] = false;
		}
		for(int i = 0; i < keyWasPressed.length; i++){
			keyWasPressed[i] = false;
		}
		for(int i = 0; i < mouseWasPressed.length; i++){
			mouseWasPressed[i] = false;
		}
	}

	public static boolean wasKeyPressed(int glfwKey) {
		return keyWasPressed[glfwKey];
	}

	public static boolean wasMousePressed(int glfwMouse) {
		return mouseWasPressed[glfwMouse];
	}

	public static boolean isMousePressed(int glfwMouseButton) {
		return mouseIsPressed[glfwMouseButton];
	}
}
