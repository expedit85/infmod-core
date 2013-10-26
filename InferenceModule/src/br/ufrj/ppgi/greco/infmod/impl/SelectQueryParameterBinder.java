package br.ufrj.ppgi.greco.infmod.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

import br.ufrj.ppgi.greco.expedit.tese.commons.sparql.SparqlExecutor;
import br.ufrj.ppgi.greco.infmod.QueryParameterBinder;

public class SelectQueryParameterBinder implements QueryParameterBinder {

	private SparqlExecutor sparqlExecutor;
	
	private final Query selectQuery;
		
	private String description;

	
	public SelectQueryParameterBinder(
				SparqlExecutor sparqlExecutor,
				Query query,
				String description)
	{
		super();
		this.sparqlExecutor = sparqlExecutor;
		this.selectQuery = query;
		this.description = description;
	}



	@Override
	public List<Query> bind(Query query) {
		
		List <Query> queries = new ArrayList<Query>();
		
		ResultSet rs = sparqlExecutor.execSelect(selectQuery);
		
		while (rs.hasNext())
		{
			QueryReplacement qr = new QueryReplacement(query);
			
			QuerySolution qs = rs.next();
			Iterator<String> varIt = qs.varNames();
			
			while (varIt.hasNext()) {
				String varName = varIt.next();
				if (qs.get(varName).isLiteral()) qr.setParameter(varName, qs.getLiteral(varName));
				else if (qs.get(varName).isResource()) qr.setParameter(varName, qs.getResource(varName));
			}
			
			queries.add(qr.getQuery());
		}
		
		return queries;
	}
	
	
	public String getDescription() {
		return description;
	}
}
