/*
  Copyright 2006 by Sean Luke
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/


package ecjpac.func;

import ec.*;
import ec.gp.*;
import ec.util.*;

import ecjpac.* ;
import ecjpac.pacman.* ;

public class If extends GPNode
{
	public String toString()
	{
		return "if";
	}

	public void checkConstraints(final EvolutionState state,
	                             final int tree,
	                             final GPIndividual typicalIndividual,
	                             final Parameter individualBase)
	{
		super.checkConstraints(state,tree,typicalIndividual,individualBase);
		if (children.length!=3)
			state.output.error("Incorrect number of children for node " +
			                   toStringForError() + " at " +
			                   individualBase);
	}

	public void eval(final EvolutionState state,
	                 final int thread,
	                 final GPData input,
	                 final ADFStack stack,
	                 final GPIndividual individual,
	                 final Problem problem)
	{
		TypeData data = (TypeData)input ;
		children[0].eval(state, thread, data, stack, 
					individual, problem);		
		if(data.ret != 0.0)
		{
			((MASONProblem)problem).exeStack.nodeStack.push(
				children[1]);
			//children[1].eval(state, thread, data, stack, 
			//		individual, problem);
		}
		else
		{
			((MASONProblem)problem).exeStack.nodeStack.push(
				children[2]);
			//children[2].eval(state, thread, data, stack, 
			//		individual, problem);
		}
		// The corresponding action for progn2 is "Agent.NOTHING"
		//((MASONProblem)problem).exeStack.currentAction = Agent.NOTHING ;		
	}
}



