package solarsystem;

import java.util.ArrayList;
import java.util.Calendar;

public class MainGameLoop {
	private ArrayList<Rocket> rockets = new ArrayList<Rocket>();
	private double daysPerSecond = 30.0;
	private final double dt = 1.0D / 60.0D;
	private Calendar today;
	
	private double time, currTime, elapsedTime, accumulator, actualElapsedTime;
	
	public void start(){
		calculateNow();
		time = System.nanoTime()/1000000000.0;
		accumulator = 0;
		int count = 0;
		while(true){
			currTime = System.nanoTime()/1000000000.0;
			count++;
			elapsedTime += (currTime - time)/100/365.25*daysPerSecond;
			actualElapsedTime += (currTime - time)*daysPerSecond;
			accumulator += currTime - time;
			
			today.add(Calendar.MILLISECOND, (int)(dt*1000*daysPerSecond));
			
			
			time = currTime;
			while(accumulator >= dt){
				accumulator -= dt;
				//update(dt);
			}
			//render()
		}
	}
	private void calculateNow(){
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
	}
}
