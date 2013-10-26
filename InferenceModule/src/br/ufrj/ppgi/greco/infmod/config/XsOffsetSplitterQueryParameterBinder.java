package br.ufrj.ppgi.greco.infmod.config;

import java.net.URI;
import java.util.Map;

import com.hp.hpl.jena.query.QueryFactory;

import br.ufrj.ppgi.greco.expedit.tese.commons.clock.Clock;
import br.ufrj.ppgi.greco.expedit.tese.commons.sparql.SparqlEndpointExecutor;
import br.ufrj.ppgi.greco.infmod.QueryParameterBinder;
import br.ufrj.ppgi.greco.infmod.impl.OffsetSplitterQueryParameterBinder;

public class XsOffsetSplitterQueryParameterBinder extends XsQueryParameterBinder
{
	public String description;
	public Integer limit;
	public String endpoint;
	public String query;

	
	@Override
	public QueryParameterBinder createQueryParameterBinder(Clock clock, Map<String, String> prefixesMap)
	{
		query = Utils.serializedSparqlPrefixes(prefixesMap) + query;
		
		return new OffsetSplitterQueryParameterBinder(
						new SparqlEndpointExecutor(URI.create(endpoint)),
						QueryFactory.create(query),
						limit,
						description);
	}

}
