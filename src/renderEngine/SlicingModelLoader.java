package renderEngine;

import java.util.Arrays;

import models.ModelData;
import vector.Vector2f;
import vector.Vector3f;

public class SlicingModelLoader {
	
	private static final int subintervals = 1;
	private static final float start = 0.0f;
	private static final float end = 1.0f;

	private static float f(float x){
		return 0;
	}
	
	private static float g(float x){
		return 1;
	}
	
	private static int verticiesTriangle = 12;
	private static int verticiesSquare = 16;
	private static int verticiesSemicircle = 64;
	
	private static Vector3f down = new Vector3f(0, -1, 0);
	private static Vector3f left = new Vector3f(-1, 0, 0);
	private static Vector3f right = new Vector3f(1, 0, 0);
	private static Vector3f up = new Vector3f(0, 1, 0);
	private static Vector3f front = new Vector3f(0, 0, 1);
	private static Vector3f back = new Vector3f(0, 0, -1);
	private static Vector3f slope = (Vector3f)(new Vector3f(0, 0.5f, -1f).normalise());
	private static Vector3f oppslope = (Vector3f)(new Vector3f(0, 0.5f, 1f).normalise());

	
	private static Vector3f[] triangleNormals = {
		slope, slope, oppslope, 
		down, oppslope, down, 
		right, right, right, 
		left, left, left
	};
	
	private static Vector3f[] squareNormals = {
			front, front, back, back,
			down, up, up, down,
			left, left, left, left,
			right, right, right, right
	};
	
	private static int[] triangleIndicies = {
		9, 10, 11, //left
		18, 20, 19, //right
		0, 12, 1,  //front
		1, 12, 13, //front
		3, 5, 15, //bottom
		5, 17, 15, //bottom
		4, 16, 2, //back
		16, 14, 2 //back
	};
	
	private static int[] squareIndicies = {
			1, 0, 16,	//front
			1, 16, 17,  //front
			6, 5, 21,  //top
			6, 21, 22, //top
			3, 2, 19, //back
			2, 18, 19, //back
			4, 7, 20, //bottom
			7, 23, 20, //bottom
			8, 9, 11,  //left
			9, 10, 11, //left
			31, 30, 29, //right
			29, 28, 31 //right
			
		};
	
	private static int currentVertices = verticiesSquare;
	private static int[] currentIndicies = squareIndicies;
	private static Vector3f[] currentNormals = squareNormals;
	
	public static ModelData load(){
		Vector3f[] vertices = new Vector3f[(subintervals + 1) * currentVertices];
		Vector3f[] normals = new Vector3f[(subintervals + 1) * currentVertices];
		Vector2f[] textures = new Vector2f[(subintervals + 1) * currentVertices];
		int[] indicies = new int[(subintervals + 1) * currentIndicies.length/3 * 4];
		
		float deltaX = (end - start) / subintervals;		
		for(int index = 0; index <= subintervals; index++){
			float x = start + deltaX * index;
			float y = g(x) - f(x);
			for(int j = 0; j < currentVertices/4; j++){
				vertices[currentVertices*index + 4*j + 0] = new Vector3f(x, 0, -f(x));
				vertices[currentVertices*index + 4*j + 1] = new Vector3f(x, y, -f(x));
				vertices[currentVertices*index + 4*j + 2] = new Vector3f(x, y, -g(x));
				vertices[currentVertices*index + 4*j + 3] = new Vector3f(x, 0, -g(x));
			}

			for(int i = 0; i < currentVertices; i++){
				normals[currentVertices*index + i] = currentNormals[i]; 
			}
			
			for(int j = 0; j < 4; j++){
				textures[currentVertices*index + 4*j + 0] = new Vector2f(0, 0);
				textures[currentVertices*index + 4*j + 1] = new Vector2f(0, 1);
				textures[currentVertices*index + 4*j + 2] = new Vector2f(1, 1);
				textures[currentVertices*index + 4*j + 3] = new Vector2f(1, 0);	
				
			}
		}
		
		for(int i = 0; i < subintervals; i++){
			for(int j = 0; j < currentIndicies.length; j++){
				indicies[currentIndicies.length*i + j] = currentVertices*i+currentIndicies[j];
			}
		}
		
		float[] verticesArray = new float[(subintervals + 1) * currentVertices * 3];
		float[] normalsArray = new float[(subintervals + 1) * currentVertices * 3];
		float[] texturesArray = new float[(subintervals + 1) * currentVertices * 2];
		float furthest = 0;
		for(int i = 0; i < vertices.length; i++){
			verticesArray[3*i] = vertices[i].x;
			verticesArray[3*i+1] = vertices[i].y;
			verticesArray[3*i+2] = vertices[i].z;
			if(vertices[i].length() > furthest){
				furthest = vertices[i].length();
			}
			
			normalsArray[3*i] = normals[i].x;
			normalsArray[3*i+1] = normals[i].y;
			normalsArray[3*i+2] = normals[i].z;
			
			texturesArray[2*i] = textures[i].x;
			texturesArray[2*i+1] = textures[i].y;
		}
		ModelData data = new ModelData(verticesArray, texturesArray, normalsArray, indicies, furthest);
		return data;
	}
}
