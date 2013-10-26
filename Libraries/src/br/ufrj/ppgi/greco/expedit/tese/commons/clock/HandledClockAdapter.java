package br.ufrj.ppgi.greco.expedit.tese.commons.clock;

import java.util.Date;

import br.ufrj.ppgi.greco.expedit.tese.commons.TimeoutException;


/**
 * A clock implementation whose exception are handled automaticaly.
 * Note, however, exception is thrown if the clock do not answer until 10 seconds. 
 * 
 * @author expedit
 *
 */
public class HandledClockAdapter implements Clock {

	private Clock clock;
	
	
	public HandledClockAdapter(Clock clock) {
		super();
		this.clock = clock;
	}
	

	@Override
	public boolean isAlive() throws ClockException {
		try {
			return clock.isAlive();
		}
		catch (ClockException e) {
			try {
				HandledClockAdapter.waitForAvailability(clock, 10000, 400);
				return clock.isAlive();
			} catch (ClockException e1) {
				throw e;
			} catch (Exception e1) {
				throw new ClockException(e1);
			}
		}
	}

	@Override
	public long getCurrentTime() throws ClockException {
		try {
			return clock.getCurrentTime();
		}
		catch (ClockException e) {
			try {
				HandledClockAdapter.waitForAvailability(clock, 10000, 400);
				return clock.getCurrentTime();
			} catch (ClockException e1) {
				throw e;
			} catch (Exception e1) {
				throw new ClockException(e1);
			}
		}
	}

	@Override
	public Date getCurrentDate() throws ClockException {
		try {
			return clock.getCurrentDate();
		}
		catch (ClockException e) {
			try {
				HandledClockAdapter.waitForAvailability(clock, 10000, 400);
				return clock.getCurrentDate();
			} catch (ClockException e1) {
				throw e;
			} catch (Exception e1) {
				throw new ClockException(e1);
			}
		}
	}


	private static void sleep(long time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}	
	}


	/**
	 * Wait for a clock availability.
	 * 
	 * @param clk the clock to wait for
	 * @param timeout maximum time this method may last
	 * @param timestep interval between clock availability tests
	 * 
	 * @throws TimeoutException
	 * @throws ClassCastException
	 */
	public static void waitForAvailability(Clock clk, long timeout, long timestep)
		throws TimeoutException {
	
		boolean success = false;
		long startTime = System.currentTimeMillis();
		Exception e1 = null;
		while (System.currentTimeMillis() - startTime < timeout) {
			try {
				clk.isAlive(); // cai no catch ate clock voltar ou passar o timeout
				success = true;
				break;
			} catch (ClockException e) {
				e1 = e;
			}
			
			HandledClockAdapter.sleep(timestep);
		}
	
		if (!success) throw new TimeoutException(e1);
	}
}
