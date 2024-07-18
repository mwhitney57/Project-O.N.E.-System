package dev.mwhitney.main;

import dev.mwhitney.enums.CountdownType;
import dev.mwhitney.listeners.CountdownListener;

/**
 * 
 * A class that counts down from a set number and provides updates as it does so.
 * <br>
 * Each countdown is incredibly accurate, and its update frequency and performance can be adjusted.
 * 
 * @author Matthew Whitney
 *
 */
public class Countdown {
	
	/** The <tt>Thread</tt> used for counting down and sending updates via the <tt>CountdownListener</tt>. */
	private Thread countdownThread;
	/** The <tt>CountdownListener</tt> used to send countdown updates. */
	private CountdownListener countdownListener;
	/** This <tt>Countdown</tt>'s <tt>CountdownType</tt>. */
	private CountdownType type = CountdownType.STANDARD;
	/** An <code>int</code> for the duration of this <tt>Countdown</tt> in milliseconds. */
	private int duration = 3000;
	/** An <code>int</code> for the sleep interval in milliseconds. The downtime in between countdown refreshes. */
	private int sleepInterval = 1000;
	/** A <tt>String</tt> of text that is tailored to this <tt>Countdown</tt> and can be accessed later. */
	private String leadingText;
	
	/**
	 * <ul>
	 * <p>	<b><i>Countdown</i></b>
	 * <p>	<code>public Countdown(CountdownType type)</code>
	 * <p>	Creates a new <tt>Countdown</tt>.
	 * @param type - the <tt>CountdownType</tt> of this <tt>Countdown</tt>.
	 * @param start - A <code>boolean</code> for whether or not this <tt>Countdown</tt> should start immediately.
	 * </ul>
	 */
	public Countdown(CountdownType type, boolean start) {
		this.type = type;
		if(start) {
			restart();
		}
	}
	
	/**
	 * <ul>
	 * <p>	<b><i>Countdown</i></b>
	 * <p>	<code>public Countdown(CountdownType type)</code>
	 * <p>	Creates a new <tt>Countdown</tt>.
	 * @param type - the <tt>CountdownType</tt> of this <tt>Countdown</tt>.
	 * </ul>
	 */
	public Countdown(CountdownType type) {
		this(type, false);
	}
	
	/**
	 * <ul>
	 * <p>	<b><i>setupThread</i></b>
	 * <p>	<code>private void setupThread()</code>
	 * <p>	Sets up the <tt>Thread</tt> used by this countdown.
	 * </ul>
	 */
	private void setupThread() {
		countdownThread = new Thread() {
			/**
			 * <ul>
			 * <p>	<b><i>accurateSleep</i></b>
			 * <p>	<code>private int accurateSleep(int sleepTime)</code>
			 * <p>	Sleeps this <tt>Thread</tt> for the amount of time specified by the passed <code>int</code>.
			 * 		This method does more than just simplify making a <code>Thread.sleep()</code> call.
			 * 		It also measures the <b>actual time slept</b> in nanoseconds before converting that number to milliseconds and returning it.
			 * 		This method will instead return <code>-1</code> if the thread was interrupted.
			 * @param sleepTime - An <code>int</code> for the amount of time to sleep in milliseconds.
			 * @return An <code>int</code> containing either the number of milliseconds that have passed, or <code>-1</code> in the event of a thread interruption.
			 * </ul>
			 */
			private int accurateSleep(int sleepTime) {
				//	Sleep then decrement the passed time.
				final long timeBefore = System.nanoTime();
				try {
					Thread.sleep(sleepTime);
				} catch (InterruptedException ie) {
					//	Finish interruption.
					Thread.currentThread().interrupt();
					return -1;
				}
				return (int) ((System.nanoTime() - timeBefore)/1000000);
			}
			
			@Override
			public void run() {
				//	Get countdown duration and sleep interval BEFORE starting in order to prevent manipulation during a countdown.
				int millisecondsLeft = duration;
				int sleepMilli = sleepInterval;
				int result = 0;
				
				//	Positive Countdown Time
				if(millisecondsLeft > 0) {
					while(millisecondsLeft > 0) {
						//	Update the time left with each loop.
						countdownListener.timeUpdated(millisecondsLeft, type);
						
						//	If the amount of time left in the countdown is less than the sleep interval, only sleep for that long.
						//	This helps to prevent a sleep cycle that would last much longer than necessary.
						//	There was going to be plus 10ms to account for inaccuracy, but with the thread sleeps being very accurate, this hurt more than it helped.
						if(millisecondsLeft < sleepMilli) {
							sleepMilli = millisecondsLeft;
						}
						
						//	Sleep then decrement the passed time.
						result = accurateSleep(sleepMilli);
						if(result == -1) break;
						else millisecondsLeft -= result;
					}
				}
				//	Negative Countdown Time (No time updates with the text)
				else if(millisecondsLeft < 0) {
					//	Send signal to update the action status label once before looping.
					countdownListener.textUpdated(null, type);
					
					while(millisecondsLeft < 0) {
						//	Reference similar code block above for description.
						if(millisecondsLeft > (-sleepMilli)) {
							sleepMilli = Math.abs(millisecondsLeft);
						}
						
						//	Sleep then increment the passed time.
						result = accurateSleep(sleepMilli);
						if(result == -1) break;
						else millisecondsLeft += result;
					}
				}
				
				//	Completed Countdown if Thread Wasn't Interrupted.
				if(result != -1) countdownListener.countdownDone(type);
			}
		};
	}
	
