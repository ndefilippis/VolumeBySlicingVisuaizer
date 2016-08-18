package EngineTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import collision.CollisionModel;
import collision.SAPStructure;
import components.BasicEntityManager;
import components.CollisionSystem;
import components.Entity;
import components.EntityManager;
import components.MotionComponent;
import components.MotionSystem;
import components.RenderComponent;
import entities.Camera;
import entities.EntityFactory;
import entities.FreeFormCamera;
import entities.Light;
import guis.GUITexture;
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
import util.Transform;
import vector.Vector2f;
import vector.Vector3f;
import vector.Vector4f;

public class SAPTest {
	private static Display display;
	private static Loader loader;
	private static EntityManager manager;
	private static EntityFactory factory;
	private static SAPStructure sapStructure = new SAPStructure();
	private static CollisionSystem collisionSystem = new CollisionSystem(sapStructure);
	private static MotionSystem motionSystem = new MotionSystem();


	public static void main(String[] args) {
		display = new Display();
		loader = new Loader();
		manager = new BasicEntityManager();
		factory = new EntityFactory(manager);
		
		InputHandler inputHandler = new InputHandler();
		
		List<Light> lights = new ArrayList<Light>();
		Light sun = new Light(new Vector3f(220, 200000, 220), new Vector3f(1f, 1f, 1f));
		lights.add(sun);

		TexturedModel cubeModel = createTexturedModel("dragon", "playerTexture", 1, false, false);
		CollisionModel cubeHitbox = createCollisionModel("dragon");
		Random random = new Random();
		
		
		Vector3f position = new Vector3f(10, 10, 20);
		Entity c1 = factory.createDynamicModel(cubeModel, cubeHitbox, new Vector3f(1f, 2f, 10), 0f, 0f, 0f, 1f, null, sapStructure);	
		Entity c2 = factory.createDynamicModel(cubeModel, cubeHitbox, new Vector3f(8f, 10f, 10), 0f, 0f, 0f, 1f, null, sapStructure);
		//Entity c3 = factory.createStaticModel(cubeModel, cubeHitbox, new Vector3f(0.5f, 0, 0), 0f, 0f, 0f, 1f, null, sapStructure);		
		c1.as(MotionComponent.class).velocity = new Vector3f(1, 2, 0);
		c2.as(MotionComponent.class).velocity = new Vector3f(0, 0, 0);
		Camera camera = new FreeFormCamera(position);
		
		MasterRenderer renderer = new MasterRenderer(loader, camera);
		renderer.setSkyboxBlend(false);
		renderer.toggleSkyBox();

		
		display.resetTime();
		while (display.shouldClose()) {
			Transform t = c1.as(Transform.class);
			inputHandler.update();
			Vector3f pos = t.getPosition();
			renderer.update();
			ParticleMaster.update(camera);
			renderer.renderShadowMap(manager.getAll(Transform.class, RenderComponent.class), sun);
			
			Vector3f direction = t.getOrientation().rotate(new Vector3f(0, 0, -1));
			collisionSystem.update(manager);
			motionSystem.update(manager);
			
			camera.update();
			for(Entity e : manager.getAll(Transform.class, RenderComponent.class)){
				renderer.processEntity(e);
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

