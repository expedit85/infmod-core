package br.ufrj.ppgi.greco.infmod;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import br.ufrj.ppgi.greco.expedit.tese.commons.UTCXslDateTimeFormat;
import br.ufrj.ppgi.greco.expedit.tese.commons.clock.Clock;
import br.ufrj.ppgi.greco.expedit.tese.commons.clock.ClockException;

import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.vocabulary.RDF;

// pull = sincrono
// push = assincrono
public class InferenceModule {

	private String name;
	private String resourceBaseUri;
	private List <Loader> loaders = new Vector<Loader>();	
	private Model localModel = null;
	private Reasoner reasoner;
	private SparqlServer sparqlServer;
	private UpdatePolicy updatePolicy;
	private MT thread;
	private Clock clock;
	private Map<String, String> prefixesMap = null;


	public InferenceModule(String resourceBaseUri, UpdatePolicy updatePolicy, Clock clock) {
		super();

		if (resourceBaseUri == null
				|| "".equals(resourceBaseUri)
				|| !resourceBaseUri.substring(resourceBaseUri.length()-1).matches("[#/]"))
			throw new IllegalArgumentException(resourceBaseUri);

		this.resourceBaseUri = resourceBaseUri;
		this.updatePolicy = updatePolicy;
		this.clock = clock;
	}


	public void addLoader(Loader loader) throws IllegalStateException {
		if (thread != null && thread.isPlaying())
			throw new IllegalStateException("No loader can be added while in play state.");

		loaders.add(loader);
	}
	
	public void removeLoader(Loader loader) throws IllegalStateException {
		if (thread != null && thread.isPlaying())
			throw new IllegalStateException("No loader can be removed while in play state.");
		
		loaders.remove(loader);
	}

	public void start() throws IllegalStateException {
		if (thread != null)
			throw new IllegalStateException("Inference Module is already active. Please, stop it and try again.");
		
		if (loaders.size() == 0)
			throw new IllegalStateException("Cannot start Inference Module due to no loader was specified.");
		
		thread = new MT();
		thread.start();
	}
	
