package ec.app.mason.virus ;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import sim.engine.* ;
import virus.VirusInfectionDemo ;

public class ThreadPoolEvaluator
{	
	private int steps = 1 ;
	private int gtest = 1 ;
	private int nThreads = 1 ;
	private SimState[] sim = null;

	public ThreadPoolEvaluator(SimState[] sim, int steps, int nThreads)
	{
		this.sim = sim ;
		this.steps = steps ;
		this.gtest = gtest ;
		this.nThreads = nThreads ;
	}

	public double evaluateParallel()
	{
		ExecutorService executor = Executors.newFixedThreadPool(this.nThreads);	
		Worker[] worker = new Worker[this.sim.length] ;	
		for (int i = 0 ; i < this.sim.length ; i++)
		{			
			worker[i] = new Worker(sim[i], this.steps);
			executor.execute(worker[i]);
		}
		executor.shutdown();
		while(!executor.isTerminated()){}

		double fitness = 0.0 ;
		for(int i = 0 ; i < worker.length ; i++)
			fitness += worker[i].getModelFitness();
		fitness = fitness/(double)this.gtest ;
		return fitness ;
	}
}

class Worker implements Runnable
{
	private SimState sim = null ;
	private int steps = 0 ;
	private double modelFitness = 0.0 ;

	public Worker(SimState sim, int steps)
	{
		this.sim = sim ; 
		this.steps = steps ;
	}

	public void run() 	
	{		
		sim.start();
		this.modelFitness = 0.0 ;
		for(int i = 0 ; i < this.steps ; i++)
			sim.schedule.step(sim);	
		this.modelFitness = -1.0 * ((VirusInfectionDemo)sim).getModelFitness();
		sim.finish();				
	}

	public double getModelFitness() { return this.modelFitness ; }
}
