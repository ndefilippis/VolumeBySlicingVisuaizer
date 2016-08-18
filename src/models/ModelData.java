package models;

public class ModelData {
 
    private float[] vertices;
    private float[] textureCoords;
    private float[] normals;
    private float[] tangents;
    private int[] indices;
 
    public ModelData(float[] vertices, float[] textureCoords, float[] normals, int[] indices) {
        this.vertices = vertices;
        this.textureCoords = textureCoords;
        this.normals = normals;
        this.indices = indices;
    }
    
    public ModelData(float[] vertices, float[] textureCoords, float[] normals, float[] tangents, int[] indices) {
        this.vertices = vertices;
        this.textureCoords = textureCoords;
        this.normals = normals;
        this.indices = indices;
    }
 
    public float[] getVertices() {
        return vertices;
    }
 
    public float[] getTextureCoords() {
        return textureCoords;
    }
    
    public float[] getTangents() {
        return tangents;
    }
 
    public float[] getNormals() {
        return normals;
    }
 
    public int[] getIndices() {
        return indices;
    }
	
 
}