package ec.app.mason.pacman ;

import java.util.*;
import ec.gp.*;
import pacman.Agent ;

/**
 * Keeps track of the GP tree execution by a Pac,
 * used by the doPolicyStep() procedure in MASONProblem ; 
 * We need this because we do not want to execute the 
 * whole tree in the doPolicyStep(), rather, at each step() 
 * method on the Pac, we will evaluate one node of the tree
 * at a time.
 **/

public class ExecutionStack 
{
	public static int currentAction = Agent.NOTHING ;
	public static Stack<GPNode> nodeStack = new Stack<GPNode>();
}
