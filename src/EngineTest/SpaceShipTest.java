package EngineTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.glfw.GLFW;

import collision.CollisionModel;
import collision.SAPStructure;
import components.AIPlayerController;
import components.AirplaneController;
import components.BasicEntityManager;
import components.CollisionSystem;
import components.Entity;
import components.EntityManager;
import components.MotionComponent;
import components.MotionSystem;
import components.RenderComponent;
import components.ShootyMcTooty;
import components.ShootySystem;
import entities.Camera;
import entities.EntityFactory;
import entities.FirstPersonCamera;
import entities.Light;
import entities.ThirdPersonCamera;
import guis.GUITexture;
import input.InputContext;
import input.InputHandler;
import models.Model;
import models.ModelData;
import models.TexturedModel;
import particles.ParticleMaster;
import renderEngine.Display;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import renderEngine.OBJFileLoader;
import textures.Texture;
import util.MousePicker;
import util.Transform;
import vector.Vector2f;
import vector.Vector3f;
import vector.Vector4f;

public class SpaceShipTest {
	private static List<GUITexture> guis = new ArrayList<GUITexture>();
	private static Display display;
	private static Loader loader;
	private static EntityManager manager;
	private static EntityFactory factory;
	private static AirplaneController airplaneController = new AirplaneController();
	private static AIPlayerController aiController = new AIPlayerController();
	private static SAPStructure sapStructure = new SAPStructure();
	private static CollisionSystem collisionSystem = new CollisionSystem(sapStructure);
	private static MotionSystem motionSystem = new MotionSystem();
	private static ShootySystem shootySystem = new ShootySystem();


