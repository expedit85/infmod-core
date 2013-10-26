package br.ufrj.ppgi.greco.infmod.config;

import java.net.URI;
import java.util.List;
import java.util.Map;

import br.ufrj.ppgi.greco.expedit.tese.commons.clock.Clock;
import br.ufrj.ppgi.greco.expedit.tese.commons.sparql.SparqlEndpointExecutor;
import br.ufrj.ppgi.greco.expedit.tese.commons.sparql.SparqlExecutor;
import br.ufrj.ppgi.greco.infmod.Loader;
import br.ufrj.ppgi.greco.infmod.QueryConfig;
import br.ufrj.ppgi.greco.infmod.impl.PullLoader;


//@XmlRootElement(name="PullLoader")
//@XmlAccessorType(XmlAccessType.FIELD)
//@XmlSeeAlso(value={String.class})
public class XsPullLoader extends XsLoader {

	//@XmlElement
	public String description;
	
	//@XmlElement
	public String endpoint;
	
	//@XmlElements({@XmlElement(type=QueryConfig.class), @XmlElement(type=QueryData.class)})
	public List <XsQueryConfig> queryConfigs;
	
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();

		sb.append(description);
		sb.append(": ");
		sb.append(endpoint);
		sb.append("\nQueries: ");

		for (XsQueryConfig qc : queryConfigs) {
			sb.append("\n\t(");
			sb.append(qc.toString());
			sb.append(") ");
		}
		return sb.toString(); 
	}


	@Override
	public Loader createLoader(Clock clock, Map<String, String> prefixesMap) {

		SparqlExecutor sparqlExecutor = new SparqlEndpointExecutor(URI.create(endpoint));

		PullLoader loader = new PullLoader(description, clock, sparqlExecutor);

		for (XsQueryConfig qc : queryConfigs) {
			QueryConfig qc2 = qc.createQueryConfig(loader, prefixesMap);
			loader.addQueryConfig(qc2);
		}
		
		return loader;
	}
}
