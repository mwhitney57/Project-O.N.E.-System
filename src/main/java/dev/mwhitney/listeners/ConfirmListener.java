package dev.mwhitney.listeners;

import java.util.EventListener;

/**
 * 
 * @author Matthew Whitney
 *
 */
public interface ConfirmListener extends EventListener {
	
	/**
	 * <ul>
	 * <p>	<b><i>confirmationReceived</i></b>
	 * <p>	<code>public void confirmationReceived(String input, boolean confirmed)</code>
	 * <p>	An interface method that invokes upon receiving confirmation.
	 * <p>	This method takes a <tt>String</tt> containing any input that comes along with the confirmation.
	 * 		A <code>boolean</code> is also received to indicate whether the confirmation was positive or negative.
	 * @param input - the <tt>String</tt> of text containing any input information.
	 * @param confirmed - a <code>boolean</code> indicating the result of the confirmation.
	 * </ul>
	 */
	public void confirmationReceived(String input, boolean confirmed);
}
