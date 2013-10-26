package br.ufrj.ppgi.greco.expedit.tese.clockserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.eclipse.jetty.http.MimeTypes;

import br.ufrj.ppgi.greco.expedit.tese.commons.UTCXslDateTimeFormat;
import br.ufrj.ppgi.greco.expedit.tese.commons.clock.ClockInfo;
import br.ufrj.ppgi.greco.expedit.tese.commons.clock.SimulatedClock;

import com.hp.hpl.jena.query.ARQ;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.sun.jersey.spi.resource.Singleton;



@Singleton
@Path ("/")
public class ClockResource {
	
	static {
		ARQ.init();
	}
	
	private SimulatedClock clock;
	private Date startDate;
	private Date finalDate;
	private Double clockSpeed = SimulatedClock.SPEED_MINUTE;
	private DateFormat fmt;
	private static TimeZone tzUTC = TimeZone.getTimeZone("UTC");


	public static Date createDate(int y, int m, int d, int h, int min, int s) {
		Calendar c = Calendar.getInstance(tzUTC);
		c.set(y, m-1, d, h, min, s);
		return c.getTime();
	}
	
	
	public static SimulatedClock startNewClock(Date startDate, Date finalDate, Double clockSpeed) {
		
		SimulatedClock clock = new SimulatedClock(startDate, finalDate);
		clock.setSpeedRate(clockSpeed);
		clock.start();
		
		return clock;
	}	
	
	
	public ClockResource() {
		fmt = new UTCXslDateTimeFormat();
		
		startDate = createDate(2010,1,1,0,0,0);
		finalDate = createDate(2010,6,30,23,59,59);
		clock     = startNewClock(startDate, finalDate, clockSpeed);
		
		System.out.format("Clock created: (%s until %s at %f\n",
				fmt.format(startDate),
				fmt.format(finalDate),
				clockSpeed);
	}
	
	
	@GET
	@Produces (MimeTypes.TEXT_HTML)
	public synchronized String getDefault() {
		return getHelp();
	}
	
