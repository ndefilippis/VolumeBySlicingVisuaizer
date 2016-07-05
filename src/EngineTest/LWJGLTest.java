package EngineTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.lwjgl.glfw.GLFW;

import collision.Broadphase;
import components.WeaponComponent;
import entities.AIPlayer;
import entities.Airplane;
import entities.Camera;
import entities.Entity;
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
import particles.ParticleMaster;
import particles.ParticleSystem;
import particles.ParticleTexture;
import renderEngine.Display;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import renderEngine.OBJFileLoader;
import terrains.Terrain;
import terrains.TerrainTexturePack;
import textures.Texture;
import util.MousePicker;
import util.Utils;
import vector.Vector2f;
import vector.Vector3f;
import water.WaterFrameBuffers;
import water.WaterTile;

public class LWJGLTest {
	private static List<Entity> entities;
	private static List<Terrain> terrains;
	private static List<GUITexture> guis;
	private static List<WaterTile> waters;
	private static Display display;
	private static Loader loader;


	public static void main(String[] args) {
		display = new Display();
		loader = new Loader();
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
		TexturedModel fernModel = createTexturedModel("fern", "fern", 2, true, false);
		TexturedModel grassModel = createTexturedModel("grassModel", "grassTexture", 1, true, true);
		TexturedModel swordModel = createTexturedModel("sword", "sword", 1, false, false);
		
		Terrain[] terrains = new Terrain[25];
		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 5; j++) {
				terrains[i * 5 + j] = new Terrain(i, j, loader,
						texturePack, blendMap);
			}
		}

		List<Entity> entities = new ArrayList<Entity>();
		Random random = new Random();
		for (int i = 0; i < 800; i++) {
			float x = (random.nextFloat()) * 1600;
			float z = (random.nextFloat()) * 1600;
			float y = Utils.getTerrainHeight(terrains, x, z);
			entities.add(new Entity(treeModel, new Vector3f(x, y, z), 0f, 0f,
					0f, 5f));
		}
		for (int i = 0; i < 800; i++) {
			float x = (random.nextFloat()) * 1600;
			float z = (random.nextFloat()) * 1600;
			float y = Utils.getTerrainHeight(terrains, x, z);
			entities.add(new Entity(fernModel, random.nextInt(4), new Vector3f(x, y, z), 0f, 0f,
					0f, 0.5f));
		}
		for (int i = 0; i < 80; i++) {
			float x = (random.nextFloat()) * 1600;
			float z = (random.nextFloat()) * 1600;
			float y = Utils.getTerrainHeight(terrains, x, z);
			entities.add(new Entity(grassModel, new Vector3f(x, y, z), 0f, 0f,
					0f, 1f));
		}
		TexturedModel lampModel = createTexturedModel("lamp", "lamp", 1, false, true);

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
			Entity lamp = new Entity(lampModel, new Vector3f(x, y, z), 0, 0, 0, 1, 14);
			entities.add(lamp);
			lights.add(light);
		}

		Light sun = new Light(new Vector3f(220, 200000, 220), new Vector3f(1f, 1f, 1f));

		lights.add(sun);

		InputHandler inputHandler = new InputHandler();
		TexturedModel planeModel = createTexturedModel("f-16", "playerTexture", 1, false, false);
		TexturedModel gunModel = createTexturedModel("sphere", "sun", 1, false, false);
		List<Entity> moveable = new ArrayList<Entity>();
		for(int i = 0; i < 90; i++){
			float x = (random.nextFloat()) + 1600;
			float z = (random.nextFloat()) + 1600;
			float y = Utils.getTerrainHeight(terrains, x, z);
			Entity aiPlayer = new AIPlayer(planeModel, new Vector3f(x, y, z), 0f, 0f, 0f, 1f, terrains);
			entities.add(aiPlayer);
			moveable.add(aiPlayer);
			Entity sword = new Entity(swordModel, new Vector3f(2.0f, 3, 0), 0f, 180f, 0f, 0.2f);
			sword.getTransform().setParent(aiPlayer.getTransform());
			entities.add(sword);
		}
		
		Airplane p = new Airplane(planeModel, 153+1600, -274+1600);	
		Entity leftGun = new Entity(gunModel, new Vector3f(-2, 0, 0), 0, 0, 0, 0.2f);
		Entity rightGun = new Entity(gunModel, new Vector3f(2, 0, 0), 0, 0, 0, 0.2f);	
		leftGun.getTransform().setParent(p.getTransform());
		rightGun.getTransform().setParent(p.getTransform());
		List<GUITexture> guis = new ArrayList<GUITexture>();
		Light flashlight = new Light(p.getPosition(), new Vector3f(1, 1, 1), new Vector3f(1, 0.0002f, 0.0001f), new Vector3f(1, 0, 0), 30f);
		lights.add(flashlight);
		
		
		Camera firstPerson = new FirstPersonCamera(p, p.getPivot());
		Camera thirdPerson = new ThirdPersonCamera(p, p.getPivot(), terrains);
		Camera camera = firstPerson;
		
		MasterRenderer renderer = new MasterRenderer(loader, camera);
		MousePicker picker = new MousePicker(camera,
				renderer.getProjectionMatrix(), terrains);

		List<Terrain> terrainList = Arrays.asList(terrains);
		
		GUITexture shadowMap = new GUITexture(renderer.getShadowMapTexture(), new Vector2f(0.5f, 0.5f), new Vector2f(0.5f, 0.5f));
		//guis.add(shadowMap);
		
		WaterFrameBuffers fbos = new WaterFrameBuffers();
		List<WaterTile> waters = new ArrayList<WaterTile>();
		WaterTile water = new WaterTile(75+1600, -75+1600, 0);
		waters.add(water);
		

		Camera reflect = new ReflectionCamera(camera, water);
		TexturedModel rocket = createTexturedModel("sphere", "sun", 1, false, false);
			
		WeaponComponent shooty1 = new WeaponComponent(new Entity(rocket, new Vector3f(0, 0, 0), 0, 0, 0, 1f), 200f, 5f);
		WeaponComponent shooty2 = new WeaponComponent(new Entity(rocket, new Vector3f(0, 0, 0), 0, 0, 0, 1f), 200f, 5f);
		boolean flashlightOn = true;
		
		ParticleTexture particleTexture = new ParticleTexture(loader.loadTexture("fire"), 8, false);
		
		//ParticleSystem system = new ParticleSystem(particleTexture, 5, 4, 0.0f, 7, 15);
		//system.setDirection(new Vector3f(0, 1, 0), 0.1f);
		Vector3f startPosition = new Vector3f(p.getPosition());
		while (display.shouldClose()) {
			inputHandler.update();
			Broadphase.getMightBeCollidingUsingSAP(moveable, entities);
			Vector3f pos = p.getPosition();
			p.update(terrains);
			renderer.update();
			picker.update();
			ParticleMaster.update(camera);
			renderer.renderShadowMap(entities, sun);
			//system.generateParticles(startPosition);
			if(input.getState("shoot")){
				shooty1.shoot(leftGun.getPosition(), picker.getCurrentRay(), entities);
				shooty2.shoot(rightGun.getPosition(), picker.getCurrentRay(), entities);
			}
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
				entities.remove(p);
				if(camera == firstPerson){
					camera = thirdPerson;
					entities.add(p);
				}
				else{
					camera = firstPerson;
					entities.remove(p);
				}
				
				reflect = new ReflectionCamera(camera, water);
			}
			shooty1.update();
			shooty2.update();
			for(Entity e : entities){
				//e.update();
			}
			camera.update();
			inputHandler.clear();
			flashlight.setPosition(new Vector3f(pos.x, pos.y+5, pos.z));
			Vector3f direction = camera.getOrientation().negate(null).rotate(new Vector3f(0, 0, -1));
			flashlight.setConeDirection(direction);
			renderer.renderScene(entities, terrainList, lights, guis, waters, camera, reflect, sun);
			
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
}
