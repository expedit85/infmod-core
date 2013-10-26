package br.ufrj.ppgi.greco.expedit.tese.commons.sparql;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;

public interface SparqlExecutor {
		
	public ResultSet execSelect(Query query);
	
	public Model execDescribe(Query query);
	
	public Model execConstruct(Query query);
	
	public boolean execAsk(Query query);
	
}
