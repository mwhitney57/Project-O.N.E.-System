package dev.mwhitney.listeners;

import dev.mwhitney.enums.CountdownType;

/**
 * 
 * An adaptive implementation of {@link CountdownListener} that allows for the overwriting of specific methods as opposed to the entire interface.
 * 
 * @author Matthew Whitney
 *
 */
public abstract class CountdownAdapter implements CountdownListener {

	/**
	 * {@inheritDoc}
	 */
	public void textUpdated(String text, CountdownType type) {};
	/**
	 * {@inheritDoc}
	 */
	public void timeUpdated(int timeMS, CountdownType type) {};
	/**
	 * {@inheritDoc}
	 */
	public void countdownDone(CountdownType type) {};
}
