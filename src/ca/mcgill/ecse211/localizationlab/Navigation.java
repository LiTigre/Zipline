/*
 * Navigation.java
 */
package ca.mcgill.ecse211.localizationlab;

import lejos.hardware.motor.EV3LargeRegulatedMotor;

/**
 * @author Team 2
 * Navigation class
 *
 */
public class Navigation {

	private static final int FORWARD_SPEED = 100;
	private static final int ROTATE_SPEED = 100;
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
	* @param leftMotor		motor that controls the left wheel
	* @param rightMotor		motor that controls the right wheel
	* @param leftRadius		radius of left wheel
	* @param rightRadius	radius of right wheel
	* @param width				width of robot
	* @param odometer			instance of odometer class
	*/
	public Navigation(EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor, double leftRadius,
			double rightRadius, double width, Odometer odometer) {
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		this.radius = rightRadius;
		this.width = width;
		this.odometer = odometer;
	}

	/**
	 * Robot travels to inputted (x, y) coordinate
	 * 
	 * @param x x point to travel to
	 * @param y y point to travel to
	 */
	void travelTo(double x, double y) {

		navigating = true;
		double deltaY = y - odometer.getY();
		double deltaX = x - odometer.getX();

		double thetaD = Math.toDegrees(Math.atan2(deltaX, deltaY));
		double thetaTurn = thetaD - odometer.getTheta();
		if (thetaTurn < -180.0) {
			turnTo(360.0 + thetaTurn);

		}
		else if (thetaTurn > 180.0) {
			turnTo(thetaTurn - 360.0);
		}
		else {
			turnTo(thetaTurn);
		}
		while (leftMotor.isMoving() && rightMotor.isMoving()) {
		}
		leftMotor.setSpeed(FORWARD_SPEED);
		rightMotor.setSpeed(FORWARD_SPEED);
		double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
		leftMotor.rotate(convertDistance(radius, distance), true);
		rightMotor.rotate(convertDistance(radius, distance), false);

	}

	/**
	* Robot turns to passed in angle
	* 
	* @param theta angle to turn to in degrees
	*/

	// TODO:DOES NOT WORK IN THREAD --> FIGURE THAT OUT
	void turnTo(double theta) {

		leftMotor.setSpeed(ROTATE_SPEED);
		rightMotor.setSpeed(ROTATE_SPEED);
		leftMotor.rotate(convertAngle(radius, width, theta), true);
		rightMotor.rotate(-convertAngle(radius, width, theta), true);
	}

	/**
	 * Turns robot to face the passed in (x, y) coordinate
	 * 
	 * @param x x coordinate to point robot to
	 * @param y	y coordinate to point robot to
	 */
	void turnToPoint(double x, double y) {
		double deltaY = y - odometer.getY();
		double deltaX = x - odometer.getX();

		double thetaD = Math.toDegrees(Math.atan2(deltaX, deltaY));
		double thetaTurn = thetaD - odometer.getTheta();
		if (thetaTurn < -180.0) {
			turnTo(360.0 + thetaTurn);

		}
		else if (thetaTurn > 180.0) {
			turnTo(thetaTurn - 360.0);
		}
		else {
			turnTo(thetaTurn);
		}
		while (leftMotor.isMoving() && rightMotor.isMoving()) {
		}
	}
	/**
	* @return whether the root is navigating
	*/
	public boolean isNavigating() {
		return this.navigating;
	}

	/**
	* @return status of the robot
	*/
	public boolean getStatus() {
		return this.active;
	}

	/**
	* Sets the robot to active
	*/
	public void activate() {
		this.active = true;
	}

	/**
	 * Sets the robot to not active
	 */
	public void deactivate() {
		this.active = false;
	}

	/**
	*	Returns distance the wheels must turn to acheive
	* wanted distance passed in 
	* 
	* @param radius			radius of wheel
	* @param distance		distance to travel
	* @return 					distance wheel must turn
	*/
	public static int convertDistance(double radius, double distance) {
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}

	/**
	* Returns the distance the robot must travel to achieve
	* the passed in angle
	* 
	* @param radius		radius of wheel
	* @param width		width of robot
	* @param angle		angle to turn
	* @return					distance needed to travel
	*/
	public static int convertAngle(double radius, double width, double angle) {
		return convertDistance(radius, Math.PI * width * angle / 360.0);
	}

}
