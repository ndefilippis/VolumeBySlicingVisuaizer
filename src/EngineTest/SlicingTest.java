package EngineTest;

import input.InputHandler;

import java.util.ArrayList;
import java.util.List;

import models.Model;
import models.ModelData;
import models.TexturedModel;
import renderEngine.Display;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import renderEngine.SlicingModelLoader;
import terrain.Terrain;
import textures.ModelTexture;
import vector.Vector3f;
import vector.Vector4f;
import entities.Camera;
import entities.Entity;
import entities.FreeFormCamera;
import entities.Light;

public class SlicingTest {

	public static void main(String[] args) {
		Display display = new Display();
		Loader loader = new Loader();

		ModelData modeldata = SlicingModelLoader.load();
		Model model = loader.loadToVAO(modeldata.getVertices(),
				modeldata.getTextureCoords(), modeldata.getNormals(),
				modeldata.getIndices());
		ModelTexture treeTexture = new ModelTexture(loader.loadTexture("texture4"));
		TexturedModel treeModel = new TexturedModel(model, treeTexture);

		List<Entity> entities = new ArrayList<Entity>();
		entities.add(new Entity(treeModel, new Vector3f(0, 0, -5), 0, 0, 0, 1f));
		List<Terrain> terrainList = new ArrayList<Terrain>();
		List<Light> lights = new ArrayList<Light>();
		
		Light sun = new Light(new Vector3f(0f, 0f, 0f), new Vector3f(1f, 1f, 1f));
		lights.add(sun);

		InputHandler inputHandler = new InputHandler();
		MasterRenderer renderer = new MasterRenderer(loader);
		renderer.toggleSkyBox();
		Camera camera = new FreeFormCamera();
		
		while (display.shouldClose()) {
			camera.move();
			inputHandler.clear();
			renderer.renderScene(entities, terrainList, lights, camera,
					new Vector4f(0, -1, 0, 150000));
			display.update();
		}
		renderer.cleanUp();
		display.cleanUp();
		loader.cleanUp();
	}

}
