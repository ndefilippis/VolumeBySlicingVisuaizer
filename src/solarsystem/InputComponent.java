package solarsystem;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.Observable;


public class InputComponent extends Observable implements MouseWheelListener, MouseMotionListener, MouseListener{
	private Vector lastLocation;
	private Vector draggedDistance = new Vector(0,0);
	private int mouseWheelTurns = 0;
	@Override
	public void mouseDragged(MouseEvent e) {
		Vector position = new Vector(e.getX(), e.getY());
		draggedDistance = position.subtract(lastLocation);
		lastLocation.set(position);
		setChanged();
		notifyObservers(e);
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		mouseWheelTurns += e.getWheelRotation();
		setChanged();
		notifyObservers(e);
	}
	
	public Vector getDraggedDistance(){
		Vector temp = new Vector(draggedDistance);
		draggedDistance.set(0, 0);
		return temp;
	}
	
	public int getMouseWheelRotation(){
		int temp = mouseWheelTurns;
		mouseWheelTurns = 0;
		return temp;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		lastLocation = new Vector(e.getX(), e.getY());
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		lastLocation.set(e.getX(), e.getY());
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

}
