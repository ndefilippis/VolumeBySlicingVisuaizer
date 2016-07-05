package EngineTest;

import java.util.ArrayList;
import java.util.List;

import entities.Camera;
import entities.Entity;
import entities.FreeFormCamera;
import entities.Light;
import guis.GUITexture;
import input.InputHandler;
import models.Model;
import models.ModelData;
import models.TexturedModel;
import renderEngine.Display;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import renderEngine.SlicingModelLoader;
import terrains.Terrain;
import textures.Texture;
import vector.Vector3f;
import water.WaterTile;

public class SlicingTest {

	public static void main(String[] args) {
		Display display = new Display();
		Loader loader = new Loader();

		ModelData modeldata = SlicingModelLoader.load();
		Model model = loader.loadToVAO(modeldata.getVertices(), modeldata.getTextureCoords(), modeldata.getNormals(),
				modeldata.getIndices());
		Texture treeTexture = new Texture(loader.loadTexture("texture4"));
		TexturedModel treeModel = new TexturedModel(model, treeTexture);

		List<Entity> entities = new ArrayList<Entity>();
		Entity e = new Entity(treeModel, new Vector3f(0, 0, -5f), 0, 0, 0, 1f);
		entities.add(e);
		List<Terrain> terrainList = new ArrayList<Terrain>();
		List<Light> lights = new ArrayList<Light>();
		List<GUITexture> guis = new ArrayList<GUITexture>();

		Light sun = new Light(new Vector3f(0f, 50f, -5f), new Vector3f(1f, 1f, 1f));
		Light sun2 = new Light(new Vector3f(20f, 0.5f, -5.5f), new Vector3f(1f, 1f, 1f));
		lights.add(sun);
		lights.add(sun2);

		Camera camera = new FreeFormCamera();
		InputHandler inputHandler = new InputHandler();
		MasterRenderer renderer = new MasterRenderer(loader, camera);
		renderer.toggleSkyBox();

		while (display.shouldClose()) {
			camera.update();
			inputHandler.clear();
			renderer.renderScene(entities, terrainList, lights, guis, new ArrayList<WaterTile>(), camera, camera, sun);
			display.update();
		}
		renderer.cleanUp();
		display.cleanUp();
		loader.cleanUp();
	}

}
