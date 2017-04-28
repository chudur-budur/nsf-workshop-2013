/*
  Copyright 2006 by Sean Luke
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/

package ecjpac.func;

import ec.* ;
import ec.util.*;
import ec.gp.* ;
import ec.gp.koza.*;

import ecjpac.* ;
import ecjpac.pacman.* ;

public class South extends GPNode
{
	public String toString()
	{
		return "south";
	}

	public void checkConstraints(final EvolutionState state,
	                             final int tree,
	                             final GPIndividual typicalIndividual,
	                             final Parameter individualBase)
	{
		super.checkConstraints(state,tree,typicalIndividual,individualBase);
		if (children.length!=0)
			state.output.error("Incorrect number of children for node " +
			                   toStringForError() + " at " +
			                   individualBase);
	}

	/** The corresponding direction for South is "Agent.S" **/
	public void eval(final EvolutionState state,
	                 final int thread,
	                 final GPData input,
	                 final ADFStack stack,
	                 final GPIndividual individual,
	                 final Problem problem)
	{
		((TypeData)input).direction = Agent.S  ;
	}
}


