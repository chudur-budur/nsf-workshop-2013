/*
  Copyright 2006 by Sean Luke
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/


package ec.app.mason.ant.func;
import ec.*;
import ec.app.ant.*;
import ec.gp.*;
import ec.util.*;
import ec.app.mason.ant.*;
import ec.util.*;
import ec.*;
import ec.gp.*;
import ec.gp.koza.*;
import java.io.*;
import java.util.*;
import ec.simple.*;
import sim.engine.*;
import sim.field.continuous.*;
import sim.util.*;

public class Move extends GPNode
    {
    public String toString() { return "move"; }

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
        MASONDemo sim = ((MASONProblem)problem).sim;
        MASONAgent agent = sim.agent;
        Continuous2D world = sim.world;
                
        Double2D pos = world.getObjectLocation(agent);
        world.setObjectLocation(agent, new Double2D(pos.x + 1, pos.y + 1));
        }
    }



