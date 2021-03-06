package tanquery;

import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PVector;
import TUIO.TuioObject;
import TUIO.TuioProcessing;

public class Calibration {
	TuioProcessing tuioClient;
	ArrayList<TuioObject> tuioObjectList;

	float a1, b1, c1, a3, b3, a2, b2, c2;
	int calPoints = 0;
	boolean calibrated = false;
	PVector[] cal = new PVector[4];
	PVector[] dots = new PVector[4];
	TanQuery parent;
	MyCalibrate my;

	int width;
	int height;
	
	/*
	public void keyPressed() {
		tuioObjectList = tuioClient.getTuioObjectList();
		// performs a new calibration
		if (parent.key == 'n') {
			parent.fill(255, 0, 0);
			parent.textFont(parent.font, 22);
			parent.text("Realizar nueva Calibracion", 800, 400);
			calibrated = false;
			calPoints = 0;
		}

		// save data points
		if (parent.key == 'c') {
			parent.fill(255, 0, 0);
			parent.textFont(parent.font, 22);
			parent.text("Guardar Punto", 800, 410);
			
			if (tuioObjectList.size() > 0) {
				TuioObject tobj = (TuioObject) tuioObjectList.get(0);
				getCalibrationPoint(tobj.getScreenX(width),
						tobj.getScreenY(height));
				parent.text("", 700, 310);
				
				
			}
		}
		if (parent.key == 'h') {
			parent.fill(255, 0, 0);
			parent.textFont(parent.font, 22);
			parent.text("Ayuda de la calibracion:", 800, 420);

		}
	}
*/
	
