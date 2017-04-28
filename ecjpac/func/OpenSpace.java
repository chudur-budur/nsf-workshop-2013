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

import java.util.ArrayList ;

public class OpenSpace extends GPNode
{
	public String toString()
	{
		return "open-space";
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

	/** Find a possible free way, hopefully when the Pac is stuck **/
	public void eval(final EvolutionState state,
	                 final int thread,
	                 final GPData input,
	                 final ADFStack stack,
	                 final GPIndividual individual,
	                 final Problem problem)
	{
		TypeData data = (TypeData)input ;
		Pac pac = ((MASONProblem)problem).pacman.pacs[0];
		ArrayList<Integer> possibleActions = new ArrayList<Integer>();
		for(int i = 0 ; i < 4 ; i++)
		{
			if(/*(i != pac.lastAction) &&*/
					pac.isPossibleToDoAction(i))
			possibleActions.add(new Integer(i));
		}
		if(possibleActions.size() > 0)		
			data.direction = possibleActions.get(state.random[thread].nextInt(possibleActions.size()));		
		else
			data.direction = Agent.NOTHING ;
		String[] dir = {"north", "east", "south", "west"};
		//System.out.println("possibly stuck, moving: " + dir[data.direction]);
	}
}