	public static void main(String[] args) {
		display = new Display();
		loader = new Loader();
		manager = new BasicEntityManager();
		factory = new EntityFactory(manager);
		InputContext input = new InputContext();
		input.addKeyAction(GLFW.GLFW_KEY_V, "toggleCamera");
		input.addKeyAction(GLFW.GLFW_KEY_F3, "toggleDebug");
		input.addMouseButtonState(GLFW.GLFW_MOUSE_BUTTON_1, "shoot");
		input.addMouseButtonState(GLFW.GLFW_MOUSE_BUTTON_2, "zoom");
		input.addJoystickButtonState(0, "shoot");
		
		List<Light> lights = new ArrayList<Light>();
		Light sun = new Light(new Vector3f(220, 200000, 220), new Vector3f(1f, 1f, 1f));

		lights.add(sun);

		InputHandler inputHandler = new InputHandler();
		TexturedModel planeModel = createTexturedModel("f-16", "playerTexture", 1, false, false);
		CollisionModel planeHitbox = createCollisionModel("f-16");
		TexturedModel gunModel = createTexturedModel("sphere", "sun", 1, false, false);
		CollisionModel gunhit = createCollisionModel("sphere");
		Random random = new Random();
		
		
		Vector3f position = new Vector3f(0, 0, 0);
		Entity p = factory.createAirplaneEntity(planeModel, planeHitbox, position, 1f, sapStructure);	
		Transform player = p.as(Transform.class);
		List<GUITexture> guis = new ArrayList<GUITexture>();
		//Light flashlight = new Light(position, new Vector3f(1, 1, 1), new Vector3f(1, 0.0002f, 0.0001f), new Vector3f(1, 0, 0), 30f);
		//lights.add(flashlight);
		
		for(int i = 0; i < 1; i++){
			float x = position.x + 16 * random.nextFloat() - 8;
			float z = position.z + 16 * random.nextFloat() - 8;
			float y = position.y + 16 * random.nextFloat() - 8;
			Entity aiPlayer = factory.createAIPlayer(planeModel, planeHitbox, new Vector3f(x, y, z), 1f, gunModel, gunhit, sapStructure);
			//Transform t = aiPlayer.as(Transform.class);
		}
		TexturedModel planetModel = createTexturedModel("sphere", "earth", 1, false, false);
		CollisionModel planetHitbox = createCollisionModel("sphere");
		Entity planet = factory.createDynamicModel(planetModel, planetHitbox, new Vector3f(-500f, 0, 0), 0f, 0f, 0f, 250f, null, sapStructure);
		planet.as(MotionComponent.class).angularVelocity = new Vector3f(0, 0.5f, 0);
		
		Camera firstPerson = new FirstPersonCamera(p);
		Camera thirdPerson = new ThirdPersonCamera(p);
		Camera camera = firstPerson;
		
		MasterRenderer renderer = new MasterRenderer(loader, camera);
		renderer.setSkyboxBlend(false);
		MousePicker picker = new MousePicker(camera, renderer.getProjectionMatrix(), null);
		
		GUITexture shadowMap = new GUITexture(renderer.getShadowMapTexture(), new Vector2f(0.5f, 0.5f), new Vector2f(0.5f, 0.5f));
		//guis.add(shadowMap);

		TexturedModel rocket = createTexturedModel("laser", "red", 1, false, false);
		CollisionModel rocketHitbox = createCollisionModel("cube");
		
		TexturedModel trail = createTexturedModel("cube", "texture4", 1, false, false);
		CollisionModel trailHitbox = createCollisionModel("cube");
		
		Entity leftGun = factory.createDecorationModel(gunModel, new Vector3f(-2, -0.5f, 0), 0, 0, 0, 0.2f, player);
		Entity rightGun = factory.createDecorationModel(gunModel, new Vector3f(2, -0.5f, 0), 0, 0, 0, 0.2f, player);
		ShootyMcTooty shooty1 = new ShootyMcTooty(rocket, rocketHitbox, 400f, 4.5f);
		leftGun.add(shooty1);
		ShootyMcTooty shooty2 = new ShootyMcTooty(rocket, rocketHitbox, 400f, 5f);
		rightGun.add(shooty2);
		ShootyMcTooty shooty3 = new ShootyMcTooty(trail, trailHitbox, 0f, 20f);
		//p.add(shooty3);
		
		display.resetTime();
		while (display.shouldClose()) {
			Transform t = p.as(Transform.class);
			inputHandler.update();
			Vector3f pos = t.getPosition();
			renderer.update();
			picker.update();
			ParticleMaster.update(camera);
			renderer.renderShadowMap(manager.getAll(Transform.class, RenderComponent.class), sun);
			
			Vector3f direction = t.getOrientation().rotate(new Vector3f(0, 0, -1));
			
			airplaneController.update(manager);
			aiController.update(manager);
			shootySystem.update(manager);
			//System.out.println(pos);
			collisionSystem.update(manager);
			motionSystem.update(manager);
			
			if(input.getState("shoot")){
				Transform lT = leftGun.as(Transform.class);
				Transform rT = rightGun.as(Transform.class);
				shooty1.shoot(lT.getPosition(), direction, manager);
				shooty2.shoot(rT.getPosition(), direction, manager);
			}
			//shooty3.shoot(t.getPosition(), direction, entities);
			
			float originalFOV = MasterRenderer.FOV;
			float newFOV;
			if(input.getState("zoom")){
				newFOV = Math.max(originalFOV - 100*Display.getFrameTimeSeconds(), 1f);
			}
			else{
				newFOV = Math.min(originalFOV + 100*Display.getFrameTimeSeconds(), 70f);
			}
			if(originalFOV != newFOV){
				renderer.setFOV(newFOV);
			}
			if(input.actionPerformed("toggleDebug")){
				renderer.toggleDebug();
			}
			if(input.actionPerformed("toggleCamera")){
				if(camera == firstPerson){
					camera = thirdPerson;
				}
				else{
					camera = firstPerson;
				}
			}
			camera.update();
			inputHandler.clear();
			//flashlight.setPosition(new Vector3f(pos.x, pos.y+5, pos.z));
			for(Entity e : manager.getAll(Transform.class, RenderComponent.class)){
				renderer.processEntity(e);
			}
			for(GUITexture gui : guis){
				renderer.processGUI(gui);
			}
			renderer.render(lights, camera, new Vector4f(0, 0, 0, 0), sun);
			
			display.update();
		}
		ParticleMaster.cleanUp();
		renderer.cleanUp();
		display.cleanUp();
		loader.cleanUp();
	}
	
	private static TexturedModel createTexturedModel(String modelString, String textureString, int numberOfRows, boolean hasTransparency, boolean hasFakeLighting){
		ModelData modeldata = OBJFileLoader.loadOBJ(modelString);
		Model model = loader.loadToVAO(modeldata.getVertices(),
				modeldata.getTextureCoords(), modeldata.getNormals(),
				modeldata.getIndices());
		Texture texture = new Texture(loader.loadTexture(textureString));
		texture.setNumberOfRows(numberOfRows);
		if(hasTransparency){
			texture.setHasTransparency(true);
		}
		if(hasFakeLighting){
			texture.setUseFakeLighting(true);
		}
		return new TexturedModel(model, texture);
	}
	
	private static CollisionModel createCollisionModel(String modelString){
		ModelData modeldata = OBJFileLoader.loadOBJ(modelString);
		return new CollisionModel(modeldata);
	}
}
