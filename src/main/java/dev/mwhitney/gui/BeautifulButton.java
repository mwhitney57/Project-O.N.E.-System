package dev.mwhitney.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JButton;
import javax.swing.SwingConstants;

import dev.mwhitney.main.Helper;

/**
 * 
 * @author Matthew Whitney
 *
 */
public class BeautifulButton extends JButton {

	/** The <tt>BeautifulButton</tt>'s unique serial. */
	private static final long serialVersionUID = 1448911228352429792L;

	/** A set of <tt>Color</tt>s used to make the background gradient for each <tt>BeautifulButton</tt>. */
	private Color colorTop = Color.LIGHT_GRAY, colorBottom = Color.LIGHT_GRAY;
	/** A set of <tt>Color</tt>s used to make the background gradient for each <tt>BeautifulButton</tt> when it is pressed. */
	private Color colorTopPressed = colorTop.darker(), colorBottomPressed = colorBottom.darker();
	
	/**
	 * <ul>
	 * <p>	<b><i>BeautifulButton</i></b>
	 * <p>	<code>public BeautifulButton(String label)</code>
	 * <p>	Creates a new <tt>BeautifulButton</tt> with the passed label.
	 * @param label - a <tt>String</tt> with the new button's label.
	 * </ul>
	 */
	public BeautifulButton(String label) {
		super(label);
		
		Helper.runSafely(() -> {
			//	Sets up the Button Attributes
			setContentAreaFilled(false);
			setVerticalTextPosition(SwingConstants.BOTTOM);
			setHorizontalTextPosition(SwingConstants.CENTER);
			setFocusPainted(false);
		});
	}
	
	/**
	 * <ul>
	 * <p>	<b><i>BeautifulButton</i></b>
	 * <p>	<code>public BeautifulButton(String label, Color colorOne, Color colorTwo)</code>
	 * <p>	Creates a new <tt>BeautifulButton</tt> with the passed label and colors.
	 * @param label - a <tt>String</tt> with the new button's label.
	 * @param colorOne - the top <tt>Color</tt> in this button's background gradient.
	 * @param colorTwo - the bottom <tt>Color</tt> in this button's background gradient.
	 * </ul>
	 */
	public BeautifulButton(String label, Color colorOne, Color colorTwo) {
		this(label);
		
		//	Sets the Background Gradient
		setBackgroundColors(colorOne, colorTwo);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		//	Create Graphics and Rendering Hints
		Graphics2D g2d = (Graphics2D) g.create();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		//	Draws the Gradient Background Based on the Pressed State
		if(getModel().isPressed()) {
			g2d.setPaint(new GradientPaint(getWidth()/2, 0, colorTopPressed, getWidth()/2, getHeight(), colorBottomPressed));
		}
		else {
			g2d.setPaint(new GradientPaint(getWidth()/2, 0, colorTop, getWidth()/2, getHeight(), colorBottom));
		}
		g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
		
		//	Dispose of the Graphics2D Object
		g2d.dispose();
		
		super.paintComponent(g);
	}
	
	@Override
	protected void paintBorder(Graphics g) {
		//	Create Graphics and Rendering Hints
		Graphics2D g2d = (Graphics2D) g.create();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		//	Draws the Rounded Border
		g2d.setColor(colorBottom);
		g2d.setStroke(new BasicStroke(5.0f));
		g2d.drawRoundRect(2, 2, getWidth()-5, getHeight()-5, 20, 20);
		
		//	Dispose of the Graphics2D Object
		g2d.dispose();
	}
	
	/**
	 * <ul>
	 * <p>	<b><i>setBackgroundColors</i></b>
	 * <p>	<code>public void setBackgroundColors(Color colorOne, Color colorTwo)</code>
	 * <p>	Sets the colors for this <tt>BeautifulButton</tt>'s background gradient.
	 * <p>	Note: This method automatically sets the button's pressed colors based off of the passed colors.
	 * 		If you would like to set your own colors for the button's pressed-state,
	 * 		then be sure to call the <code>setBackgroundColorsPressed</code> method after this one.
	 * @param colorOne - the top <tt>Color</tt> in this button's background gradient.
	 * @param colorTwo - the bottom <tt>Color</tt> in this button's background gradient.
	 * </ul>
	 */
	public void setBackgroundColors(Color colorOne, Color colorTwo) {
		colorTop = colorOne;
		colorBottom = colorTwo;
		colorTopPressed = darkenColorForPress(colorOne);
		colorBottomPressed = darkenColorForPress(colorTwo);
	}
	
	/**
	 * <ul>
	 * <p>	<b><i>setBackgroundColorsPressed</i></b>
	 * <p>	<code>public void setBackgroundColorsPressed(Color colorOnePressed, Color colorTwoPressed)</code>
	 * <p>	Sets the colors for this <tt>BeautifulButton</tt>'s background gradient.
	 * <p>	Note: This method only changes the button's background colors that display when it is in its pressed state.
	 * @param colorOnePressed - the top <tt>Color</tt> in this button's pressed background gradient.
	 * @param colorTwoPressed - the bottom <tt>Color</tt> in this button's pressed background gradient.
	 * </ul>
	 */
	public void setBackgroundColorsPressed(Color colorOnePressed, Color colorTwoPressed) {
		colorTopPressed = colorOnePressed;
		colorBottomPressed = colorTwoPressed;
	}
	
	/**
	 * <ul>
	 * <p>	<b><i>darkenColorForPress</i></b>
	 * <p>	<code>private Color darkenColorForPress(Color color)</code>
	 * <p>	Darkens the passed <tt>Color</tt> by a set amount.
	 * <p>	More specifically, this method subtracts 20 from each RGB value while leaving the Alpha value the same.
	 * 		If the reduction to any value would result in a number below 0, then the value is dropped to 0 instead.
	 * <p>	Example:
	 * 		<code>Color(18, 137, 93) --> Color(0, 117, 73)</code>
	 * @param color - the <tt>Color</tt> to be darkened.
	 * @return the darkened <tt>Color</tt>.
	 * </ul>
	 */
	private Color darkenColorForPress(Color color) {
		//	Return New Color
		return new Color(Math.max(0, color.getRed() - 20), Math.max(0, color.getGreen() - 20), Math.max(0, color.getBlue() - 20), color.getAlpha());
	}
}
