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

	static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("C"));

	static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("B"));
	private static final Port usPort = LocalEV3.get().getPort("S4");
	private static final EV3ColorSensor lightSensor = new EV3ColorSensor(LocalEV3.get().getPort("S3"));
	public static final EV3LargeRegulatedMotor sensorMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));

	public static final double WHEEL_RADIUS = 2.15;
	public static final double TRACK = 12.16;
	public static final double GRID_LENGTH = 30.48;
	private static int initialX = 0;
	private static int initialY = 0;
	private static int finalX = 0;
	private static int finalY = 0;
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
		int buttonChoice;
		
		//Display the UI
		coordinatesUI(initialX,initialY);
		buttonChoice = Button.waitForAnyPress();

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
		while (Button.waitForAnyPress() != Button.ID_ENTER);
		lightLocalizer.run();
		//convert the points to actual distances
		double realX = initialX * 30.48;
		double realY = initialY * 30.48;
		navigation.travelTo(realX, realY);

		//UI for the second coordinates
		coordinatesUI(finalX,finalY);
		buttonChoice = Button.waitForAnyPress();
		
		while (Button.waitForAnyPress() != Button.ID_ESCAPE);
		System.exit(0);

	}

	// Method that updates the x/y position on the screen and the variable
	static int modifyPoint(int pos, int firstChoice) {
		do {
			if (firstChoice == Button.ID_UP) {
				if (pos < 12) {
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
	
	//UI method to avoid repetition
	static void coordinatesUI(int posX, int posY) {
		// clear the display
		t.clear();

		// ask the user to input the X position
		t.drawString("   Value of X   ", 0, 0);
		t.drawString("                ", 0, 1);
		t.drawString("                ", 0, 2);
		t.drawString(" Press Enter to ", 0, 3);
		t.drawString("  Go to Y axis  ", 0, 4);

		int buttonChoice = Button.waitForAnyPress();
		posX = modifyPoint(posX, buttonChoice);

		// clear the display
		t.clear();

		// ask the user to input the Y position
		t.drawString("   Value of Y   ", 0, 0);
		t.drawString("                ", 0, 1);
		t.drawString("                ", 0, 2);
		t.drawString(" Press Enter to ", 0, 3);
		t.drawString("     Review     ", 0, 4);

		buttonChoice = Button.waitForAnyPress();
		posY = modifyPoint(posY, buttonChoice);

		// clear the display
		t.clear();

		// Display the (x,y) of the point inputed
		t.drawString("     Point      ", 0, 0);
		t.drawString("X:              ", 0, 1);
		t.drawString("" + posX, 2, 1);
		t.drawString("Y:              ", 0, 2);
		t.drawString("" + posY, 2, 2);
		t.drawString(" Press Enter to ", 0, 3);
		t.drawString("     Start      ", 0, 4);
	}

	// Return the X inputed (used for other classes)
	static int getX() {
		return initialX;
	}

	// Return the Y inputed (used for other classes)
	static int getY() {
		return initialY;
	}
}
