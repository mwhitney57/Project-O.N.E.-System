package dev.mwhitney.enums;

/**
 * 
 * An enum for all of the screens (pages) that can be shown on the system's UI.
 * 
 * @author Matthew Whitney
 * 
 */
public enum Screen {
	/**
	 * <ul>
	 * 	<p>	<b><i>MAIN</i></b>
	 * 	<p> The main (or home) screen that shows when the system initializes.
	 * </ul>
	 */
	MAIN,
	/**
	 * <ul>
	 * 	<p>	<b><i>SETTINGS</i></b>
	 * 	<p> The settings screen available only to administrators.
	 * </ul>
	 */
	SETTINGS,
	/**
	 * <ul>
	 * 	<p>	<b><i>SETTINGS_FP</i></b>
	 * 	<p> The fingerprint settings screen for creating, modifying, and removing fingerprint registries that is available only to administrators.
	 * </ul>
	 */
	SETTINGS_FP
}
