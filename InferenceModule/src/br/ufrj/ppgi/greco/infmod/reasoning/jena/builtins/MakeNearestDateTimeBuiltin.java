package br.ufrj.ppgi.greco.infmod.reasoning.jena.builtins;

import java.util.Calendar;

import br.ufrj.ppgi.greco.expedit.tese.commons.UTCXslDateTimeFormat;
import br.ufrj.ppgi.greco.expedit.tese.commons.clock.Clock;
import br.ufrj.ppgi.greco.expedit.tese.commons.clock.ClockException;

import com.hp.hpl.jena.datatypes.xsd.XSDDateTime;
import com.hp.hpl.jena.datatypes.xsd.impl.XSDDateTimeType;
import com.hp.hpl.jena.datatypes.xsd.impl.XSDDateType;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.impl.LiteralLabelFactory;
import com.hp.hpl.jena.reasoner.rulesys.BindingEnvironment;
import com.hp.hpl.jena.reasoner.rulesys.RuleContext;
import com.hp.hpl.jena.reasoner.rulesys.builtins.BaseBuiltin;

class MakeNearestDateTimeBuiltin extends BaseBuiltin
{
	private Clock clock;
	
	public MakeNearestDateTimeBuiltin(Clock clock) {
		super();
		this.clock = clock;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "makeNearestDateTime";
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

		Calendar cal;
		try {
			cal = UTCXslDateTimeFormat.getUTCCalendar(clock.getCurrentTime());
		} catch (ClockException e) {
			e.printStackTrace();
			return false;
		}
		
        Node n1 = getArg(0, args, context);
        
        Calendar c = null;
        
        if (n1.isLiteral()
        		&&  ((n1.getLiteralDatatype() instanceof XSDDateType)
        			|| n1.getLiteralDatatype() instanceof XSDDateTimeType))
        {
        	c = ((XSDDateTime)n1.getLiteralValue()).asCalendar();
        }
        else return false;
        
       	        
        c = (Calendar) c.clone();
        c.setTimeZone(cal.getTimeZone());
        
        c.set(Calendar.YEAR, cal.get(Calendar.YEAR));
        if (c.before(cal)) c.add(Calendar.YEAR, 1);  

        BindingEnvironment env = context.getEnv();
        
        Node now = Node.createLiteral( LiteralLabelFactory.create(new XSDDateTime(c)) );
        return env.bind(args[1], now);
	}
}