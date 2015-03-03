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

public class MASONAgent implements Steppable
    {
    public EvolutionState state;
    public int threadnum;
    public GPData input;
    public ADFStack stack;
    public GPIndividual ind;
    public GPProblem problem;
        
    public MASONAgent(EvolutionState state, int threadnum, GPData input, ADFStack stack, GPIndividual ind, GPProblem problem)
        {
        this.state = state;
        this.threadnum = threadnum;
        this.input = input;
        this.stack = stack;
        this.ind = ind;
        this.problem = problem;
        }
                
    public void step(SimState st)
        {
        GPNode root = ((GPIndividual)ind).trees[0].child;
        root.eval(state,threadnum,input,stack,ind, problem);
        }
    }
