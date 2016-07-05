package components;

import entities.Light;
import vector.Vector3f;

public class LightComponent implements Component{
	public Light light;
	private TransformComponent transformComponent;
	
	public LightComponent(TransformComponent transform, Light light, TransformComponent parent){
		this.light = light;
		this.transformComponent = transform;
		this.transformComponent.setAsChildOf(parent);
	}

	@Override
	public void update() {
	}
	
	@Override
	public ComponentType getType() {
		return ComponentType.LIGHT;
	}
}
