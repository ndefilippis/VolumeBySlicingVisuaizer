package EngineTest;

import java.util.ArrayList;
import java.util.Arrays;
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
import components.MotionSystem;
import components.NormalMapComponent;
import components.PhysicsSystem;
import components.PlayerController;
import components.RenderComponent;
import components.ShootyMcTooty;
import components.ShootySystem;
import entities.Camera;
import entities.EntityFactory;
import entities.FirstPersonCamera;
import entities.Light;
import entities.ReflectionCamera;
import entities.ThirdPersonCamera;
import guis.GUITexture;
import input.InputContext;
import input.InputHandler;
import models.Model;
import models.ModelData;
import models.TexturedModel;
import normalMappingObjConverter.NormalMappedObjLoader;
import particles.ParticleMaster;
import particles.ParticleTexture;
import renderEngine.Display;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import renderEngine.OBJFileLoader;
import terrains.Terrain;
import terrains.TerrainTexturePack;
import textures.Texture;
import util.MousePicker;
import util.Transform;
import util.Utils;
import vector.Vector2f;
import vector.Vector3f;
import water.WaterFrameBuffers;
import water.WaterTile;

public class LWJGLTest {
	private static Display display;
	private static Loader loader;
	private static EntityManager manager;
	private static EntityFactory factory;
	private static AirplaneController airplaneController = new AirplaneController();
	private static PlayerController playerController = new PlayerController();
	private static AIPlayerController aiController = new AIPlayerController();
	private static SAPStructure sapStructure = new SAPStructure();
	private static CollisionSystem collisionSystem = new CollisionSystem(sapStructure);
	private static MotionSystem motionSystem = new MotionSystem();
	private static ShootySystem shootySystem = new ShootySystem();
	private static PhysicsSystem physicsSystem = new PhysicsSystem();

	public static void main(String[] args) {
		display = new Display();
		loader = new Loader();
		manager = new BasicEntityManager();
		factory = new EntityFactory(manager);
		InputContext input = new InputContext();
		input.addKeyAction(GLFW.GLFW_KEY_F, "toggleFlashlight");
		input.addKeyAction(GLFW.GLFW_KEY_V, "toggleCamera");
		input.addKeyAction(GLFW.GLFW_KEY_F3, "toggleDebug");
		input.addMouseButtonState(GLFW.GLFW_MOUSE_BUTTON_1, "shoot");
		input.addMouseButtonState(GLFW.GLFW_MOUSE_BUTTON_2, "zoom");
		input.addJoystickButtonState(0, "shoot");
		
		Texture backgroundTexture = new Texture(loader.loadTexture("grassy2"));
		Texture rTexture = new Texture(loader.loadTexture("mud"));
		Texture gTexture = new Texture(loader.loadTexture("path"));
		gTexture.setReflectivity(0.1f);
		gTexture.setShineDamper(0.3f);
		Texture bTexture = new Texture(loader.loadTexture("grassFlowers"));
		TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, bTexture, gTexture);
		Texture blendMap = new Texture(loader.loadTexture("blendMap"));

		TexturedModel treeModel = createTexturedModel("tree", "tree", 1, false, false);
		CollisionModel treeHitBox = createCollisionModel("tree");
		TexturedModel fernModel = createTexturedModel("fern", "fern", 2, true, false);
		CollisionModel fernHitBox = createCollisionModel("fern");
		TexturedModel grassModel = createTexturedModel("grassModel", "grassTexture", 1, true, true);
		CollisionModel grassHitBox = createCollisionModel("grassModel");
		TexturedModel swordModel = createTexturedModel("sword", "sword", 1, false, false);
		CollisionModel swordHitBox = createCollisionModel("sword");
		TexturedModel barrelModel = createNormalTexturedModel("barrel", "barrel", "barrelNormal", 1, false, false);
		
