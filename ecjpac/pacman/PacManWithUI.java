/*
  Copyright 2006 by Sean Luke and George Mason University
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/

package ecjpac.pacman;
import sim.engine.*;
import sim.display.*;
import sim.portrayal.continuous.*;
import javax.swing.*;
import java.awt.*;
import sim.portrayal.simple.*;
import sim.portrayal.*;
import sim.portrayal.grid.*;
import java.awt.event.*;
import ecjpac.PolicyStep ;


/** Creates the UI for the PacMan game.  */

public class PacManWithUI extends GUIState
{
	public Display2D display;
	public JFrame displayFrame;	

	/** 
	 * The main procedure instantiate two Pacs with keyboard-input behaviour,
	 * then starts the game, nothing interesting.
	 **/
	public static void main(String[] args)
	{
		// a PacMan SimState
		final PacMan pacman = new PacMan(System.currentTimeMillis());

		// Two Pacs so far.
		final Pac[] pacs = new Pac[2] ;
		// initial actions for the Pacs.
		int[] actions = new int[]{Agent.NOTHING, Agent.NOTHING} ;

		// pacs[0] with a keyaboard-input behaviour.
		pacs[0] = new Pac(pacman, 0);
		pacs[0].setPolicyStep(new PolicyStep() {
			public void doPolicyStep()
			{
				int nextAction = pacman.getNextAction(pacs[0].tag);
				// pac man delays the next action until he can do it.  This requires a bit of special code
				if (pacs[0].isPossibleToDoAction(nextAction))
					pacs[0].performAction(nextAction);
				else if (pacs[0].isPossibleToDoAction(pacs[0].lastAction))
					pacs[0].performAction(pacs[0].lastAction);
			}
		});			
		
		// pacs[1] with a keyaboard-input behaviour.
		pacs[1] = new Pac(pacman, 1);
		pacs[1].setPolicyStep(new PolicyStep() {
			public void doPolicyStep()
			{
				int nextAction = pacman.getNextAction(pacs[1].tag);
				// pac man delays the next action until he can do it.  This requires a bit of special code
				if (pacs[1].isPossibleToDoAction(nextAction))
					pacs[1].performAction(nextAction);
				else if (pacs[1].isPossibleToDoAction(pacs[1].lastAction))
					pacs[1].performAction(pacs[1].lastAction);
			}
		});			

		// set the Pacs into the SimState.
		pacman.setPacs(pacs, actions);
		
		// create a new game.
		PacManWithUI game = new PacManWithUI(pacman) ;		
		SimpleController controller = new SimpleController(game);
		// play
		controller.pressPlay();		
	}

	public PacManWithUI()
	{
		super(new PacMan(System.currentTimeMillis()));
	}

	public PacManWithUI(SimState state)
	{
		super(state);
	}

	/** The desired FPS */
	public double FRAMES_PER_SECOND = 60;


	/** Creates a SimpleController and starts it playing. */
	public Controller createController()
	{
		SimpleController c = new SimpleController(this);
		c.pressPlay();
		return c;
	}

	public static String getName()
	{
		return "Pac Man";
	}

	public void start()
	{
		super.start();
		setupPortrayals();
	}

	public void load(SimState state)
	{
		super.load(state);
		setupPortrayals();
	}

	ValueGridPortrayal2D mazePortrayal = new ValueGridPortrayal2D();
	ContinuousPortrayal2D agentPortrayal = new ContinuousPortrayal2D();
	ContinuousPortrayal2D dotPortrayal = new ContinuousPortrayal2D();

