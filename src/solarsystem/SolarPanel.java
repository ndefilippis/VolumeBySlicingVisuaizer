package solarsystem;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JPanel;

import vector.Vector3f;


@SuppressWarnings("serial")
public class SolarPanel extends JPanel implements Observer{
	private SolarSystem solarSystem;
	private double time;
	private ArrayList<Rocket> rockets;
	private Calendar date;
	
	public SolarPanel(SolarSystem system){
		solarSystem = system;
		Dimension d = new Dimension(800, 600);
		setPreferredSize(d);
		setSize(d);
		this.setBackground(Color.BLACK);
	}
	
	private Vector3f scale = new Vector3f((float)1e-2, 1, (float)-1e-2);
	private Vector3f offset = new Vector3f(300, 0, 300);

	private Vector3f getLocalPosition(Vector3f vec){
		return new Vector3f(vec.x/scale.x+offset.x, vec.y/scale.y+offset.y, vec.z/scale.z+offset.z);
	}

	public void paintComponent(Graphics g){
		super.paintComponent(g);
		g.setColor(Color.WHITE);
		ArrayList<Vector3f> positions = solarSystem.getPlanetPositionsAtTime(time);
		ArrayList<Planet> planets = solarSystem.getPlanets();
		
		for(int i = 0; i < positions.size(); i++){
			Vector3f v1 = getLocalPosition(positions.get(i));
			
			g.setColor(planets.get(i).color);
			g.fillOval((int)v1.x-3, (int)v1.z-3, 6, 6);
			
			Vector3f vlast = getLocalPosition(positions.get(i));
			Vector3f vnext;
			double period = planets.get(i).period(time);
			for(int j = 0; j < 1000; j++){
				vnext = planets.get(i).positionAtTime(time-period/(1000.0*24)*j);
				vnext = getLocalPosition(vnext);
				g.drawLine((int)vlast.x, (int)vlast.z, (int)vnext.x, (int)vnext.z);
				vlast = vnext;
			}
			g.setColor(Color.WHITE);
			String s = planets.get(i).name();
			g.drawString(s, (int)v1.x-8*s.length()/2, (int)v1.z+12);
		}
		for(Rocket rocket : rockets){
			Vector3f v = getLocalPosition(rocket.getPostion(time));
			g.setColor(Color.CYAN);
			g.fillRect((int)(v.x-0.5), (int)(v.z-0.5), 5, 5);
		}
		
		g.setColor(Color.WHITE);
		String s = date.toInstant().toString();
		g.drawString(s, getWidth()-150, getHeight()-5);
		g.setColor(Color.YELLOW);
		int size = (int)(0.25/scale.x);
		g.fillOval((int)offset.x-size/2, (int)offset.z-size/2, (int)(size), (int)(size));
	}
	
	@Override
	public void update(Observable o, Object arg) {
		solarSystem = ((SolarController)o).getSolarSystem();
		time = ((SolarController)o).getTime();
		date = ((SolarController)o).getToday();
		rockets = ((SolarController)o).getRockets();
		repaint();
	}
	
	public void moveOffset(Vector v){
		Vector3f.add(offset, new Vector3f((float)v.x, 0, (float)v.y), offset);
	}
	
	public void zoom(double d){
		scale = (Vector3f)scale.scale((float)d);
	}

}
