package ecjpac ;

import java.io.* ;
import java.util.*;
import ec.* ;
import ec.simple.*;
import ec.util.*;
import ec.gp.*;
import sim.display.*;
import sim.engine.*;
import sim.util.* ;
import ecjpac.pacman.* ;

/** 
 * The main Tester program to test a evolved GP tree, 
 * this will fireup a UI for the PacMan game, and loads
 * the tree that is saved in a file beforehad;
 * How to run this test --
 * 	java -cp $CLASSPATH ec.app.mason.pacman.Tester -file pacman.params solutions/[saved tree file].lisp
 **/

public class Tester
{
	public static void main(String args[]) throws Exception
	{
		if( args.length != 3)
		{
			System.out.println("Error: Mismatched arguments!!");
			System.exit(0);
		}
		String[] ecjargs = new String[2] ; 
		ecjargs[0] = args[0] ;		// ecj argument 1: -file
		ecjargs[1] = args[1] ;		// ecj argument 2: parameter file name
		String treeFile = args[2] ;	// the lisp tree for testing

		System.out.println("args: " + ecjargs[0] + " " + ecjargs[1] + " " + treeFile);

		// a new dummy parameter database to fire up an EvolutionState
		ParameterDatabase parameters = Evolve.loadParameterDatabase(ecjargs);
		// new dummy evolution state to read and load the lisp tree.
		final SimpleEvolutionState evstate = (SimpleEvolutionState)Evolve.initialize(parameters, 123);
		evstate.startFresh();

		// original random tree in the population.
		System.out.println("original tree:");
		((GPIndividual)evstate.population.subpops[0].individuals[0]).trees[0].printTreeForHumans(evstate,0);
		
		// read the saved tree from the file for testing.
		System.out.println("loading a lisp tree from " + treeFile);
		String tree = loadLispTree(treeFile);		
		LineNumberReader lnr = new LineNumberReader(new StringReader(tree));
		((GPIndividual)evstate.population.subpops[0].individuals[0]).trees[0].readTree(evstate, lnr);

		// display and verify the loaded lisp tree.
		System.out.println("loaded tree:");
		((GPIndividual)evstate.population.subpops[0].individuals[0]).trees[0].printTreeForHumans(evstate,0);
		((GPIndividual)evstate.population.subpops[0].individuals[0]).trees[0].verify(evstate); // Verify the tree.

		// copy the root of the tree to be tested.
		final GPNode root = (GPNode)((GPIndividual)evstate.population.subpops[0].individuals[0]).trees[0].child.clone() ;

		// a new Pac agent.
		PacMan pacman = new PacMan(System.currentTimeMillis());
		final Pac[] pacs = new Pac[1] ;
		pacs[0] = new Pac(pacman, 0);

		// the execution stack to execute the tree without "explicitly accessing the PacMan SimState"
		final ExecutionStack exeStack = new ExecutionStack();
		// push the root -- start the execution from the root of the tree.
		exeStack.nodeStack.push(root);

		// now add the Pac behaviour, in this example we are going to 
		// execute the Pac action according to the evolved GP tree.
		pacs[0].setPolicyStep(new PolicyStep(){
			public void doPolicyStep()
			{
				GPNode top = null ;
				if(!exeStack.nodeStack.isEmpty())
				{
					// pop the stack
					top = exeStack.nodeStack.pop();
					// call eval() on it -- see the corresponding function sets in the "func" folder
					top.eval(evstate, 0, 
						((GPProblem)evstate.evaluator.p_problem).input, 
						((GPProblem)evstate.evaluator.p_problem).stack, 
						(GPIndividual)evstate.population.subpops[0].individuals[0], 
						(GPProblem)evstate.evaluator.p_problem);
				}
				else	
				{
					// The stack got emptied, push the root node again
					// i.e. restart executing the tree.
					exeStack.nodeStack.push(root);
					// or die ??
					//pacs[0].die();
					//return ;
				}

				// get current stored action
				int curAction = exeStack.currentAction ;

				// then do this --
				if (pacs[0].isPossibleToDoAction(curAction))				
					pacs[0].performAction(curAction);				
				else if (pacs[0].isPossibleToDoAction(pacs[0].lastAction))				
					pacs[0].performAction(pacs[0].lastAction);
			}
		});

		// sets the initial action for the Pacs
		int[] actions = new int[]{Agent.NOTHING};
		// set the Pacs to the PacMan simulation.
		pacman.setPacs(pacs, actions);
		// send this pacman into the GPProblem
		((MASONProblem)evstate.evaluator.p_problem).pacman = null ;
		((MASONProblem)evstate.evaluator.p_problem).pacman = pacman ;
		// a new PacMan game with UI
		PacManWithUI game = new PacManWithUI(pacman);	
		// create the controller for the game.
		SimpleController c = new SimpleController(game);
		// start playing immediately.
		c.pressPlay();

		int score = 0 ;
		synchronized(c)
		{
			while(true)
			{
				// play until there is a death
				if(pacman.deaths > 0)
				{
					c.pressPause();
					break ;
				}			
			}
		}
		// update the score and display.
		score = ((PacMan)((PacManWithUI)c.getSimulation()).state).score ;
		System.out.println("---------------->score: " + score);
		// stop the game -- why does it also terminate the java ??
		c.pressStop();
		// close the simulation and the UI of the game -- why does it also terminate the java ??
		c.doClose();
	}

	/**
	 * Procedure to read a human-readable lisp tree from the file.
	 **/
	public static String loadLispTree(String fileName) throws Exception
	{
		String tree = "" ;
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		String line;
		while ((line = br.readLine()) != null) 		
			tree += line ;		
		br.close();
		tree = tree.replaceAll("(\\r|\\n)", "");
		tree = tree.trim().replaceAll(" +", " ");
		tree = " " + tree ;
		return tree ;
	}
}