	public void setupPortrayals()
	{
		PacMan pacman = (PacMan)state;

		// Scared ghosts are blue rectangles
		SimplePortrayal2D scared = new RectanglePortrayal2D(Color.blue, 1.6);

		// Create the agent portrayal
		agentPortrayal.setField(pacman.agents);

		// The Pac.  Note that you can have multiple pacs, each with different tags, and set it up like
		// below to display them with different colors.  For now we've got it set to one pac.
		agentPortrayal.setPortrayalForClass(Pac.class, new PacPortrayal(pacman, Color.yellow)
		{
			public void draw(Object object, Graphics2D graphics, DrawInfo2D info)
			{
				if (((Pac)object).tag==0) color = Color.yellow;
				else color = Color.green;
				super.draw(object, graphics, info);
			}
		});

		// Blinky is a red ghos unless he's scared, then he's a blue ghost.
		agentPortrayal.setPortrayalForClass(Blinky.class, new FacetedPortrayal2D(
		                                        new SimplePortrayal2D[]
		                                        {
		                                            new ImagePortrayal2D(this.getClass(), "images/blinkyu.png", 2),
		                                            new ImagePortrayal2D(this.getClass(), "images/blinkyl.png", 2),
		                                            new ImagePortrayal2D(this.getClass(), "images/blinkyd.png", 2),
		                                            new ImagePortrayal2D(this.getClass(), "images/blinkyr.png", 2),
		                                            new ImagePortrayal2D(this.getClass(), "images/frightened.png", 2),
		                                            new ImagePortrayal2D(this.getClass(), "images/frightened2.png", 2),
		                                        }));

		// Pinky is a pink ghost unless he's scared, then he's a blue ghost.
		agentPortrayal.setPortrayalForClass(Pinky.class, new FacetedPortrayal2D(
		                                        new SimplePortrayal2D[]
		                                        {
		                                            new ImagePortrayal2D(this.getClass(), "images/pinkyu.png", 2),
		                                            new ImagePortrayal2D(this.getClass(), "images/pinkyl.png", 2),
		                                            new ImagePortrayal2D(this.getClass(), "images/pinkyd.png", 2),
		                                            new ImagePortrayal2D(this.getClass(), "images/pinkyr.png", 2),
		                                            new ImagePortrayal2D(this.getClass(), "images/frightened.png", 2),
		                                            new ImagePortrayal2D(this.getClass(), "images/frightened2.png", 2),
		                                        }));


		// Inky is a cyan ghost unless he's scared, then he's a blue ghost.
		agentPortrayal.setPortrayalForClass(Inky.class, new FacetedPortrayal2D(
		                                        new SimplePortrayal2D[]
		                                        {
		                                            new ImagePortrayal2D(this.getClass(), "images/inkyu.png", 2),
		                                            new ImagePortrayal2D(this.getClass(), "images/inkyl.png", 2),
		                                            new ImagePortrayal2D(this.getClass(), "images/inkyd.png", 2),
		                                            new ImagePortrayal2D(this.getClass(), "images/inkyr.png", 2),
		                                            new ImagePortrayal2D(this.getClass(), "images/frightened.png", 2),
		                                            new ImagePortrayal2D(this.getClass(), "images/frightened2.png", 2),
		                                        }));


		// Clyde is a orange ghost unless he's scared, then he's a ghost rectangle.
		agentPortrayal.setPortrayalForClass(Clyde.class, new FacetedPortrayal2D(
		                                        new SimplePortrayal2D[]
		                                        {
		                                            new ImagePortrayal2D(this.getClass(), "images/clydeu.png", 2),
		                                            new ImagePortrayal2D(this.getClass(), "images/clydel.png", 2),
		                                            new ImagePortrayal2D(this.getClass(), "images/clyded.png", 2),
		                                            new ImagePortrayal2D(this.getClass(), "images/clyder.png", 2),
		                                            new ImagePortrayal2D(this.getClass(), "images/frightened.png", 2),
		                                            new ImagePortrayal2D(this.getClass(), "images/frightened2.png", 2),
		                                        }));

		// Create the dot portrayal (also the energizers)
		dotPortrayal.setField(pacman.dots);

		// Energizers are big
		dotPortrayal.setPortrayalForClass(Energizer.class, new OvalPortrayal2D(Color.white, 1));

		// dots are small
		dotPortrayal.setPortrayalForClass(Dot.class, new OvalPortrayal2D(Color.white, 0.4));

		// set up the maze portrayal
		mazePortrayal.setPortrayalForAll(new MazeCellPortrayal(pacman.maze));
		mazePortrayal.setField(pacman.maze);

		// add the RateAdjuster
		scheduleRepeatingImmediatelyAfter(new RateAdjuster(FRAMES_PER_SECOND));

		// reschedule the displayer
		display.reset();

		// redraw the display
		display.repaint();
	}



