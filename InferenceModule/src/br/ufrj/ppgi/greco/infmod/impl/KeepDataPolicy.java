package br.ufrj.ppgi.greco.infmod.impl;

import br.ufrj.ppgi.greco.infmod.UpdatePolicy;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class KeepDataPolicy implements UpdatePolicy {
	
	private boolean duplicate;
	
	public KeepDataPolicy(boolean duplicate) {
		this.duplicate = duplicate;
	}

	@Override
	public Model getInitialModel(Model curModel) {

		if (duplicate) {
			Model m2 = ModelFactory.createDefaultModel();
			m2.add(curModel.listStatements());
			return m2;
		}
		else {
			return curModel;
		}
	}

	@Override
	public Model getModelBeforeReasoning(Model curModel, Model queryModel) {
		return queryModel;
	}

	@Override
	public Model getPublishingModel(Model curModel, Model queryModel, Model inferredModel) {
		return inferredModel;
	}

	@Override
	public boolean isCurrentModelChanged(int methodsMask) {
		// TODO Auto-generated method stub
		return false;
	}
}
