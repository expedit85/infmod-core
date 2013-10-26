package br.ufrj.ppgi.greco.expedit.tese.commons.clock;

import java.util.Date;

public interface Clock {
	public Date getCurrentDate() throws ClockException;
	public long getCurrentTime() throws ClockException;
	public boolean isAlive() throws ClockException;
}
