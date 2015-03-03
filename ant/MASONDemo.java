package ec.app.mason.ant;

import ec.app.mason.ant.func.*;
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


public class MASONDemo extends SimState
    {
    public MASONAgent agent;
    public Continuous2D world;
        
    public MASONDemo(MersenneTwisterFast random, MASONAgent a)
        {
        super(random);
        agent = a;

        // SimState has constructors for providing a random number generator instead of a seed,
        // but they are protected.  The stored seed value (which is only used by GUI tools for display purposes
        // and isn't necessary) will be 0 -- a bogus value.  That doesn't matter since what matters is the random
        // number generator itself.
                
        }
                
    public void start()
        {
        super.start();
        world = new Continuous2D(100,100,100);
        world.setObjectLocation(agent, new Double2D(0,0));
        schedule.scheduleRepeating(agent);
        }
    }
