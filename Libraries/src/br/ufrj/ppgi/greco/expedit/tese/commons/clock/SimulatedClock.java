package br.ufrj.ppgi.greco.expedit.tese.commons.clock;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


public class SimulatedClock extends Thread implements Clock {
	
	//
	// Statics constants
	
	public static final double SPEED_SLOW = 0.5;
	public static final double SPEED_NORMAL = 1.0;
	public static final double SPEED_DOUBLE = 2.0;
	public static final double SPEED_MINUTE = 60.0;
	public static final double SPEED_HOUR = 3600.0;
	public static final double SPEED_DAY = 24.0 * SPEED_HOUR;
	public static final double SPEED_WEEK = 7 * SPEED_DAY;
	public static final double SPEED_MONTH = 30 * SPEED_DAY;
	
	public static final long desiredSleepTime = 50; // ms
	
	////
	
	private Calendar c = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
	private final Date finalDate;
	private final long finalTime;
	private double speedRate;	// segundos simulados / segundos reais
	private float iterationAverageTime = 0;
	private long iterations = 0;
	private long iterationSleepTime = desiredSleepTime;
	private boolean finishThread = false;
	

	public SimulatedClock(Date startDate, Date finalDate) {
		this.c.setTime(startDate);
		this.finalDate = finalDate;
		this.finalTime = 0;
		initSpeedRate();
	}

	public SimulatedClock(Date startDate, int realDuration) {
		this.c.setTime(startDate);
		this.finalDate = null;
		this.finalTime = System.currentTimeMillis() + 1000 * realDuration;
		initSpeedRate();
	}

//	public SimulatedClock() {
//		// Define data final
//		c.set(2011, Calendar.OCTOBER, 30, 23, 59, 59);
//		finalDate = c.getTime();
//		finalTime = 0;
//
//		// Define data inicial
//		c.set(1997, Calendar.JANUARY, 01, 00, 00, 00);	// specific date
//		c.set(Calendar.MILLISECOND, 0);
//		//c.setTime(new Date());		// current date
//		
//		initSpeedRate();
//	}
		
	

	private void initSpeedRate() {
		this.speedRate = 3600 * 72;	// 3 dias / segundo
	}
	
	/**
	 * Sleeps for 50 ms and computes the exact time elapsed, in milliseconds 
	 * @return the exact time elapsed sleeping
	 */
	private long sleep() {
		long startTime = System.currentTimeMillis();
		try {
			Thread.sleep(iterationSleepTime);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		long deltaTime = System.currentTimeMillis() - startTime;
		iterationAverageTime += (deltaTime - iterationAverageTime) / ++iterations;	// long-running average

		if (iterationAverageTime > desiredSleepTime) iterationSleepTime--;
		else if (iterationAverageTime < desiredSleepTime) iterationSleepTime++;
		
		if (iterationSleepTime < 0) iterationSleepTime = 0;

		return deltaTime;
	}


	/**
	 *     Real                        Simulado
	 *  =======            ====================
	 *     1  s                   speedRate  (s)
	 *  1000 ms            1000 * speedRate (ms)
	 *  elapsedTime (ms)      simulatedIncrement = 1000 * speedRate * elapsedTime / 1000 ms
	 *                                           = speedRate * elapsedTime
	 * 
	 * @param elapsedTime
	 * @return o tempo atual da simulacao
	 */
	private synchronized Date increaseAndGet(long elapsedTime) {
		long simulatedIncrement =  (long) (speedRate * elapsedTime);
		c.add(Calendar.SECOND,      (int) (simulatedIncrement / 1000));
		c.add(Calendar.MILLISECOND, (int) (simulatedIncrement % 1000));
		return c.getTime();
	}

	@Override
	public void run() {
		if (finalDate == null) {
			while (System.currentTimeMillis() < finalTime && !finishThread) {
				increaseAndGet(sleep());
			}
		}
		else {
			while (increaseAndGet(sleep()).before(finalDate) && !finishThread) { }
		}
	}
	
	
	public void finish() {
		finishThread = true;
	}
	
	//
	// Getters and Setters
	//

	@Override
	public synchronized Date getCurrentDate() {
		return c.getTime();
	}

	@Override
	public synchronized long getCurrentTime() {
		return c.getTimeInMillis();
	}
	

	public synchronized void setSpeedRate(double speedRate) {
		this.speedRate = speedRate;
	}
	
	public synchronized double getSpeedRate() {
		return speedRate;
	}
	
	
	public float getIteractionAverageTime() {
		return iterationAverageTime;
	}
	
	public long getIteractions() {
		return iterations;
	}
	
	public long getIterationSleepTime() {
		return iterationSleepTime;
	}
}
