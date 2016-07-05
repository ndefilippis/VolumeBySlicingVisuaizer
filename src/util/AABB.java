package util;

import vector.Matrix4f;
import vector.Vector3f;
import vector.Vector4f;

public class AABB {
	private Vector3f pMin;
	private Vector3f pMax;
	
	public AABB(Vector3f p1, Vector3f p2){
		setCoords(p1, p2);
	}
	
	public AABB(AABB aabb, Transform transformation){
		Vector3f[] verticies = aabb.getVerticies();
		Matrix4f tMatrix = transformation.getWorldMatrix();
		pMin = Vector3f.Infinity();
		pMax = Vector3f.Infinity().negate(null);
		for(int i = 0; i < verticies.length; i++){
			Vector4f v = new Vector4f(verticies[i].x, verticies[i].y, verticies[i].z, 0);
			Matrix4f.transform(tMatrix, v, v);
			verticies[i] = new Vector3f(v.x, v.y, v.z);
			addUnion(verticies[i]);
		}
	}
	
	public AABB() {
		pMin = Vector3f.Infinity();
		pMax = Vector3f.Infinity().negate(null);
	}

	public boolean isInsideZone(Vector3f position){
		return (position.x >= pMin.x && position.x <= pMax.x) &&
				(position.y >= pMin.y && position.y <= pMax.y) &&
				(position.z >= pMin.z && position.z <= pMax.z);
	}
	
	public void setPosition(Vector3f p1, Vector3f p2){
		setCoords(p1, p2);	
	}
	
	public void addUnion(Vector3f point){
		pMin = new Vector3f(Math.min(pMin.x, point.x), Math.min(pMin.y, point.y), Math.min(pMin.z, point.z));
		pMax = new Vector3f(Math.max(pMax.x, point.x), Math.max(pMax.y, point.y), Math.max(pMax.z, point.z));
	}
	
	private void setCoords(Vector3f p1, Vector3f p2){
		pMin = new Vector3f(Math.min(p1.x, p2.x), Math.min(p1.y, p2.y), Math.min(p1.z, p2.z));
		pMax = new Vector3f(Math.max(p1.x, p2.x), Math.max(p1.y, p2.y), Math.max(p1.z, p2.z));
	}
	
	public boolean isIntersectingWith(AABB other){
		if(pMax.x < other.pMin.x ) return false;
		if(pMin.x > other.pMax.x ) return false;
		if(pMax.y < other.pMin.y ) return false;
		if(pMin.y > other.pMax.y ) return false;
		if(pMax.z < other.pMin.z ) return false;
		if(pMin.z > other.pMax.z ) return false;
		return true;
	}
	
	public Vector3f getScale(){
		return new Vector3f(pMax.x - pMin.x, pMax.y - pMin.y, pMax.z - pMin.z);
	}
	
	public Vector3f getMin(){
		return pMin;
	}
	
	public Vector3f getMax(){
		return pMax;
	}
	
	public Vector3f[] getVerticies(){
		Vector3f[] verticies = {
				new Vector3f(pMin.x, pMin.y, pMin.z),
				new Vector3f(pMin.x, pMin.y, pMax.z),
				new Vector3f(pMin.x, pMax.y, pMin.z),
				new Vector3f(pMin.x, pMax.y, pMax.z),
				
				new Vector3f(pMax.x, pMin.y, pMin.z),
				new Vector3f(pMax.x, pMin.y, pMax.z),
				new Vector3f(pMax.x, pMax.y, pMin.z),
				new Vector3f(pMax.x, pMax.y, pMax.z),
			};
		return verticies;
	}
	
	public Vector3f getCenter(){
		return new Vector3f((pMin.x + pMax.x)/2.0f, (pMin.y + pMax.y)/2.0f, (pMin.z + pMax.z)/2.0f);
	}
}
