package util;

import vector.Matrix4f;
import vector.Quaternion;
import vector.Vector3f;
import vector.Vector4f;

public class Transform {
	private Vector3f position;
	private Quaternion orientation;
	private float scale;
	private Transform parentTransform;
	
	public Transform(Vector3f position, Quaternion orientation, float scale){
		this.position = position;
		this.orientation = orientation;
		this.scale = scale;
	}
	
	public Transform(Vector3f position, Quaternion orientation, float scale, Transform parentTransform){
		this.position = position;
		this.orientation = orientation;
		this.scale = scale;
		this.parentTransform = parentTransform;
	}
	
	public Transform(Transform other) {
		this.position = other.position;
		this.orientation = other.orientation;
		this.scale = other.scale;
		this.parentTransform = other.parentTransform;
	}

	public Transform() {
		this.position = new Vector3f();
		this.orientation = new Quaternion();
		this.scale = 1f;
	}

	public void setParent(Transform parent){
		this.parentTransform = parent;
	}
	
	private Matrix4f getMatrix(){
		Matrix4f mat = new Matrix4f();
		mat.translate(position);
		Matrix4f.mul(mat, orientation.toMatrix4f(), mat);
		mat.scale(new Vector3f(scale, scale, scale));
		return mat;
	}
	
	public void setPosition(Vector3f position){
		this.position = position;
	}
	
	public void setOrientation(Quaternion q){
		this.orientation = q;
	}
	
	public void addPosition(Vector3f toAdd){
		Vector3f.add(position, toAdd, position);
	}
	
	public void rotate(Quaternion q){
		Quaternion.mul(orientation, q, orientation);
	}
	
	public void setOrientation(Vector3f axis, float angle){
		this.orientation = Quaternion.AxisAngle(axis, angle);
	}
	
	public void setScale(float scale){
		this.scale = scale;
	}
	
	public Vector3f getPosition(){
		Matrix4f world = getWorldMatrix();
		Vector4f v = new Vector4f(0, 0, 0, 1);
		Matrix4f.transform(world, v, v);
		return new Vector3f(v.x/v.w, v.y/v.w, v.z/v.w);
	}
	
	public Quaternion getOrientation(){
		return orientation;
	}
	
	public float getScale(){
		return scale;
	}
	
	public Vector3f transform(Vector3f point){
		Vector4f v = new Vector4f(point.x, point.y, point.z, 1.0f);
		Vector4f newPoint = Matrix4f.transform(getWorldMatrix(), v, null);
		Vector3f pos = new Vector3f(newPoint.x, newPoint.y, newPoint.z);
		pos.scale(1.0f/newPoint.w);
		return pos;
	}
	
	public Matrix4f getWorldMatrix(){
		if(parentTransform == null){
			return getMatrix();
		}
		return Matrix4f.mul(parentTransform.getWorldMatrix(), getMatrix(), null);
	}
	
	public Matrix4f getInvMatrix(){
		Matrix4f mat = new Matrix4f();
		mat.translate(position.negate(null));
		Matrix4f.mul(orientation.negate(null).toMatrix4f(), mat, mat);
		float inv_scale = 1.0f / scale;
		mat.scale(new Vector3f(inv_scale, inv_scale, inv_scale));
		return mat;
	}
}
