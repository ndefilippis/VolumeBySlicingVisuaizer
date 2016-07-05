package renderEngine;

import java.util.Arrays;

import models.ModelData;
import vector.Vector2f;
import vector.Vector3f;

public class SlicingModelLoader {
	
	private static final int subintervals = 24;
	private static final float start = -(float)Math.sqrt(2*Math.PI);
	private static final float end = (float)Math.sqrt(2*Math.PI);

	private static float f(float x){
		return (float)(Math.sin(x*x)*x*x*x/6f);
	}
	
	private static float g(float x){
		return (float)(2f*Math.sqrt(2*Math.PI - x*x));
	}
	
	private static int semicircleSubs = 64;
	
	private static int verticiesTriangle = 12;
	private static int verticiesSquare = 16;
	private static int verticiesSemicircle = semicircleSubs * 4;
	
	private static Vector3f down = new Vector3f(0, -1, 0);
	private static Vector3f left = new Vector3f(-1, 0, 0);
	private static Vector3f right = new Vector3f(-1, 0, 0);
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
	
	private static Vector3f[] circleNormals = createCircleNormals(semicircleSubs);
	
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
	
	private static int[] circleIndices = createCircleIndicies(semicircleSubs);
	
	private static int currentVertices = verticiesSemicircle;
	private static int[] currentIndicies = circleIndices;
	private static Vector3f[] currentNormals = circleNormals;
	
	public static ModelData load(){
		Vector3f[] vertices = new Vector3f[(subintervals + 1) * currentVertices];
		Vector3f[] normals = new Vector3f[(subintervals + 1) * currentVertices];
		Vector2f[] textures = new Vector2f[(subintervals + 1) * currentVertices];
		int[] indicies = new int[(subintervals + 1) * currentIndicies.length/3 * 4];
		
		float deltaX = (end - start) / subintervals;		
		for(int index = 0; index <= subintervals; index++){
			float x = start + deltaX * index;
			float y = g(x) - f(x);
			for(int j = 0; j < 4; j++){
				for(int k = 0; k < semicircleSubs; k++){
					float z = k*y/(semicircleSubs-1);
					float y2 = (float)Math.sqrt(y*y/4 - (y/2 - z) * (y/2 - z));
					vertices[currentVertices*index + semicircleSubs*j + k] = new Vector3f(x, y2, f(x)+z);
				}
				
			}
			for(int i = 0; i < currentVertices; i++){
				normals[currentVertices*index + i] = currentNormals[i]; 
			}
			for(int j = 0; j < 4; j++){
				for(int k = 0; k < semicircleSubs; k++){
					Vector3f pos = vertices[currentVertices*index + semicircleSubs*j + k];
					textures[currentVertices*index + semicircleSubs*j + k] = new Vector2f(pos.z, -pos.y);
				}
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
		for(int i = 0; i < vertices.length; i++){
			verticesArray[3*i] = vertices[i].x;
			verticesArray[3*i+1] = vertices[i].y;
			verticesArray[3*i+2] = vertices[i].z;
			
			normalsArray[3*i] = normals[i].x;
			normalsArray[3*i+1] = normals[i].y;
			normalsArray[3*i+2] = normals[i].z;
			
			texturesArray[2*i] = textures[i].x;
			texturesArray[2*i+1] = textures[i].y;
		}
		ModelData data = new ModelData(verticesArray, texturesArray, normalsArray, indicies);
		return data;
	}
	
	private static int[] createCircleIndicies(int subs){
		int up = subs;
		int left = 2*subs;
		int right = 3*subs;
		int next = 4*subs;
		int[] indices = new int[subs * 3 * 2 * (subs-2)];
		int index = 0;
		for(int i = 0; i < subs; i++){
			indices[index++] = i + 1 + up;
			indices[index++] = i + 1 + next + up;
			indices[index++] = i;
			
			indices[index++] = i + next;
			indices[index++] = i ;
			indices[index++] = i + 1 + next + up;
		}
		for(int i = 0; i < subs-2; i++){
			indices[index++] = 0 + left;
			indices[index++] = i+2 + left;
			indices[index++] = i+1 + left;
		}
		for(int i = 0; i < subs-2; i++){
			indices[index++] = 0 + right;
			indices[index++] = i+1 + right;
			indices[index++] = i+2 + right;
		}
		return indices;
	}

	private static Vector3f[] createCircleNormals(int subs) {
		Vector3f[] normals = new Vector3f[subs * 4];
		
		int index = 0;
		
		float delta = 1.0f/subs;
		float y = 0;
		float lasty = 0;
		float[] ys = new float[subs];
		for(int i = 1; i < subs; i++){
			float z = delta * i;
			y = (float)Math.sqrt(0.5*0.5 - (0.5 - z) * (0.5 - z));
			float deltay = y - lasty;
			ys[i-1] = y; 
			lasty = y;
			Vector3f normal = new Vector3f(0, z, -y);
			normal.normalise();
			normals[index] = normal;
			index++;
		}
		normals[index++] = new Vector3f(0, -1, 0);
		normals[index++] = new Vector3f(0, -1, 0);
		for(int i = 1; i < subs; i++){
			float z = delta * i;
			y = (float)Math.sqrt(0.5*0.5 - (0.5 - z) * (0.5 - z));
			float deltay = y - lasty;
			ys[i-1] = y; 
			lasty = y;
			Vector3f normal = new Vector3f(0, z, -y);
			normal.normalise();
			normals[index] = normal;
			index++;
		}
		for(int i = 0; i < subs; i++){
			normals[index++] = new Vector3f(-1, 0, 0);
		}
		for(int i = 0; i < subs; i++){
			normals[index++] = new Vector3f(1, 0, 0);
		}
		return normals;
	}
}
