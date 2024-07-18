package dev.mwhitney.main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;

import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.Timer;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import dev.mwhitney.enums.PopupButton;
import dev.mwhitney.gui.BColors;
import dev.mwhitney.gui.BeautifulButton;
import dev.mwhitney.gui.BeautifulPanel;
import dev.mwhitney.listeners.ConfirmListener;
import dev.mwhitney.listeners.VisibleListener;

/**
 * 
 * @author Matthew Whitney
 *
 */
public class NotificationPopup extends BeautifulPanel {
	
	/** The <tt>NotificationPopup</tt>'s unique serial. */
	private static final long serialVersionUID = 8171284235660190873L;
	
	//	Swing Components
	/** The <tt>GridBagConstraints</tt> used to lay out the content pane. */
	private GridBagConstraints gbc;
	/** The <tt>JTextArea</tt> containing the text of this notification pop-up. */
	private JTextPane textPane;
	/** The <tt>JTextField</tt> used for receiving text input. */
	private JTextField textField;
	/** The <tt>BeautifulButton</tt>s responsible for user input with this notification pop-up if applicable. */
	private BeautifulButton buttonOne, buttonTwo, buttonThree;
	
	//	Listeners
	/** The <tt>ConfirmListener</tt> responsible for taking confirm events. */
	private ConfirmListener confirmListener;
	/** The <tt>VisibleListener</tt> responsible for handling visibility changes. */
	private VisibleListener visibleListener;
	
	//	Variables
	/** The width and height values of the parent component or screen that this notification pop-up displays on. */
	private int screenWidth, screenHeight;
	/** A <code>boolean</code> for whether or not to hold the application focus while this notification pop-up is displayed. */
	private boolean holdsFocus;
	/** A <code>boolean</code> for whether or not this notification pop-up is currently using text input. */
	private boolean usingTextInput;
	/** An <code>int</code> representing the total number of buttons currently being used in this notification pop-up. */
	private int buttonAmount = 0;
	
	//	Timer
	/** An <code>int</code> for the lifespan of this <tt>NotificationPopup</code> in milliseconds. */
	private int lifespanTime = 2000;
	/** The <tt>Thread</tt> responsible for handling the lifespan of this <tt>NotificationPopup</tt> */
	private Timer lifespanTimer = new Timer(lifespanTime, (actionEvent) -> {
		closePopup();
	});

	
	/**
	 * <ul>
	 * <p>	<b><i>NotificationPopup</i></b>
	 * <p>	<code>public NotificationPopup(Dimension screenSize, String text, Dimension size, int buttonCount, boolean useTextInput)</code>
	 * <p>	Creates a new <tt>NotificationPopup</tt> using passed text, screen size, and pop-up size.
	 * @param screenSize - a <tt>Dimension</tt> containing the width and height of the parent component or screen.
	 * @param text - a <tt>String</tt> with the text to display in this <tt>NotificationPopup</tt>.
	 * @param size - a <tt>Dimension</tt> containing the size of this <tt>NotificationPopup</tt>.
	 * @param buttonCount - an <code>int</code> for the amount of buttons to use in this <tt>NotificationPopup</tt>.
	 * @param useTextInput - a <code>boolean</code> for whether or not to use text input.
	 * </ul>
	 */
	public NotificationPopup(Dimension screenSize, String text, Dimension size, int buttonCount, boolean useTextInput) {
		super();
		
		screenWidth = (int) screenSize.getWidth();
		screenHeight = (int) screenSize.getHeight();
		this.setMinimumSize(size);
		
		lifespanTimer.setRepeats(false);
		usingTextInput = useTextInput;
		
		//	Ensure Minimum and Maximum Button Amounts Are Respected
		if(buttonCount<0) {
			buttonCount = 0;
		}
		else if(buttonCount>3) {
			buttonCount = 3;
		}
		else if(buttonCount>0) {
			holdsFocus = true;
		}
		else {
			holdsFocus = false;
		}
		buttonAmount = buttonCount;
		

		//	Setup Windows and Components
		setupContentPane();
		setupTextPane(text);
		textField = new JTextField();
		textField.setHorizontalAlignment(JTextField.CENTER);
		textField.setFont(new Font("Montserrat", Font.PLAIN, 16));
		if(useTextInput) {
//			setupTextField();
		}
		setupButtons(buttonCount);
		if(buttonCount>0) {
			/*
			 * WHERE I LEFT OFF 4-23-22
			 * 
			 * Just trying to fix the notification contents from jumping a little after the notification becomes visible.
			 * Seems to be related to the sizing of components, coupled with the layout manager.
			 */
			
		}
		
		//	Final Visual Setup Parts
		prepare();
	}
	
