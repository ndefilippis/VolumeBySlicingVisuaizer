package util;
import entities.Camera;
import terrain.Terrain;
import vector.Matrix4f;
import vector.Quaternion;
import vector.Vector2f;
import vector.Vector3f;

public class Utils {
	public static Matrix4f createTransformationMatrix(Vector3f translation, Quaternion orientation, float scale){
	 	Matrix4f matrix = new Matrix4f();
	 	matrix.setIdentity();
	 	Matrix4f.translate(translation, matrix, matrix);
	 	Matrix4f rotationMatrix = orientation.toMatrix4f();
	 	Matrix4f.mul(matrix, rotationMatrix, matrix);
	 	Matrix4f.scale(new Vector3f(scale, scale, scale), matrix, matrix);
	 	return matrix;
	 }
	 
	 
	 public static Matrix4f createViewMatrix(Camera camera){
	 	Matrix4f viewMatrix = new Matrix4f();
	 	viewMatrix.setIdentity();
	 	Matrix4f.rotate((float)Math.toRadians(camera.getRoll()), new Vector3f(0, 0, 1), viewMatrix, viewMatrix);
	 	Matrix4f.rotate((float)Math.toRadians(camera.getPitch()), new Vector3f(1, 0, 0), viewMatrix, viewMatrix);
	 	Matrix4f.rotate((float)Math.toRadians(camera.getYaw()), new Vector3f(0, 1, 0), viewMatrix, viewMatrix);

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
	
	 public static Terrain getTerrain(Terrain[] t, float x, float z){
			int i = (int)(x/1600f + 2 );
			int j = (int)(z/1600f + 2 );
			if(i >= 0 && j >= 0 && i < 5 && j < 5){
				return t[i*5 + j];
			}
			return null;
		}
		
		public static float getTerrainHeight(Terrain[] t, float x, float z){
			int i = (int)(x/1600f + 2 );
			int j = (int)(z/1600f + 2 );
			if(i >= 0 && j >= 0 && i < 5 && j < 5){
				return t[i*5 + j].getHeightOfTerrain(x, z);
			}
			return 0;
		}
}
