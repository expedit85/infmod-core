package br.ufrj.ppgi.greco.infmod.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import br.ufrj.ppgi.greco.infmod.Loader;
import br.ufrj.ppgi.greco.infmod.QueryConfig;

//@XmlRootElement(name="QueryConfig")
//@XmlAccessorType(XmlAccessType.FIELD)
public abstract class XsQueryConfig {

	public static List<String> valuesToFind = new ArrayList<String>(4);
	
	static {
		valuesToFind.add("SELECT");
		valuesToFind.add("CONSTRUCT");
		valuesToFind.add("ASK");
		valuesToFind.add("DESCRIBE");
	}
	
	
	//@XmlElement
	public String query;
	
	
	//@XmlElement
	public String description;
	
	
	//@XmlElement
	public XsQueryParameterBinder binder;
	
	
	//@XmlElement
	public XsResultHandler resultHandler;
	
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("desc='");
		sb.append(description);
		sb.append("', query='");
		
		String qt = query.trim().replaceAll("\\s+", " ");
		String s = qt.toUpperCase();
		int index = -1;
		for (String valueToFind : valuesToFind) {
			index = s.indexOf(valueToFind);
			if (index >= 0) break;
		}
		if (index >= 0) s = qt.substring(index, index+24); 
		
		sb.append(s);
		sb.append("...', binder=");
		sb.append(binder.toString());
		sb.append(", resHdlr=");
		sb.append(resultHandler.toString());
		return sb.toString();
	}
	
	public abstract QueryConfig createQueryConfig(Loader loader, Map<String, String> prefixesMap);
}