	public void stop() throws IllegalStateException {
		if (thread == null)
			throw new IllegalStateException("Inference Module was not started.");
		
		thread.stop1();
		try {
			thread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		thread = null;
	}
	
	public void pause() throws IllegalStateException {
		if (thread == null)
			throw new IllegalStateException("Inference Module was not started.");

		thread.pause();
	}
	
	public void resume() {
		if (thread == null)
			throw new IllegalStateException("Inference Module was not started.");

		thread.play();
	}
	
	
	public void setSparqlServer(SparqlServer sparqlServer) {
		if (thread != null && thread.isPlaying())
			throw new IllegalStateException("Sparql Server cannot be changed while in play state.");

		this.sparqlServer = sparqlServer;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getResourceBaseUri() {
		return resourceBaseUri;
	}
	
	public void setReasoner(Reasoner reasoner) {
		this.reasoner = reasoner;
	}
	
	public Reasoner getReasoner() {
		return reasoner;
	}
	
	public UpdatePolicy getUpdatePolicy() {
		return updatePolicy;
	}
	

	
	private class MT extends Thread {
		
		public static final int PLAY = 1;
		public static final int PAUSE = 2;
		public static final int STOP = 3;
		
		private int state = PLAY;
		
		public MT() {
			super("Inference Module");
		}
		
		public void pause() {
			state = PAUSE;
		}
		
		public void stop1() {
			state = STOP;
		}
		
		public void play() {
			state = PLAY;
		}
		
		public boolean isPlaying() {
			return state == PLAY;
		}


		@Override
		public void run() {
			
			SimpleDateFormat fmt = new UTCXslDateTimeFormat("yyyy-MM-dd HH:mm:ss");
		

			System.out.println(InferenceModule.this.getName() + ": Starting Fuseki server");

			sparqlServer.start();
			
			System.out.println(InferenceModule.this.getName() + ": Fuseki server started");

			boolean changed = false;
			long minUpdateInterval = 5;


			for (Loader loader : loaders) {
				loader.startLoading();
			}

			while (state != STOP) {
				try {
					// Verifica mudanças no carregamento
					try {
						long startTime = clock.getCurrentTime();
						
						for (Loader loader : loaders) {
							if (loader.hasChanged()) changed = true;
						}
						
						while (clock.getCurrentTime() - startTime < minUpdateInterval) sleep(1);

					} catch (ClockException e1) {
						e1.printStackTrace();
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}


					// Atualiza endpoint caso tenha ocorrido mudança
					if (changed) {

						long startTime0 = System.nanoTime()/1000;
						
						Model initialModel = updatePolicy.getInitialModel(localModel); // GI := getInitialModel(GP)

						long szStart = initialModel.size();
						
						
						// Obtem mudanças dos carregadores
						long startTime1 = System.nanoTime()/1000;
						long oldestLoadTime = Long.MAX_VALUE;
						long newestLoadTime = 0;

						Model queryModel = ModelFactory.createDefaultModel();	// GC := {}

						for (Loader loader : loaders) {
							queryModel.add(loader.getResultModel());	// deve ser synchronized, GC := GC + GL[i]
							oldestLoadTime = Math.min(oldestLoadTime, loader.getOldestLoadTime());
							newestLoadTime = Math.max(newestLoadTime, loader.getNewestLoadTime());
						}
						queryModel.add(initialModel);	// GC := GC + GI
						
						
						
						long finalTime1 = System.nanoTime()/1000;
						long szLoader = queryModel.size();

					
						// Reasoner
						Model reasoningModel = updatePolicy.getModelBeforeReasoning(localModel, queryModel);// GAI := getModelBeforeReasoning(GP, GC)
						if (reasoningModel == localModel) reasoningModel = queryModel;
						
						long szBfReason = reasoningModel.size();

						// Inference
						long startTime2 = System.nanoTime()/1000;
						InfModel infModel = ModelFactory.createInfModel(reasoner, reasoningModel);	// GI = reason(GAI)
						long finalTime2 = System.nanoTime()/1000;
						long szInferred = infModel.size();
						

						// 
						Model finalModel = updatePolicy.getPublishingModel(localModel, queryModel, infModel);	// GP := getPublishingModel(GP, GC, GI)
						if (finalModel == localModel || finalModel == queryModel) finalModel = infModel;
						localModel = finalModel;
						
						
						// Put date/time information into the dataset
						Calendar c0 = UTCXslDateTimeFormat.getUTCCalendar(oldestLoadTime);
						Calendar c1 = UTCXslDateTimeFormat.getUTCCalendar(newestLoadTime);
						Calendar c3 = UTCXslDateTimeFormat.getUTCCalendar(clock.getCurrentTime());

						if (resourceBaseUri != null) {
							localModel.createResource(resourceBaseUri + "updateTimes")
								.addProperty(RDF.type, InferenceModuleVocabulary.Timestamp)
								.addLiteral(InferenceModuleVocabulary.oldestLoadingDateTime, c0)
								.addLiteral(InferenceModuleVocabulary.newestLoadingDateTime, c1)
								.addLiteral(InferenceModuleVocabulary.publishingDateTime,    c3);
						}
						
						long szPub = localModel.size();

						
						// Update endpoint
						long startTime3 = System.nanoTime()/1000;
						sparqlServer.update(localModel);
						long finalTime3 = System.nanoTime()/1000;
						
						changed = false;
						
						System.out.format("[%s] %s: S(%d) L(%d/%.1fms) r(%d) R(%d/%.1fms) P(%d/%.1fms) Total(%.1fms)\n",
							fmt.format(clock.getCurrentDate()),
							InferenceModule.this.getName(),
							szStart,										// tamanho antes das consultas
							szLoader, (finalTime1 - startTime1)/1000.0,		// tamanho/tempo apos consultas
							szBfReason,										// tamanho antes do reasoning
							szInferred, (finalTime2 - startTime2)/1000.0,	// tamanho/tempo estimado apos reasoning
							szPub, (finalTime3 - startTime3)/1000.0,		// tamanho/tempo de update da base
							(finalTime3 - startTime0)/1000.0);
					}


					// Pause execution
					while (state == PAUSE)
						try {
							Thread.sleep(50);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			for (Loader loader : loaders) {
				loader.stopLoading();
			}

			sparqlServer.stop();
		}
	}



	public void setPrefixMap(Map<String, String> prefixesMap) {
		this.prefixesMap = prefixesMap;
	}
	
	public Map<String, String> getPrefixesMap() {
		return prefixesMap;
	}
}
