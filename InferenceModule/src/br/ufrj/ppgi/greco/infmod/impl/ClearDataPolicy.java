package br.ufrj.ppgi.greco.infmod.impl;

import br.ufrj.ppgi.greco.infmod.UpdatePolicy;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class ClearDataPolicy implements UpdatePolicy {

	@Override
	public Model getInitialModel(Model curModel) {
		return ModelFactory.createDefaultModel();
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
		return false;
	}

}
