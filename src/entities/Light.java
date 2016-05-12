package entities;
import vector.Vector3f;

public class Light {
	private Vector3f position;
	private Vector3f color;
	private Vector3f attenuation = new Vector3f(1, 0, 0);
	private Vector3f coneDirection = new Vector3f(1, 0, 0);
	private float coneAngle = 720f;
	
	public Light(Vector3f position, Vector3f color) {
		this.position = position;
		this.color = color;
	}
	
	public Light(Vector3f position, Vector3f color, Vector3f attenuation) {
		this.position = position;
		this.color = color;
		this.attenuation = attenuation;
	}
	
	public Light(Vector3f position, Vector3f color, Vector3f attenuation, Vector3f direction, float angle) {
		this.position = position;
		this.color = color;
		this.attenuation = attenuation;
		this.coneDirection = direction;
		this.coneAngle = angle;
	}

	public Vector3f getPosition() {
		return position;
	}

	public void setPosition(Vector3f position) {
		this.position = position;
	}

	public Vector3f getColor() {
		return color;
	}

	public void setColor(Vector3f color) {
		this.color = color;
	}
	
	public void setAttenuation(Vector3f attenuation){
		this.attenuation = attenuation;
	}
	
	public Vector3f getAttenuation(){
		return attenuation;
	}
	
	public Vector3f getConeDirection(){
		return coneDirection;
	}
	
	public void setConeDirection(Vector3f direction){
		this.coneDirection = direction;
	}
	
	public float getConeAngle(){
		return coneAngle;
	}
	
}
