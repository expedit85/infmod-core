package br.ufrj.ppgi.greco.infmod.reasoning.jena.builtins;

import java.util.Calendar;

import br.ufrj.ppgi.greco.expedit.tese.commons.UTCXslDateTimeFormat;

import com.hp.hpl.jena.datatypes.xsd.XSDDateTime;
import com.hp.hpl.jena.datatypes.xsd.impl.XSDDateTimeType;
import com.hp.hpl.jena.datatypes.xsd.impl.XSDDateType;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.reasoner.rulesys.BindingEnvironment;
import com.hp.hpl.jena.reasoner.rulesys.Builtin;
import com.hp.hpl.jena.reasoner.rulesys.RuleContext;
import com.hp.hpl.jena.reasoner.rulesys.Util;
import com.hp.hpl.jena.reasoner.rulesys.builtins.BaseBuiltin;

public class TimeDiffBuiltin extends BaseBuiltin implements Builtin {

	@Override
	public String getName() {
		return "timeDiff";
	}

	@Override
	public int getArgLength()
	{
		return 3;
	}
			
	@Override
	public boolean bodyCall(Node[] args, int length, RuleContext context)
	{
		checkArgs(length, context);
		
        Node n1 = getArg(0, args, context);
        Node n2 = getArg(1, args, context);
        
        Calendar c1 = null;
        if (n1.isLiteral()
        		&&  ((n1.getLiteralDatatype() instanceof XSDDateType)
        			|| n1.getLiteralDatatype() instanceof XSDDateTimeType))
        {
        	c1 = ((XSDDateTime)n1.getLiteralValue()).asCalendar();
        }
        else return false;
        
        Calendar c2 = null;
        if (n2.isLiteral()
        		&&  ((n2.getLiteralDatatype() instanceof XSDDateType)
        			|| n2.getLiteralDatatype() instanceof XSDDateTimeType))
        {
        	c2 = ((XSDDateTime)n2.getLiteralValue()).asCalendar();
        }
        else return false;
        
        
        c1 = (Calendar) c1.clone();
        c1.setTimeZone(UTCXslDateTimeFormat.getUTCTimeZone());
        c2.setTimeZone(UTCXslDateTimeFormat.getUTCTimeZone());
        
        int days = (int) ((c1.getTimeInMillis()-c2.getTimeInMillis()) / 1000);

        BindingEnvironment env = context.getEnv();
        Node ndays = Util.makeIntNode(days);

        return env.bind(args[2], ndays);	        	
	}
}
