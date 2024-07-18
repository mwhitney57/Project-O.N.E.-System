package dev.mwhitney.gui;

import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;

/**
 * 
 * @author Matthew Whitney
 *
 */
public class DragAdapter extends MouseAdapter {

	/** A <code>boolean</code> for whether or not a finger is currently pressed on the component. */
	private boolean fingerPressed = false;
	/** An <code>int</code> for the starting y-coordinate of a mouse click. */
	private int startY = 0;
	/** The <tt>JComponent</tt> to scroll through upon receiving drag events. */
	private JComponent scrollComponent;
	
	/**
	 * <ul>
	 * <p>	<b><i>DragAdapter</i></b>
	 * <p>	<code>public DragAdapter(JComponent component)</code>
	 * <p>	Creates a new <tt>DragAdapter</tt>.
	 * @param component - the <tt>JComponent</tt> to scroll through vertically.
	 * </ul>
	 */
	public DragAdapter(JComponent component) {
		super();
		
		scrollComponent = component;
	}

    @Override
    public void mousePressed(MouseEvent e) {
    	fingerPressed = true;
        startY = e.getY();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    	fingerPressed = false;
    }

    @Override
	public void mouseDragged(MouseEvent e) {
    	if(fingerPressed) {
    		//	Adjust Viewport Based on the Y-Difference from the Click Point to the Current Position After Mouse Drag.
    		final JViewport viewPort = (JViewport) SwingUtilities.getAncestorOfClass(JViewport.class, scrollComponent);
    		if (viewPort != null) {
    			final Rectangle scrolledView = viewPort.getViewRect();
    			scrolledView.y += (startY - e.getY());
    			
    			scrollComponent.scrollRectToVisible(scrolledView);
    		}
    	}
	}
}
