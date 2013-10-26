package br.ufrj.ppgi.greco.infmod.config;

import java.util.Map;

import br.ufrj.ppgi.greco.expedit.tese.commons.clock.Clock;
import br.ufrj.ppgi.greco.infmod.QueryParameterBinder;

public abstract class XsQueryParameterBinder {

	@Override
	public String toString() {
		return this.getClass().getSimpleName();
	}

	
	public abstract QueryParameterBinder createQueryParameterBinder(Clock clock, Map<String, String> prefixesMap);
}
