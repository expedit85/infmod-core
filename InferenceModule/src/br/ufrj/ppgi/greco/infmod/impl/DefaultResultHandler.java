package br.ufrj.ppgi.greco.infmod.impl;

import br.ufrj.ppgi.greco.infmod.ResultHandler;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;


public class DefaultResultHandler implements ResultHandler {

	@Override
	public Model onSelectResponse(Query query, ResultSet result) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Model onAskResponse(Query query, boolean result) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Model onDescribeResponse(Query query, Model result) {
		// TODO Auto-generated method stub
		return result;
	}

	@Override
	public Model onConstructResponse(Query query, Model result) {
		// TODO Auto-generated method stub
		return result;
	}

}
