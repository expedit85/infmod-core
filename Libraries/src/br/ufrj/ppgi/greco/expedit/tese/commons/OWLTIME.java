package br.ufrj.ppgi.greco.expedit.tese.commons;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

public class OWLTIME {

	protected static final String uri = "http://www.w3.org/2006/time#";

	/**
	 * returns the URI for this schema
	 * 
	 * @return the URI for this schema
	 */
	public static String getURI() {
		return uri;
	}

	protected static final Resource resource(String local) {
		return ResourceFactory.createResource(uri + local);
	}

	protected static final Property property(String local) {
		return ResourceFactory.createProperty(uri, local);
	}

	public static Property li(int i) {
		return property("_" + i);
	}

	public static final Resource Interval = resource("Interval");
	public static final Resource Instant = resource("Instant");
	
	public static final Property hasBeginning = property("hasBeginning");
	public static final Property hasEnd = property("hasEnd");
	public static final Property inXSDDateTime = property("inXSDDateTime");
}
