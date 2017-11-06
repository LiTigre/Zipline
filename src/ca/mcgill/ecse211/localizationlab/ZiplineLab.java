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
 * @author Team 2
 *
 */
public class ZiplineLab {

	static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("B"));

	static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("C"));
	private static final Port usPort = LocalEV3.get().getPort("S2");
	private static final EV3ColorSensor lightSensor = new EV3ColorSensor(LocalEV3.get().getPort("S1"));
	public static final EV3LargeRegulatedMotor sensorMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));

	public static final double WHEEL_RADIUS = 2.10;
	public static final double TRACK = 13.22;
			;
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
		t.drawString("  press left	  ", 0, 0);
		t.drawString("      or right  ", 0, 1);
		t.drawString("	              ", 0, 2);
		t.drawString("                ", 0, 3);
		t.drawString("                ", 0, 4);
		buttonChoice = Button.waitForAnyPress();
		
		// clear the display
		t.clear();

		if (buttonChoice == Button.ID_LEFT) {
			leftMotor.setSpeed(50);
			rightMotor.setSpeed(50);
			
			leftMotor.rotate(Navigation.convertDistance(WHEEL_RADIUS, GRID_LENGTH * 2), true);
			rightMotor.rotate(Navigation.convertDistance(WHEEL_RADIUS, GRID_LENGTH * 2), false);			
		} else if (buttonChoice == Button.ID_RIGHT) {
			leftMotor.setSpeed(100);
			rightMotor.setSpeed(100);
			zipline.run();
		}
		
		while (Button.waitForAnyPress() != Button.ID_ESCAPE);
		System.exit(0);

	}
}
