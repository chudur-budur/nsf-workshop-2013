/*
  Copyright 2006 by Sean Luke
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/

package ecjpac ;

import java.util.*;
import ec.*;
import ec.util.*;
import ec.simple.*;
import ec.gp.*;
import ec.gp.koza.*;
import ecjpac.func.*;
import ecjpac.pacman.* ; // this is the MASON's pacman code.

public class MASONProblem extends GPProblem implements SimpleProblemForm
{
	public PacMan pacman = null ;
	public final Pac[] pacs = new Pac[1];

	// the execution stack to keep track of the execution of the tree.
	// This is implemented to avoid explicit access to the PacMan SimState.
	// As we do not want to execute the whole tree in the doPolicyStep(),
	// rather, at each step() method in the Pac, we will evaluate one node
	// of the tree at a time.
	public final ExecutionStack exeStack = new ExecutionStack();

	public void setup(final EvolutionState evstate, final Parameter base)	
	{
		super.setup(evstate, base);
		// create a new PacMan SimState
		pacman = new PacMan(System.currentTimeMillis());

		// the Pac to be tested.
		// pacs = new Pac[1] ;
		pacs[0] = new Pac(pacman, 0);
	}

	public void evaluate(final EvolutionState evstate,
	                     final Individual ind,
	                     final int subpopulation,
	                     final int threadnum)
	{
		if (!ind.evaluated)  // don't bother reevaluating
		{
			// the root of the tree.
			final GPNode root = (GPNode)((GPIndividual)ind).trees[0].child.clone() ;

			// save the root of the tree.
			exeStack.nodeStack.push(root);
			
			// Set the Pac's behaviour -- execute the GP tree.
			// The doPolicyStep() does not execute the *whole tree* at once,
			// at each step() invocation on the Agent, we will evaluate one
			// corresponding node at a time.
			pacs[0].setPolicyStep(new PolicyStep(){
				public void doPolicyStep()
				{
					GPNode top = null ;
					if(!exeStack.nodeStack.isEmpty())
					{
						// the top of the stack
						top = exeStack.nodeStack.pop();
						// call eval on it -- see the function sets in the "func" folder for details
						// eval() will update the "currentAction" value in the ExecutionStack.
						top.eval(evstate, threadnum, input, stack, 
							(GPIndividual)ind, (GPProblem)evstate.evaluator.p_problem);
					}
					else	
					{
						// The stack got emptied, push the root node again -- to repeat the execution tree
						//exeStack.nodeStack.push(root);		
						// or die??
						// pacs[0].die();
						return ;
					}

					// get the current action from the ExecutionStack
					int curAction = exeStack.currentAction ;
					// then do this --
					if (pacs[0].isPossibleToDoAction(curAction))				
						pacs[0].performAction(curAction);				
					else if (pacs[0].isPossibleToDoAction(pacs[0].lastAction))				
						pacs[0].performAction(pacs[0].lastAction);
				}
			});

			// set the Pacs into PacMan SimState
			pacman.setPacs(pacs, new int[]{Agent.NOTHING});

			// generalization test
			int maxDeath = 10 ;

			// start the simulation
			pacman.start();
			for(long i = 0 ; i < 100000 ; i++)
			//while(true)
			{
				// the step() executes one node at a time.
				pacman.schedule.step(pacman); 				
				// break when the Pac dies a maxDeath
				if(pacman.deaths > maxDeath)
					break ;
			}
			// update the score
			int score = pacman.score ;
			// to avoid devide-by-zero			
			if(score == 0) { score++ ; }

			// check if generalization test is being done
			double finalScore = score ;
			if(maxDeath > 0)
				finalScore = score/(maxDeath + 1.0) ; // take the mean
			// done.
			pacman.finish();

			// the fitness better be KozaFitness!			
			KozaFitness f = ((KozaFitness)ind.fitness);
			f.setStandardizedFitness(evstate, (float)(1.0/finalScore));  // could "not" divide by 0!
			f.hits = 0;  // don't care
			ind.evaluated = true;
		}
	}
}
