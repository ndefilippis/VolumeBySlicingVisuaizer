package terrains;

import textures.Texture;

public class TerrainTexturePack {
	private Texture backgroundTexture;
	private Texture rTexture;
	private Texture gTexture;
	private Texture bTexture;
	public TerrainTexturePack(Texture backgroundTexture, Texture rTexture, Texture gTexture,
			Texture bTexture) {
		this.backgroundTexture = backgroundTexture;
		this.rTexture = rTexture;
		this.gTexture = gTexture;
		this.bTexture = bTexture;
	}
	public Texture getBackgroundTexture() {
		return backgroundTexture;
	}
	public Texture getrTexture() {
		return rTexture;
	}
	public Texture getgTexture() {
		return gTexture;
	}
	public Texture getbTexture() {
		return bTexture;
	}
	public float[] getShineValues() {
		return new float[]{	backgroundTexture.getShineDamper(),
							rTexture.getShineDamper(),
							gTexture.getShineDamper(),
							bTexture.getShineDamper()
		};
	}
	
	public float[] getReflectivityValues() {
		return new float[]{	backgroundTexture.getReflectivity(),
							rTexture.getReflectivity(),
							gTexture.getReflectivity(),
							bTexture.getReflectivity()
		};
	}
	
	
}
