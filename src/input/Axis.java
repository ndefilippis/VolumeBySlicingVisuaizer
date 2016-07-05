package input;

public enum Axis {
	NONE(-1), HORIZONTAL(0), VERTICAL(1), TWIST(2), THROTTLE(3);
	
	public int index;
	private Axis(int index){
		this.index = index;
	}
	
	public static Axis getAxis(int i){
		switch(i){
		case 0:
			return HORIZONTAL;
		case 1:
			return VERTICAL;
		case 2:
			return TWIST;
		case 3:
			return THROTTLE;
		default:
			return NONE;
		}
	}
}
