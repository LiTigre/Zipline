/*
 * Navigation.java
 */
package ca.mcgill.ecse211.localizationlab;

import lejos.hardware.motor.EV3LargeRegulatedMotor;

/**
 * @author Christos Panaritis Kevin Chuong
 * Navigation class
 *
 */
public class Navigation {

	private static final int FORWARD_SPEED = 50;
	private static final int ROTATE_SPEED = 50;
	private static final int ERROR = 3;
	public EV3LargeRegulatedMotor leftMotor;
	public EV3LargeRegulatedMotor rightMotor;
	double amountTurned;
	private double radius;
	private double width;
	private Odometer odometer;
	public boolean navigating;
	public boolean active = false; // Checks if avoidance should run or not (bang bang)

	/**
	 * constructor
	* @param leftMotor
	* @param rightMotor
	* @param leftRadius
	* @param rightRadius
	* @param width
	* @param odometer
	*/
	public Navigation(EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor, double leftRadius,
			double rightRadius, double width, Odometer odometer) {
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		this.radius = rightRadius;
		this.width = width;
		this.odometer = odometer;
	}

	void travelTo(double x, double y) {
		double travelX = x - odometer.getX();
		double travelY = y - odometer.getY();
		double travelTotal = Math.sqrt(Math.pow(travelX, 2) + Math.pow(travelY, 2));
		turnTo(absoluteAngle(travelX, travelY));
		drive(travelTotal);
		

//		navigating = true;
//		double deltaY = y - odometer.getY();
//		double deltaX = x - odometer.getX();
//
//		double thetaD = Math.toDegrees(Math.atan2(deltaX, deltaY));
//		double thetaTurn = thetaD - odometer.getTheta();
//		if (thetaTurn < -180.0) {
//			turnTo(360.0 + thetaTurn);
//
//		}
//		else if (thetaTurn > 180.0) {
//			turnTo(thetaTurn - 360.0);
//		}
//		else {
//			turnTo(thetaTurn);
//		}
//		while (leftMotor.isMoving() && rightMotor.isMoving()) {
//		}
//		leftMotor.setSpeed(FORWARD_SPEED);
//		rightMotor.setSpeed(FORWARD_SPEED);
//		double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
//		leftMotor.rotate(convertDistance(radius, distance), true);
//		rightMotor.rotate(convertDistance(radius, distance), false);
	}

	
//	/**
//	* @param theta
//	* finds what the robot should rotate at.
//	*/
//
//	// TODO:DOES NOT WORK IN THREAD --> FIGURE THAT OUT
//	void turnTo(double theta) {
//
//		leftMotor.setSpeed(ROTATE_SPEED);
//		rightMotor.setSpeed(ROTATE_SPEED);
//		leftMotor.rotate(convertAngle(radius, width, theta), true);
//		rightMotor.rotate(-convertAngle(radius, width, theta), true);
//	}
	
	/**
	 * given a distance to travel, travel straight for that distance
	 * @param travelDist
	 */
	public void drive(double travelDist) {
		for (EV3LargeRegulatedMotor motor : new EV3LargeRegulatedMotor[] { leftMotor, rightMotor }) {
			motor.stop();
			motor.setAcceleration(3000);
		}

		leftMotor.setSpeed(ROTATE_SPEED);
		rightMotor.setSpeed(ROTATE_SPEED);
		leftMotor.rotate(convertDistance(radius, (int) (travelDist)), true);
		rightMotor.rotate(convertDistance(radius, (int) (travelDist)), true);
	}
	
	/**
	 * given an absolute theta, turn to that angle
	 */
	public void turnTo(double theta) { // ABSOLUTE angle
		double currentTheta = odometer.getTheta(); // theta of the robot in DEGREES
		double turnTheta = distance(currentTheta, theta);
		boolean crossover = false;
		boolean clockwise = false;

		double range = currentTheta + 180;
		if (range > 360) {
			range = range - 360;
			crossover = true; // range passes over 0 degrees
		}

		if (crossover) { // crossover -> currentTheta between 180 and 360
			if (theta > currentTheta || theta < range) { // in the 180 degrees counterclockwise
				clockwise = true;
			}
		}
		else { // currentTheta between 0 and 180
			if (theta < range && theta > currentTheta) { // in the 180 degrees clockwise
				clockwise = true;
			}
		}

		leftMotor.setSpeed(ROTATE_SPEED);
		rightMotor.setSpeed(ROTATE_SPEED);

		if (clockwise) {
			leftMotor.rotate(convertAngle(radius, width, turnTheta), true);
			rightMotor.rotate(-convertAngle(radius, width, turnTheta), false);
		}
		else {
			leftMotor.rotate(-convertAngle(radius, width, turnTheta), true);
			rightMotor.rotate(convertAngle(radius, width, turnTheta), false);
		}

	}
	
	/**
	 * This method, given distances to travel to, returns the absolute angle of 
	 * the location relative to the current location of the robot
	 * @param travelX distance to travel in the x direction
	 * @param travelY distance to travel in the y direction
	 */
	private double absoluteAngle(double travelX, double travelY) {
		double angle = 0;
		if (travelX + ERROR > 0) { // account for some error
			angle = Math.toDegrees(Math.atan(Math.abs(travelX) / Math.abs(travelY)));
			if (travelY < 0) {
				angle = 180 - angle;
			}
		}
		else {
			angle = Math.toDegrees(Math.atan(Math.abs(travelX) / Math.abs(travelY)));
			if (travelY < 0) {
				angle = 180 + angle;
			}
			else {
				angle = 360 - angle;
			}
		}
		return angle;
	}
	
	/**
	 * Length (angular) of a shortest way between two angles. It will be in range [0, 180]. 
	 * taken from https://stackoverflow.com/questions/7570808/how-do-i-calculate-the-difference-of-two-angle-measures
	 * @param alpha angle1 (degrees)
	 * @param beta angle2 (degrees)
	 */
	public double distance(double alpha, double beta) {
		double phi = Math.abs(beta - alpha) % 360; // This is either the distance or 360 - distance
		double distance = phi > 180 ? 360 - phi : phi;
		return distance;
	}

	/**
	* @return the navigating boolean 
	*/
	public boolean isNavigating() {
		return this.navigating;
	}

	/**
	* @return the boolean
	*/
	public boolean getStatus() {
		return this.active;
	}

	/**
	* sets the boolean to true
	*/
	public void activate() {
		this.active = true;
	}

	/**
	 * sets the boolean to false
	 */
	public void deactivate() {
		this.active = false;
	}

	/**
	* @param radius
	* @param distance
	* @return
	*/
	private static int convertDistance(double radius, double distance) {
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}

	/**
	* @param radius
	* @param width
	* @param angle
	* @return
	*/
	private static int convertAngle(double radius, double width, double angle) {
		return convertDistance(radius, Math.PI * width * angle / 360.0);
	}

}
