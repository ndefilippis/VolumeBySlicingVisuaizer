package components;

import EngineTest.SpaceShipTest;
import renderEngine.Display;
import util.Utils;
import vector.Quaternion;
import vector.Vector3f;

public class AIPlayerComponent implements Component {
	public ShootyMcTooty gun;
	public Vector3f goalLocation;
	public float speed = 20f;

	public AIPlayerComponent(ShootyMcTooty gun){
		this.goalLocation = Utils.randomVector(0, 1600, 0, 1600, 0, 1600);
		this.gun = gun;
	}
}
