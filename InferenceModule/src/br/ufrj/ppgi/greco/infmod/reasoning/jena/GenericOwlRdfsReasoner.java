package br.ufrj.ppgi.greco.infmod.reasoning.jena;

import java.util.List;

import com.hp.hpl.jena.reasoner.rulesys.GenericRuleReasoner;
import com.hp.hpl.jena.reasoner.rulesys.OWLFBRuleReasoner;
import com.hp.hpl.jena.reasoner.rulesys.Rule;

public class GenericOwlRdfsReasoner extends GenericRuleReasoner {
	
	private static List <Rule> loadRules(List<Rule> ruleList) {
		List<Rule> ruleList2 = OWLFBRuleReasoner.loadRules();
		ruleList2.addAll(ruleList);
		return ruleList2;
	}

	public GenericOwlRdfsReasoner(List<Rule> rules) {
		super(loadRules(rules));
		setOWLTranslation(true);               // not needed in RDFS case
		setTransitiveClosureCaching(true);
	}
}
