package br.ufrj.ppgi.greco.infmod.reasoning.jena.builtins;

import br.ufrj.ppgi.greco.expedit.tese.commons.UTCXslDateTimeFormat;
import br.ufrj.ppgi.greco.expedit.tese.commons.clock.Clock;
import br.ufrj.ppgi.greco.expedit.tese.commons.clock.ClockException;

import com.hp.hpl.jena.datatypes.xsd.XSDDateTime;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.impl.LiteralLabelFactory;
import com.hp.hpl.jena.reasoner.rulesys.BindingEnvironment;
import com.hp.hpl.jena.reasoner.rulesys.Builtin;
import com.hp.hpl.jena.reasoner.rulesys.RuleContext;
import com.hp.hpl.jena.reasoner.rulesys.builtins.BaseBuiltin;

public class CurTimeBuiltin extends BaseBuiltin implements Builtin {

	private Clock clock;
	

	public CurTimeBuiltin(Clock clock) {
		super();
		this.clock = clock;
	}


	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "curTime";
	}
	
	
	@Override
	public int getArgLength() {
		// TODO Auto-generated method stub
		return 1;
	}
	
	
	@Override
	public boolean bodyCall(Node[] args, int length, RuleContext context)
	{
        checkArgs(length, context);
        BindingEnvironment env = context.getEnv();

        try {
			Node now = Node.createLiteral(
					LiteralLabelFactory.create(
							new XSDDateTime(
									UTCXslDateTimeFormat.getUTCCalendar(clock.getCurrentTime())
							)) );
			return env.bind(args[0], now);
			
		} catch (ClockException e) {
			e.printStackTrace();
		}
        return false;
	}

}
