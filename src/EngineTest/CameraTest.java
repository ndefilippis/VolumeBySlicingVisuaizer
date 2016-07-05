package EngineTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.lwjgl.glfw.GLFW;

import components.WeaponComponent;
import entities.Camera;
import entities.Entity;
import entities.FirstPersonCamera;
import entities.Light;
import entities.Player;
import entities.ReflectionCamera;
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
import terrains.Terrain;
import terrains.TerrainTexturePack;
import textures.Texture;
import util.MousePicker;
import util.Utils;
import vector.Vector3f;
import water.WaterFrameBuffers;
import water.WaterTile;

public class CameraTest {
	private static List<Entity> entities;
	private static List<Terrain> terrainList;
	private static List<GUITexture> guis;
	private static List<WaterTile> waters;
	private static List<Light> lights;
	private static Display display;
	private static Loader loader;


	public static void main(String[] args) {
		display = new Display();
		loader = new Loader();
		InputContext input = new InputContext();
		input.addKeyAction(GLFW.GLFW_KEY_F, "toggleFlashlight");
		input.addKeyAction(GLFW.GLFW_KEY_V, "toggleCamera");
		input.addMouseButtonState(GLFW.GLFW_MOUSE_BUTTON_1, "shoot");
		input.addMouseButtonState(GLFW.GLFW_MOUSE_BUTTON_2, "zoom");
		
		Texture backgroundTexture = new Texture(loader.loadTexture("grassy2"));
		Texture rTexture = new Texture(loader.loadTexture("mud"));
		Texture gTexture = new Texture(loader.loadTexture("path"));
		gTexture.setReflectivity(0.1f);
		gTexture.setShineDamper(0.3f);
		Texture bTexture = new Texture(loader.loadTexture("grassFlowers"));
		TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, bTexture, gTexture);
		Texture blendMap = new Texture(loader.loadTexture("blendMap"));

		TexturedModel treeModel = createTexturedModel("sword", "sword", 1, false, false);

		Terrain[] terrains = new Terrain[1];
		for (int i = 0; i < 1; i++) {
			for (int j = 0; j < 1; j++) {
				terrains[i * 1 + j] = new Terrain(i, j, loader,
						texturePack, blendMap);
			}
		}

		List<Entity> entities = new ArrayList<Entity>();
		Random random = new Random();
		for (int i = 0; i < 80; i++) {
			float x = (random.nextFloat()) * 800;
			float z = (random.nextFloat()) * 800;
			float y = Utils.getTerrainHeight(terrains, x, z);
			entities.add(new Entity(treeModel, new Vector3f(x, y, z), 0f, 0f,
					0f, 5f));
		}
		lights = new ArrayList<Light>();
		Light sun = new Light(new Vector3f(220, 200000, 220), new Vector3f(1f, 1f, 1f));
		lights.add(sun);

		InputHandler inputHandler = new InputHandler();
		TexturedModel planeModel = createTexturedModel("person", "playerTexture", 1, false, false);
		
		Player p = new Player(planeModel, 50, 50, terrains);
		guis = new ArrayList<GUITexture>();
		
		Light flashlight = new Light(p.getPosition(), new Vector3f(1, 1, 1), new Vector3f(1, 0.0002f, 0.0001f), new Vector3f(1, 0, 0), 30f);
		lights.add(flashlight);
		
		Camera firstPerson = new FirstPersonCamera(p, p.getPivot());
		Camera thirdPerson = new ThirdPersonCamera(p, p.getPivot(), terrains);
		Camera camera = firstPerson;
		
		MasterRenderer renderer = new MasterRenderer(loader, camera);
		MousePicker picker = new MousePicker(camera,
				renderer.getProjectionMatrix(), terrains);

		terrainList = Arrays.asList(terrains);

		WaterFrameBuffers fbos = new WaterFrameBuffers();
		waters = new ArrayList<WaterTile>();
		WaterTile water = new WaterTile(75+1600, -75+1600, 0);
		waters.add(water);
		

		Camera reflect = new ReflectionCamera(camera, water);
		TexturedModel rocket = createTexturedModel("sphere", "sun", 1, false, false);
			
		WeaponComponent s = new WeaponComponent(new Entity(rocket, new Vector3f(0, 0, 0), 0, 0, 0, 1f), 200f, 5f);
		boolean flashlightOn = true;
		while (display.shouldClose()) {
			Vector3f pos = p.getPosition();
			p.update();
			renderer.update();
			picker.update();
			ParticleMaster.update(camera);
			renderer.renderShadowMap(entities, sun);
			if(input.getState("shoot")){
				s.shoot(new Vector3f(pos.x, pos.y+2, pos.z), picker.getCurrentRay(), entities);
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
			s.update();
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
