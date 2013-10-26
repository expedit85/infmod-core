package br.ufrj.ppgi.greco.infmod.config;

import java.util.Map;

import br.ufrj.ppgi.greco.expedit.tese.commons.clock.Clock;
import br.ufrj.ppgi.greco.infmod.QueryParameterBinder;
import br.ufrj.ppgi.greco.infmod.impl.DefaultQueryParameterBinder;


//@XmlRootElement(name="InfMod")
//@XmlAccessorType(XmlAccessType.FIELD)
public class XsDefaultQueryParameterBinder extends XsQueryParameterBinder {
	
	
	@Override
	public String toString() {
		return this.getClass().getSimpleName();
	}

	@Override
	public QueryParameterBinder createQueryParameterBinder(Clock clock, Map<String, String> prefixesMap) {
		return new DefaultQueryParameterBinder();
	}

}
