package ca.mcgill.ecse211.localizationlab;

import lejos.hardware.motor.EV3LargeRegulatedMotor;

/**
 * @author Team 2
 *
 */
public class Zipline implements Runnable{
	
	EV3LargeRegulatedMotor motor;
	EV3LargeRegulatedMotor left;
	EV3LargeRegulatedMotor right;
	private final int SPEED = 200;
	private volatile boolean running = true;
	
	/**
	 * @param motor		Motor that controls the pulley
	 * @param left		Motor that controls the left wheel
	 * @param right		Motor that controls the right wheel
	 */
	public Zipline(EV3LargeRegulatedMotor motor, EV3LargeRegulatedMotor left, EV3LargeRegulatedMotor right) {
		this.motor = motor;
		this.left = left;
		this.right = right;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
			motor.setSpeed(SPEED);
			right.setSpeed(100);
			left.setSpeed(100);
			motor.backward();
			right.forward();
			left.forward();
			try{
			Thread.sleep(32000);
			}
			catch(InterruptedException e){
				
			}
			motor.stop();
			right.setSpeed(0);
			left.setSpeed(0);
	}

	/**
	 * End the thread
	 */
	public void terminate() {
		this.running = false;
	}
	
}
