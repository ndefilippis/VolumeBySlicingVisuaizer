package models;
import textures.ModelTexture;

public class TexturedModel {
	private Model rawModel;
	private ModelTexture texture;
	
	public TexturedModel(Model model, ModelTexture texture){
		this.texture = texture;
		this.rawModel = model;
	}
	
	public Model getRawModel() {
		return rawModel;
	}

	public ModelTexture getTexture() {
		return texture;
	}

	
	
	
}
