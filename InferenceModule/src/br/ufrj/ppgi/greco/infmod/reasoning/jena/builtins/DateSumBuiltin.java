package br.ufrj.ppgi.greco.infmod.reasoning.jena.builtins;

import java.util.Calendar;

import br.ufrj.ppgi.greco.expedit.tese.commons.UTCXslDateTimeFormat;

import com.hp.hpl.jena.datatypes.xsd.XSDDateTime;
import com.hp.hpl.jena.datatypes.xsd.impl.XSDDateTimeType;
import com.hp.hpl.jena.datatypes.xsd.impl.XSDDateType;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.impl.LiteralLabelFactory;
import com.hp.hpl.jena.reasoner.rulesys.BindingEnvironment;
import com.hp.hpl.jena.reasoner.rulesys.RuleContext;
import com.hp.hpl.jena.reasoner.rulesys.builtins.BaseBuiltin;

class DateSumBuiltin extends BaseBuiltin
{
	public DateSumBuiltin() {
		super();
	}

	@Override
	public String getName() {
		return "dateSum";
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
        
        Calendar c = null;
        
        if (n1.isLiteral()
        		&&  ((n1.getLiteralDatatype() instanceof XSDDateType)
        			|| n1.getLiteralDatatype() instanceof XSDDateTimeType))
        {
        	c = ((XSDDateTime)n1.getLiteralValue()).asCalendar();
        }
        else return false;
        
        
        if (n2.isLiteral() && n2.getLiteralValue() instanceof Number )
        {
	        c = (Calendar) c.clone();
	        c.setTimeZone(UTCXslDateTimeFormat.getUTCTimeZone());
	        
        	Number days = (Number) n2.getLiteralValue();
	        c.add(Calendar.DAY_OF_MONTH, days.intValue());  

	        BindingEnvironment env = context.getEnv();
	        Node now = Node.createLiteral( LiteralLabelFactory.create(new XSDDateTime(c)) );
	        return env.bind(args[2], now);	        	
        }
        else return false;
	}
}