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

import java.io.* ;
import java.util.* ;

public class OpenSpaceERC extends ERC
{
	public String direction ;

	private String[] dirName = {"north", "east", "south", "west" };

	public String toStringForHumans()
	{
		return direction ;
	}

	public String encode()
	{
		return Code.encode(direction);
	}

	public boolean decode(DecodeReturn dret)
	{
		int pos = dret.pos ;
		String data = dret.data ;
		Code.decode(dret);
		if( dret.type != DecodeReturn.T_STRING)
			{ dret.data = direction ; dret.pos = pos ; return false ; }

		direction = dret.data ;
		return true ;	
	}

	public boolean nodeEquals(GPNode node)
	{
		return (node.getClass() == this.getClass() && ((OpenSpaceERC)node).direction.equals(direction)); 
	}

	public void readNode(EvolutionState state, DataInput input) throws IOException
	{
		direction = input.readLine();
	}

	public void writeNode(EvolutionState state, DataOutput output) throws IOException
	{
		output.writeBytes(direction);
	}

	public void resetNode(EvolutionState state, int thread)
	{
		int i = state.random[thread].nextInt(dirName.length) ;
		direction = dirName[i] ;
	}

	/** Hopefully find a possible free way -- when the Pac is stuck **/
	public void mutateNode(EvolutionState state, int thread)
	{
		Pac pac = ((MASONProblem)state.evaluator.p_problem).pacman.pacs[0] ;
		ArrayList<Integer> possibleActions = new ArrayList<Integer>();
		for(int i = 0 ; i < 4 ; i++)
		{
			if(pac.isPossibleToDoAction(i) && pac.lastAction != i)
				possibleActions.add(new Integer(i));
		}
		if(possibleActions.size() > 0)
			direction = dirName[possibleActions.get(state.random[thread].nextInt(possibleActions.size())).intValue()];
		else
		{
			int index = state.random[thread].nextInt(dirName.length) ;
			direction = dirName[index] ;
		}
	}

	public void eval(final EvolutionState state,
	                 final int thread,
	                 final GPData input,
	                 final ADFStack stack,
	                 final GPIndividual individual,
	                 final Problem problem)
	{
		TypeData data = (TypeData)input ;
		/*Pac pac = ((MASONProblem)problem).pacman.pacs[0];
		ArrayList<Integer> possibleActions = new ArrayList<Integer>();*/
		/*for(int i = 0 ; i < 4 ; i++)
		{
			if(/*(i != pac.lastAction) &&*/
		/*			pac.isPossibleToDoAction(i))
			possibleActions.add(new Integer(i));
		}
		if(possibleActions.size() > 0)		
			data.direction = possibleActions.get(state.random[thread].nextInt(possibleActions.size()));		
		else
			data.direction = Agent.NOTHING ;
		String[] dir = {"north", "east", "south", "west"};*/
		//System.out.println("possibly stuck, moving: " + dir[data.direction]);		
		for(int i = 0 ; i < dirName.length ; i++)
		{
			if(dirName[i].equals(direction))
			{
				data.direction = i ;
				break ;
			}
		}
	}
}



