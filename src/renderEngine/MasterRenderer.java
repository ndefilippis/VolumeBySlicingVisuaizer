package renderEngine;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;

import entities.Camera;
import entities.Entity;
import entities.Light;
import models.TexturedModel;
import shaders.StaticShader;
import shaders.TerrainShader;
import skybox.SkyboxRenderer;
import terrain.Terrain;
import vector.Matrix4f;
import vector.Vector3f;
import vector.Vector4f;

public class MasterRenderer {
	private static final float FOV = 70f;
	private static final float NEAR_PLANE = 0.1f;
	private static final float FAR_PLANE = 100000f;
	
	private boolean shouldRenderSkyBox = true;
	
	private static final float SKY_RED = 0.0f;
	private static final float SKY_GREEN = 0.0f;
	private static final float SKY_BLUE = 0.0f;
	
	private Matrix4f projectionMatrix = new Matrix4f();
	
	private StaticShader shader = new StaticShader();
	private EntityRenderer renderer;
	
	private TerrainRenderer terrainRenderer;
	private TerrainShader terrainShader = new TerrainShader();
	
	private Map<TexturedModel, List<Entity>> entities = new HashMap<TexturedModel, List<Entity>>();
	private List<Terrain> terrains = new ArrayList<Terrain>();
	
	private SkyboxRenderer skyboxRenderer;
	
	public MasterRenderer(Loader loader){
		enableCulling();
		createProjectionMatrix();
		renderer = new EntityRenderer(shader, projectionMatrix);
		terrainRenderer = new TerrainRenderer(terrainShader, projectionMatrix);
		skyboxRenderer = new SkyboxRenderer(loader, projectionMatrix);
	}
	
	public static void enableCulling(){
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
	}
	
	public static void disableCulling(){
		GL11.glDisable(GL11.GL_CULL_FACE);
	}
	
	public void renderScene(List<Entity> entities, List<Terrain> terrains, 
	List<Light> lights, Camera camera, Vector4f clipPlane){
		for(Terrain terrain : terrains){
			processTerrain(terrain);
		}
		for(Entity entity : entities){
			processEntity(entity);
		}
		render(lights, camera, clipPlane); //action!;
	}
	
	public void render(List<Light> lights, Camera camera, Vector4f clipPlane){
		Collections.sort(lights, new Comparator<Light>(){
			public int compare(Light l1, Light l2) {
				Vector3f d1 = new Vector3f(), d2 = new Vector3f();
				Vector3f.sub(l1.getPosition(), camera.getPosition(), d1);
				Vector3f.sub(l2.getPosition(), camera.getPosition(), d2);
				float distance1 = d1.length();
				float distance2 = d2.length();
				float f1 = l1.getAttenuation().x + l1.getAttenuation().y*distance1 + l1.getAttenuation().z*distance1*distance1;
				float f2 = l2.getAttenuation().x + l2.getAttenuation().y*distance2 + l2.getAttenuation().z*distance2*distance2;
				return (int)((f1 - f2)*100);
			}
		});
		prepare();
		terrainShader.start();
		terrainShader.loadClipPlane(clipPlane);
		terrainShader.loadSkyColor(SKY_RED, SKY_GREEN, SKY_BLUE);
		terrainShader.loadLights(lights);
		terrainShader.loadViewMatrix(camera);
		terrainRenderer.render(terrains);
		terrainShader.stop();
		shader.start();
		shader.loadClipPlane(clipPlane);
		shader.loadSkyColor(SKY_RED, SKY_GREEN, SKY_BLUE);
		shader.loadLights(lights);
		shader.loadViewMatrix(camera);
		renderer.render(entities);
		shader.stop();
		if(shouldRenderSkyBox){
			skyboxRenderer.render(camera);
		}
		terrains.clear();
		entities.clear();
	}
	
	public void prepare(){
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		glClearColor(SKY_RED, SKY_GREEN, SKY_BLUE, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
	}
	
	public void processTerrain(Terrain terrain){
		terrains.add(terrain);
	}
	
	public void processEntity(Entity entity){
		TexturedModel entityModel = entity.getTexturedModel();
		List<Entity> batch = entities.get(entityModel);
		if(batch != null){
			batch.add(entity);
		}
		else{
			List<Entity> newBatch = new ArrayList<Entity>();
			newBatch.add(entity);
			entities.put(entityModel, newBatch);
		}
	}
	
	public Matrix4f getProjectionMatrix(){
		return projectionMatrix;
	}
	
	public void toggleSkyBox(){
		shouldRenderSkyBox = !shouldRenderSkyBox;
	}
	
	public void cleanUp(){
		shader.cleanUp();
		terrainShader.cleanUp();
	}
	
	private void createProjectionMatrix(){
		float aspectRatio = Display.getWidth() / Display.getHeight();
		float y_scale = (float)(1f / Math.tan(Math.toRadians(FOV/2f)))*aspectRatio;
		float x_scale = y_scale / aspectRatio;
		float frustum_length = FAR_PLANE - NEAR_PLANE;
		
		projectionMatrix.setIdentity();
		projectionMatrix.m00 = x_scale;
		projectionMatrix.m11 = y_scale;
		projectionMatrix.m22 = -((FAR_PLANE + NEAR_PLANE)/frustum_length);
		projectionMatrix.m23 = -1;
		projectionMatrix.m32 = -((2 * NEAR_PLANE * FAR_PLANE)/frustum_length);
		projectionMatrix.m33 = 0;
	}
}
