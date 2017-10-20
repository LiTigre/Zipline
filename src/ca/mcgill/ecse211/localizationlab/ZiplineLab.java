// Lab2.java

package ca.mcgill.ecse211.localizationlab;

import ca.mcgill.ecse211.localizationlab.UltrasonicLocalizer.LocalizationState;
import ca.mcgill.ecse211.localizationlab.Navigation;
import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;

/**
 * @author Christos Panaritis and Kevin Chuong
 *
 */
public class ZiplineLab {

	static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("B"));

	static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("C"));
	private static final Port usPort = LocalEV3.get().getPort("S2");
	private static final EV3ColorSensor lightSensor = new EV3ColorSensor(LocalEV3.get().getPort("S1"));
	public static final EV3LargeRegulatedMotor sensorMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));

	public static final double WHEEL_RADIUS = 2.2;
	public static final double TRACK = 14.3;
	public static final double GRID_LENGTH = 30.48;
	private static int initialX = 0;
	private static int initialY = 0;
	private static int finalX = 0;
	private static int finalY = 0;
	private static int startingCorner = 0;
	static final TextLCD t = LocalEV3.get().getTextLCD();

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		// Setup Ultrasonic sensor to obtain information on distance.
		SensorModes usSensor = new EV3UltrasonicSensor(usPort);
		SampleProvider usDistance = usSensor.getMode("Distance");
		float usData[] = new float[usDistance.sampleSize()]; // Contains distance values
		//
		Odometer odometer = new Odometer(leftMotor, rightMotor);
		// final TextLCD t = LocalEV3.get().getTextLCD();
		LCDDisplay lcdDisplay = new LCDDisplay(odometer, t, usSensor, usData);
		Navigation navigation = new Navigation(leftMotor, rightMotor, WHEEL_RADIUS, WHEEL_RADIUS, TRACK, odometer);
		// Setup Light sensor to obtain data
		SampleProvider colorSample = lightSensor.getMode("Red");
		float[] lightData = new float[colorSample.sampleSize()];
		LightLocalizer lightLocalizer = new LightLocalizer(odometer, colorSample, lightData, navigation);
		Zipline zipline = new Zipline(sensorMotor, leftMotor, rightMotor);
		
		int buttonChoice;
		
		t.clear();
		// ask the user to start localizing
		t.drawString("  Coordinates	  ", 0, 0);
		t.drawString("       for      ", 0, 1);
		t.drawString("	Starting X,Y  ", 0, 2);
		t.drawString("                ", 0, 3);
		t.drawString("                ", 0, 4);
		buttonChoice = Button.waitForAnyPress();
		
		//Display the UI
		// clear the display
		t.clear();

		// ask the user to input the X position
		t.drawString("   Value of X   ", 0, 0);
		t.drawString("                ", 0, 1);
		t.drawString("                ", 0, 2);
		t.drawString(" Press Enter to ", 0, 3);
		t.drawString("  Go to Y axis  ", 0, 4);

		buttonChoice = Button.waitForAnyPress();
		initialX = modifyPoint(initialX, buttonChoice, 8);

		// clear the display
		t.clear();

		// ask the user to input the Y position
		t.drawString("   Value of Y   ", 0, 0);
		t.drawString("                ", 0, 1);
		t.drawString("                ", 0, 2);
		t.drawString(" Press Enter to ", 0, 3);
		t.drawString("     Review     ", 0, 4);

		buttonChoice = Button.waitForAnyPress();
		initialY = modifyPoint(initialY, buttonChoice, 8);

		// clear the display
		t.clear();

		// Display the (x,y) of the point inputed
		t.drawString("     Point      ", 0, 0);
		t.drawString("X:              ", 0, 1);
		t.drawString("" + initialX, 2, 1);
		t.drawString("Y:              ", 0, 2);
		t.drawString("" + initialY, 2, 2);
		t.drawString(" Press Enter to ", 0, 3);
		t.drawString("     Start      ", 0, 4);
		buttonChoice = Button.waitForAnyPress();
		
		t.clear();
		// ask the user to start localizing
		t.drawString("  Coordinates	  ", 0, 0);
		t.drawString("       for      ", 0, 1);
		t.drawString("	Corner X,Y    ", 0, 2);
		t.drawString("                ", 0, 3);
		t.drawString("                ", 0, 4);
		buttonChoice = Button.waitForAnyPress();

		//Display the UI
		// clear the display
		t.clear();

		// ask the user to input the X position
		t.drawString("   Value of X   ", 0, 0);
		t.drawString("                ", 0, 1);
		t.drawString("                ", 0, 2);
		t.drawString(" Press Enter to ", 0, 3);
		t.drawString("  Go to Y axis  ", 0, 4);

		buttonChoice = Button.waitForAnyPress();
		finalX = modifyPoint(finalX, buttonChoice, 8);

		// clear the display
		t.clear();

		// ask the user to input the Y position
		t.drawString("   Value of Y   ", 0, 0);
		t.drawString("                ", 0, 1);
		t.drawString("                ", 0, 2);
		t.drawString(" Press Enter to ", 0, 3);
		t.drawString("     Review     ", 0, 4);

		buttonChoice = Button.waitForAnyPress();
		finalY = modifyPoint(finalY, buttonChoice, 8);

		// clear the display
		t.clear();

		// Display the (x,y) of the point inputed
		t.drawString("     Point      ", 0, 0);
		t.drawString("X:              ", 0, 1);
		t.drawString("" + finalX, 2, 1);
		t.drawString("Y:              ", 0, 2);
		t.drawString("" + finalY, 2, 2);
		t.drawString(" Press Enter to ", 0, 3);
		t.drawString("     Start      ", 0, 4);
		buttonChoice = Button.waitForAnyPress();
		
		// clear the display
		t.clear();

		// ask the user to input the Y position
		t.drawString("Starting Corner ", 0, 0);
		t.drawString("                ", 0, 1);
		t.drawString("                ", 0, 2);
		t.drawString(" Press Enter to ", 0, 3);
		t.drawString("     Start      ", 0, 4);

		buttonChoice = Button.waitForAnyPress();
		startingCorner = modifyPoint(startingCorner, buttonChoice, 3);
		
		
		// clear the display
		t.clear();
		// ask the user to start localizing
		t.drawString("< Left | Right >", 0, 0);
		t.drawString("       |        ", 0, 1);
		t.drawString("Falling| Rising ", 0, 2);
		t.drawString(" Edge  | Edge   ", 0, 3);
		t.drawString("       |        ", 0, 4);

		buttonChoice = Button.waitForAnyPress();
		while (buttonChoice != Button.ID_LEFT && buttonChoice != Button.ID_RIGHT);

		if (buttonChoice == Button.ID_LEFT) {
			UltrasonicLocalizer localizer = new UltrasonicLocalizer(odometer, LocalizationState.FALLING_EDGE, usSensor,
					usData, navigation);
			odometer.start();
			lcdDisplay.start();
			localizer.localize();
		}
		else {
			UltrasonicLocalizer localizer = new UltrasonicLocalizer(odometer, LocalizationState.RISING_EDGE, usSensor,
					usData, navigation);
			odometer.start();
			lcdDisplay.start();
			localizer.localize();
		}
		while (navigation.leftMotor.isMoving()&&navigation.rightMotor.isMoving());
		lightLocalizer.run();

		//convert the points to actual distances
		double realX = getInitialX() * 30.48;
		double realY = getInitialY() * 30.48;
		if(startingCorner == 2) {
			navigation.travelTo(realX, 0);
			while (navigation.leftMotor.isMoving()&&navigation.rightMotor.isMoving());
			navigation.travelTo(0, realY);
		}
		else {
			navigation.rightMotor.setAcceleration(100);
			navigation.leftMotor.setAcceleration(100);
			navigation.travelTo(realX, realY);
		}
		
		buttonChoice = Button.waitForAnyPress();
		//convert the points to actual distances
		double realCornerX = getCornerX() * 30.48;
		double realCornerY = getCornerY() * 30.48;
		navigation.travelTo(realCornerX, realCornerY);
		
		zipline.run();
		
		while (Button.waitForAnyPress() != Button.ID_ESCAPE);
		System.exit(0);

	}

	// Method that updates the x/y position on the screen and the variable
	static int modifyPoint(int pos, int firstChoice, int maxValue) {
		do {
			if (firstChoice == Button.ID_UP) {
				if (pos < maxValue) {
					pos++;
					t.drawString("" + pos, 8, 1);
				}
			}
			else if (firstChoice == Button.ID_DOWN) {
				if (pos > 0) {
					pos--;
					t.drawString("" + pos, 8, 1);
				}
			}
			firstChoice = Button.waitForAnyPress();
		} while (firstChoice != Button.ID_ENTER);
		return pos;
	}

	// Return the initial X inputed (used for other classes)
	static int getInitialX() {
		return initialX;
	}
	
	//Return the final X inputed
	static int getCornerX() {
		return finalX;
	}

	// Return the Y inputed (used for other classes)
	static int getInitialY() {
		return initialY;
	}
	
	//Return the final Y inputed 
	static int getCornerY() {
		return finalY;
	}
}
