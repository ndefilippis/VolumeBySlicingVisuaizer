package components;

import models.ModelData;
import models.TexturedModel;

public class RenderComponent implements Component
{
	public TexturedModel texturedModel;
	private int textureIndex = 0;
	
	public RenderComponent(TexturedModel model){
		this.texturedModel = model;
	}
	
	public RenderComponent(RenderComponent other){
		this.texturedModel = other.texturedModel;
		this.textureIndex = textureIndex;
	}
	
	public RenderComponent(TexturedModel model, int textureIndex){
		this.texturedModel = model;
		this.textureIndex = textureIndex;
	}
	
	public float getTextureXOffset(){
		int column = textureIndex % texturedModel.getTexture().getNumberOfRows();
		return (float)column/(float)texturedModel.getTexture().getNumberOfRows();
	}
	
	public float getTextureYOffset(){
		int row = textureIndex/texturedModel.getTexture().getNumberOfRows();
		return (float)row/(float)texturedModel.getTexture().getNumberOfRows();
	}

	@Override
	public void update() {
	}
	
	@Override
	public ComponentType getType() {
		return ComponentType.RENDER;
	}
}
