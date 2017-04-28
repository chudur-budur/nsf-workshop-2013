/*
  Copyright 2006 by Sean Luke
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/


package ecjpac.func;

import ec.*;
import ec.gp.*;
import ec.util.*;
import sim.util.*;

import ecjpac.* ;
import ecjpac.pacman.* ;

public class IsAGhostNearby extends GPNode
{
	public String toString()
	{
		return "is-a-ghost-nearby";
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
		PacMan pacman = ((MASONProblem)problem).pacman ;
		Pac pac = pacman.pacs[0] ;
		Bag nearby = pacman.agents.getNeighborsWithinDistance(new Double2D(pac.location), 0.6); // is 0.3 fine ??
		data.ret = 0.0 ; // assuming no ghost nearby 
		for(int i = 0 ; i < nearby.numObjs ; i++)
		{
			Object obj = nearby.objs[i] ;
			if (obj instanceof Ghost && pac.location.distanceSq(pacman.agents.getObjectLocation(obj)) <= 0.4) // is 0.2 fine ??
			{
			       data.ret = 1.0 ;
			       break ;
			}					
		}
	}
}



