package collision;

import models.ModelData;
import util.AABB;
import util.Utils;
import vector.Vector3f;

public class CollisionModel {
	private float[] vertices;
    private float[] normals;
    private int[] indices;
    private Vector3f[] points;
    private AABB boundingBox;
	
	public CollisionModel(ModelData modelData){
		this.vertices = modelData.getVertices();
		this.normals = modelData.getNormals();
		this.indices = modelData.getIndices();
		boundingBox = Utils.getBoundingBox(vertices);
	}

	    public float[] getVertices() {
	        return vertices;
	    }
	    public float[] getNormals() {
	        return normals;
	    }
	 
	    public int[] getIndices() {
	        return indices;
	    }
		
		public AABB getBoundingBox(){
			return boundingBox;
		}
	 	
	 	public Vector3f[] getPoints(){
	 		if(points != null){
	 			return points;
	 		}
	 		points = new Vector3f[indices.length];
	 		for(int i = 0; i < indices.length; i++){
	 			float x = vertices[3*indices[i]+0];
	 			float y = vertices[3*indices[i]+1];
	 			float z = vertices[3*indices[i]+2];
	 			points[i] = new Vector3f(x, y, z);
	 		}
	 		return points;
	 	}
	}
