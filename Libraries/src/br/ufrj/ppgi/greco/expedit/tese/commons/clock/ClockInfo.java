package br.ufrj.ppgi.greco.expedit.tese.commons.clock;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

import br.ufrj.ppgi.greco.expedit.tese.commons.UTCXslDateTimeFormat;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.SingleValueConverter;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class ClockInfo {

	public Double speedRate;
	public Date startDate;
	public Date finalDate;
	public Float interactionAverageTime;
	public Boolean isAlive;
	public Date currentDate;
	public Long currentTime;
	
	
	// POJO
	public ClockInfo() {
		// TODO Auto-generated constructor stub
	}
	
	
	
	private static XStream xs;
	private static DateFormat fmt;
	
	static {
		fmt = new UTCXslDateTimeFormat();
				
		xs = new XStream(new DomDriver());
		xs.alias("ClockInfo", ClockInfo.class);
		xs.registerConverter(new SingleValueConverter() {
			@SuppressWarnings("rawtypes")
			@Override
			public boolean canConvert(Class clazz) {
				return Date.class.equals(clazz);
			}
			
			@Override
			public String toString(Object o) {
				return fmt.format((Date)o);
			}
			
			@Override
			public Object fromString(String s) {
				try {
					return fmt.parse(s);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				return null;
			}
		});
	}
	
	public static synchronized String toXML(ClockInfo clock) {
		return xs.toXML(clock);
	}
	
	
	public static synchronized ClockInfo fromXML(String xml) {
		return (ClockInfo) xs.fromXML(xml);
	}
};