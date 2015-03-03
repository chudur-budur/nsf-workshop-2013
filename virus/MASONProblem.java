/*
  Copyright 2006 by Sean Luke
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/


package ec.app.mason.virus;

import java.lang.Math ;
import java.lang.reflect.Field ;

import ec.*;
import ec.util.*;
import ec.vector.* ;
import ec.simple.*;
import sim.engine.*;

// the model to be tested, this line needs to removed
// as we may be going to use reflections
import virus.VirusInfectionDemo ;

/**
 * How to compile:
 * javac -cp .:$CLASSPATH *.java virus/*.java
 *
 * How to run:
 * java -cp .:$CLASSPATH ec.Evolve -file virus.params
 **/

public class MASONProblem extends Problem implements SimpleProblemForm
{
	public Parameter baseParameter ;	
	public String modelName ;
	public int steps ;
	public int gtest ;
	public int numberOfParameters ;	
	public String[] modelParameters ;
	public String[] fieldNames ;

	public Parameter evalParameter ;
	public boolean isParallel ;
	public int nThreads = 1 ;

	public static final String P_MASON_MODEL = "mason-model" ;	
	public static final String P_STEPS = "steps" ;	
	public static final String P_NUM_PARAMS = "num-params" ;	
	public static final String P_PARAM = "param" ;
	public static final String P_GTEST = "generalization-test" ;
	public static final String P_EVAL = "eval" ;
	public static final String P_EVAL_PARALLEL = "parallel" ;
	public static final String P_NTHREADS = "num-threads" ;
	public static boolean ifDebug = false ;	

	public void setup(final EvolutionState state, final Parameter base)
	{
		super.setup(state,base);
		baseParameter = new Parameter(P_MASON_MODEL);
		modelName = state.parameters.getString(baseParameter, null);
		if(MASONProblem.ifDebug) System.out.println("model name: " + modelName);
		steps = state.parameters.getInt(baseParameter.push(P_STEPS), null);
		if(MASONProblem.ifDebug) System.out.println("steps: " + steps);
		numberOfParameters = state.parameters.getInt(baseParameter.push(P_NUM_PARAMS), null);
		if(MASONProblem.ifDebug) System.out.println("num-params: " + numberOfParameters);
		modelParameters = new String[numberOfParameters] ;
		fieldNames = new String[numberOfParameters] ;
		for(int i = 0 ; i < numberOfParameters ; i++)
		{
			modelParameters[i] = state.parameters.getString(baseParameter.push(P_PARAM + "." + i), null);
			String[] fname = modelParameters[i].split("\\.");
			fieldNames[i] = fname[1] ;
			if(MASONProblem.ifDebug) System.out.println("field " + i + ": " + modelParameters[i]) ;
		}	
		gtest = state.parameters.getInt(baseParameter.push(P_GTEST), null);
		if(MASONProblem.ifDebug) System.out.println("generalization-test: " + gtest);

		evalParameter = new Parameter(P_EVAL);
		isParallel = state.parameters.getBoolean(evalParameter.push(P_EVAL_PARALLEL), null, false);
		if(MASONProblem.ifDebug) System.out.println("parallel evaluation: " + isParallel);
		if(isParallel) 
		{
			nThreads = state.parameters.getInt(evalParameter.push(P_NTHREADS), null);
			if(MASONProblem.ifDebug) System.out.println("number of threads for parallel evaluation: " + nThreads);
		}			
	}

	public void evaluate(final EvolutionState state,
	                     final Individual ind,
	                     final int subpopulation,
	                     final int threadnum)
	{
		if (!ind.evaluated)  // don't bother reevaluating
		{
			//if(MASONProblem.ifDebug) 
				//System.out.println(genomeToString(ind));

			
			// model fitness
			double fitness = 0.0 ;

			// Parallel evaluation
			if(isParallel)
			{
				long start = System.currentTimeMillis();
				SimState[] sim = new SimState[this.gtest] ;
				Class theClass = null ;
				try {
					theClass = Class.forName(modelName);
					for(int i = 0 ; i < this.gtest ; i++)
					{
						// do all MASON simulations take 'long' as a constructor argument ??
						sim[i] = (SimState)theClass.getConstructor(Long.TYPE).newInstance(
								state.random[threadnum].nextLong());
						// set parameters for the simulations
						for(int f = 0 ; f < this.fieldNames.length ; f++)
						{
							try {
								Field field = theClass.getField(fieldNames[f]);
								// this also needs to be generalized, i.e. avoid explicitly naming the 'VirusInfectionDemou'
								// and IntegerVectorIndividual
								field.setInt((VirusInfectionDemo)sim[i], ((IntegerVectorIndividual)ind).genome[f]);
							}catch(Exception e) {
								e.printStackTrace();
							}
						}	
					}
				} catch(Exception e) {
					e.printStackTrace();
				}				
								
				ThreadPoolEvaluator evaluator = new ThreadPoolEvaluator(sim, steps, nThreads);						
				fitness = evaluator.evaluateParallel();
				long end = System.currentTimeMillis();				
				System.out.println("time: " + (end - start)/1000.0);
			}
			else
			{	
				long start = System.currentTimeMillis();				
				for(int g = 0 ; g < this.gtest ; g++)
				{	
					SimState sim = null ;
					Class theClass = null ;
					try{
						theClass = Class.forName(modelName);
						// do all MASON simulations take 'long' as a constructor argument ??
						sim = (SimState)theClass.getConstructor(Long.TYPE).newInstance(state.random[threadnum].nextLong());			
					} catch(Exception e) {
						e.printStackTrace();
					}

					// set parameters for the simulations
					for(int i = 0 ; i < this.fieldNames.length ; i++)
					{
						try {
							Field field = theClass.getField(fieldNames[i]);
							// this also needs to be generalized, i.e. avoid explicitly naming the 'VirusInfectionDemou'
							// and IntegerVectorIndividual
							field.setInt((VirusInfectionDemo)sim, ((IntegerVectorIndividual)ind).genome[i]);
						}catch(Exception e) {
							e.printStackTrace();
						}
					}	
					// Generic serial evaluation
					// start the simulation			
					sim.start();								
					// run for 50 steps
					for(int i = 0 ; i < this.steps ; i++)
						sim.schedule.step(sim);

					// get model fitness, in our case the total % of people infected.
					// The class name needs to be avoided using some reflections.
					// need help.
					fitness += -1.0 * ((VirusInfectionDemo)sim).getModelFitness();
					//finish simulation
					sim.finish();
				}				
				// get the average generalization fitness
				fitness = fitness/(double)gtest ;
				long end = System.currentTimeMillis();				
				System.out.println("time: " + (end - start)/1000.0);
			}

			((SimpleFitness)ind.fitness).setFitness(state,(float)fitness,false);			
			System.out.println(genomeToString(ind) + ": " + fitness);
			if(MASONProblem.ifDebug) System.out.println("fitness: " + fitness);
			ind.evaluated = true ;
		}
	}

	public String genomeToString(Individual ind)
	{
		String genotypeStr = "" ;
		for(int i = 0 ; i < ((IntegerVectorIndividual)ind).genomeLength() ; i++)
			genotypeStr += ((IntegerVectorIndividual)ind).genome[i] + " ";
		return genotypeStr ;
	}	
}
