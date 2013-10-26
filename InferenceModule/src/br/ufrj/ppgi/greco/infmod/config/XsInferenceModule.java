package br.ufrj.ppgi.greco.infmod.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.ufrj.ppgi.greco.expedit.tese.commons.clock.Clock;
import br.ufrj.ppgi.greco.infmod.InferenceModule;
import br.ufrj.ppgi.greco.infmod.Loader;
import br.ufrj.ppgi.greco.infmod.SparqlServer;
import br.ufrj.ppgi.greco.infmod.UpdatePolicy;

import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.util.PrintUtil;

//@XmlRootElement(name="InfMod")
//@XmlAccessorType(XmlAccessType.FIELD)
//@XmlSeeAlso ( value={ PullLoader.class })
public class XsInferenceModule {

	public String name;

	public List <XsPrefix> prefixes;

	public String resourceBaseUri;
	
	public XsClock clock;
	
	public XsUpdatePolicy updatePolicy;

	// @XmlElements ({@XmlElement(type=PullLoader.class)})
	public List<XsLoader> loaders;

	// @XmlElement
	public XsInference inference;

	public XsSparqlServer sparqlServer;

	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("name: ");
		sb.append(name);
		sb.append('\n');
		sb.append("loaders:\n");
		for (XsLoader l : loaders)
			sb.append(l.toString());
		sb.append('\n');
		sb.append("rules:\n\t");
		sb.append(inference.rules.trim().replaceAll("[\\t ]+\\n+[\\t ]+", "\n"));
		return sb.toString();
	}

	public InferenceModule createInferenceModule(Clock clock)
	{
		InferenceModule infmod = new InferenceModule(resourceBaseUri,
				updatePolicy.createUpdatePolicy(), clock);
		
		if (infmod.getUpdatePolicy().isCurrentModelChanged(UpdatePolicy.ALL_METHODS)
			&& sparqlServer.fastUpdate)
		{
			System.out.println("UpdatePolicy do not allow endpoint fast update. It was turned off.");
			sparqlServer.fastUpdate = false;
		}

		infmod.setName(name);

		Map <String, String> prefixesMap = new HashMap<String, String>();
		PrintUtil.registerPrefix("infmod", "http://greco.ppgi.ufrj.br/inferencemodule/vocabulary#");
		prefixesMap.put("infmod", "http://greco.ppgi.ufrj.br/inferencemodule/vocabulary#");
		
		if (prefixes != null) {
			System.out.print("Found prefixes:");
			for (XsPrefix p : prefixes) {
				PrintUtil.registerPrefix(p.prefix, p.namespace);
				prefixesMap.put(p.prefix, p.namespace);
				System.out.print(" " + p.prefix);
			}
			System.out.println();
		}
		infmod.setPrefixMap(prefixesMap);

		for (XsLoader l : loaders) {
			Loader loader = l.createLoader(clock, prefixesMap);
			infmod.addLoader(loader);
		}

		Reasoner reasoner = inference.createReasoner(clock);
		
		System.out.println("Reasoner class: " + reasoner.getClass().getCanonicalName());
		
		infmod.setReasoner(reasoner);

		SparqlServer server = sparqlServer.createSparqlServer();
		server.setPrefixesMap(prefixesMap);
		infmod.setSparqlServer(server);

		return infmod;
	}
}
