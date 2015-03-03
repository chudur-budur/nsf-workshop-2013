package ec.app.mason.pacman ;

/**
 * The interface to be implemented with behaviours,
 * it could be used with executing a evolved GP tree,
 * user keyboard-input, Q-Learning, TD-Learning etc.
 * -- see the PacManWithUI.java and the Tester.java
 *    for details.
 **/

public interface PolicyStep
{
	public void doPolicyStep();
}