	/**
	 * <ul>
	 * <p>	<b><i>NotificationPopup</i></b>
	 * <p>	<code>public NotificationPopup(Dimension screenSize, String text, int buttonCount, boolean useTextInput)</code>
	 * <p>	Creates a new <tt>NotificationPopup</tt> using passed text and screen size with the default width and height.
	 * @param screenSize - a <tt>Dimension</tt> containing the width and height of the parent component or screen.
	 * @param text - a <tt>String</tt> with the text to display in this <tt>NotificationPopup</tt>.
	 * @param buttonCount - an <code>int</code> for the amount of buttons to use in this <tt>NotificationPopup</tt>.
	 * @param useTextInput - a <code>boolean</code> for whether or not to use text input.
	 * </ul>
	 */
	public NotificationPopup(Dimension screenSize, String text,  int buttonCount, boolean useTextInput) {
		this(screenSize, text, new Dimension(450, 300), buttonCount, useTextInput);
	}
	
	/**
	 * <ul>
	 * <p>	<b><i>NotificationPopup</i></b>
	 * <p>	<code>public NotificationPopup(Dimension screenSize String text, int lifespan)</code>
	 * <p>	Creates a new <tt>NotificationPopup</tt> using passed text, screen size, and lifespan in milliseconds.
	 * <p>	This method uses the default width and height.
	 * 		To use a custom width and height, utilize another constructor.
	 * @param screenSize - a <tt>Dimension</tt> containing the width and height of the parent component or screen.
	 * @param text - a <tt>String</tt> with the text to display in this <tt>NotificationPopup</tt>.
	 * @param lifespan - the lifespan in milliseconds.
	 * </ul>
	 */
	public NotificationPopup(Dimension screenSize, String text, int lifespan) {
		this(screenSize, text, 0, false);
		
		displayPopup();
		lifespanTime = lifespan;
		lifespanTimer.setInitialDelay(lifespanTime);
		lifespanTimer.restart();
	}
	
	/**
	 * <ul>
	 * <p>	<b><i>setupContentPane</i></b>
	 * <p>	<code>private void setupContentPane()</code>
	 * <p>	Sets up the content pane of this notification pop-up.
	 * </ul>
	 */
	private void setupContentPane() {
		//	Content Pane Setup
		this.setBackground(Color.WHITE);
		this.setLayout(new GridBagLayout());
		this.setFocusable(true);
		this.setRequestFocusEnabled(true);
		this.setVisible(false);
		
		//	Create and Set Layout Position
		gbc = new GridBagConstraints();
		gbc.weightx = 0.5;
		gbc.weighty = 1;
		gbc.insets = new Insets(5, 10, 5, 10);
	}
	
	/**
	 * <ul>
	 * <p>	<b><i>setupTextPane</i></b>
	 * <p>	<code>private void setupTextPane(String text)</code>
	 * <p>	Sets up the <tt>JTextPane</tt> of this notification pop-up.*
	 * @param text - a <tt>String</tt> with the text for the <tt>JTextPane</tt>. 
	 * </ul>
	 */
	private void setupTextPane(String text) {
		//	Text Pane Setup
		if(textPane==null) {
			textPane = new JTextPane();
			textPane.setEditable(false);
			textPane.setFocusable(false);
			textPane.setBorder(null);
			textPane.setOpaque(false);
			textPane.setFont(new Font("Montserrat", Font.PLAIN, 22));
//			textPane.setBounds(0, 0, 200, 40);
//			textPane.setPreferredSize(new Dimension(200, 40));
		}
		textPane.setText(text);
		
		//	Center Text
		final SimpleAttributeSet attributeSet = new SimpleAttributeSet();
		StyleConstants.setAlignment(attributeSet, StyleConstants.ALIGN_CENTER);
		StyleConstants.setBackground(attributeSet, new Color(0, 0, 0, 0));
		textPane.getStyledDocument().setParagraphAttributes(0, textPane.getStyledDocument().getLength(), attributeSet, false);
		
		//	Set Layout Position and Add Component
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = buttonAmount;
		this.add(textPane, gbc);
	}
	
