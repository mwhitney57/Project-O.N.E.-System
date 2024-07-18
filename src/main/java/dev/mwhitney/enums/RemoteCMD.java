package dev.mwhitney.enums;

/**
 * 
 * An enum for the expected remote commands from the communications server.
 * 
 * @author Matthew Whitney
 * 
 */
public enum RemoteCMD {
	/**
	 * <ul>
	 * 	<p>	<b><i>LOCK</i></b>
	 * 	<p>	The lock/close command for the door.
	 * </ul>
	 */
	LOCK,
	/**
	 * <ul>
	 * 	<p>	<b><i>UNLOCK</i></b>
	 * 	<p>	The unlock/open command for the door.
	 * </ul>
	 */
	UNLOCK,
	/**
	 * <ul>
	 * 	<p>	<b><i>SYSTEM_LOCK</i></b>
	 * 	<p>	The lock command for the system.
	 * </ul>
	 */
	SYSTEM_LOCK,
	/**
	 * <ul>
	 * 	<p>	<b><i>SYSTEM_UNLOCK</i></b>
	 * 	<p>	The unlock command for the system.
	 * </ul>
	 */
	SYSTEM_UNLOCK,
	/**
	 * <ul>
	 * 	<p>	<b><i>MANUALUNLOCKS_DISABLE</i></b>
	 * 	<p>	The command for disabling manual unlocks on the system.
	 * </ul>
	 */
	MANUALUNLOCKS_DISABLE,
	/**
	 * <ul>
	 * 	<p>	<b><i>MANUALUNLOCKS_ENABLE</i></b>
	 * 	<p>	The command for enabling manual unlocks on the system.
	 * </ul>
	 */
	MANUALUNLOCKS_ENABLE,
	/**
	 * <ul>
	 * 	<p>	<b><i>UNRECOGNIZED</i></b>
	 * 	<p>	A received command that is not recognized or handled.
	 * </ul>
	 */
	UNRECOGNIZED
}
