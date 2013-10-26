package br.ufrj.ppgi.greco.infmod.reasoning.jena.builtins;


import br.ufrj.ppgi.greco.expedit.tese.commons.clock.Clock;

import com.hp.hpl.jena.reasoner.rulesys.BuiltinRegistry;

public class JenaReasonerBuiltinsLoader
{	
	public static void load(Clock clock)
	{
		BuiltinRegistry.theRegistry.register(new MakeNearestDateTimeBuiltin(clock));
		BuiltinRegistry.theRegistry.register(new DateSumBuiltin());
		BuiltinRegistry.theRegistry.register(new DateDiffBuiltin());
		BuiltinRegistry.theRegistry.register(new TimeDiffBuiltin());
		BuiltinRegistry.theRegistry.register(new MakeNamedNodeBuiltin());
		BuiltinRegistry.theRegistry.register(new CurTimeBuiltin(clock));
		BuiltinRegistry.theRegistry.register(new LocalNameBuiltin());
	}
}
