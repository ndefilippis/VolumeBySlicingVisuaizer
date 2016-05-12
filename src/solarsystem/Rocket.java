package solarsystem;
import vector.Vector3f;


public class Rocket {
	private double speed;
	private double mass = 100;
	private double lastTime;
	private float initialTime;
	private float angle;
	
	private Vector3f startPosition;
	private Vector3f lastPosition3D;
	private Vector3f velocity3D;
	
	public Rocket(double speed, double mass){
		this.speed = speed;
		this.mass = mass;
	}
	
	public void launch(Vector3f start, Vector3f initialDirection, double launchTime){
		this.lastPosition3D = start;
		this.startPosition = start;
		this.lastTime = launchTime;
		initialTime = (float)launchTime;
		lastPosition3D = new Vector3f((float)start.x, (float)start.y, (float)start.z);
		angle = (float)Math.atan2(start.z, start.x);
		velocity3D = (Vector3f) initialDirection.normalise(new Vector3f()).scale((float)speed);
	}
	
	public Vector3f getPostion(double time){
		Vector3f force = new Vector3f();
		
		for(Planet p : Planet.values()){
			double magnitude = -1.98136E-25*this.mass*p.mass;
			Vector3f dist = Vector3f.sub(lastPosition3D, p.positionAtTime(time), new Vector3f());
			magnitude /= dist.lengthSquared();
			dist = (Vector3f)dist.normalise().scale((float)magnitude);
			Vector3f.add(dist, force, force);
		}
		Vector3f wow = (Vector3f)(force.scale(1f/(float)this.mass*(float)(time - lastTime)));
		Vector3f.add(velocity3D, wow, velocity3D);
		Vector3f.add(lastPosition3D, (Vector3f)velocity3D.scale((float)(time - lastTime)), lastPosition3D);
		lastTime = time;
		return new Vector3f(
			(float)(startPosition.x+speed*Math.cos(angle)*(time-initialTime)),
			0f, 
			(float)(startPosition.z+speed*Math.sin(angle)*(time-initialTime)));
		//return new Vector3f(lastPosition3D);
	}

	public float getAngle() {
		return angle;
	}
}
