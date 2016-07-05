package models;

import util.AABB;

public class Model {
	private int vaoID;
	private int vertexCount;
	private float boundingSphere;
	private AABB boundingBox;

	public Model(int vaoID, int vertexCount, float boundingSphere, AABB boundingBox) {
		this.vaoID = vaoID;
		this.vertexCount = vertexCount;
		this.boundingSphere = boundingSphere;
		this.boundingBox = boundingBox;
		
	}

	public int getVaoID() {
		return vaoID;
	}
	
	public float getBoundingSphere(){
		return boundingSphere;
	}
	
	public AABB getBoundingBox(){
		return boundingBox;
	}

	public int getVertexCount() {
		return vertexCount;
	}
}
