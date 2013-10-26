package br.ufrj.ppgi.greco.infmod.impl;

import java.util.ArrayList;
import java.util.List;

import br.ufrj.ppgi.greco.expedit.tese.commons.sparql.SparqlExecutor;
import br.ufrj.ppgi.greco.infmod.QueryParameterBinder;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

public class OffsetSplitterQueryParameterBinder implements QueryParameterBinder {

	private SparqlExecutor sparqlExecutor;
	
	private final Query selectQuery;
			
	private String description;
	
	private Integer blockSize;
	
	
	public OffsetSplitterQueryParameterBinder(
				SparqlExecutor sparqlExecutor,
				Query selectQuery,
				Integer blockSize,
				String description)
	{
		super();
		this.sparqlExecutor = sparqlExecutor;
		this.selectQuery = selectQuery;
		this.blockSize = blockSize;
		this.description = description;
	}

	@Override
	public List<Query> bind(Query query)
	{
		List <Query> queries = new ArrayList<Query>();
		
		//System.out.println("sec query:\n" + selectQuery.toString());
		
		ResultSet rs = sparqlExecutor.execSelect(selectQuery);

		if (rs.hasNext())
		{
			String srcVarName = selectQuery.getResultVars().get(0);
			
			QuerySolution qs = rs.next();
			int upperBound = qs.getLiteral(srcVarName).getInt();

			//System.out.println("count = " + upperBound);
			
			for (int offset = 0; offset < upperBound; offset += blockSize)
			{
				Query q = query.cloneQuery();
				q.setOffset(offset);
				q.setLimit(blockSize);
				//System.out.println("pri query:\n" + q.toString());
				queries.add(q);
			}
		}
		
		return queries;
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return description;
	}

}
