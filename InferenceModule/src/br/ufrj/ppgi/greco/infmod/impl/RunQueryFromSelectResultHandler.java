package br.ufrj.ppgi.greco.infmod.impl;

import java.util.Iterator;
import java.util.List;

import br.ufrj.ppgi.greco.expedit.tese.commons.sparql.SparqlExecutor;
import br.ufrj.ppgi.greco.infmod.QueryParameterBinder;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class RunQueryFromSelectResultHandler extends DefaultResultHandler {

	private SparqlExecutor sparqlExecutor;
	private Query query;

	
	public RunQueryFromSelectResultHandler(SparqlExecutor sparqlExecutor, Query query) {
		super();
		this.sparqlExecutor = sparqlExecutor;
		this.query = query;
	}

	
	static class MyBinder implements QueryParameterBinder {
		
		private QuerySolution qs;

		public MyBinder(QuerySolution qs) {
			this.qs = qs;
		}
		
		@Override
		public List<Query> bind(Query query) {
			
			QueryReplacement qr = new QueryReplacement(query);
			
			Iterator<String> varIt = qs.varNames();
			
			while (varIt.hasNext()) {
				String varName = varIt.next();
				if (qs.get(varName).isLiteral()) qr.setParameter(varName, qs.getLiteral(varName));
				else if (qs.get(varName).isResource()) qr.setParameter(varName, qs.getResource(varName));
			}

			return QueryReplacement.createQueryList(qr.getQuery());
		}

		@Override
		public String getDescription() {
			// TODO Auto-generated method stub
			return null;
		}
	}


	@Override
	public Model onSelectResponse(Query query, ResultSet result) {

		Model m = ModelFactory.createDefaultModel();
		
		while (result.hasNext()) {

			QuerySolution qs = result.next();
			
			MyBinder binder = new MyBinder(qs);
			Query q = binder.bind(this.query).get(0);
			
			System.out.println(q);
			System.out.println("======");
			
			if (q.isDescribeType()) {
				Model m1 = sparqlExecutor.execDescribe(q);
				m.add(m1);
			}
			else if (q.isConstructType()) {
				Model m1 = sparqlExecutor.execConstruct(q);
				m.add(m1);
			}
			else { }
		}
		
		return m;
	}
	
}
