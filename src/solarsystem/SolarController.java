package solarsystem;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Observable;
import java.util.Observer;

import vector.Vector3f;

public class SolarController extends Observable implements Observer{
	private SolarSystem system;
	private SolarPanel view;
	private ArrayList<Rocket> rockets;
	private double daysPerSecond = 30.0;
	private final double dt = 1.0D / 60.0D;
	private Calendar today;
	double angle = 1;
	double speed = 1000;

	public SolarController(SolarSystem system, SolarPanel panel){
		today = Calendar.getInstance();
		this.system = system;
		this.view = panel;
		InputComponent input = new InputComponent();
		rockets = new ArrayList<Rocket>();
		panel.addMouseListener(input);
		panel.addMouseMotionListener(input);
		panel.addMouseWheelListener(input);
		input.addObserver(this);
		addObserver(view);
	}
	
	
	public void setUpdateRate(double d){
		daysPerSecond = d;
	}
	
	public double getUpdateRate(){
		return daysPerSecond;
	}
	
	private double time, currTime, elapsedTime, accumulator, actualElapsedTime;
	
	public void start(){
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
		while(true){
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
			
			if((int)(elapsedTime*360000L)%50== 0){
				//launchRocket(angle, speed);
			}
			setChanged();
			notifyObservers();
		}
	}
	
	public double getTime(){
		return elapsedTime + accumulator;
	}
	
	public SolarSystem getSolarSystem(){
		return system;
	}
	
	public Calendar getToday(){
		return today;
	}

	@Override
	public void update(Observable o, Object arg) {
		InputComponent input = (InputComponent)(o);
		if(arg instanceof MouseWheelEvent){
			view.zoom(Math.pow(Math.E, input.getMouseWheelRotation()/15.0));
		}
		else if (arg instanceof MouseEvent){
			view.moveOffset(input.getDraggedDistance());
		}
	}
	
	public ArrayList<Rocket> getRockets(){
		return new ArrayList<Rocket>(rockets);
	}

	public void launchRocket(Vector3f initialDirection, double speed) {
		Rocket rocket = new Rocket(speed, 100);
		rocket.launch(Planet.EARTH.positionAtTime(elapsedTime), initialDirection, elapsedTime);
		rockets.add(rocket);
		setChanged();
		notifyObservers();
	}
}
