package br.ufrj.ppgi.greco.infmod.config.arq;

import com.hp.hpl.jena.sparql.expr.NodeValue;
import com.hp.hpl.jena.sparql.function.FunctionBase2;

public class GetLastIndexOfFunction extends FunctionBase2 {

	@Override
	public NodeValue exec(NodeValue v1, NodeValue v2)
	{
		String s1 = v1.getString();
		String s2 = v2.getString();
		
		return NodeValue.makeInteger(s2.lastIndexOf(s1));
	}

}
