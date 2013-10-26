package br.ufrj.ppgi.greco.expedit.tese.commons.sparql;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;

public class SparqlModelExecutor implements SparqlExecutor {

	private Model model;
	
	
	public SparqlModelExecutor(Model model) {
		super();
		this.model = model;
	}

	public void setModel(Model model) {
		this.model = model;
	}
	
	@Override
	public ResultSet execSelect(Query query) {
		QueryExecution qe = QueryExecutionFactory.create(query, model);
		return qe.execSelect();
	}

	@Override
	public Model execDescribe(Query query) {
		QueryExecution qe = QueryExecutionFactory.create(query, model);
		return qe.execDescribe();
	}

	@Override
	public Model execConstruct(Query query) {
		QueryExecution qe = QueryExecutionFactory.create(query, model);
		return qe.execConstruct();
	}

	@Override
	public boolean execAsk(Query query) {
		QueryExecution qe = QueryExecutionFactory.create(query, model);
		return qe.execAsk();
	}

}
