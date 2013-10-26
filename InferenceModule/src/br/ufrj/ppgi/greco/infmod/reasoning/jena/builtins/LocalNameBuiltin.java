package br.ufrj.ppgi.greco.infmod.reasoning.jena.builtins;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.reasoner.rulesys.BindingEnvironment;
import com.hp.hpl.jena.reasoner.rulesys.Builtin;
import com.hp.hpl.jena.reasoner.rulesys.RuleContext;
import com.hp.hpl.jena.reasoner.rulesys.builtins.BaseBuiltin;
import com.hp.hpl.jena.sparql.expr.ExprEvalException;
import com.hp.hpl.jena.sparql.util.FmtUtils;

public class LocalNameBuiltin extends BaseBuiltin implements Builtin {

	@Override
	public String getName() {
		return "localname";
	}
	
	@Override
	public int getArgLength() {
		// TODO Auto-generated method stub
		return 2;
	}
	
	
	@Override
	public boolean bodyCall(Node[] args, int length, RuleContext context)
	{
		checkArgs(length, context);

        Node nIn = getArg(0, args, context);

		if ( ! nIn.isURI() )
			throw new ExprEvalException("Not a URI: "+FmtUtils.stringForNode(nIn)) ;

		String str = nIn.getLocalName() ;
		
		Node nStr = Node.createLiteral(str);
		
        BindingEnvironment env = context.getEnv();

        return env.bind(args[1], nStr);
	}

}
