package particles;

import entities.Camera;
import renderEngine.Display;
import vector.Vector2f;
import vector.Vector3f;

public class Particle {
	
	private Vector3f position;
	private Vector3f velocity;
	private float gravityEffect;
	private float lifeLength;
	private float rotation;
	private float scale;
	
	private ParticleTexture texture;
	
	private Vector2f texOffset1 = new Vector2f();
	private Vector2f texOffset2 = new Vector2f();
	private float blend;
	
	private static float gravity = -50f;
	
	private float elapsedTime = 0f;
	private float distance;

	public Particle(ParticleTexture texture, Vector3f position, Vector3f velocity, float gravityEffect, float lifeLength, float rotation,
			float scale, boolean shouldAddImmediatly) {
		this.position = position;
		this.velocity = velocity;
		this.gravityEffect = gravityEffect;
		this.lifeLength = lifeLength;
		this.rotation = rotation;
		this.scale = scale;
		this.texture = texture;
		if(shouldAddImmediatly){
			ParticleMaster.addParticle(this);
		}
	}
	
	public Particle(Particle other){
		this.position = other.position;
		this.velocity = other.velocity;
		this.gravityEffect = other.gravityEffect;
		this.lifeLength = other.lifeLength;
		this.rotation = other.rotation;
		this.scale = other.scale;
		ParticleMaster.addParticle(this);
		
	}

	protected Vector3f getPosition() {
		return position;
	}

	protected float getRotation() {
		return rotation;
	}

	protected float getScale() {
		return scale;
	}
	
	protected Vector2f getTexOffset1() {
		return texOffset1;
	}

	protected Vector2f getTexOffset2() {
		return texOffset2;
	}

	protected float getBlend() {
		return blend;
	}

	protected ParticleTexture getTexture() {
		return texture;
	}
	
	protected float getDistance(){
		return distance;
	}

	protected boolean update(Camera camera){
		velocity.y += gravity * gravityEffect * Display.getFrameTimeSeconds();
		Vector3f change = new Vector3f(velocity);
		change.scale(Display.getFrameTimeSeconds());
		Vector3f.add(change, position, position);
		distance = Vector3f.sub(camera.getPosition(), position, null).lengthSquared();
		updateTextureCoords();
		elapsedTime += Display.getFrameTimeSeconds();
		return elapsedTime < lifeLength;
	}
	
	private void updateTextureCoords(){
		float lifeFactor = elapsedTime / lifeLength;
		int stageCount = texture.getNumberOfRows() * texture.getNumberOfRows();
		float atlasProgression = lifeFactor * stageCount;
		int index1 = (int)Math.floor(atlasProgression);
		int index2 = index1 < stageCount -1 ?  index1 + 1 : index1;
		this.blend = atlasProgression % 1;
		setTextureOffset(texOffset1, index1);
		setTextureOffset(texOffset2, index2);
		
	}
	
	private void setTextureOffset(Vector2f offset, int index){
		int column = index % texture.getNumberOfRows();
		int row = index / texture.getNumberOfRows();
		offset.x = (float) column / texture.getNumberOfRows();
		offset.y = (float) row / texture.getNumberOfRows();
	}
	
}
