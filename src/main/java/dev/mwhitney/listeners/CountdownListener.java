package dev.mwhitney.listeners;

import dev.mwhitney.enums.CountdownType;

/**
 * 
 * An interface that listens for {@link Countdown} updates.
 * 
 * @author Matthew Whitney
 *
 */
public interface CountdownListener {

	/**
	 * <ul>
	 * <p>	<b><i>textUpdated</i></b>
	 * <p>	<code>public void textUpdated(String text, CountdownType type)</code>
	 * <p>	An interface method that invokes upon receiving a request to update the text.
	 * <p>	This method takes a <tt>String</tt> containing the updated text.
	 * 		However, if no new information would be provided by passing a <tt>String</tt>, then passing <code>null</code> is acceptable.
	 * 		The passing of a <code>null</code> <tt>String</tt> with this method indicates that this call serves only to notify.
	 * @param text - a <tt>String</tt> containing the updated text.
	 * @param type - A <tt>CountdownType</tt> for the type of the calling countdown.
	 * </ul>
	 */
	public void textUpdated(String text, CountdownType type);
	/**
	 * <ul>
	 * <p>	<b><i>timeUpdated</i></b>
	 * <p>	<code>public void timeUpdated(int timeMS, CountdownType type)</code>
	 * <p>	An interface method that invokes upon the time left in the countdown being updated.
	 * @param timeMS - An <code>int</code> for the amount of countdown time left in milliseconds.
	 * @param type - A <tt>CountdownType</tt> for the type of the calling countdown.
	 * </ul>
	 */
	public void timeUpdated(int timeMS, CountdownType type);
	/**
	 * <ul>
	 * <p>	<b><i>countdownDone</i></b>
	 * <p>	<code>public void countdownDone(CountdownType type)</code>
	 * <p>	An interface method that invokes upon the countdown completing.
	 * @param type - A <tt>CountdownType</tt> for the type of the calling countdown.
	 * </ul>
	 */
	public void countdownDone(CountdownType type);
}
