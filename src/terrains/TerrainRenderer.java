package terrains;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import models.Model;
import util.Utils;
import vector.Matrix4f;
import vector.Quaternion;
import vector.Vector3f;

public class TerrainRenderer implements Observer{
	 
    private TerrainShader shader;
 
    public TerrainRenderer(TerrainShader shader, Matrix4f projectionMatrix) {
        this.shader = shader;
        shader.start();
        shader.loadProjectionMatrix(projectionMatrix);
        shader.connectTextureUnits();
        shader.stop();
    }
 
    public void render(List<Terrain> terrains, Matrix4f toShadowSpace) {
    	shader.loadToShadowSpaceMatrix(toShadowSpace);
        for (Terrain terrain : terrains) {
            prepareTerrain(terrain);
            loadModelMatrix(terrain);
            GL11.glDrawElements(GL11.GL_TRIANGLES, terrain.getModel().getVertexCount(),
                    GL11.GL_UNSIGNED_INT, 0);
            unbindTexturedModel();
        }
    }
 
    private void prepareTerrain(Terrain terrain) {
        Model rawModel = terrain.getModel();
        GL30.glBindVertexArray(rawModel.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);
        bindTextures(terrain);
        shader.loadShineVariables(terrain.getTexturePack().getShineValues(), terrain.getTexturePack().getReflectivityValues());
    }
    
    private void bindTextures(Terrain terrain){
    	TerrainTexturePack texturePack = terrain.getTexturePack();
    	GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.getBackgroundTexture().getID());
        GL13.glActiveTexture(GL13.GL_TEXTURE1);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.getrTexture().getID());
        GL13.glActiveTexture(GL13.GL_TEXTURE2);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.getgTexture().getID());
        GL13.glActiveTexture(GL13.GL_TEXTURE3);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.getbTexture().getID());
        GL13.glActiveTexture(GL13.GL_TEXTURE4);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, terrain.getBlendMap().getID());
    }
 
    private void unbindTexturedModel() {
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);
        GL30.glBindVertexArray(0);
    }
 
    private void loadModelMatrix(Terrain terrain) {
        Matrix4f transformationMatrix = terrain.getTransform().getWorldMatrix();
        shader.loadTransformationMatrix(transformationMatrix);
    }
 
    @Override
	public void update(Observable o, Object arg) {
		shader.start();
		shader.loadProjectionMatrix((Matrix4f)arg);
		shader.stop();
	}
}