	/**
	 * <ul>
	 * <p>	<b><i>setupTextField</i></b>
	 * <p>	<code>private void setupTextField()</code>
	 * <p>	Sets up the <tt>JTextField</tt> used for user input.
	 * </ul>
	 */
	private void setupTextField() {
		//	Text Field Setup
		if(textField==null) {
			textField = new JTextField();
			textField.setHorizontalAlignment(JTextField.CENTER);
			textField.setFont(new Font("Montserrat", Font.PLAIN, 16));
		}
//		textField.setPreferredSize(new Dimension(textPane.getWidth()*2, 40));
		
		if(usingTextInput) {
			//	Set Layout Position and Add Component
			gbc.gridx = 0;
			gbc.gridy = 1;
			gbc.gridwidth = buttonAmount;
			this.add(textField, gbc);
		}
	}
	
	/**
	 * <ul>
	 * <p>	<b><i>setupButtons</i></b>
	 * <p>	<code>private void setupButtons()</code>
	 * <p>	Sets up the <tt>JButton</tt> components for confirmation usage.
	 * @param buttonCount - an <code>int</code> for the amount of buttons to setup.
	 * </ul>
	 */
	private void setupButtons(int buttonCount) {
		//	Initialize and Configure Button
		if(buttonOne==null) {
			buttonOne = new BeautifulButton("");
			buttonOne.setFocusPainted(false);
			buttonOne.setForeground(Color.WHITE);
			buttonOne.setFont(new Font("Montserrat", Font.BOLD, 30));
			buttonTwo = new BeautifulButton("");
			buttonTwo.setFocusPainted(false);
			buttonTwo.setForeground(Color.WHITE);
			buttonTwo.setFont(buttonOne.getFont());
			buttonThree = new BeautifulButton("");
			buttonThree.setFocusPainted(false);
			buttonThree.setForeground(Color.WHITE);
			buttonThree.setFont(buttonOne.getFont());
		}
		buttonOne.setPreferredSize(new Dimension(175, 100));
		buttonOne.setText("Yes");
		buttonOne.setBackgroundColors(BColors.BEAUTIFUL_LIGHTGREEN, BColors.BEAUTIFUL_GREEN);
		for(ActionListener actionListener : buttonOne.getActionListeners()) {
			buttonOne.removeActionListener(actionListener);
		}
		buttonOne.addActionListener((actionEvent) -> {
			if(usingTextInput) {
				confirmListener.confirmationReceived(textField.getText(), true);
			}
			else {
				confirmListener.confirmationReceived("", true);
			}
		});
		
		//	Set Layout Position and Add Component
		gbc.gridwidth = 1;
		gbc.gridx = 0;
		gbc.gridy = GridBagConstraints.RELATIVE;
		this.add(buttonOne, gbc);
		
		//	2 Buttons
		if(buttonCount>1) {
			//	Initialize and Configure Button
			if(buttonTwo==null) {
				buttonTwo = new BeautifulButton("");
				buttonTwo.setFocusPainted(false);
				buttonTwo.setForeground(Color.WHITE);
				buttonTwo.setFont(buttonOne.getFont());
			}
			buttonTwo.setPreferredSize(new Dimension(175, 100));
			buttonTwo.setText("No");
			buttonTwo.setBackgroundColors(BColors.BEAUTIFUL_LIGHTRED, BColors.BEAUTIFUL_DARKRED);
			for(ActionListener actionListener : buttonTwo.getActionListeners()) {
				buttonTwo.removeActionListener(actionListener);
			}
			buttonTwo.addActionListener((actionEvent) -> {
				confirmListener.confirmationReceived("", false);
			});
			
			//	Set Layout Position and Add Component
			gbc.gridx = 1;
			gbc.gridy = GridBagConstraints.RELATIVE;
			this.add(buttonTwo, gbc);
			
			//	3 Buttons
			if(buttonCount>2) {
				//	Initialize and Configure Button
				if(buttonThree==null) {
					buttonThree = new BeautifulButton("");
					buttonThree.setFocusPainted(false);
					buttonThree.setForeground(Color.WHITE);
					buttonThree.setFont(buttonOne.getFont());
				}
				buttonThree.setPreferredSize(new Dimension(175, 100));
				buttonThree.setText("Cancel");
				buttonThree.setBackgroundColors(BColors.BEAUTIFUL_DARKLIGHTRED, BColors.BEAUTIFUL_DARKDARKRED);
				for(ActionListener actionListener : buttonThree.getActionListeners()) {
					buttonThree.removeActionListener(actionListener);
				}
				buttonThree.addActionListener((actionEvent) -> {
					confirmListener.confirmationReceived("", false);
				});
				
				//	Set Layout Position and Add Component
				gbc.gridx = 2;
				gbc.gridy = GridBagConstraints.RELATIVE;
				this.add(buttonThree, gbc);
			}
		}
	}
	
