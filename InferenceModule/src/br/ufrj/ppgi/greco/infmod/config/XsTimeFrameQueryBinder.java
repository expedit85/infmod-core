package br.ufrj.ppgi.greco.infmod.config;

import java.util.Map;

import br.ufrj.ppgi.greco.expedit.tese.commons.clock.Clock;
import br.ufrj.ppgi.greco.infmod.QueryParameterBinder;
import br.ufrj.ppgi.greco.infmod.impl.TimeFrameQueryBinder;

public class XsTimeFrameQueryBinder extends XsQueryParameterBinder {

	public String frameMillis;

	
	@Override
	public String toString() {
		return super.toString() + ", frmMs=" + (long) Utils.evaluateExpression(frameMillis);
	}


	@Override
	public QueryParameterBinder createQueryParameterBinder(Clock clock, Map<String, String> prefixesMap) {
		
		double frmLong = Utils.evaluateExpression(frameMillis);
		
		return new TimeFrameQueryBinder(clock, (long) frmLong);
	}

}
