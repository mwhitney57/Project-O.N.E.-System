package dev.mwhitney.enums;

/**
 * 
 * An enum for the types of countdowns that are run using the <tt>CountdownManager</tt> and its respective <tt>Countdown</tt>s.
 * 
 * @author Matthew Whitney
 * 
 */
public enum CountdownType {
	/**
	 * <ul>
	 * 	<p>	<b><i>STANDARD</i></b>
	 * 	<p> The standard countdown type used for general, non-specific purposes.
	 * </ul>
	 */
	STANDARD,
	/**
	 * <ul>
	 * 	<p>	<b><i>OPEN_CYCLE</i></b>
	 * 	<p>	The open/close cycle countdown type.
	 * </ul>
	 */
	OPEN_CYCLE,
	/**
	 * <ul>
	 * 	<p>	<b><i>MANUAL_UNLOCKS</i></b>
	 * 	<p> The manual unlocks countdown type.
	 * </ul>
	 */
	MANUAL_UNLOCKS,
	/**
	 * <ul>
	 *	<p>	<b><i>FAILED_UNLOCKS</i></b>
	 *	<p>	The failed unlocks countdown type.
	 *		A failed unlock occurs when someone attempts to use an unauthorized fingerprint to open the door or change the system state.
	 * </ul>
	 */
	FAILED_UNLOCKS,
	/**
	 * <ul>
	 *	<p>	<b><i>BACKGROUND</i></b>
	 *	<p>	The background countdown type.
	 *		This type of countdown does not work with text. It is for countdowns that just need to work in the background and call back when they have completed.
	 * </ul>
	 */
	BACKGROUND
}
