package br.ufrj.ppgi.greco.infmod.config;

import java.util.Map;

import br.ufrj.ppgi.greco.infmod.Loader;
import br.ufrj.ppgi.greco.infmod.QueryConfig;
import br.ufrj.ppgi.greco.infmod.impl.PullLoader;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.Syntax;

//@XmlRootElement(name="QueryData")
//@XmlAccessorType(XmlAccessType.FIELD)
public class XsQueryData extends XsQueryConfig {

	//@XmlElement
	public String interval;
	
	
	@Override
	public String toString() {
		return super.toString() + ", interval=" + (long) Utils.evaluateExpression(this.interval);
	}

	
	
	public QueryConfig createQueryConfig(Loader loader, Map<String, String> prefixesMap) {

		PullLoader.QueryData qc2;

		PullLoader loader2 = (PullLoader) loader;

		// Adiciona prefixos a consulta.
		// TODO handle cases in which prefixes are yet defined.
		this.query = Utils.serializedSparqlPrefixes(prefixesMap) + this.query;
		
		//System.out.println(this.query);
		
		Query query = QueryFactory.create(this.query, Syntax.syntaxARQ);
		long intervalLong = Math.max((long) Utils.evaluateExpression(this.interval), 1000);
		qc2 = loader2.new QueryData(query, this.description, intervalLong);

		qc2.setBinder(binder.createQueryParameterBinder(loader2.getClock(), prefixesMap));
		if (resultHandler != null)
			qc2.setResultHandler(resultHandler.createResultHandler());
		else
			qc2.setResultHandler(null);

		return qc2;
	}
}
