package dev.mwhitney.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import dev.mwhitney.main.Helper;

/**
 * 
 * @author Matthew Whitney
 *
 */
public class BeautifulPanel extends JPanel {

	/** The <tt>BeautifulPanel</tt>'s unique serial. */
	private static final long serialVersionUID = 4070974763627935907L;
	
	/** An <code>int</code> for the size of the drop shadow behind this <tt>BeautifulPanel</tt> in pixels. */
	private int shadowSize = 0;
	
	/**
	 * <ul>
	 * <p>	<b><i>BeautifulPanel</i></b>
	 * <p>	<code>public BeautifulPanel(int dropShadowSize)</code>
	 * <p>	Creates a new <tt>BeautifulPanel</tt>.
	 * <p>	Note: This constructor automatically sets two notable settings which relate to its beauty.
	 * 		First, it sets the created <tt>BeautifulPanel</tt> to be <code>opaque</code>.
	 * 		Secondly, it automatically sets the border to be empty, but a certain amount of pixels wide in all directions based on the passed <code>int</code>.
	 * 		This border size relates to both the edge padding and the width of the drop shadow on all sides.
	 * 		Changing these properties may result in an unexpected appearance.
	 * <p>	<b>Default Drop Shadow Size:</b> 30
	 * </ul>
	 */
	public BeautifulPanel(int dropShadowSize) {
		super();
		
		//	Sets the Panel to be Opaque and Adds An Empty Border
		Helper.runAsync(() -> {
			setOpaque(false);
			setBorder(BorderFactory.createCompoundBorder(this.getBorder(), BorderFactory.createEmptyBorder(10 + dropShadowSize, 20 + dropShadowSize, 10 + dropShadowSize, 20 + dropShadowSize)));
		});
		
		//	Set Drop Shadow Size
		shadowSize = dropShadowSize;
	}
	
	/**
	 * <ul>
	 * <p>	<b><i>BeautifulPanel</i></b>
	 * <p>	<code>public BeautifulPanel()</code>
	 * <p>	Creates a new <tt>BeautifulPanel</tt>.
	 * <p>	Note: This constructor automatically sets two notable settings which relate to its beauty.
	 * 		First, it sets the created <tt>BeautifulPanel</tt> to be <code>opaque</code>.
	 * 		Secondly, it automatically sets the border to be empty, but <code>50 pixels</code> wide in all directions.
	 * 		This border size relates to both the edge padding and the width of the drop shadow on all sides.
	 * 		Changing these properties may result in an unexpected appearance.
	 * <p>	<b>Default Drop Shadow Size:</b> 30
	 * </ul>
	 */
	public BeautifulPanel() {
		this(30);
	}
	
    @Override
    protected void paintComponent(Graphics g) {
    	super.paintComponent(g);
    	
    	//	Create Graphics and Rendering Hints
		Graphics2D g2d = (Graphics2D) g.create();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		//	Draw the Rounded Drop Shadow
		for (int i = 0; i < shadowSize; i++) {
            g2d.setColor(new Color(0, 0, 0, (int) (( 10.0 / shadowSize) * i)));
            g2d.fillRoundRect(i, i, this.getWidth() - ((i * 2) + 1), this.getHeight() - (i * 2), 90, 90);
        }
		
		//	Draw the Rounded Panel
		g2d.setColor(getBackground());
		g2d.fillRoundRect(shadowSize, shadowSize, getWidth() - shadowSize*2, getHeight() - shadowSize*2, 60, 60);
		
		//	Dispose of the Graphics2D Object
		g2d.dispose();
    }
}
