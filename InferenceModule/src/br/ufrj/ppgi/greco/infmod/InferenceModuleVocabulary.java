package br.ufrj.ppgi.greco.infmod;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

public class InferenceModuleVocabulary {

	protected static final String uri = "http://greco.ppgi.ufrj.br/inferencemodule/vocabulary#";

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
	
	

	public static final Resource Timestamp = resource("Timestamp");
		
	public static final Property oldestLoadingDateTime = property("oldestLoadingDateTime");
	public static final Property newestLoadingDateTime = property("newestLoadingDateTime");
	public static final Property publishingDateTime = property("publishingDateTime");
}
