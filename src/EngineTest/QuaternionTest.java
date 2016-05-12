package EngineTest;

import vector.Quaternion;
import vector.Vector3f;
import vector.Vector4f;

public class QuaternionTest {
	public static void main(String[] args){
		Quaternion q1 = new Quaternion();
		
		q1.setFromAxisAngle(new Vector4f(1, 1, 1, (float)(Math.PI/3)));
		System.out.println("Q1: " + q1);
		
		Quaternion q2 = new Quaternion();
		q2.setFromAxisAngle(new Vector4f(1, 1, 1, (float)(Math.PI/3)));
		
		Quaternion q3 = Quaternion.mul(q2, q1, null);
		float theta = (float)Math.acos(q3.getW()) * 2;
		float sinTheta = (float)Math.sin(theta / 2);
		
		Vector3f axis = new Vector3f(q3.getX(), q3.getY(), q3.getZ());
		axis.scale(1 / sinTheta);
		System.out.println(axis + ": " + Math.toDegrees(theta));
	}
}
