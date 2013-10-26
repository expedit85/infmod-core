package br.ufrj.ppgi.greco.infmod.config.arq;

import java.security.InvalidParameterException;
import java.util.Calendar;

import com.hp.hpl.jena.sparql.expr.NodeValue;
import com.hp.hpl.jena.sparql.function.FunctionBase3;

public class SumDateTimeFunction extends FunctionBase3
{
	@Override
	public NodeValue exec(NodeValue nTime, NodeValue nDays, NodeValue nSec)
	{
        Calendar c = null;
        
        try {
	
	        if (nTime.isLiteral() &&  nTime.isDateTime())
	        {
	        	c = nTime.getDateTime().asCalendar();
	        }
		
			int days = (int) nDays.getFloat();
			int seconds = (int) nSec.getFloat();
			
			c = (Calendar) c.clone();
			c.add(Calendar.DAY_OF_YEAR, days);
			c.add(Calendar.SECOND, seconds);
			
			return NodeValue.makeDateTime(c);
        }
        catch (Exception e)
        {
        	e.printStackTrace();
        	throw new InvalidParameterException(
            		nTime.toString() + ", " + nDays.toString() + ", " + nSec.toString());
        }
	}

}
