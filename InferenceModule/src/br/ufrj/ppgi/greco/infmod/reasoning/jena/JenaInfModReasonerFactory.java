package br.ufrj.ppgi.greco.infmod.reasoning.jena;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import br.ufrj.ppgi.greco.expedit.tese.commons.clock.Clock;
import br.ufrj.ppgi.greco.infmod.InfModReasonerFactory;
import br.ufrj.ppgi.greco.infmod.reasoning.jena.builtins.JenaReasonerBuiltinsLoader;

import com.hp.hpl.jena.Jena;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.ReasonerRegistry;
import com.hp.hpl.jena.reasoner.rulesys.GenericRuleReasoner;
import com.hp.hpl.jena.reasoner.rulesys.Rule;
import com.hp.hpl.jena.util.FileManager;

public class JenaInfModReasonerFactory implements InfModReasonerFactory <Rule> {

	protected static final String URI = "http://greco.ppgi.ufrj.br/infmod/reasonerfactory#jena";
	
	private List <String> ontologies;
	private Model schema;
	private List<Rule> rules = null;
	
	
	public JenaInfModReasonerFactory(Clock clock) {
		ontologies = new ArrayList<String>();
		schema = ModelFactory.createDefaultModel();
		rules = new ArrayList<Rule>();
		

		JenaReasonerBuiltinsLoader.load(clock);
	}

	

	@Override
	public void addRule(Rule rule) {
		this.rules.add(rule);
	}
	
	public void addRules(List<Rule> rules) {
		this.rules.addAll(rules);
	}
	
	public void setRules(List<Rule> rules) {
		this.rules = rules;
	}
	
	public List<Rule> getRules() {
		return this.rules;
	}
	
	
	@Override
	public void addOntology(String uri) {
		ontologies.add(uri);
	}
	
	
	private boolean loadSchema()
	{
		boolean useOwl = false;
		if (ontologies.size() > 0)
		{
			int size = 0;
			useOwl = true;
			for (String uri : ontologies)
			{
				Model m = null;
				
				m = ModelFactory.createOntologyModel();
				
				//m = ModelFactory.createDefaultModel();
				//m.read(uri);
				m = FileManager.get().readModel(m, uri);//current
				//m = FileManager.get().loadModel(uri);	//rev230
				schema.add(m);
								
				size += m.size();
			}
			
			try {
				schema.write(new FileWriter("schema.turtle"), "TURTLE");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			System.out.println("At least " + Math.max(size, schema.size()) 
					+ " triples were added to the schema (TBox)");
		}
		return useOwl;
	}



	@Override
	public Reasoner create(Resource configuration)
	{
		Reasoner reasoner = null;

		boolean useRules = (rules != null) && (!rules.isEmpty());

		boolean useOwl = loadSchema();
		
		if (useOwl && useRules)
		{
			reasoner = new GenericOwlRdfsReasoner(rules);
			reasoner = reasoner.bindSchema(schema);
		}
		else if (useOwl && !useRules)
		{
			reasoner = ReasonerRegistry.getOWLReasoner();
			reasoner = reasoner.bindSchema(schema);
		}
		else if (!useOwl && useRules)
		{
			reasoner = new GenericRuleReasoner(rules);
		}
		else
		{
			reasoner = ReasonerRegistry.getRDFSSimpleReasoner();//acho que eh o mais simples
		}
		
		return reasoner;
	}

	@Override
	public Model getCapabilities() {
		return null;
	}

	@Override
	public String getURI() {
		return URI;
	}



	@Override
	public String getDescription()
	{
		return String.format(
				"Jena %s (build date: %s)",
					Jena.VERSION, Jena.BUILD_DATE);
	}
}
