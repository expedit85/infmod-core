package br.ufrj.ppgi.greco.infmod.config;

import java.net.URI;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;

import br.ufrj.ppgi.greco.expedit.tese.commons.sparql.SparqlEndpointExecutor;
import br.ufrj.ppgi.greco.expedit.tese.commons.sparql.SparqlExecutor;
import br.ufrj.ppgi.greco.infmod.ResultHandler;
import br.ufrj.ppgi.greco.infmod.impl.RunQueryFromSelectResultHandler;

public class XsRunQueryFromSelectResultHandler extends XsResultHandler {
	
	public String endpoint;
	public String query;

	@Override
	public ResultHandler createResultHandler() {
		
		SparqlExecutor sparqlExecutor = new SparqlEndpointExecutor(URI.create(endpoint));
		Query q = QueryFactory.create(query);
		
		return new RunQueryFromSelectResultHandler(sparqlExecutor, q);
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName();
	}
}
