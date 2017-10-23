package ca.mcgill.ecse211.localizationlab;

import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class Zipline implements Runnable{
	
	EV3LargeRegulatedMotor motor;
	EV3LargeRegulatedMotor left;
	EV3LargeRegulatedMotor right;
	private final int SPEED = 200;
	private volatile boolean running = true;
	
	public Zipline(EV3LargeRegulatedMotor motor, EV3LargeRegulatedMotor left, EV3LargeRegulatedMotor right) {
		this.motor = motor;
		this.left = left;
		this.right = right;
	}
	
	public void run() {
			motor.setSpeed(SPEED);
			right.setSpeed(SPEED);
			left.setSpeed(SPEED);
			motor.backward();
			right.forward();
			left.forward();
			//right.stop();
			//left.stop();
	}
	
	public void terminate() {
		this.running = false;
	}
	
}
