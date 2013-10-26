package br.ufrj.ppgi.greco.infmod;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;


// TODO deveria passar bound query como parametro tambem?
public interface ResultHandler {

	public Model onSelectResponse(Query query, ResultSet result);

	public Model onAskResponse(Query query, boolean result);
	
	public Model onDescribeResponse(Query query, Model result);
	
	public Model onConstructResponse(Query query, Model result);
}