	/**
	 * <ul>
	 * <p>	<b><i>prepare</i></b>
	 * <p>	<code>public void prepare()</code>
	 * <p>	Prepares this <tt>NotificationPopup</tt> by compacting and correctly positioning it and its components.
	 * 		This method also repaints and revalidates this <tt>NotificationPopup</tt>.
	 * </ul>
	 */
	private void prepare() {
		this.revalidate();
		this.repaint();
		buttonOne.setSize(buttonOne.getPreferredSize());
		buttonTwo.setSize(buttonTwo.getPreferredSize());
		buttonThree.setSize(buttonThree.getPreferredSize());
		textField.setSize(textField.getPreferredSize());
		textPane.setMinimumSize(textPane.getPreferredSize());
		this.setSize(this.getPreferredSize());
		this.setLocation((int) (screenWidth/2 - this.getWidth()/2), (int) (screenHeight/2 - this.getHeight()/2));
	}
	
	/**
	 * <ul>
	 * <p>	<b><i>displayPopup</i></b>
	 * <p>	<code>public void displayPopup()</code>
	 * <p>	Displays this <tt>NotificationPopup</tt>.
	 * </ul>
	 */
	public void displayPopup() {
		if (visibleListener != null) {
			visibleListener.isNowShown();
		}
		//	Displays the NotificationPopup
		Helper.runSafelyAndWait(new Runnable() {
			@Override
			public void run() {
				setVisible(true);
			}
		});
	}
	
	/**
	 * <ul>
	 * <p>	<b><i>hidePopup</i></b>
	 * <p>	<code>public void hidePopup()</code>
	 * <p>	Hides this <tt>NotificationPopup</tt>.
	 * </ul>
	 */
	public void hidePopup() {
		//	Hides the NotificationPopup
		Helper.runSafelyAndWait(new Runnable() {
			@Override
			public void run() {
				setVisible(false);
			}
		});
		if(visibleListener!=null) {
			visibleListener.isNowHidden();
		}
	}
	
	/**
	 * <ul>
	 * <p>	<b><i>closePopup</i></b>
	 * <p>	<code>public void closePopup()</code>
	 * <p>	Closes this <tt>NotificationPopup</tt>.
	 * <p>	This is currently just another way to call the <code>hidePopup()</code> method.
	 * </ul>
	 */
	public void closePopup() {
		//	Hide Popup
		hidePopup();
//		Helper.runSafelyAndWait(() -> {
//		});
	}
	
