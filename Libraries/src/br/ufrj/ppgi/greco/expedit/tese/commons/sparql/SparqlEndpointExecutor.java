package br.ufrj.ppgi.greco.expedit.tese.commons.sparql;

import java.net.URI;

import com.hp.hpl.jena.query.ARQ;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.sparql.engine.http.QueryEngineHTTP;

public class SparqlEndpointExecutor implements SparqlExecutor {

	private URI endpointUri;
	private String username;
	private String password;
	
	
	private QueryExecution createQueryExecution(Query query) {
		
		ARQ.set(ARQ.useSAX, true);
		
		if (username == null || password == null) {
			return QueryExecutionFactory.sparqlService(endpointUri.toString(), query);		
		}
		else {
			QueryEngineHTTP qe = QueryExecutionFactory.createServiceRequest(endpointUri.toString(), query);
			qe.setBasicAuthentication(username, password.toCharArray());
			return qe;
		}
	}
	
	
	public SparqlEndpointExecutor(URI endpointUri) {
		this.endpointUri = endpointUri;
	}
	
	public SparqlEndpointExecutor(URI endpointUri, String username, String password) {
		this.endpointUri = endpointUri;
		this.username = username;
		this.password = password != null ? password : "";
	}
	
	
	@Override
	public ResultSet execSelect(Query query) {
		QueryExecution qe = createQueryExecution(query);
		return qe.execSelect();
	}

	@Override
	public Model execDescribe(Query query) {
		QueryExecution qe = createQueryExecution(query);
		return qe.execDescribe();
	}

	@Override
	public Model execConstruct(Query query) {
		QueryExecution qe = createQueryExecution(query);
		return qe.execConstruct();
	}

	@Override
	public boolean execAsk(Query query) {
		QueryExecution qe = createQueryExecution(query);
		return qe.execAsk();
	}

}
