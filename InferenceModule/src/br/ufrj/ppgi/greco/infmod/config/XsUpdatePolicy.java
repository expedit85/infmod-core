package br.ufrj.ppgi.greco.infmod.config;

import br.ufrj.ppgi.greco.infmod.UpdatePolicy;

public abstract class XsUpdatePolicy {

	@Override
	public String toString() {
		return super.toString();
	}
	
	
	public abstract UpdatePolicy createUpdatePolicy();
}