	/**
	 * <ul>
	 * <p>	<b><i>reset</i></b>
	 * <p>	<code>public void reset(String text, int buttonCount, boolean useTextInput)</code>
	 * <p>	Resets this <tt>NotificationPopup</tt>, preparing all of its contents using new parameters.
	 * <p>	This method does not affect the <tt>NotificationPopup</tt> panel itself.
	 * 		Instead, it focuses on the children of the popup, preparing them for a new look.
	 * @param text - a <tt>String</tt> with the text to display in this <tt>NotificationPopup</tt>.
	 * @param buttonCount - an <code>int</code> for the amount of buttons to use in this <tt>NotificationPopup</tt>.
	 * @param useTextInput - a <code>boolean</code> for whether or not to use text input.
	 * </ul>
	 */
	public void reset(String text, int buttonCount, boolean useTextInput) {
		//	Stop lifespan Timer if active and hide popup if visible.
		lifespanTimer.stop();
		if(this.isVisible()) {
			hidePopup();
		}
		
		usingTextInput = useTextInput;
		
		//	Ensure Minimum and Maximum Button Amounts Are Respected
		if (buttonCount < 0) {
			buttonCount = 0;
		}
		else if (buttonCount > 3) {
			buttonCount = 3;
		}
		else if (buttonCount > 0) {
			holdsFocus = true;
		}
		else {
			holdsFocus = false;
		}
		buttonAmount = buttonCount;
		final int finalBtnCount = buttonCount;
		
		confirmListener = null;
		Helper.runSafelyAndWait(() -> {
			//	Removes all child components.
			this.removeAll();
			
			//	Create and Set Layout Position
			this.setLayout(new GridBagLayout());
			gbc = new GridBagConstraints();
			gbc.weightx = 0.5;
			gbc.weighty = 1;
			gbc.insets = new Insets(5, 10, 5, 10);
			
			// Setup Components
			setupTextPane(text);
			if (useTextInput) {
				setupTextField();
			}
			if (finalBtnCount > 0) {
				setupButtons(finalBtnCount);
			}
			
			prepare();
		});
	}
	
	/**
	 * <ul>
	 * <p>	<b><i>reset</i></b>
	 * <p>	<code>public void reset(String text, int lifespan)</code>
	 * <p>	Resets this <tt>NotificationPopup</tt>, preparing all of its contents using new parameters.
	 * <p>	This method does not affect the <tt>NotificationPopup</tt> panel itself.
	 * 		Instead, it focuses on the children of the popup, preparing them for a new look.
	 * @param text - a <tt>String</tt> with the text to display in this <tt>NotificationPopup</tt>.
	 * @param lifespan - the lifespan in milliseconds.
	 * </ul>
	 */
	public void reset(String text, int lifespan) {
		reset(text, 0, false);
		
		displayPopup();
		lifespanTime = lifespan;
		lifespanTimer.setInitialDelay(lifespanTime);
		lifespanTimer.restart();
	}
	
	/**
	 * <ul>
	 * <p>	<b><i>getNotificationText</i></b>
	 * <p>	<code>public void getNotificationText()</code>
	 * <p>	Gets this <tt>NotificationPopup</tt>'s text.
	 * @return The text being displayed on this <tt>NotificationPopup</tt> in the form of a <tt>String</tt>.
	 * </ul>
	 */
	public String getNotificationText() {
		return textField.getText();
	}
	
	/**
	 * <ul>
	 * <p>	<b><i>setNotificationText</i></b>
	 * <p>	<code>public void setNotificationText(String newText)</code>
	 * <p>	Sets this <tt>NotificationPopup</tt>'s text for it to display.
	 * @param newText - the <tt>String</tt> to display on this <tt>NotificationPopup</tt>.
	 * </ul>
	 */
	public void setNotificationText(String newText) {
		System.out.println("Set Notif Text");
		hidePopup();
		System.out.println("Done Hiding Popup");
		Helper.runSafelyAndWait(() -> {
			textPane.setText(newText);
			prepare();
		});
		System.out.println("Done The Run and Wait Ish");
		displayPopup();
		System.out.println("Done Displaying");
	}
	
