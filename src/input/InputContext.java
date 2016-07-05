package input;

import java.util.HashMap;
import java.util.Map;

public class InputContext {
	private Map<Integer, String> keyEventMap = new HashMap<Integer, String>();
	private Map<Integer, String> mouseButtonEventMap = new HashMap<Integer, String>();
	private Map<Axis, String> mouseMovementEventMap = new HashMap<Axis, String>();
	private Map<Axis, String> scrollEventMap = new HashMap<Axis, String>();
	private Map<Integer, String> joystickButtonEventMap = new HashMap<Integer, String>();
	private Map<Axis, String> joystickMovementEventMap = new HashMap<Axis, String>();
	
	private Map<String, Boolean> actionMap = new HashMap<String, Boolean>();
	private Map<String, Boolean> stateMap = new HashMap<String, Boolean>();
	private Map<String, Float> rangeMap = new HashMap<String, Float>();
	
	public InputContext(){
		InputHandler.registerComponent(this);
	}
	
	public InputContext(int i) {
		InputHandler.registerComponent(this, i);
	}

	public String getKeyAction(int key){
		return keyEventMap.get(key);
	}
	
	public String getMouseButtonAction(int mouse){
		return mouseButtonEventMap.get(mouse);
	}
	
	public String getMouseMovementAction(Axis axis){
		return mouseMovementEventMap.get(axis);
	}
	
	public String getScrollAction(Axis axis){
		return scrollEventMap.get(axis);
	}
	
	public String getJoystickMovementAction(Axis axis){
		return joystickMovementEventMap.get(axis);
	}
	
	public String getJoystickButtonAction(int button){
		return joystickButtonEventMap.get(button);
	}
	
	public void setActionPerformed(String action, boolean state){
		actionMap.put(action, state);
	}
	
	public void setStateState(String action, boolean state){
		stateMap.put(action, state);
	}
	
	public void setRangeValue(String action, float value){
		rangeMap.put(action, value);
	}
	
	public void addKeyState(int key, String action){
		keyEventMap.put(key, action);
		stateMap.put(action, false);
	}
	
	public void addKeyAction(int key, String action){
		keyEventMap.put(key, action);
		actionMap.put(action, false);
	}
	
	public void addMouseButtonState(int key, String action){
		mouseButtonEventMap.put(key, action);
		stateMap.put(action, false);
	}
	
	public void addMouseButtonAction(int key, String action){
		mouseButtonEventMap.put(key, action);
		actionMap.put(action, false);
	}
	
	public void addMouseRange(Axis axis, String action){
		mouseMovementEventMap.put(axis, action);
		rangeMap.put(action, 0.0f);
	}
	
	public void addScrollRange(Axis axis, String action){
		scrollEventMap.put(axis, action);
		rangeMap.put(action, 0.0f);
	}
	
	public void addJoystickRange(Axis axis, String action){
		joystickMovementEventMap.put(axis, action);
		rangeMap.put(action, 0.0f);
	}
	public void addJoystickButtonState(int key, String action){
		joystickButtonEventMap.put(key, action);
		stateMap.put(action, false);
	}
	
	public void addJoystickButtonAction(int key, String action){
		joystickButtonEventMap.put(key, action);
		actionMap.put(action, false);
	}
	
	public boolean getState(String action){
		return stateMap.get(action);
	}
	
	public boolean actionPerformed(String action){
		return actionMap.get(action);
	}
	
	public float getRange(String action){
		return rangeMap.get(action);
	}

	public InputType getKeyInputTypeFor(int key){
		if(keyEventMap.containsKey(key)){
			return getInputTypeFor(keyEventMap.get(key));
		}
		return InputType.NONE;
	}
	
	public InputType getJoystickButtonInputTypeFor(int button){
		if(joystickButtonEventMap.containsKey(button)){
			return getInputTypeFor(joystickButtonEventMap.get(button));
		}
		return InputType.NONE;
	}
	
	public InputType getMouseButtonInputTypeFor(int mouse){
		if(mouseButtonEventMap.containsKey(mouse)){
			return getInputTypeFor(mouseButtonEventMap.get(mouse));
		}
		return InputType.NONE;
	}
	
	public InputType getMouseMovementInputTypeFor(Axis axis){
		if(mouseMovementEventMap.containsKey(axis)){
			return getInputTypeFor(mouseMovementEventMap.get(axis));
		}
		return InputType.NONE;
	}
	
	public InputType getScrollInputTypeFor(Axis axis){
		if(scrollEventMap.containsKey(axis)){
			return getInputTypeFor(scrollEventMap.get(axis));
		}
		return InputType.NONE;
	}
	
	public InputType getJoystickMovementInputTypeFor(Axis axis) {
		if(joystickMovementEventMap.containsKey(axis)){
			return getInputTypeFor(joystickMovementEventMap.get(axis));
		}
		return InputType.NONE;
	}
	
	public InputType getInputTypeFor(String action){
		if(actionMap.containsKey(action)) return InputType.ACTION;
		if(stateMap.containsKey(action)) return InputType.STATE;
		if(rangeMap.containsKey(action)) return InputType.RANGE;
		return InputType.NONE;
	}

	
}
