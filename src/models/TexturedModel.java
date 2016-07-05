package models;
import textures.Texture;

public class TexturedModel {
	private Model rawModel;
	private Texture texture;
	
	public TexturedModel(Model model, Texture texture){
		this.texture = texture;
		this.rawModel = model;
	}
	
	public Model getRawModel() {
		return rawModel;
	}

	public Texture getTexture() {
		return texture;
	}

	
	
	
}
