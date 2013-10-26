package br.ufrj.ppgi.greco.infmod.impl;

import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Resource;


/**
 * Replaces query variable by a given Resource (its URI) or Literal (its lexical form).
 * Provides methods which construct query and query list objects. 
 *
 * @author expedit
 *
 */
public class QueryReplacement
{
	private String queryStr;

	
	public QueryReplacement(Query initialQuery) {
		this.queryStr = initialQuery.toString();
	}
	
	public QueryReplacement(String initialQueryString) {
		this.queryStr = initialQueryString;
	}
	
	
	private void setParameter(String varName, String value) {
		
		String varRegex = "[\\?$]" + varName + "([ .;,\\)}\\s])";
		String replacement = value + "$1";

		queryStr = queryStr.replaceAll(varRegex, replacement);
	}

	public void setParameter(String varName, Literal literal) {
		
//			System.out.println(literal.getLexicalForm());
//			System.out.println(literal.getLanguage());
//			System.out.println(literal.getDatatypeURI());
//			System.out.println(literal.getString());
		
		String value = "\"" + literal.getLexicalForm() + "\"";
		
		if (!literal.getLanguage().equals("")) value += "@" + literal.getLanguage();
		if (literal.getDatatype() != null) value += "^^<" + literal.getDatatypeURI() + ">";
		
		setParameter(varName, value);
	}
	
	public void setParameter(String varName, Resource resource) {
		
		setParameter(varName, "<"+resource.toString()+">");
	}
	
	
	public Query getQuery() {
		return QueryFactory.create(queryStr);
	}
	
	
	public List<Query> getQueryAsList()
	{
		ArrayList<Query> qlist = new ArrayList<Query>();
		qlist.add(getQuery());			
		return qlist;
	}

	public static List<Query> createQueryList(Query ... queries)
	{
		ArrayList<Query> qlist = new ArrayList<Query>();
		for (Query q : queries) qlist.add(q);			
		return qlist;
	}
}
