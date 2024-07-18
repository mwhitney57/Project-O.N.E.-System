package dev.mwhitney.listeners;

/**
 * 
 * @author Matthew Whitney
 *
 */
public interface VisibleListener {

	/**
	 * <ul>
	 * <p>	<b><i>isNowShown</i></b>
	 * <p>	<code>public void isNowShown()</code>
	 * <p>	An interface method that invokes upon being shown.
	 * </ul>
	 */
	public void isNowShown();
	
	/**
	 * <ul>
	 * <p>	<b><i>isNowHidden</i></b>
	 * <p>	<code>public void isNowHidden()</code>
	 * <p>	An interface method that invokes upon being hidden.
	 * </ul>
	 */
	public void isNowHidden();
}
