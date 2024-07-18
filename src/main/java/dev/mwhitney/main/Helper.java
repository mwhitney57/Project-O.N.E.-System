package dev.mwhitney.main;

import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

/**
 * 
 * A set of easily-accessible, independent functions that perform helpful tasks.
 * 
 * @author Matthew Whitney
 *
 */
public class Helper {

	/**
	 * <ul>
	 * <p>	<b><i>runSafely</i></b>
	 * <p>	<code>public static void runSafely(Runnable runnable)</code>
	 * <p>	Runs the passed <tt>Runnable</tt> safely on the event dispatch thread (EDT).
	 * 		This method ensures that the passed <tt>Runnable</tt> will execute on the EDT.
	 * 		Before it runs, it checks to see if it is already on the EDT, in which case it will just run the code.
	 * 		If it is not already on the EDT, it will call on {@link SwingUtilities}'s <code>invokeLater(Runnable)</code> method.
	 * @param runnable - a <tt>Runnable</tt> with the code to run safely.
	 * </ul>
	 */
	public static void runSafely(Runnable runnable) {
//		System.out.println("Started Run"); // DEBUG
//		if(SwingUtilities.isEventDispatchThread()) {
//			runnable.run();
//		}
//		else {
			//	Testing running it with invokeLater() regardless. A post indicated it should be harmless to do so.
			SwingUtilities.invokeLater(runnable);
//		}
//		System.out.println("Done Run"); // DEBUG
	}
	
	/**
	 * <ul>
	 * <p>	<b><i>runSafelyAndWait</i></b>
	 * <p>	<code>public static void runSafelyAndWait(Runnable runnable)</code>
	 * <p>	Runs the passed <tt>Runnable</tt> safely on the event dispatch thread (EDT) and awaits its completion.
	 * 		This method ensures that the passed <tt>Runnable</tt> will execute on the EDT.
	 * 		Before it runs, it checks to see if it is already on the EDT, in which case it will just run the code.
	 * 		If it is not already on the EDT, it will call on {@link SwingUtilities}'s <code>invokeAndWait(Runnable)</code> method.
	 * <p>	<b>Note:</b> This method will wait until the code in the passed <tt>Runnable</tt> has finished executing before returning.
	 * 		If this behavior is not desired, please refer to the <code>runSafely(Runnable)</code> method.
	 * @param runnable - a <tt>Runnable</tt> with the code to run safely.
	 * </ul>
	 */
	public static void runSafelyAndWait(Runnable runnable) {
//		System.out.println("Started Run&Wait"); // DEBUG
		if(SwingUtilities.isEventDispatchThread()) {
			runnable.run();
		}
		else {
			try {
				SwingUtilities.invokeAndWait(runnable);
			} catch (InvocationTargetException | InterruptedException e) {e.printStackTrace();}
		}
//		System.out.println("Done Run&Wait"); // DEBUG
	}
	
	/**
	 * <ul>
	 * <p>	<b><i>runAsync</i></b>
	 * <p>	<code>public static void runAsync(Runnable runnable)</code>
	 * <p>	Runs the code in the passed <tt>Runnable</tt> asynchronously.
	 * 		This method allows for quick and easy creation and execution of a new <tt>Thread</tt>.
	 * 		However, it may not be the best approach depending on the situation.
	 * 		It is likely more resource-efficient to use <tt>CompletableFuture</tt> and its methods instead.
	 * @param runnable - a <tt>Runnable</tt> with the code to be run on the new <tt>Thread</tt> asynchronously.
	 * </ul>
	 */
	public static void runAsync(Runnable runnable) {
		final Thread thread = new Thread(runnable);
		thread.start();
	}
}
