package EngineTest;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import entities.Entity;
import entities.FreeFormCamera;
import entities.Light;
import input.InputHandler;
import models.Model;
import models.ModelData;
import models.TexturedModel;
import renderEngine.Display;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import renderEngine.OBJFileLoader;
import solarsystem.Planet;
import solarsystem.Rocket;
import textures.ModelTexture;
import vector.Vector3f;
import vector.Vector4f;

public class LWJGLPlanetTest{
	private static String[] planet_textures = {"texture4", "mercury", 
			"venus", "earth", "mars","jupiter","saturn","uranus","neptune"};
	private static TexturedModel[] planetModels = new TexturedModel[9];
	private static double time, currTime, elapsedTime, accumulator, actualElapsedTime;
	private static Calendar today;
	private static double daysPerSecond = 30.0;
	private static ArrayList<Rocket> rockets = new ArrayList<Rocket>();
	private static final double dt = 1.0D / 60.0D;
	
	public static void main(String[] args){
		Display display = new Display();
		Loader loader = new Loader();
		InputHandler input = new InputHandler();
		
		ModelData modeldata = OBJFileLoader.loadOBJ("sphere");
		Model model = loader.loadToVAO(modeldata.getVertices(), modeldata.getTextureCoords(), modeldata.getNormals(),
				modeldata.getIndices());
		for(int i = 0; i < 9; i++){
			ModelTexture Texture = new ModelTexture(loader.loadTexture(planet_textures[i]));
			planetModels[i] = new TexturedModel(model, Texture);
		}
		
		modeldata = OBJFileLoader.loadOBJ("box");
		model = loader.loadToVAO(modeldata.getVertices(), modeldata.getTextureCoords(), modeldata.getNormals(),
				modeldata.getIndices());
			ModelTexture Texture = new ModelTexture(loader.loadTexture("texture5"));
			TexturedModel rocketModel = new TexturedModel(model, Texture);
		
		today = Calendar.getInstance();
		int y = today.get(Calendar.YEAR);
		int m = today.get(Calendar.MONTH);
		int d = today.get(Calendar.DATE);
		int a = y/100;
		int b = 2 - a + a/4;
		if(m == 1 || m == 2){
			y = y-1;
			m = m+12;
		}
		double jd = (int)(365.25*y)+(int)(30.6001*(m+1))+d+1720994.5 + b;
		elapsedTime = (jd-2415020.0)/36525.0;
		time = System.nanoTime()/1000000000.0;
		accumulator = 0;
		int count = 0;
		
		List<Entity> entities = new ArrayList<Entity>();
		Random random = new Random();
		for (int i = 0; i < planetModels.length; i++) {
			Vector3f v = (Vector3f)(Planet.values()[i].positionAtTime(elapsedTime+accumulator).scale(25f));
			float px = v.x;
			float py = v.y;
			float pz = v.z;
			entities.add(new Entity(planetModels[i],
					new Vector3f(px, py, pz), 0f, 0f, 0f,  (float)Math.log10(Planet.values()[i].getSize()/150f)));
		}


		Light light = new Light(new Vector3f(0, 0, 0), new Vector3f(1f, 1f, 1f));
		List<Light> lights = new ArrayList<Light>();
		
		lights.add(light);
		
		

		FreeFormCamera camera = new FreeFormCamera();
		MasterRenderer renderer = new MasterRenderer(loader);
		while (display.shouldClose()) {
			currTime = System.nanoTime()/1000000000.0;
			count++;
			elapsedTime += (currTime - time)/100/365.25*daysPerSecond;
			actualElapsedTime += (currTime - time)*daysPerSecond;
			accumulator += (currTime - time)/100/365.25*daysPerSecond;
			
			today.add(Calendar.MILLISECOND, (int)(dt*1000*daysPerSecond));
			
			
			time = currTime;
			while(accumulator >= Math.abs(dt/100/365.25*daysPerSecond)){
				accumulator -= Math.abs(dt/100/365.25*daysPerSecond);
			}
			camera.move();
			input.clear();
			//renderer.processTerrain(terrain1);
			for (Entity entity : entities) {
				renderer.processEntity(entity);
				//System.out.println(entity.getScale());
			}
			entities.clear();
			for (int i = 0; i < planetModels.length; i++) {
				Vector3f v = (Vector3f)(Planet.values()[i].positionAtTime(elapsedTime+accumulator).scale(25f));
				entities.add(new Entity(planetModels[i],
						v, 0f, 0f, 0f,  (float)Math.log10(Planet.values()[i].getSize()/150f)));
			}
			for(int i = 0; i < rockets.size(); i++){
				Vector3f v = (Vector3f)(rockets.get(i).getPostion(elapsedTime+accumulator).scale(25f));
				entities.add(new Entity(rocketModel, v, 90f - rockets.get(i).getAngle(), 0f, rockets.get(i).getAngle() - 90f, 1f));
			}
			if(elapsedTime*3600000f % 2 < 1)
				launchRocket(elapsedTime, 1000);
			renderer.render(lights, camera, new Vector4f(0, 1, 0, 15));
			display.update();
		}
		
		renderer.cleanUp();
		display.cleanUp();
		loader.cleanUp();
	}
	
	public static void launchRocket(double angle, double speed) {
		Rocket rocket = new Rocket(speed, 100);
		Vector3f v = Planet.EARTH.positionAtTime(elapsedTime);
		rocket.launch(v, new Vector3f((float)Math.cos(angle), 0, (float)Math.sin(angle)), elapsedTime);
		rockets.add(rocket);
	}

}