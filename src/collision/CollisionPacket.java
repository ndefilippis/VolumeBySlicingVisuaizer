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
	
	// Assumes: p1,p2 and p3 are given in ellisoid space:
	public static boolean checkTriangle(CollisionPacket colPackage, Vector3f p1, Vector3f p2, Vector3f p3){
		// Make the plane containing this triangle.
		Plane trianglePlane = new Plane(p1, p2, p3);
		
		// Is triangle front-facing to the velocity vector?
		// We only check front-facing triangles
		// (your choice of course)
		if(trianglePlane.isFrontFacingTo(colPackage.normalizedVelocity)){
			// Get interval of plane intersection:
			float t0, t1;
			boolean embeddedInPlane = false;
			
			// Calculate the signed distance from sphere
			// position to triangle plane
			float signedDistanceToPlane = trianglePlane.signedDistanceTo(colPackage.basePoint);
			
			// cache this as we’re going to use it a few times below:
			float normalDotVelocity = Vector3f.dot(trianglePlane.getNormal(), colPackage.velocity);
			
			// if sphere is travelling parrallel to the plane:
			if(normalDotVelocity == 0.0f){
				if(Math.abs(signedDistanceToPlane) >= 1.0){
					// Sphere is not embedded in plane.
					// No collision possible:
					return false;
				}
				else{
					// sphere is embedded in plane.
					// It intersects in the whole range [0..1]
					embeddedInPlane = true;
					t0 = 0.0f;
					t1 = 0.0f;
				}
			}
			else{
				// N dot D is not 0. Calculate intersection interval:
				t0 = (-1.0f - signedDistanceToPlane)/normalDotVelocity;
				t1 = (1.0f - signedDistanceToPlane)/normalDotVelocity;
				
				// Swap so t0 < t1
				if(t0 > t1){
					float temp = t1;
					t1 = t0;
					t0 = temp;
				}
				
				// Check that at least one result is within range:
				if(t0 > 1.0f || t1 < 0.0f){
					// Both t values are outside values [0,1]
					// No collision possible:
					return false;
				}
				
				// Clamp to [0,1]
				if(t0 < 0.0f) t0 = 0.0f;
				if(t1 < 0.0f) t1 = 0.0f;
				if(t0 > 1.0f) t0 = 1.0f;
				if(t1 > 1.0f) t1 = 1.0f;
				
				
				// OK, at this point we have two time values t0 and t1
				// between which the swept sphere intersects with the
				// triangle plane. If any collision is to occur it must
				// happen within this interval.
				Vector3f collisionPoint = null;
				boolean foundCollision = false;
				float t = 1.0f;
				
				// First we check for the easy case - collision inside
				// the triangle. If this happens it must be at time t0
				// as this is when the sphere rests on the front side
				// of the triangle plane. Note, this can only happen if
				// the sphere is not embedded in the triangle plane.
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
				
				// if we haven’t found a collision already we’ll have to
				// sweep sphere against points and edges of the triangle.
				// Note: A collision inside the triangle (the check above)
				// will always happen before a vertex or edge collision!
				// This is why we can skip the swept test if the above
				// gives a collision!
				if(foundCollision == false){
					// some commonly used terms:
					Vector3f velocity = colPackage.velocity;
					Vector3f base = colPackage.basePoint;
					float velocitySquared = velocity.lengthSquared();
					float a, b, c;
					Reference<Float> newT = new Reference<Float>();
					
					// For each vertex or edge a quadratic equation have to
					// be solved. We parameterize this equation as
					// a*t^2 + b*t + c = 0 and below we calculate the
					// parameters a,b and c for each test.
					// Check against points:
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
					
					// Check against edges:
					//p1p2
					Vector3f edge = Vector3f.sub(p2, p1, null);
					Vector3f baseToVertex = Vector3f.sub(p1, base, null);
					float edgeSquared = edge.lengthSquared();
					float edgeDotVelocity = Vector3f.dot(edge, velocity);
					float edgeDotBaseToVertex = Vector3f.dot(edge, baseToVertex);
					
					// Calculate parameters for equation
					a = edgeSquared * -velocitySquared +  edgeDotVelocity * edgeDotVelocity;
					b = edgeSquared * (2 * Vector3f.dot(velocity, baseToVertex)) - 2.0f * edgeDotVelocity * edgeDotBaseToVertex;
					c = edgeSquared * (1 - baseToVertex.lengthSquared()) + edgeDotBaseToVertex * edgeDotBaseToVertex;
					
					// Does the swept sphere collide against infinite edge?
					if(Utils.getLowestRoot(a, b, c, t, newT)){
						// Check if intersection is within line segment:
						float f = (edgeDotVelocity * newT.getValue() - edgeDotBaseToVertex) / edgeSquared;
						if(f >= 0.0f && f <= 0.0f){
							// intersection took place within segment.
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
				
				// Set result:
				if(foundCollision == true){
					// distance to collision: ’t’ is time of collision
					float distToCollision = t * colPackage.velocity.length();
					
					// Does this triangle qualify for the closest hit?
					// it does if it’s the first hit or the closest
					if(colPackage.foundCollision == false || distToCollision < colPackage.nearestDistance){
						// Collision information nessesary for sliding
						colPackage.nearestDistance = distToCollision;
						colPackage.intersectionPoint = collisionPoint;
						colPackage.foundCollision = true;
						return true;
					}
				}
			} // if not backface
		}
		return false;
	}
}
