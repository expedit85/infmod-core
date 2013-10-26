package br.ufrj.ppgi.greco.infmod.impl;

import java.util.List;

import br.ufrj.ppgi.greco.expedit.tese.commons.clock.Clock;
import br.ufrj.ppgi.greco.expedit.tese.commons.clock.ClockException;
import br.ufrj.ppgi.greco.expedit.tese.commons.sparql.SparqlExecutor;
import br.ufrj.ppgi.greco.infmod.Loader;
import br.ufrj.ppgi.greco.infmod.QueryConfig;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;


public class PullLoader extends Loader implements Runnable {
	
	private SparqlExecutor sparqlExecutor;
	private Thread thread;
	private boolean mustTerminate = false;
	private long waitingTime = 0;
	
	public class QueryData extends QueryConfig {
		
		public static final long DEFAULT_TIME = 10*60*1000;	// 10 MIN

		private long   lastExecutionTime = 0;
		private long   timeBetweenExecs; // minimum time
		private double averageTimeBetweenExecs = 0.0;	// deve tender a timeBetweenExecs
		private long   executionCount = 0;
		
		private long [] timeBetweenExecsList;
		private int head = 0, tail = 0;
		private double movingAverageTimeBetweenExecsSum = 0.0;
		private int timeBetweenExecsListSize = 0;
		
		
		public QueryData(Query query,
						 String description,
						 long timeBetweenExecs) {
			super(query, description);
			
			this.timeBetweenExecs = timeBetweenExecs;
			
			timeBetweenExecsList = new long[32];
		}
		
		
		public long getLastExecutionTime() {
			return lastExecutionTime;
		}
		
		public long getTimeBetweenExecs() {
			return timeBetweenExecs;
		}
		
		public void setLastExecutionTime(long lastExecutionTime) {
			if (this.lastExecutionTime > 0) {
				long delta = lastExecutionTime - this.lastExecutionTime;
				averageTimeBetweenExecs += (delta - averageTimeBetweenExecs) / ++executionCount;


				/*
				 * Add before full (size=5):
				 *       0     1     2     3     4     5
				 *    +-----+-----+-----+-----+-----+-----+
				 *    |  A  |  B  |  C  |  D  |  E  |     |
				 *    +-----+-----+-----+-----+-----+-----+
				 *     head                          tail
				 *
				 * Add fills entire list (size=5):
				 *       0     1     2     3     4     5
				 *    +-----+-----+-----+-----+-----+-----+
				 *    |  A  |  B  |  C  |  D  |  E  |  F  |
				 *    +-----+-----+-----+-----+-----+-----+
				 *     head                                 tail (=head)
				 *                     ||
				 *                     \/
				 *       0     1     2     3     4     5
				 *    +-----+-----+-----+-----+-----+-----+
				 *    |  -  |  B  |  C  |  D  |  E  |  F  |
				 *    +-----+-----+-----+-----+-----+-----+
				 *     tail  head
				 *     
				 * Add advances head of list (size=5):
				 *       0     1     2     3     4     5
				 *    +-----+-----+-----+-----+-----+-----+
				 *    |  G  |  -  |  C  |  D  |  E  |  F  |
				 *    +-----+-----+-----+-----+-----+-----+
				 *           tail  head
				 * 
				 */
				timeBetweenExecsList[tail] = delta;
				tail = (tail + 1) % timeBetweenExecsList.length;
				movingAverageTimeBetweenExecsSum += delta;

				if (tail % timeBetweenExecsList.length == head) {
					movingAverageTimeBetweenExecsSum -= timeBetweenExecsList[head];
					head = (head + 1) % timeBetweenExecsList.length;
				}
				else timeBetweenExecsListSize++;
			}
			
			this.lastExecutionTime = lastExecutionTime;
		}
		
		public long getExecutionCount() {
			return executionCount;
		}
		
		public double getAverageTimeBetweenExecs() {
			return averageTimeBetweenExecs;
		}
		
