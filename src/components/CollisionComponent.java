package components;

import collision.CollisionModel;

public class CollisionComponent implements Component {
	public CollisionModel collisionModel;
	public boolean isStatic;
	public boolean foundCollision;
	

	public CollisionComponent(CollisionModel collisionModel, boolean isStatic) {
		this.collisionModel = collisionModel;
		this.isStatic = isStatic;;
	}
}
