package entities;
import util.Transform;
import vector.Quaternion;
import vector.Vector3f;

public class Light {
	private Transform transform;
	private Vector3f color;
	private Vector3f attenuation = new Vector3f(1, 0, 0);
	private Vector3f coneDirection = new Vector3f(1, 0, 0);
	private float coneAngle = 720f;
	
	public Light(Vector3f position, Vector3f color) {
		this.transform = new Transform(position, new Quaternion(), 1.0f);
		this.color = color;
	}
	
	public Light(Vector3f position, Vector3f color, Vector3f attenuation) {
		this.transform = new Transform(position, new Quaternion(), 1.0f);
		this.color = color;
		this.attenuation = attenuation;
	}
	
	public Light(Vector3f position, Vector3f color, Vector3f attenuation, Vector3f direction, float angle) {
		this.transform = new Transform(position, Quaternion.AxisAngle(direction, 0f), 1.0f);
		this.color = color;
		this.attenuation = attenuation;
		this.coneDirection = direction;
		this.coneAngle = angle;
	}

	public Vector3f getPosition() {
		return transform.getPosition();
	}

	public void setPosition(Vector3f position) {
		this.transform.setPosition(position);
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
	
	public void setParent(Transform parent){
		transform.setParent(parent);
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
