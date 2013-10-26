package br.ufrj.ppgi.greco.infmod.config;

import java.util.List;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.Syntax;

import br.ufrj.ppgi.greco.infmod.UpdatePolicy;
import br.ufrj.ppgi.greco.infmod.impl.KeepDataBySparqlPolicy;


public class XsKeepDataBySparqlPolicy extends XsUpdatePolicy {

	public List <String> queries;
	public XsResultHandler resultHandler;

	@Override
	public UpdatePolicy createUpdatePolicy() {

		Query [] qs = new Query[queries.size()];
		for (int i = 0; i < queries.size(); i++)
			qs[i] = QueryFactory.create(queries.get(i), Syntax.syntaxARQ);

		return new KeepDataBySparqlPolicy(qs, resultHandler.createResultHandler());
	}
}
