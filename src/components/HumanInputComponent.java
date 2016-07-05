package components;

import input.InputContext;

public abstract class HumanInputComponent implements Component{
	protected InputContext context;
	
	public HumanInputComponent(InputContext context){
		this.context = context;
	}

	@Override
	public abstract void update();
}
