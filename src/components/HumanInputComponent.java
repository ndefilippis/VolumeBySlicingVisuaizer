package components;

import input.InputContext;

public abstract class HumanInputComponent implements Component{
	InputContext context;
	
	public HumanInputComponent(InputContext context){
		this.context = context;
	}
	
	public InputContext getContext(){
		return context;
	}
}
