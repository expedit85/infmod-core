package br.ufrj.ppgi.greco.infmod.config;

import java.net.URI;
import java.util.Map;

import br.ufrj.ppgi.greco.expedit.tese.commons.clock.Clock;
import br.ufrj.ppgi.greco.expedit.tese.commons.sparql.SparqlEndpointExecutor;
import br.ufrj.ppgi.greco.infmod.QueryParameterBinder;
import br.ufrj.ppgi.greco.infmod.impl.SelectQueryParameterBinder;

import com.hp.hpl.jena.query.QueryFactory;

public class XsSelectQueryParameterBinder extends XsQueryParameterBinder {
	
	public String description;
	
	public String endpoint;
	
	public String query;

	@Override
	public QueryParameterBinder createQueryParameterBinder(Clock clock, Map<String, String> prefixesMap) {
		
		query = Utils.serializedSparqlPrefixes(prefixesMap) + this.query; 

		SelectQueryParameterBinder binder = new SelectQueryParameterBinder(
				new SparqlEndpointExecutor(URI.create(endpoint)),
				QueryFactory.create(query),
				description
		);
		
		return binder;
	}

}
