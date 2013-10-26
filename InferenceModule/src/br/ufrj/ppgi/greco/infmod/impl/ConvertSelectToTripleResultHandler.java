package br.ufrj.ppgi.greco.infmod.impl;


import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

public class ConvertSelectToTripleResultHandler extends DefaultResultHandler {
	
	public Resource subject;
	public Property predicate;
	public RDFNode object;
	
	private String sVarName, pVarName, oVarName;
	
	
	public ConvertSelectToTripleResultHandler(String s, String p, Object o) {

		if (s == null) throw new IllegalArgumentException("subject is null");
		if (p == null) throw new IllegalArgumentException("predicate is null");
		if (o == null) throw new IllegalArgumentException("object is null");

		s = s.trim();
		p = p.trim();

		if (isVariable(s)) sVarName = s.substring(1);
		else subject = ResourceFactory.createResource(s);

		if (isVariable(p)) pVarName = p.substring(1);
		else predicate = ResourceFactory.createProperty(p);

		if (o instanceof String) {
			String so = (String)o;
			if (isVariable(so)) oVarName = so.substring(1);	// variavel
			else object = ResourceFactory.createPlainLiteral(so);
		}
		else if (o instanceof RDFNode) object = (RDFNode) o;
		else throw new IllegalArgumentException("object was not recognized: " + o.getClass().getCanonicalName());
	}

	
	private boolean isVariable(String s) {
		if (s.startsWith("?")) return true;
		else return false;
	}
	
	
	@Override
	public Model onSelectResponse(Query query, ResultSet result) {
		Model m = ModelFactory.createDefaultModel();
		
		while (result.hasNext()) {

			QuerySolution qs = result.next();

			Resource s = sVarName == null ? subject   : qs.getResource(sVarName);
			Property p = pVarName == null ? predicate : (Property) qs.getResource(pVarName);
			RDFNode  o = oVarName == null ? object    : qs.get(oVarName);
			
			try {
				if (s != null && p != null && o != null) m.add(s, p, o);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return m;
	}
}