	public static void main(String _args[]) {

		try {
			PApplet.main(new String[] { tanquery.TanQuery.class.getName() });
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	public Calibration(TanQuery frame) {
		this.width = frame.width;
		this.height = frame.height;
		parent = frame;
		// dot is the original calibration image
		int calibInset = 50;
		dots[0] = new PVector(calibInset, calibInset); // top left
		dots[1] = new PVector(width - calibInset, calibInset); // top right
		dots[2] = new PVector(calibInset, height - calibInset); // bot left
		dots[3] = new PVector(width - calibInset, height - calibInset); // bot
																		// right
		parent.text("Loading calibration file...", 800, 50);
		// if we already have some data, then we used the saved configuration
		String lines[] = parent.loadStrings("calibration.txt");
		if (lines != null ) {
			for (int i = 0; i < lines.length; i++) {
				System.out.println("Liena"+i+": "+lines[i]);
				
			}
		}
		
		if (lines != null && lines.length == 8) {
			parent.fill(255, 0, 0);
			parent.textFont(frame.font, 22);
			parent.text("Loading calibration file...", 800, 50);

			b3 = Float.parseFloat(lines[0]);
			b2 = Float.parseFloat(lines[1]);
			a2 = Float.parseFloat(lines[2]);
			c2 = Float.parseFloat(lines[3]);
			a3 = Float.parseFloat(lines[4]);
			b1 = Float.parseFloat(lines[5]);
			a1 = Float.parseFloat(lines[6]);
			c1 = Float.parseFloat(lines[7]);
			calibrated = true;
			System.out.println("Done.");

		}

	}

	public Calibration(){
		super();
	}
	public void mostrar(MyCalibrate my) {//Solo es de prueba para probar loadstrings()
		this.my=my;
		my.text("Loading calibration file...", 800, 50);
		// if we already have some data, then we used the saved configuration
		String lines[] = my.loadStrings("calibration.txt");
		if (lines != null ) {
			for (int i = 0; i < lines.length; i++) {
				System.out.println("Liena"+i+": "+lines[i]);
				
			}
		}
		calibrated = true;
		System.out.println("****----- Done.------****");
	}

	@SuppressWarnings("static-access")
	public void escribir(){
		String [] list=new String[20];
		
		
		try {
			String lineas="escribiendo"+";"+"Muchas veces"+";"+"las cosas"+";"+"Se pueden probar mejor"+";"+"Aun sin saberlo"+";";
			list=my.split(lineas, ';');
			my.saveStrings("calibration2.txt", list);
			
			String lines[] =new String[20]; 
					lines=my.loadStrings("calibration2.txt");
			if (lines != null ) {
				for (int i = 0; i < lines.length; i++) {
					System.out.println("Liena"+i+": "+lines[i]);
					
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
public void setup() {

	}

	public int tx(TuioObject tobj) {
		return this.corrected_x(tobj);
	}

	public int ty(TuioObject tobj) {
		return this.corrected_y(tobj);
	}

	void getCalibrationPoint(int x, int y) {
		if (calibrated == false) {
			cal[calPoints++] = new PVector(x, y);

			if (calPoints == 4) {
				if (calibrate() == 0)
					calibrated = true;
				else
					calPoints = 0;
			}
		}
	}

	public int corrected_x(TuioObject tobj) {
		if (tobj != null)
			return (int) ((this.a1 * tobj.getScreenX(width) + this.b1
					* tobj.getScreenY(height) + this.c1) / (this.a3
					* tobj.getScreenX(width) + this.b3
					* tobj.getScreenY(height) + 1));
		else
			return -1;
	}

	public int corrected_y(TuioObject tobj) {
		if (tobj != null)
			return (int) ((a2 * tobj.getScreenX(width) + b2
					* tobj.getScreenY(height) + c2) / (a3
					* tobj.getScreenX(width) + b3 * tobj.getScreenY(height) + 1));
		else
			return -1;
	}

	@SuppressWarnings("static-access")
	public int calibrate() {
		System.out.println("running calibration");

		float[][] matrix = {
				{ -1, -1, -1, -1, 0, 0, 0, 0 },

				{ -cal[0].x, -cal[1].x, -cal[2].x, -cal[3].x, 0, 0, 0, 0 },
				{ -cal[0].y, -cal[1].y, -cal[2].y, -cal[3].y, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, -1, -1, -1, -1 },
				{ 0, 0, 0, 0, -cal[0].x, -cal[1].x, -cal[2].x, -cal[3].x },
				{ 0, 0, 0, 0, -cal[0].y, -cal[1].y, -cal[2].y, -cal[3].y },
				{ cal[0].x * dots[0].x, cal[1].x * dots[1].x,
						cal[2].x * dots[2].x, cal[3].x * dots[3].x,
						cal[0].x * dots[0].y, cal[1].x * dots[1].y,
						cal[2].x * dots[2].y, cal[3].x * dots[3].y },
				{ cal[0].y * dots[0].x, cal[1].y * dots[1].x,
						cal[2].y * dots[2].x, cal[3].y * dots[3].x,
						cal[0].y * dots[0].y, cal[1].y * dots[1].y,
						cal[2].y * dots[2].y, cal[3].y * dots[3].y }, };

		float[] bb = { -dots[0].x, -dots[1].x, -dots[2].x, -dots[3].x,
				-dots[0].y, -dots[1].y, -dots[2].y, -dots[3].y };

		// gauß-elimination

		for (int j = 1; j < 4; j++) {

			for (int i = 1; i < 8; i++) {
				matrix[i][j] = -matrix[i][j] + matrix[i][0];
			}
			bb[j] = -bb[j] + bb[0];
			matrix[0][j] = 0;

		}

		for (int i = 2; i < 8; i++) {
			matrix[i][2] = -matrix[i][2] / matrix[1][2] * matrix[1][1]
					+ matrix[i][1];
		}
		bb[2] = -bb[2] / matrix[1][2] * matrix[1][1] + bb[1];
		matrix[1][2] = 0;

		for (int i = 2; i < 8; i++) {
			matrix[i][3] = -matrix[i][3] / matrix[1][3] * matrix[1][1]
					+ matrix[i][1];
		}
		bb[3] = -bb[3] / matrix[1][3] * matrix[1][1] + bb[1];
		matrix[1][3] = 0;

		for (int i = 3; i < 8; i++) {
			matrix[i][3] = -matrix[i][3] / matrix[2][3] * matrix[2][2]
					+ matrix[i][2];
		}
		bb[3] = -bb[3] / matrix[2][3] * matrix[2][2] + bb[2];
		matrix[2][3] = 0;

		System.out.println("var57, var56, var55");
		System.out.println(matrix[4][6] + " " + matrix[4][5] + " "
				+ matrix[4][4]);

		for (int j = 5; j < 8; j++) {
			for (int i = 4; i < 8; i++) {
				matrix[i][j] = -matrix[i][j] + matrix[i][4];
			}
			bb[j] = -bb[j] + bb[4];
			matrix[3][j] = 0;
		}

		for (int i = 5; i < 8; i++) {
			matrix[i][6] = -matrix[i][6] / matrix[4][6] * matrix[4][5]
					+ matrix[i][5];
		}

		bb[6] = -bb[6] / matrix[4][6] * matrix[4][5] + bb[5];
		matrix[4][6] = 0;

		for (int i = 5; i < 8; i++) {
			matrix[i][7] = -matrix[i][7] / matrix[4][7] * matrix[4][5]
					+ matrix[i][5];
		}
		bb[7] = -bb[7] / matrix[4][7] * matrix[4][5] + bb[5];
		matrix[4][7] = 0;

		for (int i = 6; i < 8; i++) {
			matrix[i][7] = -matrix[i][7] / matrix[5][7] * matrix[5][6]
					+ matrix[i][6];
		}
		bb[7] = -bb[7] / matrix[5][7] * matrix[5][6] + bb[6];
		matrix[5][7] = 0;

		matrix[7][7] = -matrix[7][7] / matrix[6][7] * matrix[6][3]
				+ matrix[7][3];
		bb[7] = -bb[7] / matrix[6][7] * matrix[6][3] + bb[3];
		matrix[6][7] = 0;

		System.out.println("data dump");
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				System.out.print(matrix[i][j] + ",");
			}
			System.out.println("");
		}

		System.out.println("bb");
		for (int j = 0; j < 8; j++) {
			System.out.print(bb[j] + ",");
		}

		System.out.println("");

		b3 = bb[7] / matrix[7][7];
		b2 = (bb[6] - (matrix[7][6] * b3 + matrix[6][6] * a3)) / matrix[5][6];
		a2 = (bb[5] - (matrix[7][5] * b3 + matrix[6][5] * a3 + matrix[5][5]
				* b2))
				/ matrix[4][5];
		c2 = (bb[4] - (matrix[7][4] * b3 + matrix[6][5] * a3 + matrix[5][4]
				* b2 + matrix[4][4] * a2))
				/ matrix[3][4];
		a3 = (bb[3] - (matrix[7][3] * b3)) / matrix[6][3];
		b1 = (bb[2] - (matrix[7][2] * b3 + matrix[6][2] * a3 + matrix[5][2]
				* b2 + matrix[4][2] * a2 + matrix[3][2] * c2))
				/ matrix[2][2];
		a1 = (bb[1] - (matrix[7][1] * b3 + matrix[6][1] * a3 + matrix[5][1]
				* b2 + matrix[4][1] * a2 + matrix[3][1] * c2 + matrix[2][1]
				* b1))
				/ matrix[1][1];
		c1 = (bb[0] - (matrix[7][0] * b3 + matrix[6][0] * a3 + matrix[5][0]
				* b2 + matrix[4][0] * a2 + matrix[3][0] * c2 + matrix[2][0]
				* b1 + matrix[1][0] * a1))
				/ matrix[0][0];

		if (Float.isNaN(b3))
			return 1;
		if (Float.isNaN(b2))
			return 1;
		if (Float.isNaN(a2))
			return 1;
		if (Float.isNaN(c2))
			return 1;
		if (Float.isNaN(a3))
			return 1;
		if (Float.isNaN(b1))
			return 1;
		if (Float.isNaN(a1))
			return 1;
		if (Float.isNaN(c1))
			return 1;

		System.out.println("calibrated OK");

		String data = "" + b3 + ";" + b2 + ";" + a2 + ";" + c2 + ";" + a3 + ";"
				+ b1 + ";" + a1 + ";" + c1;

		parent.saveStrings("calibration.txt", parent.split(data, ';'));

		return 0;
	}

	public void draw() {
		int lineLength = 10;

		parent.stroke(255);
		parent.fill(255,0,0);
		parent.ellipse(dots[calPoints].x, dots[calPoints].y, lineLength * 2,
				lineLength * 2);
		parent.line(dots[calPoints].x - lineLength, dots[calPoints].y,
				dots[calPoints].x + lineLength, dots[calPoints].y);
		parent.line(dots[calPoints].x, dots[calPoints].y - lineLength,
				dots[calPoints].x, dots[calPoints].y + lineLength);
	}

	

}
