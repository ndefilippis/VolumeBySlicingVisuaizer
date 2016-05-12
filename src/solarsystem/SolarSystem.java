package solarsystem;
import java.util.ArrayList;

import vector.Vector3f;


public class SolarSystem {
	private ArrayList<Planet> planets = new ArrayList<Planet>();

	public SolarSystem(){
		for(Planet p : Planet.values()){
			if(p == Planet.SUN) continue;
			planets.add(p);
		}
	}
	
	public ArrayList<Vector3f> getPlanetPositionsAtTime(double time){
		ArrayList<Vector3f> planetPositions = new ArrayList<Vector3f>();
		for(Planet p : planets){
			planetPositions.add(p.positionAtTime(time));
		}
		return planetPositions;
	}

	public ArrayList<Planet> getPlanets() {
		return planets;
	}
}
