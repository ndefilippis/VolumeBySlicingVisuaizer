package components;

public class BulletComponent implements Component{
	public float maxLifetime;
	public float currentLifetime;
	
	public BulletComponent(float maxLifetime){
		this.maxLifetime = maxLifetime;
	}
}
