package dev.mwhitney.main;

import dev.mwhitney.enums.CountdownType;
import dev.mwhitney.listeners.CountdownAdapter;
import dev.mwhitney.listeners.CountdownListener;

/**
 * 
 * A class that manages countdowns of various types via instances of {@link Countdown}.
 * 
 * @author Matthew Whitney
 *
 */
public class CountdownManager extends CountdownAdapter {
	
	//	Countdown Variables
	/** The <tt>CountdownListener</tt> used by each <tt>Countdown</tt> to send countdown updates. */
	private CountdownListener countdownListener;
	/** The <tt>Countdown</tt> used for all standard countdowns. */
	private Countdown standardCountdown;
	/** The <tt>Countdown</tt> used for the open/close cycle countdown. */
	private Countdown openCycleCountdown;
	/** The <tt>Countdown</tt> used for the manual unlocks countdown. */
	private Countdown manualUnlocksCountdown;
	/** The <tt>Countdown</tt> used for the failed unlocks countdown. */
	private Countdown failedUnlocksCountdown;
	
	/** A <tt>Countdown</tt> responsible for resetting the status text after a certain amount of time of inactivity. */
	private Countdown inactivityTextCountdown;
	
	/**
	 * <ul>
	 * <p>	<b><i>CountdownManager</i></b>
	 * <p>	<code>public CountdownManager()</code>
	 * <p>	Creates a new <tt>CountdownManager</tt>.
	 * </ul>
	 */
	public CountdownManager() {
		super();
		
		//	Setup CountdownManager-only Countdown(s), then all other Countdowns.
		inactivityTextCountdown = new Countdown(CountdownType.BACKGROUND);
		inactivityTextCountdown.setDurationMS(50);
		inactivityTextCountdown.setCountdownListener(new CountdownAdapter() {
			@Override
			public void countdownDone(CountdownType type) {
				if(!standardCountdown.isRunning() && !manualUnlocksCountdown.isRunning()) {
					CountdownManager.this.textUpdated(null, CountdownType.STANDARD);
				}
			}
		});;
		setupCountdowns();
	}
	
	/**
	 * <ul>
	 * <p>	<b><i>setupCountdowns</i></b>
	 * <p>	<code>private void setupCountdowns()</code>
	 * <p>	Sets up the <tt>Countdown</tt>s used by this <tt>CountdownManager</tt>.
	 * </ul>
	 */
	private void setupCountdowns() {
		standardCountdown = new Countdown(CountdownType.STANDARD);
		openCycleCountdown = new Countdown(CountdownType.OPEN_CYCLE);
		manualUnlocksCountdown = new Countdown(CountdownType.MANUAL_UNLOCKS);
		failedUnlocksCountdown = new Countdown(CountdownType.FAILED_UNLOCKS);
		
		countdownListener = new CountdownListener() {
			@Override
			public void textUpdated(String text, CountdownType type) {
				//	Receiving null here indicates a call to update the text with only the countdown's custom text.
				if(text == null) {
					switch(type) {
						case STANDARD:
							CountdownManager.this.textUpdated(standardCountdown.getLeadingText(), type);
							break;
						case OPEN_CYCLE:
							CountdownManager.this.textUpdated(openCycleCountdown.getLeadingText(), type);
							break;
						case MANUAL_UNLOCKS:
							CountdownManager.this.textUpdated(manualUnlocksCountdown.getLeadingText(), type);
							break;
						case FAILED_UNLOCKS:
						case BACKGROUND:
							//	Ignore. These countdowns run in the background and have no special text.
							break;
					}
				}
			}

			@Override
			public void timeUpdated(int timeMS, CountdownType type) {
				int seconds = timeMS/1000;
				
				switch(type) {
					case STANDARD:
						final String standardText = standardCountdown.getLeadingText();
						//	Convert time into prepared string including the prefix, then send updated text.
						//	Only update the text when the open/close cycle (which takes precedence) is not running.
						if(!openCycleCountdown.isRunning()) {
							if(seconds >= 60) {
								CountdownManager.this.textUpdated(standardText + String.format("%02d:%02d", seconds/60, seconds%60) + ".", type);
							}
							else if(seconds > 1) {
								CountdownManager.this.textUpdated(standardText + String.format("%02d:%02d", seconds/60, seconds%60) + ".", type);
							}
							else {
								CountdownManager.this.textUpdated(standardText + String.format("%02d:%02d", seconds/60, seconds%60) + ".", type);
							}
						}
						break;
					case OPEN_CYCLE:
						final String openCycleText = openCycleCountdown.getLeadingText();
						//	Convert time into prepared string including the prefix, then send updated text.
						if(seconds >= 60) {
							CountdownManager.this.textUpdated(openCycleText + String.format("%02d:%02d", seconds/60, seconds%60) + ".", type);
						}
						else if(seconds > 1) {
							CountdownManager.this.textUpdated(openCycleText + String.format("%02d:%02d", seconds/60, seconds%60) + ".", type);
						}
						else {
							CountdownManager.this.textUpdated(openCycleText + String.format("%02d:%02d", seconds/60, seconds%60) + ".", type);
						}
						break;
					case MANUAL_UNLOCKS:
						final String manualUnlocksText = manualUnlocksCountdown.getLeadingText();
						//	Convert time into prepared string including the prefix, then send updated text.
						//	Only update the text when the open/close cycle and standard countdowns (which takes precedence, in that order) are not running.
						if(!openCycleCountdown.isRunning() && !standardCountdown.isRunning()) {
							if(seconds >= 60) {
								CountdownManager.this.textUpdated(manualUnlocksText + String.format("%02d:%02d", seconds/60, seconds%60) + ".", type);
							}
							else if(seconds > 1) {
								CountdownManager.this.textUpdated(manualUnlocksText + String.format("%02d:%02d", seconds/60, seconds%60) + ".", type);
							}
							else {
								CountdownManager.this.textUpdated(manualUnlocksText + String.format("%02d:%02d", seconds/60, seconds%60) + ".", type);
							}
						}
						break;
					case FAILED_UNLOCKS:
					case BACKGROUND:
						//	Ignore. These countdowns run in the background and have no special text.
						break;
				}
			}

			@Override
			public void countdownDone(CountdownType type) {
				if(type != CountdownType.BACKGROUND && type != CountdownType.FAILED_UNLOCKS) {
					//	These countdowns run in the background and have no special text, so only run this method with types that handle text.
					updateDefaultTextOnInactivity();
				}
				
				//	Pass along that a Countdown has completed.
				CountdownManager.this.countdownDone(type);
			}
		};
		standardCountdown.setCountdownListener(countdownListener);
		openCycleCountdown.setCountdownListener(countdownListener);
		manualUnlocksCountdown.setCountdownListener(countdownListener);
		failedUnlocksCountdown.setCountdownListener(countdownListener);
	}
	
