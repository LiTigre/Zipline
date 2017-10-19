package ca.mcgill.ecse211.localizationlab;

import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class Zipline extends Thread{
	
	EV3LargeRegulatedMotor motor;
	private final int SPEED = 50;
	
	public Zipline(EV3LargeRegulatedMotor motor) {
		this.motor = motor;
	}
	
	public void run() {
		motor.setSpeed(SPEED);
		motor.backward();
	}
	
}
