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

public class IsStuck extends GPNode
{
	public String toString()
	{
		return "is-stuck";
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

	public void eval(final EvolutionState state,
	                 final int thread,
	                 final GPData input,
	                 final ADFStack stack,
	                 final GPIndividual individual,
	                 final Problem problem)
	{
		TypeData data = (TypeData)input ;
		Pac pac = (Pac)((MASONProblem)problem).pacman.pacs[0] ;
		data.ret = 0.0 ; // assuming not stuck
		if(pac.isStuck())
		{
			data.ret = 1.0 ; // stuck
			//System.out.println("got stuck!");
		}
	}
}



