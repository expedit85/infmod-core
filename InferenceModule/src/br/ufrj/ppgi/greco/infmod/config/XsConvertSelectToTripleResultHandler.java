package br.ufrj.ppgi.greco.infmod.config;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.ufrj.ppgi.greco.infmod.ResultHandler;
import br.ufrj.ppgi.greco.infmod.impl.ConvertSelectToTripleResultHandler;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResourceFactory;


public class XsConvertSelectToTripleResultHandler extends XsResultHandler {

	public String subject;
	public String predicate;
	public String object;
	public String objectType;
	
	private static Model model = ModelFactory.createDefaultModel();
	
	@Override
	public String toString() {
		return this.getClass().getSimpleName();
	}

	@Override
	public ResultHandler createResultHandler() {

		RDFNode obj = null;

		if ("Integer".equalsIgnoreCase(objectType)) {
			obj = ResourceFactory.createTypedLiteral(object, XSDDatatype.XSDinteger);
		}
		else if ("Datetime".equalsIgnoreCase(objectType)) {
			obj = ResourceFactory.createTypedLiteral(object, XSDDatatype.XSDdateTime);
		}
		else if ("Float".equalsIgnoreCase(objectType)) {
			obj = ResourceFactory.createTypedLiteral(object, XSDDatatype.XSDfloat);
		}
		else if ("Double".equalsIgnoreCase(objectType)) {
			obj = ResourceFactory.createTypedLiteral(object, XSDDatatype.XSDdouble);
		}
		else if ("URI".equalsIgnoreCase(objectType)) {
			obj = ResourceFactory.createResource(object);
		}
		else if ("Var".equalsIgnoreCase(objectType)) {
			obj = null;
		}
		else if ("TypedLiteral".equalsIgnoreCase(objectType)) {
			String uriRegex = "([a-z]+:(?://)?[a-zA-z0-9\\._-]+(?::[0-9]+)?(?:/.+)*)";
			// Ainda precisa ser melhor testada
			String[] regexlist = new String [] {
					"'([^']*)'^^" + uriRegex,
					"\"([^\"]*)\"^^" + uriRegex };

			Matcher m = getMatcher(object, regexlist);
			if (m == null) throw new IllegalArgumentException(String.format("'%s' does not match STRING^^URI regex", object));
			obj = model.createLiteral(m.group(1), m.group(2));
		}
		else if ("LangTagLiteral".equalsIgnoreCase(objectType)) {
			// Ainda precisa ser melhor testada
			String[] regexlist = new String [] {
					"'([^']*)'@([a-zA-z-]+)",
					"\"([^\"]*)\"@([a-zA-z-]+)" };

			Matcher m = getMatcher(object, regexlist);
			if (m == null) throw new IllegalArgumentException(String.format("'%s' does not match STRING@LANG regex", object));
			obj = model.createLiteral(m.group(1), m.group(2));
		}
	

		return new ConvertSelectToTripleResultHandler(subject, predicate, obj != null ? obj : object);
	}

	private Matcher getMatcher(String object2, String[] regexlist) {
		for (String regex : regexlist) {
			Pattern p = Pattern.compile(regex);
			Matcher m = p.matcher(object2);
			if (m.matches()) return m;
		}
		return null;
	}
}
