package dev.mwhitney.security;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.gpio.digital.DigitalOutputConfigBuilder;
import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.plugin.pigpio.PiGpioPlugin;

/**
 * 
 * @author Matthew Whitney
 *
 */
public class RelayLock {
	
	/* 
	 * DEBUG PIN STATE:
	 * relayPin.addListener(System.out::println);
	 */
	
	/** The <tt>Context</tt> used to help interface with the Raspberry Pi's GPIO pins. */
	private Context pi4j = null;
	/** A <tt>DigitalOutput</tt> object used for communicating with the door lock relay. */
	private DigitalOutput relayPin = null;
	
	/**
	 * <ul>
	 * <p>	<b><i>RelayLock</i></b>
	 * <p>	<code>public RelayLock()</code>
	 * <p>	Creates a new <tt>RelayLock</tt>.
	 * <p>	Note: There should only be one object of this class active at a time.
	 * </ul>
	 */
	public RelayLock() {
		pi4j = Pi4J.newAutoContext();
		final DigitalOutputConfigBuilder relayConfig = DigitalOutput.newConfigBuilder(pi4j)
			       .id("relay")
			       .name("Relay")
			       .address(21)
			       .shutdown(DigitalState.LOW)
			       .initial(DigitalState.LOW)
			       .provider(PiGpioPlugin.DIGITAL_OUTPUT_PROVIDER_ID);
		relayPin = pi4j.create(relayConfig);
	}
	
	/**
	 * <ul>
	 * <p>	<b><i>activateRelay</i></b>
	 * <p>	<code>public void activateRelay()</code>
	 * <p>	Activates the connected relay, turning it on.
	 * </ul>
	 */
	public void activateRelay() {
		relayPin.high();
	}
	
	/**
	 * <ul>
	 * <p>	<b><i>deactivateRelay</i></b>
	 * <p>	<code>public void deactivateRelay()</code>
	 * <p>	Deactivates the connected relay, turning it off.
	 * </ul>
	 */
	public void deactivateRelay() {
		relayPin.low();
	}
	
	/**
	 * <ul>
	 * <p>	<b><i>prepareForShutdown</i></b>
	 * <p>	<code>public void prepareForShutdown()</code>
	 * <p>	Prepares the relevant GPIO pins and all connected components for shutdown.
	 * </ul>
	 */
	public void prepareForShutdown() {
		relayPin.shutdown(pi4j);
		pi4j.shutdown();
	}
}
