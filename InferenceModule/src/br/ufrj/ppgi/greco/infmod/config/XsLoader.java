package br.ufrj.ppgi.greco.infmod.config;

import java.util.Map;

import br.ufrj.ppgi.greco.expedit.tese.commons.clock.Clock;
import br.ufrj.ppgi.greco.infmod.Loader;

public abstract class XsLoader {

	public abstract Loader createLoader(Clock clock, Map<String, String> prefixesMap);
}
