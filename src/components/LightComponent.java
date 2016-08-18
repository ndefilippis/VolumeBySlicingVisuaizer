package components;

import entities.Light;

public class LightComponent implements Component{
	public Light light;
	
	public LightComponent(Light light){
		this.light = light;
	}
}
