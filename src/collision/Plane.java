package collision;

import vector.Vector3f;

public class Plane {
	private Vector3f origin;
	private Vector3f normal;
	private float[] equation = new float[4];
	
	public Plane(Vector3f origin, Vector3f normal){
		this.origin = origin;
		this.normal = normal;
		equation[0] = normal.x;
		equation[1] = normal.y;
		equation[2] = normal.z;
		equation[3] = -Vector3f.dot(normal, origin);
	}
	
	public Plane(Vector3f p1, Vector3f p2, Vector3f p3){
		Vector3f.cross(Vector3f.sub(p2, p1, null), Vector3f.sub(p3, p1, null) , normal);
		normal.normalise();
		origin = p1;
		
		equation[0] = normal.x;
		equation[1] = normal.y;
		equation[2] = normal.z;
		equation[3] = -Vector3f.dot(normal, origin);
	}
	
	public boolean isFrontFacingTo(Vector3f direction){
		return Vector3f.dot(normal, direction) <= 0;
	}
	
	public float signedDistanceTo(Vector3f point){
		return Vector3f.dot(point, normal) + equation[3];
	}

	protected Vector3f getOrigin() {
		return origin;
	}

	protected Vector3f getNormal() {
		return normal;
	}

	protected float[] getEquation() {
		return equation;
	}
	
	
}
