/*
  Copyright 2006 by Sean Luke
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/


package ec.app.mason.pacman.func;

import ec.*;
import ec.gp.*;
import ec.util.*;

import pacman.* ;
import ec.app.mason.pacman.* ;

public class Or extends GPNode
{
	public String toString()
	{
		return "or";
	}

	public void checkConstraints(final EvolutionState state,
	                             final int tree,
	                             final GPIndividual typicalIndividual,
	                             final Parameter individualBase)
	{
		super.checkConstraints(state,tree,typicalIndividual,individualBase);
		if (children.length!=2)
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
		boolean left = (data.ret != 0.0);
		children[1].eval(state, thread, data, stack, 
					individual, problem);
		boolean right = (data.ret != 0.0);
		data.ret = (left || right) ? 1.0 : 0.0 ;
	}
}



