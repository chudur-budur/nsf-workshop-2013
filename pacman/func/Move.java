/*
  Copyright 2006 by Sean Luke
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/

package ec.app.mason.pacman.func;

import ec.* ;
import ec.util.*;
import ec.gp.* ;
import ec.gp.koza.*;

import pacman.* ;
import ec.app.mason.pacman.* ;

public class Move extends GPNode
{
	public String toString()
	{
		return "move";
	}

	public void checkConstraints(final EvolutionState state,
	                             final int tree,
	                             final GPIndividual typicalIndividual,
	                             final Parameter individualBase)
	{
		super.checkConstraints(state,tree,typicalIndividual,individualBase);
		if (children.length!=1)
			state.output.error("Incorrect number of children for node " +
			                   toStringForError() + " at " +
			                   individualBase);
	}

	/** The corresponding action for move-south is "Agent.S" **/
	public void eval(final EvolutionState state,
	                 final int thread,
	                 final GPData input,
	                 final ADFStack stack,
	                 final GPIndividual individual,
	                 final Problem problem)
	{
		children[0].eval(state, thread, input, stack, individual, problem);
		TypeData data = (TypeData)input ;
		((MASONProblem)problem).exeStack.currentAction 
				= data.direction;	
	}
}



