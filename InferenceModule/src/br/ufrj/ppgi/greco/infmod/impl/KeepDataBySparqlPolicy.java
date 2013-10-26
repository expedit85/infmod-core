package br.ufrj.ppgi.greco.infmod.impl;

import java.util.Arrays;
import java.util.List;

import br.ufrj.ppgi.greco.expedit.tese.commons.sparql.SparqlModelExecutor;
import br.ufrj.ppgi.greco.infmod.ResultHandler;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;


public class KeepDataBySparqlPolicy extends KeepDataPolicy {

	private List <Query> queries;
	private ResultHandler resultHandler;
	
	public KeepDataBySparqlPolicy(Query [] queries, ResultHandler resultHandler) {
		super(false);
		this.queries = Arrays.asList(queries);
		this.resultHandler = resultHandler;
	}

	@Override
	public Model getInitialModel(Model curModel) {
		
		Model model = ModelFactory.createDefaultModel();
		
		if (curModel == null) curModel = model;//poderia criar uma vazio, mas o resultado eh o mesmo
		SparqlModelExecutor sme = new SparqlModelExecutor(curModel);
		
		for (Query query : queries) {

			Model m = null;
			
			if (query.isSelectType()) {
				ResultSet rs = sme.execSelect(query);
				m = resultHandler.onSelectResponse(query, rs);
			}
			else if (query.isAskType()) {
				boolean res = sme.execAsk(query);
				m = resultHandler.onAskResponse(query, res);
			}
			else if (query.isConstructType()) {
				Model res = sme.execConstruct(query);
				m = resultHandler.onConstructResponse(query, res);
			}
			else if (query.isDescribeType()) {
				Model res = sme.execDescribe(query);
				m = resultHandler.onDescribeResponse(query, res);
			}
			
			if (m != null) model.add(m);

		}
		
		return model;
	}
	
	
	@Override
	public boolean isCurrentModelChanged(int methodsMask) {
		return super.isCurrentModelChanged(methodsMask);
	}
}
