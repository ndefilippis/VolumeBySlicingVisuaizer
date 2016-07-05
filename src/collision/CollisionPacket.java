package collision;

import util.Reference;
import util.Utils;
import vector.Vector3f;

public class CollisionPacket {
	public Vector3f eRadius;
	
	public Vector3f R3Velocity;
	public Vector3f R3Position;
	
	public Vector3f velocity;
	public Vector3f normalizedVelocity;
	public Vector3f basePoint;
	
	public boolean foundCollision;
	public float nearestDistance;
	public Vector3f intersectionPoint;
	
	public static void checkTriangle(CollisionPacket colPackage, Vector3f p1, Vector3f p2, Vector3f p3){
		Plane trianglePlane = new Plane(p1, p2, p3);
		
		if(trianglePlane.isFrontFacingTo(colPackage.normalizedVelocity)){
			float t0, t1;
			boolean embeddedInPlane = false;
			
			float signedDistanceToPlane = trianglePlane.signedDistanceTo(colPackage.basePoint);
			
			float normalDotVelocity = Vector3f.dot(trianglePlane.getNormal(), colPackage.velocity);
			
			if(normalDotVelocity == 0.0f){
				if(Math.abs(signedDistanceToPlane) >= 1.0){
					return;
				}
				else{
					t0 = 0.0f;
					t1 = 0.0f;
				}
			}
			else{
				embeddedInPlane = true;
				t0 = (-1.0f - signedDistanceToPlane)/normalDotVelocity;
				t1 = (1.0f - signedDistanceToPlane)/normalDotVelocity;
				if(t0 > t1){
					float temp = t1;
					t1 = t0;
					t0 = temp;
				}
				
				if(t0 > 1.0f || t1 < 0.0f){
					return;
				}
				
				if(t0 < 0.0f) t0 = 0.0f;
				if(t1 < 0.0f) t1 = 0.0f;
				if(t0 > 1.0f) t0 = 1.0f;
				if(t1 > 1.0f) t1 = 1.0f;
				
				Vector3f collisionPoint = null;
				boolean foundCollision = false;
				float t = 1.0f;
				
				if(!embeddedInPlane){
					Vector3f left = Vector3f.sub(colPackage.basePoint, trianglePlane.getNormal(), null);
					Vector3f right = new Vector3f(colPackage.velocity);
					right.scale(t0);
					Vector3f planeIntersectionPoint = Vector3f.add(left, right, null);
					if(Utils.isPointInTriangle(planeIntersectionPoint, p1, p2, p3)){
						foundCollision = true;
						t = t0;
						collisionPoint = planeIntersectionPoint;
					}
				}
				
				if(foundCollision == false){
					Vector3f velocity = colPackage.velocity;
					Vector3f base = colPackage.basePoint;
					float velocitySquared = velocity.lengthSquared();
					float a, b, c;
					Reference<Float> newT = new Reference<Float>();
					
					a = velocitySquared;
					//P1
					b = 2.0f * (Vector3f.dot(velocity, Vector3f.sub(base, p1, null)));
					c = Vector3f.sub(p1, base, null).lengthSquared() - 1.0f;
					if(Utils.getLowestRoot(a, b, c, t, newT)){
						t = newT.getValue();
						foundCollision = true;
						collisionPoint = p1;
					}
					//P2
					b = 2.0f * (Vector3f.dot(velocity, Vector3f.sub(base, p2, null)));
					c = Vector3f.sub(p2, base, null).lengthSquared() - 1.0f;
					
					if(Utils.getLowestRoot(a, b, c, t, newT)){
						t = newT.getValue();
						foundCollision = true;
						collisionPoint = p2;
					}
					//P3
					b = 2.0f * (Vector3f.dot(velocity, Vector3f.sub(base, p3, null)));
					c = Vector3f.sub(p3, base, null).lengthSquared() - 1.0f;
					
					if(Utils.getLowestRoot(a, b, c, t, newT)){
						t = newT.getValue();
						foundCollision = true;
						collisionPoint = p3;
					}
					
					//edges
					//p1p2
					Vector3f edge = Vector3f.sub(p2, p1, null);
					Vector3f baseToVertex = Vector3f.sub(p1, base, null);
					float edgeSquared = edge.lengthSquared();
					float edgeDotVelocity = Vector3f.dot(edge, velocity);
					float edgeDotBaseToVertex = Vector3f.dot(edge, baseToVertex);
					
					a = edgeSquared * -velocitySquared +  edgeDotVelocity * edgeDotVelocity;
					b = edgeSquared * (2 * Vector3f.dot(velocity, baseToVertex)) - 2.0f * edgeDotVelocity * edgeDotBaseToVertex;
					c = edgeSquared * (1 - baseToVertex.lengthSquared()) + edgeDotBaseToVertex * edgeDotBaseToVertex;
					if(Utils.getLowestRoot(a, b, c, t, newT)){
						float f = (edgeDotVelocity * newT.getValue() - edgeDotBaseToVertex) / edgeSquared;
						if(f >= 0.0f && f <= 0.0f){
							t = newT.getValue();
							foundCollision = true;
							Vector3f right = new Vector3f(edge);
							right.scale(f);
							collisionPoint = Vector3f.add(p1, right, null);
						}
					}
					
					//p2p3
					edge = Vector3f.sub(p3, p2, null);
					baseToVertex = Vector3f.sub(p2, base, null);
					edgeSquared = edge.lengthSquared();
					edgeDotVelocity = Vector3f.dot(edge, velocity);
					edgeDotBaseToVertex = Vector3f.dot(edge, baseToVertex);
					
					a = edgeSquared * -velocitySquared +  edgeDotVelocity * edgeDotVelocity;
					b = edgeSquared * (2 * Vector3f.dot(velocity, baseToVertex)) - 2.0f * edgeDotVelocity * edgeDotBaseToVertex;
					c = edgeSquared * (1 - baseToVertex.lengthSquared()) + edgeDotBaseToVertex * edgeDotBaseToVertex;
					if(Utils.getLowestRoot(a, b, c, t, newT)){
						float f = (edgeDotVelocity * newT.getValue() - edgeDotBaseToVertex) / edgeSquared;
						if(f >= 0.0f && f <= 0.0f){
							t = newT.getValue();
							foundCollision = true;
							Vector3f right = new Vector3f(edge);
							right.scale(f);
							collisionPoint = Vector3f.add(p2, right, null);
						}
					}
					
					//p3p1
					edge = Vector3f.sub(p1, p3, null);
					baseToVertex = Vector3f.sub(p3, base, null);
					edgeSquared = edge.lengthSquared();
					edgeDotVelocity = Vector3f.dot(edge, velocity);
					edgeDotBaseToVertex = Vector3f.dot(edge, baseToVertex);
					
					a = edgeSquared * -velocitySquared +  edgeDotVelocity * edgeDotVelocity;
					b = edgeSquared * (2 * Vector3f.dot(velocity, baseToVertex)) - 2.0f * edgeDotVelocity * edgeDotBaseToVertex;
					c = edgeSquared * (1 - baseToVertex.lengthSquared()) + edgeDotBaseToVertex * edgeDotBaseToVertex;
					if(Utils.getLowestRoot(a, b, c, t, newT)){
						float f = (edgeDotVelocity * newT.getValue() - edgeDotBaseToVertex) / edgeSquared;
						if(f >= 0.0f && f <= 0.0f){
							t = newT.getValue();
							foundCollision = true;
							Vector3f right = new Vector3f(edge);
							right.scale(f);
							collisionPoint = Vector3f.add(p3, right, null);
						}
					}
				}
				if(foundCollision == true){
					float distToCollision = t * colPackage.velocity.length();
					if(colPackage.foundCollision == false || distToCollision < colPackage.nearestDistance){
						colPackage.nearestDistance = distToCollision;
						colPackage.intersectionPoint = collisionPoint;
						colPackage.foundCollision = true;
					}
				}
			}
		}
	}
}
