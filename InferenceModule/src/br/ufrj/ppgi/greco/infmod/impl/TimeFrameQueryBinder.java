package br.ufrj.ppgi.greco.infmod.impl;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import br.ufrj.ppgi.greco.expedit.tese.commons.clock.Clock;
import br.ufrj.ppgi.greco.expedit.tese.commons.clock.ClockException;
import br.ufrj.ppgi.greco.infmod.QueryParameterBinder;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.ResourceFactory;


public class TimeFrameQueryBinder implements QueryParameterBinder {
	
	private Clock clk;
	private long measureFrameMillis;
	
	public TimeFrameQueryBinder(Clock clock, long measureFrameMillis) {
		this.clk = clock;
		this.measureFrameMillis = measureFrameMillis;
	}
	

	@Override
	public List<Query> bind(Query query) {

		try {
			QueryReplacement qr = new QueryReplacement(query);
			
			long finalTime = clk.getCurrentTime();
			long startTime = finalTime - measureFrameMillis;

			Calendar c = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

			c.setTimeInMillis(startTime);
			Literal startDate = ResourceFactory.createTypedLiteral(c);
			qr.setParameter("startDate", startDate);

			c.setTimeInMillis(finalTime);
			Literal finalDate = ResourceFactory.createTypedLiteral(c);
			qr.setParameter("finalDate", finalDate);

//			// DEBUG
//			String sql = "select count(*) " +
//			"from measure " +
//			"where date > '" + startDate.getLexicalForm().replaceAll("T", " ").replace("Z", "") + "' " +
//			"and date < '" + finalDate.getLexicalForm().replaceAll("T", " ").replace("Z", "") + "' " +
//			"and measurement > 0;";
//
//			SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
//			fmt.setTimeZone(TimeZone.getTimeZone("UTC"));
//			System.out.println(
//					"At " + fmt.format(clk.getCurrentDate()) +
//					"\nSQL equivalent: " + sql +
//					"\nSPARQL equivalente: " + createQueryFromString());
//			// END DEBUG

			return QueryReplacement.createQueryList(qr.getQuery());

		} catch (ClockException e) {
			throw new RuntimeException(e);
		}
	}


	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}
}
