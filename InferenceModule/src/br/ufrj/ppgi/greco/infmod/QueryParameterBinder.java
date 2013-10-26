package br.ufrj.ppgi.greco.infmod;

import com.hp.hpl.jena.query.Query;
import java.util.List;

public interface QueryParameterBinder {

	/**
	 * Return a new query whose variables were substituted to their values.
	 * Classes should implement this method by using the following code:
	 * <pre>
	 *   QueryReplacement qrep = new QueryReplacement(query);
	 *  
	 *   String var1 = &lt;variable name>;
	 *   Literal value1 = &lt;create a literal object>; // could be a Resource too
	 *   qrep.setParameter(var1, value1);
	 *  
	 *   // more setParameter's here ...
	 *   
	 *   // more QueryReplacement's creation here ...
	 *  
	 *   List &lt;Query> queries = qrep.getQueryAsList();
	 *   queries.add(&lt;other bound query:)
	 *   return queries;
	 * </pre>
	 * 
	 * @param query input query
	 * @return the new query
	 */
	public List<Query> bind(Query query);
	
	
	
	/**
	 * QueryParameterBinder's description.
	 * @return A description of what this object does.
	 */
	public String getDescription();
}
