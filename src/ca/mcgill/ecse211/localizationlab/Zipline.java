package ca.mcgill.ecse211.localizationlab;

import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class Zipline implements Runnable{
	
	EV3LargeRegulatedMotor motor;
	private final int SPEED = 50;
	private volatile boolean running = true;
	
	public Zipline(EV3LargeRegulatedMotor motor) {
		this.motor = motor;
	}
	
	public void run() {
		while (running) {
			motor.setSpeed(SPEED);
			motor.backward();			
		}
	}
	
	public void terminate() {
		this.running = false;
	}
	
}