	@GET
	@Path ("/datetime")
	@Produces ("text/plain")
	public synchronized String getDate() {
		return fmt.format(clock.getCurrentDate());
	}
	
	
	@GET
	@Path ("/milliseconds")
	@Produces ("text/plain")
	public synchronized String getTime() {
		return "" + clock.getCurrentTime();
	}
	
	
	@GET
	@Path ("/restart")
	@Produces ("text/plain")
	public synchronized String restart() {
		try {
			clock.finish();
			clock.join();
			clock = startNewClock(startDate, finalDate, clockSpeed);
			
			System.out.println("Clock restarted.");

		} catch (InterruptedException e) {
			throw new WebApplicationException(e, 500);
		}
		
		return getDate();
	}
	
	
	@GET
	@Path ("/configure")
	@Produces ("text/plain")
	public synchronized String configure(
			@QueryParam ("startdate")   String startDate,
			@QueryParam ("finaldate")   String finalDate,
			@QueryParam ("speedrate")   String clockSpeed) {

		try {
			if (startDate == null || finalDate == null) {
				clock.setSpeedRate(Double.parseDouble(clockSpeed));

				System.out.format("Current clock speed modified: at %s\n", clockSpeed);

				return getDate();
			}
			else {
				this.startDate  = fmt.parse(startDate);
				this.finalDate  = fmt.parse(finalDate);
				this.clockSpeed = Double.parseDouble(clockSpeed);

				System.out.format("Global reconfiguration: %s until %s at %s\n", startDate, finalDate, clockSpeed);
				
				return restart();
			}
			
		} catch (NumberFormatException e) {
			throw new WebApplicationException(e, 500);
		} catch (ParseException e) {
			throw new WebApplicationException(e, 500);
		}
	}
	
	
	@GET
	@Path ("/isalive")
	@Produces ("text/plain")
	public synchronized String isAlive() {
		return "" + clock.isAlive();
	}
	

	
	@GET
	@Path ("/info")
	@Produces (MimeTypes.TEXT_XML)
	public synchronized String getInfo() {
		
		ClockInfo info = new ClockInfo();

		info.speedRate = clock.getSpeedRate();
		info.startDate = this.startDate;
		info.finalDate = this.finalDate;
		info.interactionAverageTime = clock.getIteractionAverageTime();
		info.isAlive = clock.isAlive();
		info.currentDate = clock.getCurrentDate();
		info.currentTime = clock.getCurrentTime();
		
		return ClockInfo.toXML(info);
	}
	
	
	@GET
	@Path ("/help")
	@Produces (MimeTypes.TEXT_HTML)
	public synchronized String getHelp() {

		try {
			InputStream is = ClassLoader.getSystemResourceAsStream("help.html");
			InputStreamReader isr = new InputStreamReader(is, Charset.forName("UTF-8"));
			BufferedReader br = new BufferedReader(isr);
			StringBuilder sb = new StringBuilder();
			
			String line = null;
			try {
				while ((line = br.readLine()) != null) {
					sb.append(line);
					sb.append('\n');
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			br.close();
			
			return sb.toString();
			
		} catch (Exception e) {
			throw new WebApplicationException(e, 500);
		}
	}
	
	
	@GET
	@Path ("/admin")
	@Produces (MimeTypes.TEXT_HTML)
	public synchronized String getAdmin() {

		try {
			InputStream is = ClassLoader.getSystemResourceAsStream("admin.html");
			InputStreamReader isr = new InputStreamReader(is, Charset.forName("UTF-8"));
			BufferedReader br = new BufferedReader(isr);
			StringBuilder sb = new StringBuilder();
			
			String line = null;
			try {
				while ((line = br.readLine()) != null) {
					sb.append(line);
					sb.append('\n');
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			br.close();
			
			return sb.toString();
			
		} catch (Exception e) {
			throw new WebApplicationException(e, 500);
		}
	}


	@POST
	@Path ("/sparql")
	@Produces ( { "application/sparql-results+xml", MimeTypes.TEXT_JSON } )
	public synchronized Response doPostSparqlDateTime(
			@FormParam ("query") String queryStr,
			@FormParam ("output") String output ) {
		return doGetSparqlDataTime(queryStr, output);
	}

	
	/**
	 * Sample: http://localhost:3030/sparql?query=select%20%3Fdate&output=xml
	 * 
	 * @param queryStr
	 * @param output
	 * @return
	 */
	@GET
	@Path ("/sparql")
	@Produces ( { "application/sparql-results+xml", MimeTypes.TEXT_JSON } )
	public synchronized Response doGetSparqlDataTime(
			@QueryParam ("query") String queryStr,
			@QueryParam ("output") String output ) {

		try {
			String xmlResult = "<?xml version=\"1.0\"?>" +
					 "<sparql xmlns=\"http://www.w3.org/2005/sparql-results#\">" +
					 "<head><variable name=\"date\"/></head>" +
					 "<results>" +
					 "<result><binding name=\"date\">" +
					 "<literal datatype=\"http://www.w3.org/2001/XMLSchema#dateTime\">%s</literal>" +
					 "</binding></result>" +
					 "</results>" +
					 "</sparql>";

			String jsonResult = "{ \"head\": { \"vars\": [ \"date\" ] } , " +
					"\"results\": { \"bindings\": [ { \"date\": { \"type\": \"literal\" , \"value\": \"%s\" } } ] } }";
			
			String result = "json".equals(output) ? jsonResult : xmlResult;

			Query query = QueryFactory.create(queryStr);

			if (query.isSelectType()) {
				List<String> vars = query.getResultVars();

				if (vars.size() != 1 || !"date".equals(vars.get(0))) {
					throw new WebApplicationException(500);
				}
				else {
					String type = "json".equals(output) ?
							MimeTypes.TEXT_JSON :
							"application/sparql-results+xml";
					String res = String.format(result, fmt.format(clock.getCurrentDate()));
					return Response.ok().entity(res).type(type).build();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new WebApplicationException(500);
		}

		throw new WebApplicationException(500);
	}
}

