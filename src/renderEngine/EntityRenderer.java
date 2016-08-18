package renderEngine;

import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import components.Entity;
import components.RenderComponent;
import models.Model;
import models.TexturedModel;
import shaders.StaticShader;
import textures.Texture;
import util.Transform;
import vector.Matrix4f;

public class EntityRenderer implements Observer{

	private StaticShader shader;
	
	public EntityRenderer(StaticShader shader, Matrix4f projectionMatrix){
		this.shader = shader;
		
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.connectTextureUnits();
		shader.stop();
	}
	
	
	
	public void render(Map<TexturedModel, List<Entity>> entities, Matrix4f toShadowSpace){
		shader.loadToShadowSpaceMatrix(toShadowSpace);
		for(TexturedModel model : entities.keySet()){
			prepareTexturedModel(model);
			List<Entity> batch = entities.get(model);
			for(Entity entity:batch){
				prepareInstance(entity);
				GL11.glDrawElements(GL11.GL_TRIANGLES, model.getRawModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
			}
			unbindTexturedModel();
		}
	}
	
	private void prepareTexturedModel(TexturedModel model){
		Model rawModel = model.getRawModel();
		GL30.glBindVertexArray(rawModel.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		Texture texture = model.getTexture();
		shader.loadNumberOfRows(texture.getNumberOfRows());
		if(texture.isHasTransparency()){
			MasterRenderer.disableCulling();
		}
		shader.loadFakeLightingVariable(texture.isUseFakeLighting());
		shader.loadShineVariables(texture.getShineDamper(), texture.getReflectivity());
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getID());
	}
	
	private void unbindTexturedModel(){
		MasterRenderer.enableCulling();
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL30.glBindVertexArray(0);
	}
	private void prepareInstance(Entity entity){
		Matrix4f transform = entity.as(Transform.class).getWorldMatrix();
		shader.loadTransformationMatrix(transform);
		shader.loadOffset( 
			entity.as(RenderComponent.class).getTextureXOffset(), 
			entity.as(RenderComponent.class).getTextureYOffset()
		);
	}
	
	@Override
	public void update(Observable o, Object arg) {
		shader.start();
		shader.loadProjectionMatrix((Matrix4f)arg);
		shader.stop();
	}
	
}
