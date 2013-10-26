package br.ufrj.ppgi.greco.expedit.tese.commons;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class UTCXslDateTimeFormat extends SimpleDateFormat {

	private static final long serialVersionUID = 6223198982332827442L;
	
	private static TimeZone utcTz = TimeZone.getTimeZone("UTC");
	
	
	public UTCXslDateTimeFormat(String format) {
		super(format);
		this.setTimeZone(utcTz);
	}	

	public UTCXslDateTimeFormat() {
		super("yyyy-MM-dd'T'HH:mm:ss'Z'");
		this.setTimeZone(utcTz);
	}
	
	
	public static TimeZone getUTCTimeZone() {
		return utcTz;
	}
	
	public static Calendar getUTCCalendar() {
		return Calendar.getInstance(utcTz);
	}
	
	public static Calendar getUTCCalendar(long millis) {
		Calendar c = Calendar.getInstance(utcTz);
		c.setTimeInMillis(millis);
		return c;
	}
}