		public double getMovingAverageTimeBetweenExecs() {
			return movingAverageTimeBetweenExecsSum / timeBetweenExecsListSize;
		}
	}
	

	
	public PullLoader(String name, Clock clock, SparqlExecutor sparqlExecutor) {
		super(name, clock);
		this.sparqlExecutor = sparqlExecutor;
	}
	
	
	@Override
	public void addQueryConfig(QueryConfig qc) {
		if (!qc.getClass().equals(QueryData.class))
			throw new IllegalArgumentException("Parameter should be an instance of QueryData.");

		QueryData qd = (QueryData) qc;
		
		if (waitingTime == 0) waitingTime = qd.getTimeBetweenExecs();
		else {
			long cont, res = 0, n2 = qd.getTimeBetweenExecs();

			for (cont = 1; cont <= waitingTime; cont++)
			{
				if ((waitingTime%cont == 0) && (n2%cont == 0)) res = cont;
			}
			
			waitingTime = res;
		}
		
		super.addQueryConfig(qc);
	}
	

	@Override
	public void startLoading() {
		thread = new Thread(this, "PullLoader: '" + super.getName() + "'");
		thread.start();
		mustTerminate = false;
	}

	@Override
	public void stopLoading() {
		mustTerminate = true;
		try {
			thread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}




	/**
	 * 
	 * 
	 */
	@Override
	public void run() {
		
		while (!this.mustTerminate) {

			try {
				for (QueryConfig queryConfig : getQueries()) {
					QueryData qc = (QueryData) queryConfig;

					long delta = clock.getCurrentTime() - qc.getLastExecutionTime();

					if (delta >= qc.getTimeBetweenExecs()) {

						List <Query> queries = createBoundQueries(qc);
						
						Model model = ModelFactory.createDefaultModel();
						boolean askResult = true;
						CompoundResultSet rsResult = new CompoundResultSet();
						
						int numResults = 0;
						
						if (queries.size() > 0)
						{
							for (Query query : queries)
							{
								if (query.isDescribeType()) {
									Model m = sparqlExecutor.execDescribe(query);	// bound query
									model.add(m);
									numResults += m.size(); 
								}
								else if (query.isConstructType()) {
									Model m = sparqlExecutor.execConstruct(query);
									model.add(m);
									numResults += m.size();
								}
								else if (query.isAskType()) {
									boolean res = sparqlExecutor.execAsk(query);
									askResult = askResult && res;
									numResults = -1;
								}
								else if (query.isSelectType()) {
									ResultSet rs = sparqlExecutor.execSelect(query);
									rsResult.add(rs); 
									numResults = -1;
								}
							}
	
							if (queries.get(0).isDescribeType()) {
								super.notifyResultHandler(qc, model);		// original query
							}
							else if (queries.get(0).isConstructType()) {
								super.notifyResultHandler(qc, model);
							}
							else if (queries.get(0).isAskType()) {
								super.notifyResultHandler(qc, askResult);
							}
							else if (queries.get(0).isSelectType()) {
								super.notifyResultHandler(qc, rsResult);
							}
						}
						
						// Deve ser setado apenas após consulta ter sido executada
						qc.setLastExecutionTime(clock.getCurrentTime());

						System.out.format(	// DEBUG
								"Loader: %s | Query: %s | ResultCount=%d | ExecInterval(s): ideal=%d avg=%1.0f moving avg=%1.0f\n",
								this.getName(),
								qc.getDescription(),
								numResults,
								qc.getTimeBetweenExecs()/1000,								
								qc.getAverageTimeBetweenExecs()/1000.0,
								qc.getMovingAverageTimeBetweenExecs()/1000.0);
					}
				}

				// Libera CPU
				long startTime = clock.getCurrentTime();
				while (clock.isAlive() && (clock.getCurrentTime() - startTime < waitingTime)) Thread.sleep(10);
				
			} catch (ClockException e) {
				e.printStackTrace();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		System.out.println("Loader exiting: " + this.getName());
	}
}
