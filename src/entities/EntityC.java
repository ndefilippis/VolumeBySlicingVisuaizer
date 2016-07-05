package entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.lwjgl.glfw.GLFW;

import collision.CollisionModel;
import components.AIPlayerControllerComponent;
import components.AirplaneInputComponent;
import components.CameraFocusComponent;
import components.CollisionComponent;
import components.Component;
import components.ComponentType;
import components.MotionComponent;
import components.PhysicsComponent;
import components.PlayerInputComponent;
import components.RenderComponent;
import components.TransformComponent;
import input.Axis;
import input.InputContext;
import models.TexturedModel;
import vector.Quaternion;
import vector.Vector3f;

public class EntityC {
	private static long nextAvailableID = 0;
	public final long entityID;
	private List<Component> componentList;
	
	public EntityC(List<Component> componentList){
		this.componentList = componentList;
		this.entityID = nextAvailableID++;
	}

	public <T extends Component> T getComponent(ComponentType type){
		for(Component c : componentList){
			if(c.getType() == type){
				return (T)c;
			}
		}
		throw new IllegalArgumentException("Type " + type + " not found");
	}

	public void update(){
		for(Component c : componentList){
			c.update();
		}
	}
	
	public static EntityC createPlayerEntity(TexturedModel model, CollisionModel hitbox, Vector3f position, float scale){
		InputContext playerInput = new InputContext(0);
		playerInput.addKeyState(GLFW.GLFW_KEY_Q, "roll_left");
		playerInput.addKeyState(GLFW.GLFW_KEY_E, "roll_right");
		playerInput.addMouseRange(Axis.HORIZONTAL, "yaw");
		playerInput.addMouseRange(Axis.VERTICAL, "pitch");
		playerInput.addKeyState(GLFW.GLFW_KEY_SPACE, "jumping");
		playerInput.addKeyState(GLFW.GLFW_KEY_LEFT_CONTROL, "croutch");
		playerInput.addKeyState(GLFW.GLFW_KEY_W, "forward");
		playerInput.addKeyState(GLFW.GLFW_KEY_A, "left");
		playerInput.addKeyState(GLFW.GLFW_KEY_D, "right");
		playerInput.addKeyState(GLFW.GLFW_KEY_S, "back");
		playerInput.addKeyState(GLFW.GLFW_KEY_LEFT_SHIFT, "sprint");
		List<Component> componentList = new ArrayList<Component>();
		RenderComponent shapeComponent = new RenderComponent(model);
		TransformComponent transformComponent = new TransformComponent(position, new Quaternion(), scale);
		CameraFocusComponent cameraFocusComponent = new CameraFocusComponent(5f, transformComponent);
		MotionComponent motionComponent = new MotionComponent(transformComponent);
		CollisionComponent collisionComponent = new CollisionComponent(motionComponent, transformComponent, hitbox);
		PhysicsComponent physicsComponent = new PhysicsComponent(motionComponent, collisionComponent);
		PlayerInputComponent playerInputComponent = new PlayerInputComponent(playerInput, transformComponent, motionComponent, cameraFocusComponent, physicsComponent);
		
		
		componentList.add(playerInputComponent);
		componentList.add(physicsComponent);
		componentList.add(collisionComponent);
		componentList.add(motionComponent);
		componentList.add(shapeComponent);
		componentList.add(transformComponent);
		componentList.add(cameraFocusComponent);
		return new EntityC(componentList);
		
	}
	
	public static EntityC createAirplaneEntity(TexturedModel model, CollisionModel hitbox, Vector3f position, float scale){
		InputContext airplaneInput = new InputContext();
		airplaneInput.addJoystickRange(Axis.VERTICAL, "pitch");
		airplaneInput.addJoystickRange(Axis.TWIST, "yaw");
		airplaneInput.addJoystickRange(Axis.HORIZONTAL, "roll");
		airplaneInput.addJoystickRange(Axis.THROTTLE, "speedScale");
		List<Component> componentList = new ArrayList<Component>();
		RenderComponent shapeComponent = new RenderComponent(model);
		TransformComponent transformComponent = new TransformComponent(position, new Quaternion(), scale);
		CameraFocusComponent cameraFocusComponent = new CameraFocusComponent(5f, transformComponent);
		MotionComponent motionComponent = new MotionComponent(transformComponent);
		AirplaneInputComponent airplaneInputComponent = new AirplaneInputComponent(airplaneInput, transformComponent, motionComponent, cameraFocusComponent);
		CollisionComponent collisionComponent = new CollisionComponent(motionComponent, transformComponent, hitbox);
		PhysicsComponent physicsComponent = new PhysicsComponent(motionComponent, collisionComponent);
		
		componentList.add(airplaneInputComponent);
		componentList.add(physicsComponent);
		componentList.add(collisionComponent);
		componentList.add(motionComponent);
		componentList.add(cameraFocusComponent);
		componentList.add(shapeComponent);
		componentList.add(transformComponent);
		return new EntityC(componentList);
	}

	public static EntityC createAIPlayer(TexturedModel model, CollisionModel hitbox, Vector3f position, float scale){
		List<Component> componentList = new ArrayList<Component>();
		RenderComponent shapeComponent = new RenderComponent(model);
		TransformComponent transformComponent = new TransformComponent(position, new Quaternion(), scale);
		MotionComponent motionComponent = new MotionComponent(transformComponent);
		CollisionComponent collisionComponent = new CollisionComponent(motionComponent, transformComponent, hitbox);
		PhysicsComponent physicsComponent = new PhysicsComponent(motionComponent, collisionComponent);
		AIPlayerControllerComponent aiComponent = new AIPlayerControllerComponent(transformComponent, motionComponent, physicsComponent);
		
	
		componentList.add(physicsComponent);
		componentList.add(collisionComponent);
		componentList.add(motionComponent);
		componentList.add(shapeComponent);
		componentList.add(transformComponent);
		componentList.add(aiComponent);
		return new EntityC(componentList);
	}
	
	public static EntityC createStaticModel(TexturedModel model, CollisionModel hitbox, Vector3f position, float rotX, float rotY, float rotZ, float scale, TransformComponent parent){
		List<Component> componentList = new ArrayList<Component>();
		RenderComponent shapeComponent = new RenderComponent(model);
		TransformComponent transformComponent = new TransformComponent(position, Quaternion.setFromEulerAngles(rotX, rotY, rotZ), scale);
		if(parent != null){
			transformComponent.setAsChildOf(parent);
		}
		CollisionComponent collisionComponent = new CollisionComponent(transformComponent, hitbox);	
	
	
		componentList.add(collisionComponent);
		componentList.add(shapeComponent);
		componentList.add(transformComponent);
		return new EntityC(componentList);
	}

	public static EntityC createLightEntity(TexturedModel model, CollisionModel hitbox, Vector3f position, float rotX, float rotY, float rotZ, float scale, float height, Vector3f lightColor, Vector3f lightAttenuation, TransformComponent parent){
		List<Component> componentList = new ArrayList<Component>();
		RenderComponent shapeComponent = new RenderComponent(model);
		TransformComponent transformComponent = new TransformComponent(position, Quaternion.setFromEulerAngles(rotX, rotY, rotZ), scale);
		if(parent != null){
			transformComponent.setAsChildOf(parent);
		}
		CollisionComponent collisionComponent = new CollisionComponent(transformComponent, hitbox);
		LightComponent lightComponent = new LightComponent(transformComponent)
	
	
		componentList.add(collisionComponent);
		componentList.add(shapeComponent);
		componentList.add(transformComponent);
		return new EntityC(componentList);
	}
}
