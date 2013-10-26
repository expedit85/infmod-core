package br.ufrj.ppgi.greco.infmod;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.PropertyConfigurator;
import org.openjena.atlas.iterator.Iter;
import org.openjena.fuseki.Fuseki;
import org.openjena.fuseki.server.SPARQLServer;

import br.ufrj.ppgi.greco.expedit.tese.commons.SnorqlPublisher;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.shared.Lock;
import com.hp.hpl.jena.sparql.SystemARQ;
import com.hp.hpl.jena.sparql.core.DatasetGraph;
import com.hp.hpl.jena.sparql.core.DatasetGraphFactory;
import com.hp.hpl.jena.sparql.core.Quad;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

public class SparqlServer {

	static {
		Fuseki.init();
		
		
		try {
			Properties p = new Properties();
			p.load(ClassLoader.getSystemResourceAsStream("fuseki.log.properties"));
			PropertyConfigurator.configure(p);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private String path;
	private int port;
	private SPARQLServer server = null;
	private DatasetGraph datasetGraph;
	private Map<String, String> prefixesMap = null;
	private boolean fastUpdate;


	public SparqlServer(String path, int port, boolean fastUpdate) {
		super();

		if (!path.matches("/[a-zA-Z0-9_-]+/"))
			throw new IllegalArgumentException("Invalid dataset path:" + path);

		this.fastUpdate = fastUpdate;
		this.path = path;
		this.port = port;
	}
	


	public void start() {
		datasetGraph = DatasetGraphFactory.createMem();
		server = new SPARQLServer(datasetGraph, path, port, false);
		SnorqlPublisher.publishSnorqlContent(server.getServer(), "../InferenceModule/", prefixesMap, path);

//		ClassLoader loader = this.getClass().getClassLoader();
//		String [] classes = {
//			"org/slf4j/spi/LocationAwareLogger.class",
//        	"org/openjena/fuseki/server/SPARQLServer.class",
////        	"org/eclipse/jetty/util/log/JettyAwareLogger.class",
//        	"org/eclipse/jetty/util/log/Slf4jLog.class"
//		};
//
//		for (String s : classes) {
//			try {
//				MessageDigest md = MessageDigest.getInstance("MD5");
//				InputStream is = loader.getResourceAsStream(s);
//				DigestInputStream dis = new DigestInputStream(is, md);
//				while (dis.read() >= 0);
//				//md.digest();
//				System.out.println("MD5("+loader.getResource(s)+") = " + Hex.encodeHexString(md.digest()));
//				dis.close();
//			} catch (NoSuchAlgorithmException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
		    
		server.start();
	}

	public void stop() {
		if (server != null) server.stop();
	}


	public void setFastUpdate(boolean fastUpdate) {
		this.fastUpdate = fastUpdate;
	}

	public boolean isFastUpdate() {
		return fastUpdate;
	}
	
	
	public String getPath() {
		return path;
	}

	public int getPort() {
		return port;
	}
	
	public Map<String, String> getPrefixesMap() {
		return prefixesMap;
	}
	
	public void setPrefixesMap(Map<String, String> prefixesMap) {
		this.prefixesMap = prefixesMap;
	}



	public void update(DatasetGraph dsg)
	{
		if (fastUpdate) fastUpdate(dsg.getDefaultGraph());
		else slowUpdate(dsg.getDefaultGraph());
	}

	public void update(Model model)
	{
		if (fastUpdate) fastUpdate(model.getGraph());
		else slowUpdate(model.getGraph());
	}
	
	
	private void fastUpdate(Graph graph)
	{
		long startTime = System.nanoTime()/1000;
		
		Lock lock = datasetGraph.getLock();	// Copied from Fuseki code
		enter(datasetGraph, lock, Lock.WRITE) ;

		try
		{
			datasetGraph.setDefaultGraph(graph);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			SystemARQ.sync(datasetGraph);	// Copied from Fuseki code
			leave(datasetGraph, lock, Lock.WRITE) ;
		}
		
		//DatasetRegistry.get().put(path, model.getG);
		
		long finalTime = System.nanoTime()/1000;
		
        System.out.println(
            	String.format("Endpoint fast update: Total(%.1fms)",
            		(finalTime - startTime) / 1000.0));
	}
	
	
	private void slowUpdate(Graph graph)
	{
		//System.out.println("Updating Fuseky graph: " + graph.getClass().getCanonicalName());
		long startTime = System.nanoTime()/1000;
		
		Lock lock = datasetGraph.getLock();	// Copied from Fuseki code
        enter(datasetGraph, lock, Lock.WRITE) ;

        int size = 0;
        long time1 = 0;
        long time2 = 0;
		long time3 = 0;
		try {
			time1 = System.nanoTime()/1000;
			
        	// Limpa dataset atual
        	Iterator<Quad> it = datasetGraph.find();
        	for (Quad q : Iter.toList(it)) {
        		datasetGraph.delete(q);
        	}
        	if (datasetGraph.size() != 0) throw new RuntimeException("grafo nao ficou vazio");
        	
        	
        	time2 = System.nanoTime()/1000;

        	// Adiciona novas triplas ao dataset
        	Graph g = datasetGraph.getDefaultGraph();
        	ExtendedIterator<Triple> it2 = graph.find(Node.ANY, Node.ANY, Node.ANY);
        	while (it2.hasNext())
        	{
        		size++;
        		g.add(it2.next());
        	}
        	
        	
        	time3 = System.nanoTime()/1000;
        }
        catch (Exception e) {
        	e.printStackTrace();
        }
        finally {
        	SystemARQ.sync(datasetGraph);	// Copied from Fuseki code
        	leave(datasetGraph, lock, Lock.WRITE) ;
        }
        
        long finalTime = System.nanoTime()/1000;
        
        System.out.println(
        	String.format("Endpoint update (by copying): CLR(%.1fms) ADD(%d/%.1fms) Total(%.1fms)",
        		(time2 - time1) / 1000.0, 
        		size,
        		(time3 - time2) / 1000.0,
        		(finalTime - startTime) / 1000.0));
	}

	
	// Copied from Fuseki code
    private void enter(DatasetGraph dsg, Lock lock, boolean readLock)
    {
        if ( lock == null )
            lock = dsg.getLock() ;
        if ( lock == null )
            return ;
        lock.enterCriticalSection(readLock);
    }
    
    // Copied from Fuseki code
    private void leave(DatasetGraph dsg, Lock lock, boolean readLock)
    {
        if ( lock == null )
            lock = dsg.getLock() ;
        if ( lock == null )
            return ;
        lock.leaveCriticalSection() ;
    }    	
}
