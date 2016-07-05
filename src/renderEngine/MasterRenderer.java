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
import java.util.Observable;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;

import debug.DebugRenderer;
import entities.Camera;
import entities.Entity;
import entities.Light;
import guis.GUIRenderer;
import guis.GUITexture;
import models.TexturedModel;
import particles.InsertionSort;
import particles.ParticleMaster;
import shaders.StaticShader;
import shadows.ShadowMapMasterRenderer;
import skybox.SkyboxRenderer;
import terrains.Terrain;
import terrains.TerrainRenderer;
import terrains.TerrainShader;
import vector.Matrix4f;
import vector.Vector3f;
import vector.Vector4f;
import water.WaterFrameBuffers;
import water.WaterRenderer;
import water.WaterTile;

public class MasterRenderer extends Observable{
	public static float FOV = 70f;
	public static final float NEAR_PLANE = 0.1f;
	public static final float FAR_PLANE = 100000f;
	
	private boolean shouldRenderSkyBox = true;
	private boolean shouldDebug = false;
	
	private static float SKY_RED = 0.1f;
	private static float SKY_GREEN = 0.3f;
	private static float SKY_BLUE = 0.5f;
	
	private Matrix4f projectionMatrix = new Matrix4f();
	
	private StaticShader shader = new StaticShader();
	private EntityRenderer renderer;
	
	private TerrainRenderer terrainRenderer;
	private TerrainShader terrainShader = new TerrainShader();
	
	private Map<TexturedModel, List<Entity>> entities = new HashMap<TexturedModel, List<Entity>>();
	private List<Terrain> terrains = new ArrayList<Terrain>();
	private List<GUITexture> guis = new ArrayList<GUITexture>();
	private List<WaterTile> waters = new ArrayList<WaterTile>();
	
	private WaterFrameBuffers waterFBOS;
	
	private WaterRenderer waterRenderer;
	private GUIRenderer guiRenderer;
	
	private SkyboxRenderer skyboxRenderer;
	private ShadowMapMasterRenderer shadowMapRenderer;
	
	private DebugRenderer debugRenderer;
	
	public MasterRenderer(Loader loader, Camera camera){
		enableCulling();
		createProjectionMatrix();
		waterFBOS = new WaterFrameBuffers();
		renderer = new EntityRenderer(shader, projectionMatrix);
		terrainRenderer = new TerrainRenderer(terrainShader, projectionMatrix);
		skyboxRenderer = new SkyboxRenderer(loader, projectionMatrix);
		shadowMapRenderer = new ShadowMapMasterRenderer(camera);
		waterRenderer = new WaterRenderer(loader, projectionMatrix, waterFBOS);
		debugRenderer = new DebugRenderer(projectionMatrix, loader);
		ParticleMaster.init(loader, this);
		guiRenderer = new GUIRenderer(loader);
		this.addObserver(renderer);
		this.addObserver(terrainRenderer);
		this.addObserver(skyboxRenderer);
		this.addObserver(waterRenderer);
		this.addObserver(debugRenderer);
	}
	
	public static void enableCulling(){
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
	}
	
	public static void disableCulling(){
		GL11.glDisable(GL11.GL_CULL_FACE);
	}
	
	public static void enableClipping(){
		GL11.glEnable(GL30.GL_CLIP_DISTANCE0);
	}
	
	public static void disableClipping(){
		GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
	}
	
	public void renderScene(List<Entity> entities, List<Terrain> terrains, 
	List<Light> lights, List<GUITexture> guis, List<WaterTile> waterTiles, Camera camera, Camera reflectionCamera, Light sun){
		for(Terrain terrain : terrains){
			processTerrain(terrain);
		}
		for(Entity entity : entities){
			processEntity(entity);
		}
		enableClipping();
		for(WaterTile tile : waterTiles){
			waterFBOS.bindReflectionFrameBuffer();
			render(lights, reflectionCamera, new Vector4f(0, 1, 0, -tile.getHeight()-0.1f), sun);
			waterFBOS.bindRefractionFrameBuffer();
			render(lights, camera, new Vector4f(0, -1, 0, -tile.getHeight()+0.1f), sun);
			waterFBOS.unbindCurrentFrameBuffer();
			waters.add(tile);
		}
		disableClipping();
		for(GUITexture gui : guis){
			processGUI(gui);
		}
		render(lights, camera, new Vector4f(0, -1, 0, 15000000), sun); //action!;
		clear();
	}

	public void render(List<Light> lights, Camera camera, Vector4f clipPlane, Light sun){
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
		terrainRenderer.render(terrains, shadowMapRenderer.getToShadowMapSpaceMatrix());
		terrainShader.stop();
		shader.start();
		shader.loadClipPlane(clipPlane);
		shader.loadSkyColor(SKY_RED, SKY_GREEN, SKY_BLUE);
		shader.loadLights(lights);
		shader.loadViewMatrix(camera);
		renderer.render(entities, shadowMapRenderer.getToShadowMapSpaceMatrix());
		shader.stop();
		if(shouldRenderSkyBox){
			skyboxRenderer.render(camera, SKY_RED, SKY_GREEN, SKY_BLUE);
		}
		waterRenderer.render(waters, camera, sun);
		ParticleMaster.renderParticle(camera);
		if(shouldDebug){
			debugRenderer.render(camera, entities);
		}
		guiRenderer.render(guis);
		
	}
	
	public void clear(){
		terrains.clear();
		entities.clear();
		guis.clear();
		waters.clear();
	}
	
	public void update(){
		
	}
	
	public void prepare(){
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		glClearColor(SKY_RED, SKY_GREEN, SKY_BLUE, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
        GL13.glActiveTexture(GL13.GL_TEXTURE5);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, getShadowMapTexture());
	}
	
	public void processTerrain(Terrain terrain){
		terrains.add(terrain);
	}
	
	private void processGUI(GUITexture gui) {
		guis.add(gui);
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
	
	public void toggleDebug(){
		shouldDebug = !shouldDebug;
	}
	
	public void renderShadowMap(List<Entity> entityList, Light sun){
		for(Entity entity : entityList){
			processEntity(entity);
		}
		shadowMapRenderer.render(entities, sun);
		entities.clear();
	}
	
	public int getShadowMapTexture(){
		return shadowMapRenderer.getShadowMap();
	}
	
	public void cleanUp(){
		shader.cleanUp();
		terrainShader.cleanUp();
		shadowMapRenderer.cleanUp();
		waterFBOS.cleanUp();
		waterRenderer.cleanUp();
		guiRenderer.cleanUp();
		deleteObservers();
	}
	
	public void setFOV(float newFOV){
		FOV = newFOV;
		createProjectionMatrix();
		setChanged();
		notifyObservers(projectionMatrix);
	}
	
	private void createProjectionMatrix(){
		float aspectRatio = Display.getWidth() / Display.getHeight();
		float y_scale = (float)(1f / Math.tan(Math.toRadians(FOV/2f)));
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