	public void init(final Controller c)
	{
		super.init(c);

		// make the displayer
		display = new Display2D(448,560,this)
		{
			public void createConsoleMenu() { }
			public void quit()
			{
				super.quit();
				((SimpleController) c).doClose();
			}
		};

		display.setBackdrop(Color.black);

		displayFrame = display.createFrame();
		displayFrame.setTitle("MASON Pac Man");
		c.registerFrame(displayFrame);   // register the frame so it appears in the "Display" list
		displayFrame.setVisible(true);

		// Notice the order: first the background, then the dots, then the agents, then the overlay
		display.attach( mazePortrayal, "Maze");
		// display.attach( background, "Background");
		display.attach( dotPortrayal, "Dots", 8, 8, true);
		display.attach( agentPortrayal, "Agents", 8, 8, true);
		display.attach( new Overlay(this), "Overlay");

		// Some stuff to make this feel less like MASON
		// delete the header
		display.remove(display.header);
		// delete all listeners
		display.removeListeners();
		// delete the scroll bars
		display.display.setVerticalScrollBarPolicy(display.display.VERTICAL_SCROLLBAR_NEVER);
		display.display.setHorizontalScrollBarPolicy(display.display.HORIZONTAL_SCROLLBAR_NEVER);
		// when we close the window, the application quits
		displayFrame.setDefaultCloseOperation(javax.swing.JFrame.DISPOSE_ON_CLOSE);
		// can't resize
		displayFrame.setResizable(false);
		// add antialiasing and interpolation
		display.insideDisplay.setupHints(true, false, false);

		// the window won't be the right size now -- modify it.
		displayFrame.pack();

		// Now we add in the listeners we want
		addListeners(display);
	}


	/** Creates key listeners which issue requests to the simulation. */
	public void addListeners(final Display2D display)
	{
		final PacMan pacman = (PacMan)state;
		final SimpleController cont = (SimpleController) controller;

		// Make us able to take focus -- this is by default true usually anyway
		display.setFocusable(true);

		// Make us request focus whenever our window comes up
		displayFrame.addWindowListener(new WindowAdapter()
		{
			public void windowActivated(WindowEvent e)
			{
				display.requestFocusInWindow();
			}
		});

		// the display frame has just been set visible so we need to request focus once
		display.requestFocusInWindow();


		display.addKeyListener(new KeyAdapter()
		{
			public void keyPressed(KeyEvent e)
			{
				int c = e.getKeyCode();
				switch(c)
				{
				case KeyEvent.VK_UP:
					pacman.actions[0] = Pac.N;
					break;
				case KeyEvent.VK_DOWN:
					pacman.actions[0] = Pac.S;
					break;
				case KeyEvent.VK_LEFT:
					pacman.actions[0] = Pac.W;
					break;
				case KeyEvent.VK_RIGHT:
					pacman.actions[0] = Pac.E;
					break;
				case KeyEvent.VK_W:
					pacman.actions[1] = Pac.N;
					break;
				case KeyEvent.VK_S:
					pacman.actions[1] = Pac.S;
					break;
				case KeyEvent.VK_A:
					pacman.actions[1] = Pac.W;
					break;
				case KeyEvent.VK_D:
					pacman.actions[1] = Pac.E;
					break;
				case KeyEvent.VK_R:             // Reset the board.  Easiest way: stop and play, which calls start()
					cont.pressStop();
					cont.pressPlay();
					break;
				case KeyEvent.VK_P:             // Pause or unpause the game
					cont.pressPause();
					break;
				case KeyEvent.VK_M:             // Call forth MASON's new simulation window
					if (cont.getPlayState() != cont.PS_PAUSED)  // pause it!
						cont.pressPause();
					cont.doNew();

					// the MASON window belongs to our frame, so Java stupidly doesn't send
					// us a window activated event when the MASON window is closed and our
					// frame comes to the fore again.  So we have to manually do request
					// focus again here.
					display.requestFocusInWindow();
					break;
				}
			}
		});
	}


	public void quit()
	{
		super.quit();
		display = null;

		if (displayFrame!=null)
		{
			JFrame f = displayFrame;
			displayFrame = null;
			f.dispose();
		}
	}

}
