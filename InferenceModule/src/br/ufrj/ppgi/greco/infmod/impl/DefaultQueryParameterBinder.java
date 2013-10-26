package br.ufrj.ppgi.greco.infmod.impl;

import java.util.List;

import br.ufrj.ppgi.greco.infmod.QueryParameterBinder;

import com.hp.hpl.jena.query.Query;


// TODO colocar metodos uteis ou que permitem fazer bind estatico

/**
 * Offers a set of methods for easing query parameter substituting
 */
public class DefaultQueryParameterBinder implements QueryParameterBinder {
	
	
	/**
	 * Does nothing, just return a new unmodified query.
	 */
	@Override
	public List<Query> bind(Query query) {
		
		QueryReplacement qrep = new QueryReplacement(query);
		
		//...
		
		return QueryReplacement.createQueryList(qrep.getQuery());
	}


	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}
}
