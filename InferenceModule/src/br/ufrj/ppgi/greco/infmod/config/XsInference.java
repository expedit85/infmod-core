package br.ufrj.ppgi.greco.infmod.config;

import java.util.List;

import br.ufrj.ppgi.greco.expedit.tese.commons.clock.Clock;
import br.ufrj.ppgi.greco.infmod.reasoning.jena.JenaInfModReasonerFactory;
import br.ufrj.ppgi.greco.infmod.reasoning.pellet.PelletInfModReasonerFactory;

import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.rulesys.Rule;

public class XsInference {
	
	public String reasoner;

	public List<String> ontologies;
	
	public String rules;
	
	
	public Reasoner createReasoner(Clock clock) {
				
		if ("PELLET".equalsIgnoreCase(reasoner))
		{
			PelletInfModReasonerFactory factory = new PelletInfModReasonerFactory();
			
			if (ontologies != null) {
				for (String onto : ontologies) {
					factory.addOntology(onto);
				}
			}
			
			System.out.println(factory.getDescription());
			
			return factory.create(null);
		}
		else	// JENA
		{
			JenaInfModReasonerFactory factory = new JenaInfModReasonerFactory(clock);
			
			if (ontologies != null) {
				for (String onto : ontologies) {
					factory.addOntology(onto);
				}
			}
			
			if (rules != null) factory.addRules(Rule.parseRules(rules));
			
			System.out.println(factory.getDescription());
			
			return factory.create(null);
			
		}
	}
}
