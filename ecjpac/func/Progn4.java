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

public class Progn4 extends GPNode
{
	public String toString()
	{
		return "progn4";
	}

	public void checkConstraints(final EvolutionState state,
	                             final int tree,
	                             final GPIndividual typicalIndividual,
	                             final Parameter individualBase)
	{
		super.checkConstraints(state,tree,typicalIndividual,individualBase);
		if (children.length!=4)
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
		((MASONProblem)problem).exeStack.nodeStack.push(children[0]);
		((MASONProblem)problem).exeStack.nodeStack.push(children[1]);
		((MASONProblem)problem).exeStack.nodeStack.push(children[2]);
		((MASONProblem)problem).exeStack.nodeStack.push(children[3]);
		// The corresponding action for progn4 is "Agent.NOTHING"
		((MASONProblem)problem).exeStack.currentAction = Agent.NOTHING ;		
	}
}



