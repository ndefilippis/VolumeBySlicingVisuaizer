package components;

public class NullComponent implements Component{

	@Override
	public void update() {
	}
	@Override
	public ComponentType getType() {
		return ComponentType.NULL;
	}
}
