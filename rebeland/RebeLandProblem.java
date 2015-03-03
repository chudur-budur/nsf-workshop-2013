package ec.app.mason.rebeland ;

import ec.*;
import ec.util.*;
import ec.vector.* ;
import ec.simple.*;
import ec.multiobjective.MultiObjectiveFitness;

import sim.engine.*;

import rebeland2.City;
import rebeland2.CityBuilder;
import rebeland2.Household;
import rebeland2.Parameters;
import rebeland2.RebeLand2;

/**
 * How to compile:
 * javac -cp .:$CLASSPATH:rebeland2.jar:commons-math3-3.0.jar:masoncsc.jar:quaqua-color-chooser-only.jar *.java
 * How to run:
 * java -cp .:$CLASSPATH:rebeland2.jar:commons-math3-3.0.jar:masoncsc.jar:quaqua-color-chooser-only.jar ec.Evolve -file rebeland.params
 **/

public class RebeLandProblem extends Problem implements SimpleProblemForm
{
	//Thread [] threads = new Thread[Runtime.getRuntime().availableProcessors()];
	Thread [] threads = new Thread[2];

	int maxCount = 8 ; //10, this generalization test
	int counter = 0;
	double [][] results = new double [maxCount][2];

	public synchronized void resetCounter() { counter = 0; }

	public void evaluate(final EvolutionState state,
	                     final Individual ind,
	                     final int subpopulation,
	                     final int threadnum)
	{
		if(!ind.evaluated)
		{
			System.out.print(((DoubleVectorIndividual)ind).genotypeToStringForHumans());
			double meanSatisfaction = 0.0 , stolenMoney = 0.0 ;

			resetCounter();

			for(int i = 0; i < threads.length; i++)			
				threads[i]= new Thread(new MyThread(this, ind));			
			for(int i = 0; i < threads.length; i++)			
				threads[i].start();			

			for(int i = 0; i < threads.length; i++)
			{
				try {
					threads[i].join();
				}
				catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			for(int i = 0; i < results.length; i++)
			{
				meanSatisfaction += results[i][0];
				stolenMoney += results[i][1];
			}

			meanSatisfaction = meanSatisfaction/results.length;
			stolenMoney = stolenMoney/results.length;

			// ((SimpleFitness)ind.meanSatisfaction).setFitness(state,(float)meanSatisfaction,false);
			double[] objectives = new double[2] ;
			objectives[0] = meanSatisfaction ;
			objectives[1] = stolenMoney ;
			((MultiObjectiveFitness)ind.fitness).setObjectives(state, objectives);
			ind.evaluated = true ;
			System.out.println(" :: satisfaction: " + meanSatisfaction + ", stolen: " + stolenMoney + "\n");
		}
	}
}

class MyThread implements Runnable
{
	SimState reb ;
	int currentJob;
	RebeLandProblem parent;
	Individual ind;

	public MyThread( RebeLandProblem parent, Individual ind)
	{
		this.parent = parent;
		this.ind = ind;

	}
	public double getAverageSatisfaction(SimState state)
	{
		CityBuilder cities = ((RebeLand2)state).getStateGovernment().getCities();
		double totalSatisfaction = 0.0;
		for (City c : cities)
			for (Household h : c.getPopulation())
				totalSatisfaction += h.getSatisfaction();
		return totalSatisfaction / ((RebeLand2)state).getParams().populationAndResources.getTotalPopulation();
	}

	public double getMoneyStolenByGovernment(SimState state)
	{
		return ((RebeLand2)state).getStateGovernment().getStolenMoney();
	}

	public double[] eval()
	{
		double[] outputs = new double[2];
		long x = System.currentTimeMillis();
		String[] args = new String [2];
		reb = new RebeLand2(x, args, new Parameters(x));

		// Modify Model according to individual

		Parameters params = ((RebeLand2)reb).getParams();

		double[] geneVal = ((DoubleVectorIndividual)ind).genome ;

		params.finances.setStateCorruptionRate(geneVal[0]);	// double  from 0.0 to 1.0, initial 0.1
		params.finances.setTaxRate(geneVal[1]);			// double  from 0.0 to 1.0, initial 0.2
		params.finances.setMaxStateReserveRate(geneVal[2]);	// double  from 0.0 to 1.0, initial 0.8
		params.finances.setMinStateReserveRate(geneVal[3]);	// double from 0.0 to 1.0, initial 0.2, has to be less than one before it
		params.finances.setMinimumBenefitsShare(geneVal[4]);	// double from 0 to 1.0, initial 0.2

		params.urbanConflict.setMaxPolicePerCapita(geneVal[5]);		// double from 0 to 1.0, initial 0.1
		params.ruralConflict.setInitialReserveArmyRatio(geneVal[6]);	// double from 0 to 1.0 initial 0.2
		params.ruralConflict.setStandingArmySize((int)
				(geneVal[7] * params.populationAndResources.getTotalPopulation())); // double from 0.0 to 1.0 initial 0.02

		// not sure about yet, remapping the range from [0.0-1.0] to [1.0-100.0]
		double mappedVal = 1.0 + (geneVal[8] * 99.0) ;
		params.eventDistributions.setStateAttackInterval(mappedVal); // double range from 1 to 100, initial 15

		reb.start();
		// 1000 is the step count
		for(int i = 0 ; i < 1000 ; i++)
		{
			reb.schedule.step(reb);
		}
		//System.out.println("one simulation finished");

		// get statistics from model
		outputs[0] = getAverageSatisfaction(reb); // fitness from 0 to 1 higher is better
		outputs[1] = getMoneyStolenByGovernment(reb); // fitness from 0 to infinity higher is better
		return outputs;
	}

	public static synchronized int getNextJob(RebeLandProblem parent)
	{
		int x = parent.counter;
		parent.counter++;
		return x;
	}

	@Override
	public void run()
	{
		while(true)
		{
			currentJob = getNextJob(parent);
			if(currentJob >= parent.maxCount)			
				return;			
			else
			{
				double[] x = eval();
				parent.results[currentJob][0] = x[0];
				parent.results[currentJob][1] = x[1];
			}
		}
	}
}
