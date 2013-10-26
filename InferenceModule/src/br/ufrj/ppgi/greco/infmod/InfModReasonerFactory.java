package br.ufrj.ppgi.greco.infmod;

import java.util.List;

import com.hp.hpl.jena.reasoner.ReasonerFactory;

public interface InfModReasonerFactory<R> extends ReasonerFactory {

	public void addOntology(String uri);
		
	public void addRule(R rule);
	
	public void addRules(List<R> rules);
	
	public String getDescription();
}
