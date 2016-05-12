package entities;
import models.TexturedModel;
import vector.Quaternion;
import vector.Vector3f;

public class Entity {
	
	private TexturedModel texturedModel;
	private Vector3f position;
	//private float rotX, rotY, rotZ;
	private Quaternion orientation;
	private float scale;
	private float height = 0;
	
	public Entity(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
		this.texturedModel = model;
		this.position = position;
		//this.rotX = rotX;
		//this.rotY = rotY;
		//this.rotZ = rotZ;
		this.orientation = Quaternion.setFromEulerAngles(rotX, rotY, rotZ);
		this.scale = scale;
	}
	
	public Entity(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale, float height) {
		this.texturedModel = model;
		this.position = position;
		//this.rotX = rotX;
		//this.rotY = rotY;
		//this.rotZ = rotZ;
		this.orientation = Quaternion.setFromEulerAngles(rotX, rotY, rotZ);
		this.scale = scale;
		this.height = height;
	}

	public void increasePosition(float dx, float dy, float dz){
		this.position.x += dx;
		this.position.y += dy;
		this.position.z += dz;
	}
	
	public void rotate(Quaternion q){
		q.normalise();
		orientation = Quaternion.mul(orientation, q, orientation);
		orientation.normalise();
	}
	
	/*public void increaseRotation(float dx, float dy, float dz){
		this.rotX += dx;
		this.rotY += dy;
		this.rotZ += dz;
	}*/

	public TexturedModel getTexturedModel() {
		return texturedModel;
	}

	public void setTexturedModel(TexturedModel model) {
		this.texturedModel = model;
	}

	public Vector3f getPosition() {
		return position;
	}

	public void setPosition(Vector3f position) {
		this.position = position;
	}

	/*public float getRotX() {
		return rotX;
	}

	public void setRotX(float rotX) {
		this.rotX = rotX;
	}

	public float getRotY() {
		return rotY;
	}

	public void setRotY(float rotY) {
		this.rotY = rotY;
	}

	public float getRotZ() {
		return rotZ;
	}

	public void setRotZ(float rotZ) {
		this.rotZ = rotZ;
	}*/

	public Quaternion getOrientation(){
		return orientation;
	}
	
	public void setOrientation(Quaternion q){
		orientation = q;
	}

	public float getScale() {
		return scale;
	}

	public void setScale(float scale) {
		this.scale = scale;
	}

	public float getHeight() {
		return height;
	}
	
	
}
