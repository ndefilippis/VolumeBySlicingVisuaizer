package solarsystem;
import java.awt.Color;
import vector.Vector3f;

public enum Planet {
	SUN(	Color.YELLOW,	1.9891E30,		1392000,
			0,				0, 				0, 				0,
			0,				0,				0,				0,
			0.016709,		-1.151E-9,		0,				0,
			0,				0,				0,				0,
			282.9404,		4.70935E-5,		0,				0,
			0,				0,				0,				0
			),
	MERCURY( Color.RED,		6.4185E23,		4878,
			178.179078,		149474.07078,	0.0003011,		0,
			0.3870986,		0,				0,				0,
			0.20561421,		0.00002046,	   -0.000000030,	0,
			7.002881,		0.0018608,	   -0.0000183,		0,
			28.753753,		0.3702806,		0.0001208,		0,
			47.145944,		1.1852083,		0.0001739,		0
	),
	VENUS(	Color.ORANGE,	4.8685E24,		12104,
			342.767053,		58519.21191,	0.0003097,		0,
			0.7233316,		0,				0,				0,
			0.00682069,	   -0.00004774,		0.000000091,	0,
			3.393631,		0.0010058,	   -0.0000010,		0,
			54.384186,		0.5081861,	   -0.0013864,		0,
			75.779647,		0.8998500,		0.0004100,		0
			),
	EARTH(	Color.BLUE,		5.9736E24,		12756,
			99.696688,		36000.76892,	0.0003025,		0,
			1.0000002,		0,				0,				0,
			0.01675104,	   -0.0000418, 	   -0.000000126,	0,
			0,				0,				0,				0,
			0,				0,				0,				0,
			0,				0,				0,				0
			),
	MARS(	Color.RED,		6.4185E23,		6787,
			293.737334,		19141.69551,	0.0003107,		0,
			1.5236883,		0,				0,				0,
			0.09331290,		0.000092064,   -0.000000077,	0,
			1.850333,	   -0.0006750,		0.0000126,		0,
			285.431761,		1.0697667,		0.0001313,		0.00000414,
			48.786442,		0.7709917,	   -0.0000014,	   -0.00000533
			),
	JUPITER(Color.ORANGE,	1.8986E27,		142800,
			238.049257,		3036.301986,	0.0003347,	   -0.00000165,
			5.202561,		0,				0,				0,
			0.04833475,		0.000164180,   -0.0000004676,   -0.0000000017,
			1.308736,	   -0.0056961,		0.0000039,		0,
			273.277558,		0.5594317,		0.00070405,		0.00000508,
			99.443414,		1.0105300,		0.00035222,	   -0.00000851
			),
	SATURN(Color.WHITE,		1.8986E27,		120000,
			266.564377,		1223.509884,	0.0003245,	   -0.0000058,
			9.554747,		0,				0,				0,
			0.05589232,	   -0.00034550,   -0.000000728,		0.00000000074,
			2.492519,	   -0.0039189,	   -0.00001549,		0.00000004,
			338.307800,		1.0852207,		0.00097854,		0.00000992,
			112.790414,		0.8731951,	   -0.00015218,    -0.00000531
			),
	URANUS(Color.BLUE,		8.6810E25,		51200,
			244.197470,		429.863546,		0.0003160,	   -0.00000060,
			19.21814,		0,				0,				0,
			0.0463444,	   -0.00002658,		0.000000077,	0,
			0.772464,		0.0006253,		0.0000395,		0,
			98.071581,		0.9857650,	   -0.0010745,	   -0.00000061,
			73.477111,		0.4986678,		0.0013117,		0
			),
	NEPTUNE(Color.CYAN,		10.243E25,		48600,
			84.457994,		219.885914,		0.0003205,	   -0.00000060,
			30.10957,		0,				0,				0,
			0.00899704,		0.000006330,   -0.000000002,	0,
			1.779242,	   -0.0095436,	   -0.0000091,		0,
			276.045975,		0.3256394,		0.00014095,		0.000004113,
			130.681389,		1.0989350,		0.00024987,	   -0.000004718
			);
	
	//distance measures in km
	//period measured in days
	
	public Color color;
	public double mass;
	private double size;
	private Quartic meanLongitude;
	private Quartic semiMajorAxis;
	private Quartic eccentricity;
	private Quartic inclination;
	private Quartic perihelion;
	private Quartic ascendingNode;
	
	private Planet( Color c, double mass, double size,
					double L1, double L2, double L3, double L4,
					double a1, double a2, double a3, double a4,
					double e1, double e2, double e3, double e4,
					double i1, double i2, double i3, double i4,
					double w1, double w2, double w3, double w4,
					double o1, double o2, double o3, double o4){
		this.color = c;
		this.mass = mass;
		this.size = size;
		meanLongitude 	= new Quartic(L1, L2, L3, L4);
		semiMajorAxis 	= new Quartic(a1, a2, a3, a4);
		eccentricity 	= new Quartic(e1, e2, e3, e4);
		inclination 	= new Quartic(i1, i2, i3, i4);
		perihelion 		= new Quartic(w1, w2, w3, w4);
		ascendingNode 	= new Quartic(o1, o2, o3, o4);
	
	}

	
	private double longitudeOfPerihelion(double time){
		return perihelion.get(time) + ascendingNode.get(time);
	}
	private double meanAnomoly(double time){
		return meanLongitude.get(time) - longitudeOfPerihelion(time);
	}
	
	public Vector3f positionAtTime(double time){
		double e = Math.min(eccentricity.get(time), 1);
		double M = Math.toRadians(meanAnomoly(time));
		double a = semiMajorAxis.get(time);
		double L = Math.toRadians(meanLongitude.get(time));
		double N = Math.toRadians(ascendingNode.get(time));
		double w = Math.toRadians(perihelion.get(time));
		double i = Math.toRadians(inclination.get(time));
		
		double E0 = M + e*Math.sin(M)*(1.0 + e*Math.cos(M));
		double E1 = E0;
			do{
				double temp = E1;
				E1 = E0 - (E0 - e*Math.sin(E0) - M)/(1- e*Math.cos(E0));
				E0 = temp;
			}
			while(Math.abs(E1 - E0) > 0.0001);
		double E = E1;
		
		double xv = a * Math.cos(E) - e;
		double yv = a * Math.sqrt(1-e*e)*Math.sin(E);
		double r = Math.sqrt(xv*xv + yv*yv);
		double v = Math.atan2(yv, xv);
		double xh = r * (Math.cos(N)*Math.cos(v+w) - Math.sin(N)*Math.sin(v+w)*Math.cos(i));
		double yh = r * (Math.sin(N)*Math.cos(v+w) + Math.cos(N)*Math.sin(v+w)*Math.cos(i));
		double zh = r * (Math.sin(v+w)*Math.sin(i));
	
		return new Vector3f((float)xh, (float)zh, (float)yh);
	}
	
	private class Quartic{
		private final double a1, a2, a3, a4;
		
		public Quartic(double a1, double a2, double a3, double a4){
			this.a1 = a1;
			this.a2 = a2;
			this.a3 = a3;
			this.a4 = a4;
		}
		
		public double get(double x){
			return a1 + a2*x + a3*x*x + a4*x*x*x;
		}
	}

	public double period(double time) {
		return Math.pow(semiMajorAxis.get(time), 1.5);
	}

	public float getSize() {
		return (float)size;
	}
}
