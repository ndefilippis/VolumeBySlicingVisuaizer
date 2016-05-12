package EngineTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import entities.Camera;
import entities.Entity;
import entities.FirstPersonCamera;
import entities.Light;
import entities.LightEntity;
import entities.Player;
import entities.ShootyMcTooty;
import input.CameraPivot;
import input.InputHandler;
import models.Model;
import models.ModelData;
import models.TexturedModel;
import renderEngine.Display;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import renderEngine.OBJFileLoader;
import terrain.Terrain;
import terrain.TerrainTexture;
import terrain.TerrainTexturePack;
import textures.ModelTexture;
import util.MousePicker;
import util.Utils;
import vector.Vector3f;
import vector.Vector4f;
import water.WaterFrameBuffers;
import water.WaterRenderer;
import water.WaterShader;
import water.WaterTile;

public class LWJGLTest {

	public static void main(String[] args) {
		Display display = new Display();
		Loader loader = new Loader();

		TerrainTexture backgroundTexture = new TerrainTexture(
				loader.loadTexture("grassy2"));
		TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("mud"));
		TerrainTexture gTexture = new TerrainTexture(
				loader.loadTexture("grassFlowers"));
		TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("path"));

		TerrainTexturePack texturePack = new TerrainTexturePack(
				backgroundTexture, rTexture, bTexture, gTexture);

		TerrainTexture blendMap = new TerrainTexture(
				loader.loadTexture("blendMap"));

		ModelData modeldata = OBJFileLoader.loadOBJ("tree");
		Model model = loader.loadToVAO(modeldata.getVertices(),
				modeldata.getTextureCoords(), modeldata.getNormals(),
				modeldata.getIndices());
		ModelTexture treeTexture = new ModelTexture(loader.loadTexture("tree"));
		TexturedModel treeModel = new TexturedModel(model, treeTexture);

		modeldata = OBJFileLoader.loadOBJ("fern");
		model = loader.loadToVAO(modeldata.getVertices(),
				modeldata.getTextureCoords(), modeldata.getNormals(),
				modeldata.getIndices());
		ModelTexture fernTexture = new ModelTexture(loader.loadTexture("fern"));
		fernTexture.setHasTransparency(true);
		TexturedModel fernModel = new TexturedModel(model, fernTexture);

		modeldata = OBJFileLoader.loadOBJ("grassModel");
		model = loader.loadToVAO(modeldata.getVertices(),
				modeldata.getTextureCoords(), modeldata.getNormals(),
				modeldata.getIndices());
		ModelTexture grassTexture = new ModelTexture(
				loader.loadTexture("grassTexture"));
		grassTexture.setHasTransparency(true);
		grassTexture.setUseFakeLighting(true);
		TexturedModel grassModel = new TexturedModel(model, grassTexture);

		Terrain[] terrains = new Terrain[25];
		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 5; j++) {
				terrains[i * 5 + j] = new Terrain(i - 2, j - 2, loader,
						texturePack, blendMap, "heightmap");
			}
		}

		List<Entity> entities = new ArrayList<Entity>();
		Random random = new Random();
		for (int i = 0; i < 800; i++) {
			float x = (random.nextFloat() - 0.5f) * 6400;
			float z = (random.nextFloat() - 0.5f) * 6400;
			float y = Utils.getTerrainHeight(terrains, x, z);
			entities.add(new Entity(treeModel, new Vector3f(x, y, z), 0f, 0f,
					0f, 5f));
		}
		for (int i = 0; i < 80; i++) {
			float x = (random.nextFloat() - 0.5f) * 6400;
			float z = (random.nextFloat() - 0.5f) * 6400;
			float y = Utils.getTerrainHeight(terrains, x, z);
			entities.add(new Entity(fernModel, new Vector3f(x, y, z), 0f, 0f,
					0f, 0.5f));
		}
		for (int i = 0; i < 80; i++) {
			float x = (random.nextFloat() - 0.5f) * 6400;
			float z = (random.nextFloat() - 0.5f) * 6400;
			float y = Utils.getTerrainHeight(terrains, x, z);
			entities.add(new Entity(grassModel, new Vector3f(x, y, z), 0f, 0f,
					0f, 1f));
		}
		modeldata = OBJFileLoader.loadOBJ("lamp");
		model = loader.loadToVAO(modeldata.getVertices(),
				modeldata.getTextureCoords(), modeldata.getNormals(),
				modeldata.getIndices());
		ModelTexture lampTexture = new ModelTexture(loader.loadTexture("lamp"));
		lampTexture.setUseFakeLighting(true);
		TexturedModel lampModel = new TexturedModel(model, lampTexture);

		List<Light> lights = new ArrayList<Light>();
		for (int i = 0; i < 25; i++) {
			float x = i * 100;// (random.nextFloat() - 0.5f) * 100;
			float z = 0;/* (random.nextFloat() - 0.5f) * 100 */
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

		Light sun = new Light(new Vector3f(2000, 20000, 200), new Vector3f(1f, 1f, 1f));

		lights.add(sun);

		InputHandler inputHandler = new InputHandler();
		modeldata = OBJFileLoader.loadOBJ("person");
		model = loader.loadToVAO(modeldata.getVertices(),
				modeldata.getTextureCoords(), modeldata.getNormals(),
				modeldata.getIndices());
		ModelTexture planeTexture = new ModelTexture(
				loader.loadTexture("playerTexture"));
		TexturedModel planeModel = new TexturedModel(model, planeTexture);

		Player p = new Player(planeModel, 153, -274);
		//entities.add(p);
		
		Light flashlight = new Light(p.getPosition(), new Vector3f(10, 10, 10), new Vector3f(1, 0.002f, 0.01f), new Vector3f(1, 0, 0), 30f);
		lights.add(flashlight);
		
		MasterRenderer renderer = new MasterRenderer(loader);
		Camera camera = new FirstPersonCamera(p, p.getPivot());
		MousePicker picker = new MousePicker(camera,
				renderer.getProjectionMatrix(), terrains);

		List<Terrain> terrainList = Arrays.asList(terrains);

		WaterShader waterShader = new WaterShader();
		WaterFrameBuffers fbos = new WaterFrameBuffers();
		WaterRenderer waterRenderer = new WaterRenderer(loader, waterShader,
				renderer.getProjectionMatrix(), fbos);
		List<WaterTile> waters = new ArrayList<WaterTile>();
		WaterTile water = new WaterTile(75, -75, 0);
		waters.add(water);

		Camera refract = new Camera() {
			@Override
			public Vector3f getPosition() {
				Vector3f pos = camera.getPosition();
				float distance = 2 * (pos.y - water.getHeight());
				return new Vector3f(pos.x, pos.y - distance, pos.z);
			}

			@Override
			public float getPitch() {
				return -camera.getPitch();
			}

			@Override
			public float getYaw() {
				return camera.getYaw();
			}

			@Override
			public float getRoll() {
				return -camera.getRoll();
			}

			@Override
			public void move() {
				return;
			}

		};
		Camera reflect = new Camera() {
			@Override
			public Vector3f getPosition() {
				return camera.getPosition();
			}

			@Override
			public float getPitch() {
				return camera.getPitch();
			}

			@Override
			public float getYaw() {
				return camera.getYaw();
			}

			@Override
			public float getRoll() {
				return camera.getRoll();
			}

			@Override
			public void move() {
				return;
			}

		};
		
		modeldata = OBJFileLoader.loadOBJ("person");
		model = loader.loadToVAO(modeldata.getVertices(), modeldata.getTextureCoords(), modeldata.getNormals(),
				modeldata.getIndices());
			ModelTexture Texture = new ModelTexture(loader.loadTexture("texture5"));
			TexturedModel rocket = new TexturedModel(model, Texture);
			
		List<ShootyMcTooty> s = new ArrayList<ShootyMcTooty>();
		boolean flashlightOn = true;
		while (display.shouldClose()) {

			p.update(terrains);
			picker.update();
			Vector3f pos = p.getPosition();
			if(InputHandler.isMousePressed(GLFW.GLFW_MOUSE_BUTTON_1)){
				
				Entity e = new Entity(planeModel, new Vector3f(pos.x, pos.y+5, pos.z), 270f, 270f, 0, 1f);
				entities.add(e);
				s.add(new ShootyMcTooty(e, picker.getCurrentRay()));
				if(s.size() > 50){
					entities.remove(s.get(0).getEntity());
					s.remove(0);
				}
			}
			if(InputHandler.wasKeyPressed(GLFW.GLFW_KEY_F)){
				if(flashlightOn){
					lights.remove(flashlight);
				}
				else{
					lights.add(flashlight);
				}
				flashlightOn = !flashlightOn;
			}
			CameraPivot aa = p.getPivot();
			for(ShootyMcTooty b : s){
				b.update();
			}
			GL11.glEnable(GL30.GL_CLIP_DISTANCE0);
			camera.move();

			fbos.bindReflectionFrameBuffer();
			renderer.renderScene(entities, terrainList, lights, refract,
					new Vector4f(0, 1, 0, -water.getHeight()-0.1f));

			fbos.bindRefractionFrameBuffer();
			renderer.renderScene(entities, terrainList, lights, reflect,
					new Vector4f(0, -1, 0, water.getHeight()+0.1f));

			inputHandler.clear();
			camera.move();
			flashlight.setPosition(new Vector3f(pos.x, pos.y+5, pos.z));
			flashlight.setConeDirection(p.getPivot().getLookDirection());
			GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
			fbos.unbindCurrentFrameBuffer();
			renderer.renderScene(entities, terrainList, lights, camera,
					new Vector4f(0, -1, 0, 150000));
			waterRenderer.render(waters, camera, sun);
			display.update();
		}
		fbos.cleanUp();
		waterShader.cleanUp();
		renderer.cleanUp();
		display.cleanUp();
		loader.cleanUp();
	}

}
