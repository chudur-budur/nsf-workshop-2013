/*
  Copyright 2006 by Sean Luke
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/


package ec.app.mason.ant;

import ec.app.mason.ant.func.*;
import ec.util.*;
import ec.*;
import ec.gp.*;
import ec.gp.koza.*;
import java.io.*;
import java.util.*;
import ec.simple.*;
import sim.engine.*;
import sim.field.continuous.*;

public class MASONProblem extends GPProblem implements SimpleProblemForm
    {
    public MASONDemo sim;
        
    public void evaluate(final EvolutionState state, 
        final Individual ind, 
        final int subpopulation,
        final int threadnum)
        {
        if (!ind.evaluated)  // don't bother reevaluating
            {
            GPData input = (GPData)(this.input);
        
            // Make an agent who will hold our Individual's GP Tree
            MASONAgent agent = new MASONAgent(state, threadnum, input, stack, (GPIndividual)ind, this);
                        
            // Make a MASON Simulation
            sim = new MASONDemo(state.random[threadnum], agent);
            sim.start();
            for(int step = 0 ; step < 10; step++)
                sim.schedule.step(sim);
            sim.finish();

            // get the distance the agent traveled
            double distanceSq = sim.world.getObjectLocation(agent).lengthSq();

            // the fitness better be KozaFitness!
            KozaFitness f = ((KozaFitness)ind.fitness);
            f.setStandardizedFitness(state,(float)(1.0 / distanceSq));  // could divide by 0!
            f.hits = 0;  // don't care
            ind.evaluated = true;
            }
        }
    }
