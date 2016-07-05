package components;

import util.Transform;
import vector.Quaternion;
import vector.Vector3f;

public class TransformComponent implements Component{
	public Transform transform;
	
	public TransformComponent(){
		this.transform = new Transform();
	}
	
	public TransformComponent(Transform t){
		this.transform = t;
	}
	
	public TransformComponent(Vector3f position, Quaternion rot, float scale){
		this.transform = new Transform(position, rot, scale);
	}
	
	public void setAsChildOf(TransformComponent parent){
		transform.setParent(parent.transform);
	}
	
	public void setAsParentOf(TransformComponent child){
		child.transform.setParent(transform);
	}

	@Override
	public void update() {
	}
	
	@Override
	public ComponentType getType() {
		return ComponentType.TRANSFORM;
	}
}
