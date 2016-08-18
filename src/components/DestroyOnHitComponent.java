package components;

public class DestroyOnHitComponent implements Component {
	public CollisionComponent collision;
	
	public DestroyOnHitComponent(CollisionComponent collison){
		this.collision = collision;
	}


}
