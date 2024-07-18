package dev.mwhitney.enums;

/**
 * 
 * An enum for all of the button options available to use with the {@link NotificationPopup} class.
 * 
 * @author Matthew Whitney
 * 
 */
public enum PopupButton {
	/**
	 * <ul>
	 * 	<p>	<b><i>YES</i></b>
	 * 	<p>	A <b>Yes</b> button, colored green.
	 * </ul>
	 */
	YES,
	/**
	 * <ul>
	 * 	<p>	<b><i>NO</i></b>
	 * 	<p>	A <b>No</b> button, colored red.
	 * </ul>
	 */
	NO,
	/**
	 * <ul>
	 * 	<p>	<b><i>CANCEL</i></b>
	 * 	<p>	A <b>Cancel</b> button, colored [INSERT COLOR! !!! ! ! ! !!].
	 * </ul>
	 */
	CANCEL,
	/**
	 * <ul>
	 * 	<p>	<b><i>ADMIN</i></b>
	 * 	<p>	An <b>Admin</b> button, colored [INSERT COLOR! !!! ! ! ! !!].
	 * 		It is used to set whether a user should be an Administrator.
	 * </ul>
	 */
	ADMIN,
	/**
	 * <ul>
	 * 	<p>	<b><i>NAME</i></b>
	 * 	<p>	A <b>Name</b> button, colored [INSERT COLOR! !!! ! ! ! !!].
	 * 		It is used to set the name of a user.
	 * </ul>
	 */
	NAME,
	/**
	 * <ul>
	 * 	<p>	<b><i>EMERGENCY_USES</i></b>
	 * 	<p>	An <b>Emergency Uses/EUs</b> button, colored [INSERT COLOR! !!! ! ! ! !!].
	 * 		It is used to reset the emergency uses count of a user.
	 * </ul>
	 */
	EMERGENCY_USES,
	/**
	 * <ul>
	 * 	<p>	<b><i>NONE</i></b>
	 * 	<p>	An option to indicate that the button should not be used.
	 * </ul>
	 */
	NONE
}
