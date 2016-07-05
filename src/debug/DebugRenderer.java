package debug;

import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import components.ComponentType;
import components.TransformComponent;
import entities.Camera;
import entities.EntityC;
import models.Model;
import models.ModelData;
import models.TexturedModel;
import renderEngine.Loader;
import renderEngine.OBJFileLoader;
import util.AABB;
import util.Transform;
import vector.Matrix4f;
import vector.Vector3f;

public class DebugRenderer implements Observer{
	
	private DebugShader shader;
	private Model boundingModel;
	
	public DebugRenderer(Matrix4f projectionMatrix, Loader loader){
		this.shader = new DebugShader();
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.stop();
		ModelData sphereModelData = OBJFileLoader.loadOBJ("cube");
		boundingModel = loader.loadToVAO(sphereModelData.getVertices(), 
			sphereModelData.getTextureCoords(), sphereModelData.getNormals(), 
			sphereModelData.getIndices());
	}
	
	public void render(Camera camera, Map<TexturedModel, List<EntityC>> entitiesMap){
		shader.start();
		shader.loadViewMatrix(camera);
		for(TexturedModel key : entitiesMap.keySet()){
			List<EntityC> entities = entitiesMap.get(key);
			AABB box = key.getRawModel().getBoundingBox();
			for(EntityC e : entities){
				TransformComponent tComponent = (TransformComponent)e.getComponent(ComponentType.TRANSFORM);
				prepareBoundingSphere();
				loadModelMatrix(tComponent.transform, box);
				GL11.glDrawElements(GL11.GL_LINES, boundingModel.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
			}
		}
		shader.stop();
	}

	private void loadModelMatrix(Transform transformation, AABB scale) {
		AABB transformedBoundingBox = new AABB(scale, transformation);
		Matrix4f newTransformationMatrix = new Matrix4f();
		Vector3f center = transformedBoundingBox.getCenter();
		Vector3f translate = Vector3f.add(transformation.getPosition(), center, null);
		newTransformationMatrix.translate(translate);
		newTransformationMatrix.scale(transformedBoundingBox.getScale());
		shader.loadTransformationMatrix(newTransformationMatrix);
	}

	private void prepareBoundingSphere(){
		GL30.glBindVertexArray(boundingModel.getVaoID());
		GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);
	}

	@Override
	public void update(Observable o, Object arg) {
		shader.start();
		shader.loadProjectionMatrix((Matrix4f)arg);
		shader.stop();
	}
	
	
}
