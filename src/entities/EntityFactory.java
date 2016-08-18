package entities;

import org.lwjgl.glfw.GLFW;

import collision.CollisionModel;
import collision.SAPStructure;
import components.AIPlayerComponent;
import components.AirplaneInputComponent;
import components.CameraFocusComponent;
import components.CollisionComponent;
import components.Entity;
import components.EntityManager;
import components.LightComponent;
import components.MotionComponent;
import components.NormalMapComponent;
import components.PhysicsComponent;
import components.PlayerInputComponent;
import components.RenderComponent;
import components.ShootyMcTooty;
import input.Axis;
import input.InputContext;
import models.TexturedModel;
import util.Transform;
import vector.Quaternion;
import vector.Vector3f;

public class EntityFactory {
	private EntityManager manager;

	public EntityFactory(EntityManager manager){
		this.manager = manager;
	}
	public Entity createPlayerEntity(TexturedModel model, CollisionModel hitbox, Vector3f position, float scale, SAPStructure sap){
		Entity e = manager.createEntity();
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
		e.add(new RenderComponent(model));
		e.add(new Transform(position, new Quaternion(), scale));
		e.add(new CameraFocusComponent(new Vector3f(0, 5, 0)));
		e.add(new MotionComponent());
		e.add(new CollisionComponent(hitbox, false));
		e.add(new PhysicsComponent());
		e.add(new PlayerInputComponent(playerInput));
		sap.add(e);
		return e;
		
	}
	
	public Entity createAirplaneEntity(TexturedModel model, CollisionModel hitbox, Vector3f position, float scale, SAPStructure sap){
		Entity e = manager.createEntity();
		InputContext airplaneInput = new InputContext(0);
		airplaneInput.addKeyState(GLFW.GLFW_KEY_Q, "roll_left");
		airplaneInput.addKeyState(GLFW.GLFW_KEY_E, "roll_right");
		airplaneInput.addMouseRange(Axis.VERTICAL, "pitch");
		airplaneInput.addKeyAction(GLFW.GLFW_KEY_LEFT_SHIFT, "turbo");
		airplaneInput.addKeyAction(GLFW.GLFW_KEY_PERIOD, "speedUp");
		airplaneInput.addKeyAction(GLFW.GLFW_KEY_COMMA, "speedDown");
		airplaneInput.addMouseRange(Axis.HORIZONTAL, "yaw");
		//airplaneInput.addJoystickRange(Axis.TWIST, "yaw");
		//airplaneInput.addJoystickRange(Axis.THROTTLE, "speedScale");
		airplaneInput.addJoystickButtonAction(6, "barrelRoll");
		e.add(new RenderComponent(model));
		e.add(new Transform(position, new Quaternion(), scale));
		e.add(new CameraFocusComponent(new Vector3f(0f, 1f, -4f)));
		e.add(new MotionComponent());
		e.add(new AirplaneInputComponent(airplaneInput));
		e.add(new CollisionComponent(hitbox, false));
		sap.add(e);
		return e;
	}

	public Entity createAIPlayer(TexturedModel model, CollisionModel hitbox, Vector3f position, float scale, TexturedModel projectile, CollisionModel projectileHitbox, SAPStructure sap){
		Entity e = manager.createEntity();
		e.add(new RenderComponent(model));
		e.add(new Transform(position, new Quaternion(), scale));
		e.add(new MotionComponent());
		e.add(new CollisionComponent(hitbox, false));
		//e.add(new PhysicsComponent(motionComponent, collisionComponent));
		ShootyMcTooty s1 = new ShootyMcTooty(projectile, projectileHitbox, 100f, 0.05f);
		e.add(s1);
		e.add(new AIPlayerComponent(s1));
		sap.add(e);
		return e;
	}
	
	public Entity createDynamicModel(TexturedModel model, CollisionModel hitbox, Vector3f position, float rotX, float rotY, float rotZ, float scale, Transform parent, SAPStructure sap){
		Entity e = manager.createEntity();
		e.add(new RenderComponent(model));
		Transform transformComponent = new Transform(position, Quaternion.setFromEulerAngles(rotX, rotY, rotZ), scale);
		if(parent != null){
			transformComponent.setParent(parent);
		}
		e.add(transformComponent);
		e.add(new CollisionComponent(hitbox, false));
		e.add(new MotionComponent());	
		sap.add(e);
		return e;
	}
	
	public Entity createStaticModel(TexturedModel model, CollisionModel hitbox, Vector3f position, float rotX, float rotY, float rotZ, float scale, Transform parent, SAPStructure sap){
		Entity e = manager.createEntity();
		e.add(new RenderComponent(model));
		Transform transformComponent = new Transform(position, Quaternion.setFromEulerAngles(rotX, rotY, rotZ), scale);
		if(parent != null){
			transformComponent.setParent(parent);
		}
		e.add(transformComponent);
		e.add(new CollisionComponent(hitbox, true));	
		sap.add(e);
		return e;
	}
	
	public Entity createNormalMappedStaticModel(TexturedModel model, CollisionModel hitbox, Vector3f position, float rotX, float rotY, float rotZ, float scale, Transform parent, SAPStructure sap){
		Entity e = manager.createEntity();
		e.add(new RenderComponent(model));
		e.add(new NormalMapComponent());
		Transform transformComponent = new Transform(position, Quaternion.setFromEulerAngles(rotX, rotY, rotZ), scale);
		if(parent != null){
			transformComponent.setParent(parent);
		}
		e.add(transformComponent);
		e.add(new CollisionComponent(hitbox, true));	
		sap.add(e);
		return e;
	}
	
	public Entity createDecorationModel(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale, Transform parent){
		Entity e = manager.createEntity();
		e.add(new RenderComponent(model));
		Transform transformComponent = new Transform(position, Quaternion.setFromEulerAngles(rotX, rotY, rotZ), scale);
		if(parent != null){
			transformComponent.setParent(parent);
		}	
		e.add(transformComponent);
		return e;
	}
	
	public Entity createNormalMappedDecorationModel(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale, Transform parent){
		Entity e = manager.createEntity();
		e.add(new RenderComponent(model));
		e.add(new NormalMapComponent());
		Transform transformComponent = new Transform(position, Quaternion.setFromEulerAngles(rotX, rotY, rotZ), scale);
		if(parent != null){
			transformComponent.setParent(parent);
		}	
		e.add(transformComponent);
		return e;
	}
	
	public Entity createDecorationModel(TexturedModel model, int textureID, Vector3f position, float rotX, float rotY, float rotZ, float scale, Transform parent){
		Entity e = manager.createEntity();
		e.add(new RenderComponent(model, textureID));
		Transform transformComponent = new Transform(position, Quaternion.setFromEulerAngles(rotX, rotY, rotZ), scale);
		if(parent != null){
			transformComponent.setParent(parent);
		}	
		e.add(transformComponent);
	
		return e;
	}

	public Entity createLightEntity(TexturedModel model, CollisionModel hitbox, Vector3f position, float rotX, float rotY, float rotZ, float scale, float height, Vector3f lightColor, Vector3f lightAttenuation, Transform parent, SAPStructure sap){
		Entity e = manager.createEntity();
		e.add(new RenderComponent(model));
		Transform transformComponent = new Transform(position, Quaternion.setFromEulerAngles(rotX, rotY, rotZ), scale);
		if(parent != null){
			transformComponent.setParent(parent);
		}
		e.add(transformComponent);
		e.add(new CollisionComponent(hitbox, true));
		Light light = new Light(new Vector3f(0, height, 0), lightColor, lightAttenuation);
		e.add(new LightComponent(light));
		sap.add(e);
		return e;
	}
}
