package br.ufrj.ppgi.greco.infmod.config;

import br.ufrj.ppgi.greco.infmod.UpdatePolicy;
import br.ufrj.ppgi.greco.infmod.impl.ClearDataPolicy;

public class XsClearDataPolicy extends XsUpdatePolicy {

	@Override
	public UpdatePolicy createUpdatePolicy() {
		return new ClearDataPolicy();
	}
}
