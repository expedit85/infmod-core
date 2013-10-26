package br.ufrj.ppgi.greco.infmod.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Map.Entry;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.util.Locator;
import com.hp.hpl.jena.util.TypedStream;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class Utils {

	private static ScriptEngineManager mgr = new ScriptEngineManager();
	private static ScriptEngine engine = mgr.getEngineByName("JavaScript");


	public static double evaluateExpression(String expression) {
	    try {
	    	Object result = engine.eval(expression);
	    	if (result instanceof Number) return Double.parseDouble(result.toString());
	    	else throw new Exception(result.getClass().getCanonicalName());
		} catch (ScriptException e) {
			throw new RuntimeException(e);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}


	@SuppressWarnings("rawtypes")
	public static XsInferenceModule loadConfigFromFile(final File file) throws FileNotFoundException {
		XStream xs = new XStream(new DomDriver());
		
		Class [] aliasClasses = new Class [] {
			XsClearDataPolicy.class,
			XsConvertSelectToTripleResultHandler.class,
			XsDefaultQueryParameterBinder.class,
			XsDefaultResultHandler.class,
			XsInferenceModule.class,
			XsKeepDataBySparqlPolicy.class,
			XsLoader.class,
			XsPullLoader.class,
			XsQueryConfig.class,
			XsQueryData.class,
			XsQueryParameterBinder.class,
			XsResultHandler.class,
			XsSparqlServer.class,
			XsTimeFrameQueryBinder.class,
			XsUpdatePolicy.class,
			XsRunQueryFromSelectResultHandler.class,
			XsPrefix.class,
			XsInference.class,
			XsSelectQueryParameterBinder.class,
			XsClock.class,
			XsOffsetSplitterQueryParameterBinder.class
		}; 

		for (Class clazz : aliasClasses) {
			xs.alias(clazz.getSimpleName().substring(2), clazz);
		}
		
		
		xs.addImplicitCollection(XsInference.class, "ontologies");
		
		xs.alias("Query", String.class);
		xs.alias("ontology", String.class);
		
		xs.aliasAttribute(XsClock.class, "baseUri", "baseUri");

		XsInferenceModule infMod = (XsInferenceModule) xs.fromXML(new InputStreamReader(new FileInputStream(file)));
		
		
		FileManager.get().addLocator(new Locator() {
			
			private File path = file.getParentFile();
			
			@Override
			public TypedStream open(String filenameOrURI) {
				File file = new File(path, filenameOrURI);
				if (file.exists()) {
					try {
						return new TypedStream(new FileInputStream(file));
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
				}
				return null;
			}
			
			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return "Locator which searches config file directory";
			}
		});
		
		return infMod;
	}
	
	
	
	static String serializedSparqlPrefixes(Map<String, String> prefixesMap)
	{
		StringBuffer sb = new StringBuffer();
		for (Entry<String, String> entry : prefixesMap.entrySet())
		{
			sb.append("PREFIX ");
			sb.append(entry.getKey().trim());
			sb.append(": <");
			sb.append(entry.getValue().trim());
			sb.append(">\n");
		}
		return sb.toString();
	}
}