	/**
	 * <ul>
	 * <p>	<b><i>startCountdown</i></b>
	 * <p>	<code>public void startCountdown(String countdownText, int seconds, CountdownType type)</code>
	 * <p>	Starts a countdown of the passed type, for the passed amount of seconds, using the passed prefix text.
	 * <p>	<b>Note:</b> The passed <code>int</code> with the number of seconds to count down from can be positive or negative.
	 * 		Passing a positive number will indicate that the countdown text should include the remaining amount of seconds till it completes.
	 * 		Inversely, passing a negative number indicates that the remaining time should be excluded from the updated countdown text.
	 * 		Either way, the number to count down from will be an absolute (non-negative) version of the passed <code>int</code>.
	 * @param countdownText - a <tt>String</tt> containing the prefix text used in the updated countdown text.
	 * @param seconds - an <code>int</code> for the amount of seconds to count down from.
	 * @param type - a <tt>CountdownType</tt> corresponding to the type of <tt>Countdown</tt> to start.
	 * </ul>
	 */
	public void startCountdown(String countdownText, int seconds, CountdownType type) {
		switch(type) {
			case STANDARD:
				standardCountdown.setDuration(seconds);
				standardCountdown.setLeadingText(countdownText);
				standardCountdown.restart();
				break;
			case OPEN_CYCLE:
				openCycleCountdown.setDuration(seconds);
				openCycleCountdown.setLeadingText(countdownText);
				openCycleCountdown.restart();
				break;
			case MANUAL_UNLOCKS:
				manualUnlocksCountdown.setDuration(seconds);
				manualUnlocksCountdown.setLeadingText(countdownText);
				manualUnlocksCountdown.restart();
				break;
			case FAILED_UNLOCKS:
				failedUnlocksCountdown.setDuration(seconds);
				failedUnlocksCountdown.restart();
				break;
			case BACKGROUND:
				//	Nothing for now. Probably ideal to eventually expand this so Countdowns can be created, started, and cancelled dynamically.
				break;
		}
	}
	
	/**
	 * <ul>
	 * <p>	<b><i>stopCountdown</i></b>
	 * <p>	<code>public void stopCountdown(CountdownType type)</code>
	 * <p>	Stops any active countdown of the passed type immediately and sends a blank text update.
	 * </ul>
	 */
	public void stopCountdown(CountdownType type) {
		switch(type) {
			case STANDARD:
				break; // Do Nothing for now.
			case OPEN_CYCLE:
				openCycleCountdown.stop();
				break;
			case MANUAL_UNLOCKS:
				manualUnlocksCountdown.stop();
				countdownListener.textUpdated(null, type);
				break;
			case FAILED_UNLOCKS:
				failedUnlocksCountdown.stop();
				break;
			case BACKGROUND:
				//	Nothing for now. Probably ideal to eventually expand this so Countdowns can be created, started, and cancelled dynamically.
				break;
		}
	}
	
	/**
	 * <ul>
	 * <p>	<b><i>updateDefaultTextOnInactivity</i></b>
	 * <p>	<code>private void updateDefaultTextOnInactivity()</code>
	 * <p>	Starts the <tt>Countdown</tt> responsible for resetting the action status text upon countdown inactivity.
	 * </ul>
	 */
	private void updateDefaultTextOnInactivity() {
		if(!inactivityTextCountdown.isRunning()) {
			inactivityTextCountdown.restart();
		}
	}
}
