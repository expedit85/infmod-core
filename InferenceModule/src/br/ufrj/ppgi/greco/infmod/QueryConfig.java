package br.ufrj.ppgi.greco.infmod;

import br.ufrj.ppgi.greco.infmod.impl.DefaultQueryParameterBinder;
import br.ufrj.ppgi.greco.infmod.impl.DefaultResultHandler;

import com.hp.hpl.jena.query.Query;


public class QueryConfig {

	private boolean sealed = false;
	private final Query query;
	private final String description;
	private String baseUri;
	private ResultHandler resultHandler;
	private QueryParameterBinder binder;
	private long lastLoadTime = 0;

	
	public synchronized void complete() {
		if (sealed) return;
		
		if (baseUri == null) baseUri = "";
		if (binder == null) binder = new DefaultQueryParameterBinder();
		if (resultHandler == null) resultHandler = new DefaultResultHandler();
		
		sealed = true;
	}

	protected QueryConfig(Query query, String description) {
		super();
		this.query = query;
		this.description = description;
	}	


	public String getDescription() {
		return description;
	}
	
	public Query getQuery() {
		return query;
	}
	
	public String getBaseUri() {
		return baseUri;
	}
	
	public ResultHandler getResultHandler() {
		return resultHandler;
	}

	public QueryParameterBinder getBinder() {
		return binder;
	}
	
	
	public void setBaseUri(String baseUri) {
		if (sealed) throw new IllegalStateException("Object is already sealed.");
		this.baseUri = baseUri;
	}
	
	public void setBinder(QueryParameterBinder binder) {
		if (sealed) throw new IllegalStateException("Object is already sealed.");
		this.binder = binder;
	}
	
	public void setResultHandler(ResultHandler resultHandler) {
		if (sealed) throw new IllegalStateException("Object is already sealed.");
		this.resultHandler = resultHandler;
	}
	
	public boolean isSealed() {
		return sealed;
	}
	
	public void setLastLoadTime(long lastLoadTime) {
		this.lastLoadTime = lastLoadTime;
	}
	
	public long getLastLoadTime() {
		return lastLoadTime;
	}
}