		Terrain[] terrains = new Terrain[25];
		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 5; j++) {
				terrains[i * 5 + j] = new Terrain(i, j, loader,
						texturePack, blendMap);
			}
		}
		
		Random random = new Random();
		for (int i = 0; i < 80; i++) {
			float x = (random.nextFloat()) * 1600;
			float z = (random.nextFloat()) * 1600;
			float y = Utils.getTerrainHeight(terrains, x, z);
			factory.createStaticModel(treeModel, treeHitBox, new Vector3f(x, y, z), 0f, 0f, 0f, 5f, null, sapStructure);
		}
		for (int i = 0; i < 80; i++) {
			float x = (random.nextFloat()) * 1600;
			float z = (random.nextFloat()) * 1600;
			float y = Utils.getTerrainHeight(terrains, x, z);
			factory.createDecorationModel(fernModel, random.nextInt(4), new Vector3f(x, y, z), 0f, 0f, 0f, 0.5f, null);
		}
		for (int i = 0; i < 80; i++) {
			float x = (random.nextFloat()) * 1600;
			float z = (random.nextFloat()) * 1600;
			float y = Utils.getTerrainHeight(terrains, x, z);
			factory.createDecorationModel(grassModel, new Vector3f(x, y, z), 0f, 0f, 0f, 1f, null);
		}
		
		TexturedModel lampModel = createTexturedModel("lamp", "lamp", 1, false, true);
		CollisionModel lampHitBox = createCollisionModel("lamp");

		List<Light> lights = new ArrayList<Light>();
		for (int i = 0; i < 25; i++) {
			float x = 1600 + i * 100;// (random.nextFloat() - 0.5f) * 100;
			float z = 1600;/* (random.nextFloat() - 0.5f) * 100 */
			;
			float y = Utils.getTerrainHeight(terrains, x, z);

			float r = 2;
			float g = 2;
			float b = 2;
			Light light = new Light(new Vector3f(x, y+15, z), new Vector3f(r, g, b), new Vector3f(1, 0.01f, 0.002f));
			Entity lamp = factory.createStaticModel(lampModel, lampHitBox, new Vector3f(x, y, z), 0, 0, 0, 1, null, sapStructure);
			lights.add(light);
		}

		Light sun = new Light(new Vector3f(220, 200000, 220), new Vector3f(1f, 1f, 1f));

		lights.add(sun);

		InputHandler inputHandler = new InputHandler();
		TexturedModel planeModel = createTexturedModel("person", "playerTexture", 1, false, false);
		CollisionModel planeHitbox = createCollisionModel("person");
		TexturedModel gunModel = createTexturedModel("sphere", "sun", 1, false, false);
		List<Entity> moveable = new ArrayList<Entity>();
		for(int i = 0; i < 10; i++){
			float x = 1000*(random.nextFloat()) + 1600;
			float z = 1000*(random.nextFloat()) + 1600;
			float y = Utils.getTerrainHeight(terrains, x, z);
			//Entity aiPlayer = factory.createAIPlayer(planeModel, planeHitbox, new Vector3f(x, y, z), 1f, gunModel, planeHitbox, sapStructure);
			//Transform t = aiPlayer.as(Transform.class);
			//Entity sword = factory.createDecorationModel(swordModel, new Vector3f(2.0f, 3, 0), 0f, 180f, 0f, 0.2f, t);
		}
		
		Vector3f position = new Vector3f(153+1600, Utils.getTerrainHeight(terrains, 153+1600, -274+1600), -274+1600);
		factory.createNormalMappedDecorationModel(barrelModel, position, 0, 0, 0, 1, null);
		Entity p = factory.createPlayerEntity(planeModel, planeHitbox, position, 1f, sapStructure);	
		Transform player = p.as(Transform.class);
		Entity leftGun = factory.createDecorationModel(gunModel, new Vector3f(-2, 0, 0), 0, 0, 0, 0.2f, player);
		Entity rightGun = factory.createDecorationModel(gunModel, new Vector3f(2, 0, 0), 0, 0, 0, 0.2f, player);
		List<GUITexture> guis = new ArrayList<GUITexture>();
		Light flashlight = new Light(position, new Vector3f(1, 1, 1), new Vector3f(1, 0.0002f, 0.0001f), new Vector3f(1, 0, 0), 30f);
		lights.add(flashlight);
		
		
		Camera firstPerson = new FirstPersonCamera(p);
		Camera thirdPerson = new ThirdPersonCamera(p, terrains);
		Camera camera = firstPerson;
		RenderComponent playerRenderComponent = p.as(RenderComponent.class);
		p.remove(playerRenderComponent);
		
		MasterRenderer renderer = new MasterRenderer(loader, camera);
		MousePicker picker = new MousePicker(camera, renderer.getProjectionMatrix(), terrains);

		List<Terrain> terrainList = Arrays.asList(terrains);
		
		GUITexture shadowMap = new GUITexture(renderer.getShadowMapTexture(), new Vector2f(0.5f, 0.5f), new Vector2f(0.5f, 0.5f));
		//guis.add(shadowMap);
		
		WaterFrameBuffers fbos = new WaterFrameBuffers();
		List<WaterTile> waters = new ArrayList<WaterTile>();
		WaterTile water = new WaterTile(75+1600, -75+1600, 0);
		waters.add(water);
		

		Camera reflect = new ReflectionCamera(camera, water);
		TexturedModel rocket = createTexturedModel("sphere", "sun", 1, false, false);
		CollisionModel rocketHitbox = createCollisionModel("sphere");
			
		ShootyMcTooty shooty1 = new ShootyMcTooty(rocket, rocketHitbox, 200f, 4.5f);
		ShootyMcTooty shooty2 = new ShootyMcTooty(rocket, rocketHitbox, 200f, 5f);
		leftGun.add(shooty1);
		rightGun.add(shooty2);
		boolean flashlightOn = true;
		
		ParticleTexture particleTexture = new ParticleTexture(loader.loadTexture("fire"), 8, false);
		
		//ParticleSystem system = new ParticleSystem(particleTexture, 5, 4, 0.0f, 7, 15);
		//system.setDirection(new Vector3f(0, 1, 0), 0.1f);
		display.resetTime();
		while (display.shouldClose()) {
			Transform t = p.as(Transform.class);
			inputHandler.update();
			Vector3f pos = t.getPosition();
			renderer.update();
			picker.update();
			ParticleMaster.update(camera);
			renderer.renderShadowMap(manager.getAll(Transform.class, RenderComponent.class), sun);
			
			//system.generateParticles(startPosition);
			if(input.getState("shoot")){
				Transform lT = leftGun.as(Transform.class);
				Transform rT = rightGun.as(Transform.class);
				shooty1.shoot(lT.getPosition(), picker.getCurrentRay(), manager);
				shooty2.shoot(rT.getPosition(), picker.getCurrentRay(), manager);
			}
			airplaneController.update(manager);
			playerController.update(manager);
			physicsSystem.update(manager);
			aiController.update(manager);
			shootySystem.update(manager);
			collisionSystem.update(manager);
			motionSystem.update(manager);
			
			
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
			if(input.actionPerformed("toggleFlashlight")){
				if(flashlightOn){
					lights.remove(flashlight);
				}
				else{
					lights.add(flashlight);
				}
				flashlightOn = !flashlightOn;
			}
			if(input.actionPerformed("toggleDebug")){
				renderer.toggleDebug();
			}
			if(input.actionPerformed("toggleCamera")){
				if(camera == firstPerson){
					camera = thirdPerson;
					p.add(playerRenderComponent);
				}
				else{
					camera = firstPerson;
					p.remove(playerRenderComponent);
				}
				
				reflect = new ReflectionCamera(camera, water);
			}
			camera.update();
			inputHandler.clear();
			flashlight.setPosition(new Vector3f(pos.x, pos.y+5, pos.z));
			Vector3f direction = camera.getOrientation().negate(null).rotate(new Vector3f(0, 0, -1));
			flashlight.setConeDirection(direction);
			renderer.renderScene(manager.getAll(Transform.class, RenderComponent.class), terrainList, lights, guis, waters, camera, reflect, sun);
			
			display.update();
		}
		ParticleMaster.cleanUp();
		renderer.cleanUp();
		display.cleanUp();
		loader.cleanUp();
	}
	
	private static TexturedModel createNormalTexturedModel(String modelString, String textureString, String normalMapString, int numberOfRows, boolean hasTransparency, boolean hasFakeLighting){
		Model model = NormalMappedObjLoader.loadOBJ(modelString, loader);
		Texture texture = new Texture(loader.loadTexture(textureString));
		texture.setNormalMap(loader.loadTexture(normalMapString));
		texture.setNumberOfRows(numberOfRows);
		if(hasTransparency){
			texture.setHasTransparency(true);
		}
		if(hasFakeLighting){
			texture.setUseFakeLighting(true);
		}
		return new TexturedModel(model, texture);
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
