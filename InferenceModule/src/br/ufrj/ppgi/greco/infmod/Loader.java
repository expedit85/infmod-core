package br.ufrj.ppgi.greco.infmod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.ufrj.ppgi.greco.expedit.tese.commons.clock.Clock;
import br.ufrj.ppgi.greco.expedit.tese.commons.clock.ClockException;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;


/**
 * TODO Ver quais métodos precisam ser synchronized.
 * TODO Ver possiveis IllegalStateException's.
 * 
 * @author expedit
 *
 */
public abstract class Loader {

	private String name;
	protected Clock clock;
	private List <QueryConfig> queries = new ArrayList <QueryConfig>();
	
	/**
	 * Guarda ultimo resultado de cada uma das consultas
	 */
	private Map <Integer, Model> queryResults = new HashMap<Integer, Model>();
	
	private boolean changed;
	private Model initialModel;
	
	private long oldestLoadTime = Long.MAX_VALUE;
	private long newestLoadTime = 0;

	
	
	
	public Loader(String name, Clock clock) {
		this.name = name;
		this.changed = false;
		this.clock = clock;
	}
	

	/**
	 * Adiciona uma configuração de consulta. Este método pode ser sobrescrito para
	 * ter um comportamento especifico.
	 * 
	 * @param qc
	 */
	public void addQueryConfig(QueryConfig qc) {
		qc.complete();
		queries.add(qc);
		//queries.put(qc.getQuery().hashCode(), qc);
	}
	
	
	protected List <QueryConfig> getQueries() {
		return queries;
	}
	
	
	/**
	 * Determina o Model que terá os resultados de todas as consultas deste Loader acrescentadas.
	 * </br>
	 * O Model será utilizado apenas pelo método {@link #getResultModel()} em append-only.
	 * Caso nenhum model inicial seja fornecido, {@link #getResultModel()} criará um padrão do Jena
	 * usando o método ModelFactory.createDefaultModel().
	 * </br>
	 * Este método é útil caso se deseje usar um Model diferente do padrão, como por exemplo
	 * em disco ou remoto como os dos TDB, Sesame ou Virtuoso.
	 * 
	 * @param initialModel o Model a ter triplas acrescentadas (append only).
	 */
	public void setInitialModel(Model initialModel) {
		this.initialModel = initialModel;
	}


	protected void notifyResultHandler(QueryConfig qc, ResultSet result) {
		Query query = qc.getQuery();

		if (query.isSelectType()) {
			Model m = qc.getResultHandler().onSelectResponse(qc.getQuery(), result);
			putQueryResultModel(qc, m);
		}
	}

	protected void notifyResultHandler(QueryConfig qc, Model result) {
		Query query = qc.getQuery();

		if (query.isConstructType()) {
			Model m = qc.getResultHandler().onConstructResponse(qc.getQuery(), result);
			putQueryResultModel(qc, m);			
		}
		else if (query.isDescribeType()) {
			Model m = qc.getResultHandler().onDescribeResponse(qc.getQuery(), result);
			putQueryResultModel(qc, m);;			
		}
	}

	protected void notifyResultHandler(QueryConfig qc, boolean result) {
		Query query = qc.getQuery();

		if (query.isAskType()) {
			Model m = qc.getResultHandler().onAskResponse(qc.getQuery(), result);
			putQueryResultModel(qc, m);			
		}
	}


	/**
	 * Guarda novo Model como resultado de uma consulta
	 * @param query a consulta
	 * @param queryResultModel o Model com o resultado
	 */
	private synchronized void putQueryResultModel(QueryConfig qc, Model queryResultModel) {
		this.changed = true;
		try {
			qc.setLastLoadTime(clock.getCurrentTime());
		} catch (ClockException e) {
			e.printStackTrace();
		}
		queryResults.put(qc.hashCode(), queryResultModel);
	}
	
	
	/**
	 * Cria a consulta executavel por meio da chamada do QueryParameterBinder associado à consulta.
	 * @param query a consulta original (parametrizada)
	 * @return a consulta a ser executada (parametros substituidos)
	 */
	protected List <Query> createBoundQueries(QueryConfig qc) {
		return qc.getBinder().bind(qc.getQuery());	// obtem bound query
	}


	/**
	 * Inicia carregamento contínuo
	 */
	public abstract void startLoading();


	/**
	 * Termina carregamento contínuo
	 */
	public abstract void stopLoading();


	/**
	 * Informe se houve modificação nas fontes de dados
	 * @return true se houve modificação
	 */
	public boolean hasChanged() {
		return changed;
	}


	public long getOldestLoadTime() {
		return oldestLoadTime;
	}

	public long getNewestLoadTime() {
		return newestLoadTime;
	}


	public synchronized Model getResultModel() {
		changed = false;

		if (initialModel == null)
			initialModel = ModelFactory.createDefaultModel();

		Model tempModel = initialModel;

		oldestLoadTime = Long.MAX_VALUE;
		newestLoadTime = 0;
		for (QueryConfig qc : queries) {
			Integer key = qc.hashCode();

			oldestLoadTime = Math.min(oldestLoadTime, qc.getLastLoadTime());
			newestLoadTime = Math.max(newestLoadTime, qc.getLastLoadTime());
			
			Model m = queryResults.get(key);
			if (m != null) tempModel.add(m);
		}

		initialModel = null;
		return tempModel;
	}
	
	
	public String getName() {
		return name;
	}
	
	public Clock getClock() {
		return clock;
	}
}
