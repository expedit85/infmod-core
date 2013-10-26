package br.ufrj.ppgi.greco.expedit.tese.commons.clock;

import java.net.URI;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class CachedNetworkClock extends NetworkClock {

	private Calendar c = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
	
	private long lastChange;	// last time, in millis, data was requested and obtained
	private double speedRate;	// last speed rate obtained
	private long lastTime;	// last time in millis (estimated or obtained)
	private boolean aliveness;	// last aliveness information obtained
	
	
	public CachedNetworkClock(URI baseUri) throws ClockException {
		super(baseUri);

		c.setTimeInMillis(super.getCurrentTime());
		update();
	}
	
	
	private synchronized void update() throws ClockException
	{
		aliveness = super.isAlive();
		speedRate = super.getSpeedRate();
		
		long curTime = super.getCurrentTime();
		if (curTime < lastTime)
		{
			speedRate *= 0.95;	// reduz ate normalizar
			System.out.println("CachedNetworkClock: speed rate was adjusted = " + speedRate);
		}
		
		lastTime = Math.max(curTime, lastTime);
		lastChange = System.currentTimeMillis();
	}
	
	
	
	public synchronized Date getCurrentDate() throws ClockException
	{
		getCurrentTime();
		return c.getTime();
	}
	
	public synchronized long getCurrentTime() throws ClockException
	{
		long elapsedTime = System.currentTimeMillis() - lastChange;
		if (elapsedTime < 50)
		{
			long simulatedIncrement = (long) (speedRate * elapsedTime);
			c.add(Calendar.SECOND,      (int) (simulatedIncrement / 1000));
			c.add(Calendar.MILLISECOND, (int) (simulatedIncrement % 1000));
			lastTime = c.getTimeInMillis();
			lastChange = System.currentTimeMillis();
		}
		else
		{
			update();
		}
		
		return lastTime;
	}
	
	public synchronized boolean isAlive() throws ClockException
	{
		return aliveness;
	}
	
	
	public static CachedNetworkClock createInstance() throws ClockException
	{
		String clockUrl = NetworkClock.getClockURL();
		CachedNetworkClock clock = new CachedNetworkClock(URI.create(clockUrl));
		return clock;
	}
	
	
	public static CachedNetworkClock createInstance(URI clockUri) throws ClockException
	{
		CachedNetworkClock clock = new CachedNetworkClock(clockUri);
		return clock;
	}
}