	/**
	 * <ul>
	 * <p>	<b><i>isHoldingFocus</i></b>
	 * <p>	<code>public boolean isHoldingFocus()</code>
	 * <p>	Returns a <code>boolean</code>  indicating whether or not this <tt>NotificationPopup</tt> holds the application focus.
	 * <p>	If the notification does not hold focus, then it can be closed by the focus being lost.
	 * 		If the focus is held, then the application cannot be closed this way.
	 * @return <code>true</code> if this <tt>NotificationPopup</tt> is holding the focus; <code>false</code> otherwise.
	 * </ul>
	 */
	public boolean isHoldingFocus() {
		return holdsFocus;
	}
	
	/**
	 * <ul>
	 * <p>	<b><i>setHoldsFocus</i></b>
	 * <p>	<code>public void setHoldsFocus(boolean willHoldFocus)</code>
	 * <p>	Sets whether or not this <tt>NotificationPopup</tt> should hold the application focus.
	 * <p>	If the notification does not hold focus, then it can be closed by the focus being lost.
	 * 		If the focus is held, then the application cannot be closed this way.
	 * @param holdFocus - a <code>boolean</code> for whether or not this <tt>NotificationPopup</tt> should hold the application focus.
	 * </ul>
	 */
	public void setHoldsFocus(boolean holdFocus) {
		holdsFocus = holdFocus;
	}
	
	/**
	 * <ul>
	 * <p>	<b><i>getButton</i></b>
	 * <p>	<code>public BeautifulButton getButton(int buttonNumber)</code>
	 * <p>	Gets a <tt>BeautifulButton</tt> belonging to this <tt>NotificationPopup</tt> based on the passed <code>int</code>.
	 * <p>	Given the maximum of three buttons per pop-up, this method will return one of the following:
	 * 		<code> yes button (0)</code>, the <code>no button (1)</code>, or the <code>spare button (2)</code>.
	 * 		If a number less than 0 or greater than 2 is supplied, then this method will return <code>null</code>.
	 * 		Similarly, if one of the specified buttons has or will not be created, then this will also result in a <code>null</code> return.
	 * @param buttonNumber - the index of the button to get.
	 * @return one of the three available <tt>BeautifulButton</tt>s belonging to this <tt>NotificationPopup</tt>.
	 * </ul>
	 */
	public BeautifulButton getButton(int buttonNumber) {
		if(buttonNumber==0) {
			return buttonOne;
		}
		else if(buttonNumber==1) {
			return buttonTwo;
		}
		else if(buttonNumber==2) {
			return buttonThree;
		}
		else {
			return null;
		}
	}
	
	public void setButton(int btn, PopupButton selection) {
		BeautifulButton button = null;
		switch(selection) {
			case YES:
				break;
			case NO:
				break;
			case CANCEL:
				break;
			case ADMIN:
				break;
			case NAME:
				break;
			case EMERGENCY_USES:
				break;
			case NONE:
				//	set the btn number to be NONE and not used
				return;
			default:
				return;
		}
		switch(btn) {
			case 1:
				buttonOne = button;
			case 2:
				buttonTwo = button;
			case 3:
				buttonThree = button;
		}
	}
	
	/**
	 * <ul>
	 * <p>	<b><i>setConfirmListener</i></b>
	 * <p>	<code>public void setConfirmListener(ConfirmListener cl)</code>
	 * <p>	Sets the one and only <tt>ConfirmListener</tt> to be used by this <tt>NotificationPopup</tt>.
	 * @param cl - the <tt>ConfirmListener</tt> to be used by this <tt>NotificationPopup</tt>.
	 * </ul>
	 */
	public void setConfirmListener(ConfirmListener cl) {
		confirmListener = cl;
	}
	
	/**
	 * <ul>
	 * <p>	<b><i>setVisibleListener</i></b>
	 * <p>	<code>public void setVisibleListener(VisibleListener vl)</code>
	 * <p>	Sets the one and only <tt>VisibleListener</tt> to be used by this <tt>NotificationPopup</tt>.
	 * @param vl - the <tt>VisibleListener</tt>to be used by this <tt>NotificationPopup</tt>.
	 * </ul>
	 */
	public void setVisibleListener(VisibleListener vl) {
		visibleListener = vl;
	}
}
