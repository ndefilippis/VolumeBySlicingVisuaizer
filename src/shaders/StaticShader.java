package shaders;

import java.util.List;

import entities.Camera;
import entities.Light;
import util.Utils;
import vector.Matrix4f;
import vector.Vector2f;
import vector.Vector3f;
import vector.Vector4f;

public class StaticShader extends ShaderProgram{

	private static final int MAX_LIGHTS = 10;

	private static final String VERTEX_FILE = "src/shaders/vertexShader.txt";
	private static final String FRAGMENT_FILE = "src/shaders/fragmentShader.txt";

	private int location_transformationMatrix;
	private int location_projectionMatrix;
	private int location_viewMatrix;
	private int location_lightPosition[];
	private int location_lightColor[];
	private int location_attenuation[];
	private int location_coneDirection[];
	private int location_coneAngle[];
	private int location_shineDamper;
	private int location_reflectivity;
	private int location_useFakeLighting;
	private int location_skyColor;
	private int location_numberOfRows;
	private int location_offset;
	private int location_plane;
	private int location_toShadowMapSpace;
	private int location_shadowMap;
	
	public StaticShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	public void connectTextureUnits(){
		super.loadInt(location_shadowMap, 5);
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoords");
		super.bindAttribute(2, "normal");
	}

	@Override
	protected void getAllUniformLocations() {
		location_transformationMatrix = super.getUniformLocation("transformationMatrix");
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
		location_viewMatrix = super.getUniformLocation("viewMatrix");
		location_shineDamper = super.getUniformLocation("shineDamper");
		location_reflectivity = super.getUniformLocation("reflectivity");
		location_useFakeLighting = super.getUniformLocation("useFakeLighting");
		location_skyColor = super.getUniformLocation("skyColor");
		location_numberOfRows = super.getUniformLocation("numberOfRows");
		location_offset = super.getUniformLocation("offset");
		location_plane = super.getUniformLocation("plane");
		location_toShadowMapSpace = super.getUniformLocation("toShadowMapSpace");
		location_shadowMap = super.getUniformLocation("shadowMap");
		
		location_lightPosition = new int[MAX_LIGHTS];
		location_lightColor = new int[MAX_LIGHTS];
		location_attenuation = new int[MAX_LIGHTS];
		location_coneDirection = new int[MAX_LIGHTS];
		location_coneAngle = new int[MAX_LIGHTS];
		for(int i = 0; i < MAX_LIGHTS; i++){
			location_lightPosition[i] = super.getUniformLocation("lightPosition[" + i + "]");
			location_lightColor[i] = super.getUniformLocation("lightColor[" + i + "]");
			location_attenuation[i] = super.getUniformLocation("attenuation[" + i + "]");
			location_coneDirection[i] = super.getUniformLocation("coneDirection[" + i + "]");
			location_coneAngle[i] = super.getUniformLocation("coneAngle[" + i + "]");
		}
	}
	
	public void loadNumberOfRows(int numberOfRows){
		super.loadFloat(location_numberOfRows, numberOfRows);
	}
	
	public void loadOffset(float x, float y){
		super.loadVector(location_offset, new Vector2f(x, y));
	}
	
	public void loadSkyColor(float r, float g, float b){
		super.loadVector(location_skyColor, new Vector3f(r, g, b));
	}
	
	public void loadFakeLightingVariable(boolean useFake){
		super.loadBoolean(location_useFakeLighting, useFake);
	}
	
	public void loadShineVariables(float damper, float reflectivity){
		super.loadFloat(location_reflectivity, reflectivity);
		super.loadFloat(location_shineDamper, damper);
	}
	
	public void loadTransformationMatrix(Matrix4f matrix){
		super.loadMatrix(location_transformationMatrix, matrix);
	}
	
	public void loadProjectionMatrix(Matrix4f matrix){
		super.loadMatrix(location_projectionMatrix, matrix);
	}
	
	public void loadViewMatrix(Camera camera){
		super.loadMatrix(location_viewMatrix, Utils.createViewMatrix(camera));
	}
	
	public void loadClipPlane(Vector4f clipPlane){
		super.loadVector(location_plane, clipPlane);
	}
	
	public void loadToShadowSpaceMatrix(Matrix4f matrix){
		super.loadMatrix(location_toShadowMapSpace, matrix);
	}
	
	public void loadLights(List<Light> lights){
		for(int i = 0; i < MAX_LIGHTS; i++){
			if(i < lights.size()){
				super.loadVector(location_lightPosition[i], lights.get(i).getPosition());
				super.loadVector(location_lightColor[i], lights.get(i).getColor());
				super.loadVector(location_attenuation[i], lights.get(i).getAttenuation());
				super.loadVector(location_coneDirection[i], lights.get(i).getConeDirection());
				super.loadFloat(location_coneAngle[i], lights.get(i).getConeAngle());
			}
			else{
				super.loadVector(location_lightPosition[i], new Vector3f(0, 0, 0));
				super.loadVector(location_lightColor[i], new Vector3f(0, 0, 0));
				super.loadVector(location_attenuation[i], new Vector3f(1, 0, 0));
				super.loadVector(location_coneDirection[i], new Vector3f(0, 0, 0));
				super.loadFloat(location_coneAngle[i], 0);
			}
		}
	}
}
