package solarsystem;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import vector.Vector3f;


public class RocketScience {
	
	private static JButton reverse = new JButton("<<");
	private static JButton backward = new JButton("<");
	private static JButton pause = new JButton("||");
	private static JButton play = new JButton(">");
	private static JButton forward = new JButton(">>");
	
	private static JButton launch = new JButton("Launch");
	private static JLabel angleLabel = new JLabel("Angle (deg): ");
	private static JTextField angle = new JTextField(5);
	private static JLabel speedLabel = new JLabel("Speed (AU/cen): ");
	private static JTextField speed = new JTextField(5);
	
	public static void main(String[] args){
		
		JFrame frame = new JFrame();
		frame.setTitle("Rocket Science");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(800, 600);
		frame.getContentPane().setLayout(new BorderLayout());
		JPanel menuBar = new JPanel();
		menuBar.setLayout(new FlowLayout());
		menuBar.add(reverse);
		menuBar.add(backward);
		menuBar.add(pause);
		menuBar.add(play);
		menuBar.add(forward);
		menuBar.add(Box.createHorizontalStrut(200));
		menuBar.add(angleLabel);
		menuBar.add(angle);
		menuBar.add(speedLabel);
		menuBar.add(speed);
		menuBar.add(launch);
		frame.getContentPane().add(menuBar, BorderLayout.PAGE_END);
		SolarSystem system = new SolarSystem();
		SolarPanel panel = new SolarPanel(system);
		
		frame.getContentPane().add(panel, BorderLayout.CENTER);
		final SolarController controller = new SolarController(system, panel);
		
		reverse.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.setUpdateRate(-Math.abs(controller.getUpdateRate())*2);
			}
		});
		
		backward.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.setUpdateRate(controller.getUpdateRate()/2);
			}
		});
		
		pause.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.setUpdateRate(0.01);
			}
		});
		
		play.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.setUpdateRate(30);
			}
		});
		
		forward.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.setUpdateRate(controller.getUpdateRate()*2);
			}
		});
		launch.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				try{
					double anglea = Math.PI*Double.parseDouble(angle.getText())/180.0;
					double speeda = Double.parseDouble(speed.getText());
					controller.launchRocket(new Vector3f((float)Math.cos(anglea), 0, (float)Math.sin(anglea)), speeda);
				}
				catch(NumberFormatException ex){
					//controller.displayInvalidResponse(ex);
				}
			}
		});
		
		
		
		
		
		frame.pack();
		frame.setVisible(true);
		controller.start();
	}
}
