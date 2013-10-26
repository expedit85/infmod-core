package br.ufrj.ppgi.greco.infmod.config;

import br.ufrj.ppgi.greco.infmod.SparqlServer;

public class XsSparqlServer {

	public String path;
	public Integer port;
	public boolean fastUpdate;
	
	
	public SparqlServer createSparqlServer() {
		return new SparqlServer(path, port, fastUpdate);
	}
}
