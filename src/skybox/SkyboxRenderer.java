package skybox;
import java.util.Observable;
import java.util.Observer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import entities.Camera;
import models.Model;
import renderEngine.Display;
import renderEngine.Loader;
import vector.Matrix4f;

public class SkyboxRenderer implements Observer{
	
private static final float SIZE = 500f;
	
	private static final float[] VERTICES = {        
	    -SIZE,  SIZE, -SIZE,
	    -SIZE, -SIZE, -SIZE,
	    SIZE, -SIZE, -SIZE,
	     SIZE, -SIZE, -SIZE,
	     SIZE,  SIZE, -SIZE,
	    -SIZE,  SIZE, -SIZE,

	    -SIZE, -SIZE,  SIZE,
	    -SIZE, -SIZE, -SIZE,
	    -SIZE,  SIZE, -SIZE,
	    -SIZE,  SIZE, -SIZE,
	    -SIZE,  SIZE,  SIZE,
	    -SIZE, -SIZE,  SIZE,

	     SIZE, -SIZE, -SIZE,
	     SIZE, -SIZE,  SIZE,
	     SIZE,  SIZE,  SIZE,
	     SIZE,  SIZE,  SIZE,
	     SIZE,  SIZE, -SIZE,
	     SIZE, -SIZE, -SIZE,

	    -SIZE, -SIZE,  SIZE,
	    -SIZE,  SIZE,  SIZE,
	     SIZE,  SIZE,  SIZE,
	     SIZE,  SIZE,  SIZE,
	     SIZE, -SIZE,  SIZE,
	    -SIZE, -SIZE,  SIZE,

	    -SIZE,  SIZE, -SIZE,
	     SIZE,  SIZE, -SIZE,
	     SIZE,  SIZE,  SIZE,
	     SIZE,  SIZE,  SIZE,
	    -SIZE,  SIZE,  SIZE,
	    -SIZE,  SIZE, -SIZE,

	    -SIZE, -SIZE, -SIZE,
	    -SIZE, -SIZE,  SIZE,
	     SIZE, -SIZE, -SIZE,
	     SIZE, -SIZE, -SIZE,
	    -SIZE, -SIZE,  SIZE,
	     SIZE, -SIZE,  SIZE
	};
	
	//right left top bottom back front
	private static String[] DAY_TEXTURE_FILES = {"skybox-1", "skybox-3", "skybox-4", "skybox-5", "skybox-0", "skybox-2"};
	private static String[] NIGHT_TEXTURE_FILES = {"skybox-1", "skybox-3", "skybox-4", "skybox-5", "skybox-0", "skybox-2"};
	
	private Model cube;
	private int dayTexture;
	private int nightTexture;
	private SkyboxShader shader;
	private float time = 0;
	
	//1 - front
	//3 - right
	
	
	public SkyboxRenderer(Loader loader, Matrix4f projectionMatrix){
		cube = loader.loadToVAO(VERTICES, 3);
		dayTexture = loader.loadCubeMap(DAY_TEXTURE_FILES);
		nightTexture = loader.loadCubeMap(NIGHT_TEXTURE_FILES);
		shader = new SkyboxShader();
		shader.start();
		shader.connectTextureUnits();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.stop();
	}
	
	public void render(Camera camera, float r, float g, float b){
		shader.start();
		shader.loadViewMatrix(camera);
		shader.loadFogColor(r, g, b);
		GL30.glBindVertexArray(cube.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		bindTextures();
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, cube.getVertexCount());
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
		shader.stop();
	}

	private void bindTextures(){
		time += Display.getFrameTimeSeconds() * 1000;
		time %= 24000;
		int texture1;
		int texture2;
		float blendFactor;		
		if(time >= 0 && time < 5000){
			texture1 = nightTexture;
			texture2 = nightTexture;
			blendFactor = (time - 0)/(5000 - 0);
		}else if(time >= 5000 && time < 8000){
			texture1 = nightTexture;
			texture2 = dayTexture;
			blendFactor = (time - 5000)/(8000 - 5000);
		}else if(time >= 8000 && time < 21000){
			texture1 = dayTexture;
			texture2 = dayTexture;
			blendFactor = (time - 8000)/(21000 - 8000);
		}else{
			texture1 = dayTexture;
			texture2 = nightTexture;
			blendFactor = (time - 21000)/(24000 - 21000);
		}

		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texture1);
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texture2);
		shader.loadBlendFactor(blendFactor);
	}
	
	@Override
	public void update(Observable o, Object arg) {
		shader.start();
		shader.loadProjectionMatrix((Matrix4f)arg);
		shader.stop();
	}

	public void setShouldBlend(boolean b) {
		shader.setShouldBlend(b);
	}
}
