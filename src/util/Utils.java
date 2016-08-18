package util;
import java.util.List;
import java.util.Random;

import entities.Camera;
import terrains.Terrain;
import vector.Matrix4f;
import vector.Vector2f;
import vector.Vector3f;

public class Utils {
	private static Random random = new Random();
	 
	public static Matrix4f createTransformationMatrix(Vector2f translation, Vector2f scale) {
		Matrix4f matrix = new Matrix4f();
		matrix.setIdentity();
		Matrix4f.translate(translation, matrix, matrix);
		Matrix4f.scale(new Vector3f(scale.x, scale.y, 1f), matrix, matrix);
		return matrix;
	}
	 
	 
	 public static Matrix4f createViewMatrix(Camera camera){
	 	Matrix4f viewMatrix = new Matrix4f();
	 	viewMatrix.setIdentity();
	 	Matrix4f.mul(camera.getOrientation().toMatrix4f(), viewMatrix, viewMatrix);

	 	Vector3f cameraPos = camera.getPosition();
	 	Vector3f negCameraPos = cameraPos.negate(new Vector3f());
	 	Matrix4f.translate(negCameraPos, viewMatrix, viewMatrix);
	 	return viewMatrix;
	 }
	 
	 public static float barryCentric(Vector3f p1, Vector3f p2, Vector3f p3, Vector2f pos) {
			float det = (p2.z - p3.z) * (p1.x - p3.x) + (p3.x - p2.x) * (p1.z - p3.z);
			float l1 = ((p2.z - p3.z) * (pos.x - p3.x) + (p3.x - p2.x) * (pos.y - p3.z)) / det;
			float l2 = ((p3.z - p1.z) * (pos.x - p3.x) + (p1.x - p3.x) * (pos.y - p3.z)) / det;
			float l3 = 1.0f - l1 - l2;
			return l1 * p1.y + l2 * p2.y + l3 * p3.y;
		}
		
	public static boolean isPointInTriangle(Vector3f point, Vector3f pa, Vector3f pb, Vector3f pc){
		Vector3f e10 = Vector3f.sub(pb, pa, null);
		Vector3f e20 = Vector3f.sub(pc, pa, null);
		
		float a = Vector3f.dot(e10, e10);
		float b = Vector3f.dot(e10, e20);
		float c = Vector3f.dot(e20, e20);
		float ac_bb = (a * c) - (b * b);
		
		Vector3f vp = new Vector3f(point.x - pa.x, point.y - pa.y, point.z - pa.z);
		float d = Vector3f.dot(vp,e10);
		float e = Vector3f.dot(vp, e20);
		float x = (d * c) - (e * b);
		float y = (e * a) - (d * b);
		float z = x + y - ac_bb;
		
		return (((int)z & ~ (int)x | (int) y) & 0x80000000) != 0;
	}
	
	public static boolean getLowestRoot(float a, float b, float c, float maxR, Reference<Float> root){
		float determinant = b * b - 4.0f *a * c;
		if(determinant < 0.0f) return false;
		
		float sqrtD = (float)Math.sqrt(determinant);
		float r1 = (-b - sqrtD) / (2 * a);
		float r2 = (-b + sqrtD) / (2 * a);
		if(r1 > r2){
			float temp = r1;
			r1 = r2;
			r2 = temp;
		}
		
		if(r1 > 0 && r1 < maxR){
			root.setValue(r1);
			return true;
		}
		if(r2 > 0 && r2 < maxR){
			root.setValue(r2);
			return true;
		}
		return false;
	}
	
	public static Terrain getTerrain(List<Terrain> t, float x, float z){
		return getTerrain(t.toArray(new Terrain[t.size()]), x, z);
	}
	
	public static float getTerrainHeight(List<Terrain> t, float x, float z){
		return getTerrainHeight(t.toArray(new Terrain[t.size()]), x, z);
	}
	
	 public static Terrain getTerrain(Terrain[] t, float x, float z){
			int i = (int)(x/Terrain.SIZE);
			int j = (int)(z/Terrain.SIZE);
			int length = (int)Math.sqrt(t.length);
			if(i >= 0 && j >= 0 && i < length && j < length){
				return t[i*length + j];
			}
			return null;
		}
		
		public static float getTerrainHeight(Terrain[] t, float x, float z){
			int i = (int)(x/Terrain.SIZE);
			int j = (int)(z/Terrain.SIZE);
			int length = (int)Math.sqrt(t.length);
			if(i >= 0 && j >= 0 && i < length && j < length){
				return t[i*length + j].getHeightOfTerrain(x, z);
			}
			return -Float.MAX_VALUE;
		}

		public static Vector3f randomVector(float minX, float maxX, float minY, float maxY, float minZ, float maxZ) {
			float x = random(minX, maxX);
			float y = random(minY, maxY);
			float z = random(minZ, maxZ);
			return new Vector3f(x, y, z);
		}
		
		public static float random(float min, float max){
			return random.nextFloat()*(max - min) + min;
		}
		
		public static float getBoundingSphere(float[] positions){
			float biggestSquare = 0;
			for(int i = 0; i < positions.length/3; i++){
				float x = positions[3*i + 0];
				float y = positions[3*i + 1];
				float z = positions[3*i + 2];
				float distSq = x*x + y*y + z*z;
				if(distSq > biggestSquare){
					biggestSquare = distSq;
				}
			}
			return (float)Math.sqrt(biggestSquare);
		}
		
		public static AABB getBoundingBox(float[] positions){
			float minX = Float.MAX_VALUE;
			float minY = Float.MAX_VALUE;
			float minZ = Float.MAX_VALUE;
			
			float maxX = -Float.MAX_VALUE;
			float maxY = -Float.MAX_VALUE;
			float maxZ = -Float.MAX_VALUE;
			
			for(int i = 0; i < positions.length/3; i++){
				float x = positions[3*i + 0];
				float y = positions[3*i + 1];
				float z = positions[3*i + 2];

				if(x < minX) minX = x;
				if(x > maxX) maxX = x;
				
				if(y < minY) minY = y;
				if(y > maxY) maxY = y;
				
				if(z < minZ) minZ = z;
				if(z > maxZ) maxZ = z;
			}
			return new AABB(new Vector3f(minX, minY, minZ), new Vector3f(maxX, maxY, maxZ));
		}


		public static float clamp(float value, float min, float max) {
			return Math.max(Math.min(value, max), min);
		}

}