	/**
	 * <ul>
	 * <p>	<b><i>setDuration</i></b>
	 * <p>	<code>public void setDuration(int time)</code>
	 * <p>	Sets the duration of this <tt>Countdown</tt> in seconds.
	 * @param time - An <code>int</code> for this <tt>Countdown</tt>'s new duration in seconds.
	 * </ul>
	 */
	public void setDuration(int time) {
		duration = time * 1000;
	}
	
	/**
	 * <ul>
	 * <p>	<b><i>setDurationMS</i></b>
	 * <p>	<code>public void setDurationMS(int timeMS)</code>
	 * <p>	Sets the duration of this <tt>Countdown</tt> in milliseconds.
	 * @param time - An <code>int</code> for this <tt>Countdown</tt>'s new duration in milliseconds.
	 * </ul>
	 */
	public void setDurationMS(int timeMS) {
		duration = timeMS;
	}
	
	/**
	 * <ul>
	 * <p>	<b><i>setSleepInterval</i></b>
	 * <p>	<code>public void setSleepInterval(int interval)</code>
	 * <p>	Sets the sleep interval of this <tt>Countdown</tt>'s <tt>Thread</tt> in milliseconds.
	 * 		This is the amount of time the <tt>Thread</tt> sleeps for before updating the time remaining in the countdown.
	 * 		For slight increases in accuracy, setting a lower interval may be beneficial.
	 * 		However, it would increase the amount of iterations and updates, which may impact performance.
	 * @param interval - An <code>int</code> for the sleep interval in milliseconds.
	 * </ul>
	 */
	public void setSleepInterval(int interval) {
		sleepInterval = interval;
	}
	
	/**
	 * <ul>
	 * <p>	<b><i>getLeadingText</i></b>
	 * <p>	<code>public String getLeadingText()</code>
	 * <p>	Gets this <tt>Countdown</tt>'s custom, leading text.
	 * @return a <tt>String</tt> containing the leading text.
	 * </ul>
	 */
	public String getLeadingText() {
		return leadingText;
	}
	
	/**
	 * <ul>
	 * <p>	<b><i>setLeadingText</i></b>
	 * <p>	<code>public void setLeadingText(String text)</code>
	 * <p>	Sets this <tt>Countdown</tt>'s custom, leading text.
	 * @param text - a <tt>String</tt> containing the leading text.
	 * </ul>
	 */
	public void setLeadingText(String text) {
		leadingText = text;
	}

	/**
	 * <ul>
	 * <p>	<b><i>start</i></b>
	 * <p>	<code>private void start()</code>
	 * <p>	Starts this <tt>Countdown</tt>.
	 * </ul>
	 */
	private void start() {
		//	Start the countdown.
		countdownThread.start();
	}
	
	/**
	 * <ul>
	 * <p>	<b><i>restart</i></b>
	 * <p>	<code>public void restart()</code>
	 * <p>	Restarts this <tt>Countdown</tt>, stopping it if it is running, resetting it, then starting it again.
	 * </ul>
	 */
	public void restart() {
		//	Ensure the Thread is stopped before it is modified.
		stop();
		
		//	Restart the countdown.
		setupThread();
		start();
	}
	
	/**
	 * <ul>
	 * <p>	<b><i>stop</i></b>
	 * <p>	<code>public void stop()</code>
	 * <p>	Stops this <tt>Countdown</tt>, interrupting it if it's still executing.
	 *		A <tt>Countdown</tt> cannot be truly paused. Therefore, per the name, this function stops it.
	 *		The only way to reuse this <tt>Countdown</tt> instance is to use the <code>restart()</code> function.
	 * </ul>
	 */
	public void stop() {
		if(isRunning()) {
			countdownThread.interrupt();
		}
	}
	
	/**
	 * <ul>
	 * <p>	<b><i>isRunning</i></b>
	 * <p>	<code>public boolean isRunning()</code>
	 * <p>	Checks to see if this <tt>Countdown</tt> is currently running.
	 * @return <code>true</code> if this <tt>Countdown</tt> is currently running; <code>false</code> otherwise.
	 * </ul>
	 */
	public boolean isRunning() {
		return (countdownThread != null && countdownThread.isAlive());
	}
	
	/**
	 * <ul>
	 * <p>	<b><i>setCountdownListener</i></b>
	 * <p>	<code>public void setCountdownListener(CountdownListener cl)</code>
	 * <p>	Sets the one and only <tt>CountdownListener</tt> to be used by this <tt>Countdown</tt>.
	 * @param cl - the <tt>CountdownListener</tt> to be used by this <tt>Countdown</tt>.
	 * </ul>
	 */
	public void setCountdownListener(CountdownListener cl) {
		countdownListener = cl;
	}
}
	