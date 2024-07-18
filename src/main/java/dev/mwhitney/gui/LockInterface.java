package dev.mwhitney.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import dev.mwhitney.enums.CountdownType;
import dev.mwhitney.enums.RemoteCMD;
import dev.mwhitney.enums.Screen;
import dev.mwhitney.listeners.ConfirmListener;
import dev.mwhitney.listeners.MessageListener;
import dev.mwhitney.listeners.VisibleListener;
import dev.mwhitney.main.CountdownManager;
import dev.mwhitney.main.FileManager;
import dev.mwhitney.main.Helper;
import dev.mwhitney.main.NotificationPopup;
import dev.mwhitney.remote.RemoteClient;
import dev.mwhitney.security.FingerprintScanner;
import dev.mwhitney.security.RelayLock;

/**
 * 
 * @author Matthew Whitney
 *
 */
public class LockInterface extends JFrame {

	/** The <tt>LockInterface</tt>'s unique serial. */
	private static final long serialVersionUID = 6806573214858621792L;
	
	//	Primary Objects
	/** A <tt>FingerprintScanner</tt> to manage fingerprints for authentication. */
	private FingerprintScanner fingerprintScanner;
	/** A <tt>RelayLock</tt> for controlling the relay that connects to the door lock. */
	private RelayLock relayLock;
	/** A <tt>FileManager</tt> for managing all of the Project O.N.E. files, including the properties file. */
	private FileManager fileManager;
	/** A <tt>RemoteClient</tt> for sending and receiving messages with the communications server for remote control. */
	private RemoteClient remoteClient;
	/** A <tt>CountdownManager</tt> for managing countdowns relating to the action status label. */
	private CountdownManager countdownManager;
	
	
	//	Door Lock/Unlock Process
	/**
	 * A <code>boolean</code> for whether or not the system is locked.
	 * When the system is locked, only administrators and authorized emergency bypass users may open the door.
	 * When the system is unlocked, opening the door does not require authorization.
	 */
	private boolean systemLocked = false;
	/** A <code>boolean</code> for whether or not a door open cycle attempt is currently in-progress. */
	private boolean doorOpenInProgress = false;
	/** An <code>int</code> for how long the open cycle lasts in milliseconds. */
	private int openCycleLengthMS = 3000;
	
	/** A <code>boolean</code> for whether or not manual unlocks are enabled. */
	private boolean manualUnlocksEnabled = true;
	/** A <code>int</code> for the amount of consecutive failed unlock attempts. */
	private int consecutiveFailedUnlocks = 0;
	
	
	//	User Interface Screen Settings & Return Values
	/** A set of <code>int</code> values for the screen's width and height. */
	public static final int SCREEN_WIDTH = (int) 800,
							SCREEN_HEIGHT = (int) 480;
	/** An <code>int</code> value representing the current UI screen. */
	private Screen currentScreen = Screen.MAIN;
	/** A <tt>String</tt> containing the <b>current</b> default text being used in the action status label. */
	private String currentDefaultText = "";
	/** A <tt>String</tt> containing the <b>standard</b> default text used in the action status label. */
	final private static String DEFAULT_TEXT = "";
	/** A <tt>String</tt> containing the <b>system disabled</b> default text used in the action status label. */
	final private static String DEFAULT_TEXT_SYSDISABLED = "System disabled by an Administrator.";
	
	
	//	Swing Components
	/** A <tt>JLayeredPanel</tt> for the content of the <tt>LockInterface</tt>. */
	private JLayeredPane contentPane;
	/** A <tt>JLabel</tt> responsible for holding the background image. */
	private JLabel lblBackgroundImage;
	/** A <tt>NotificationPopup</tt> responsible for displaying notifications and confirmation messages. */
	private NotificationPopup popupPane;
	/** A <tt>JPanel</tt> responsible for providing a partially-transparent layer between the main interface and the <code>notificationPane</code>. */
	private JPanel shadowPane;
	
	/** A <tt>JButton</tt> responsible for opening the door. */
	private JButton btnOpen;
	/** A <tt>BeautifulButton</tt> responsible for locking and unlocking the system. */
	private BeautifulButton btnLock;
	/** A <tt>BeautifulButton</tt> used to access the settings. */
	private BeautifulButton btnSettings;
	/** A <tt>JLabel</tt> responsible for displaying the status of any current action, such as opening the door lock. */
	private JLabel lblActionStatus;
	
	/** A <tt>JPanel</tt> for the settings screen content. */
	private JPanel settingsPane;
	/** A <tt>BeautifulButton</tt> used to go back from a settings page. */
	private BeautifulButton btnSettingsBack;
	/** A <tt>BeautifulButton</tt> used to access the fingerprint settings. */
	private BeautifulButton btnSettingsFP;
	/** A <tt>BeautifulButton</tt> used to enable or disable manual unlocks. */
	private BeautifulButton btnManualUnlocks;
	/** A <tt>BeautifulButton</tt> used to safely exit the user interface. */
	private BeautifulButton btnExit;
	
	/** A <tt>JPanel</tt> for the fingerprint settings screen content. */
	private JPanel settingsFPPane;
	/** A <tt>BeautifulButton</tt> used for registering fingerprints. */
	private BeautifulButton btnRegisterFP;
	/** A <tt>BeautifulButton</tt> used to set a fingerprint's nickname. */
	private BeautifulButton btnEditFP;
	/** A <tt>BeautifulButton</tt> used to remove a fingerprint from the system. */
	private BeautifulButton btnRemoveFP;
	/** A <tt>JList{@literal<String>}</tt> that contains the <code>listModel</code> full of registered fingerprints. */
	private JList<String> fingerprintList;
	/** A <tt>DefaultListModel</tt> that holds the items of the <code>fingerprintList</code>. */
	private DefaultListModel<String> listModel;
	
	
	//	Image Icons Used in the LockInterface
	/** An <tt>ImageIcon</tt> for the locked variant of the background. */
	private final ImageIcon imageBackgroundLocked = new ImageIcon(LockInterface.class.getResource("/club/minimunch57/images/backgroundLocked.png"));
	/** An <tt>ImageIcon</tt> for the unlocked variant of the background. */
	private final ImageIcon imageBackgroundUnlocked = new ImageIcon(LockInterface.class.getResource("/club/minimunch57/images/backgroundUnlocked.png"));
	/** An <tt>ImageIcon</tt> for the background shown while in the settings. */
	private final ImageIcon imageBackgroundSettings = new ImageIcon(LockInterface.class.getResource("/club/minimunch57/images/backgroundSettings.png"));
	/** An <tt>ImageIcon</tt> for the locked variant of the lock/unlock button. */
	private final ImageIcon iconLockLocked = new ImageIcon(LockInterface.class.getResource("/club/minimunch57/images/lockLocked.png"));
	/** An <tt>ImageIcon</tt> for the unlocked variant of the lock/unlock button. */
	private final ImageIcon iconLockUnlocked = new ImageIcon(LockInterface.class.getResource("/club/minimunch57/images/lockUnlocked.png"));
	/** An <tt>ImageIcon</tt> for the locked variant of the open button. */
	private final ImageIcon iconOpenLocked = new ImageIcon(LockInterface.class.getResource("/club/minimunch57/images/openLocked.png"));
	/** An <tt>ImageIcon</tt> for the pressed and locked variant of the open button. */
	private final ImageIcon iconOpenLockedP = new ImageIcon(LockInterface.class.getResource("/club/minimunch57/images/openLockedP.png"));
	/** An <tt>ImageIcon</tt> for the unlocked variant of the open button. */
	private final ImageIcon iconOpenUnlocked = new ImageIcon(LockInterface.class.getResource("/club/minimunch57/images/openUnlocked.png"));
	/** An <tt>ImageIcon</tt> for the pressed and unlocked variant of the open button. */
	private final ImageIcon iconOpenUnlockedP = new ImageIcon(LockInterface.class.getResource("/club/minimunch57/images/openUnlockedP.png"));
	/** An <tt>ImageIcon</tt> for a fingerprint icon. */
	private final ImageIcon iconFingerprint = new ImageIcon(LockInterface.class.getResource("/club/minimunch57/images/fingerprint128.png"));
	/** An <tt>ImageIcon</tt> for a gear or settings icon. */
	private final ImageIcon iconGear = new ImageIcon(LockInterface.class.getResource("/club/minimunch57/images/gear48C.png"));
	/** An <tt>ImageIcon</tt> for a gear or settings icon. */
	private final ImageIcon iconGearBig = new ImageIcon(LockInterface.class.getResource("/club/minimunch57/images/gear64.png"));
	/** An <tt>ImageIcon</tt> for an edit icon. */
	private final ImageIcon iconEdit = new ImageIcon(LockInterface.class.getResource("/club/minimunch57/images/edit64.png"));
	/** An <tt>ImageIcon</tt> for a remove or 'X' icon. */
	private final ImageIcon iconRemove = new ImageIcon(LockInterface.class.getResource("/club/minimunch57/images/remove64.png"));
	
	
	//	Colors
	/** A set of <tt>Color</tt>s used for the <b>locked</b> system state. */
	private final Color colorLocked1 = new Color(255, 32, 41), colorLocked2 = new Color(163, 0, 4);
	/**	A set of <tt>Color</tt>s used for the <b>unlocked</b> system state. */
	private final Color colorUnlocked1 = new Color(90, 255, 32), colorUnlocked2 = new Color(40, 163, 0);

	
	//	Main Method
	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			try {
				//	Start the GUI
				new LockInterface();
			} catch (Exception e) {
				System.out.println("<!> Error initializing the application.");
				e.printStackTrace();
			}
		});
	}
	
	/**
	 * <ul>
	 * <p>	<b><i>LockInterface</i></b>
	 * <p>	<code>public LockInterface()</code>
	 * <p>	Creates the <tt>LockInterface</tt>.
	 * </ul>
	 */
	public LockInterface() {
		super("LockInterface");
		
		//	Setup the interface.
		setupInterface();
		
		// 	Run non-Swing/GUI code off of the EDT for good practice.
		CompletableFuture.runAsync(() -> {
			//	Instantiate core objects.
			fingerprintScanner = new FingerprintScanner();
			relayLock = new RelayLock();
			fileManager = new FileManager();
			setupRemoteClient();
			setupCountdownManager();
			
			//	Sync settings and fingerprint models across the system.
			syncSettings();
			syncModels();
		}).join();
		
		//	Enables manual input once the system is fully online.
		enableManualUnlocks();
		displayInterface();
	}
	
	/**
	 * <ul>
	 * <p>	<b><i>setupInterface</i></b>
	 * <p>	<code>private void setupInterface()</code>
	 * <p>	Sets up the <tt>LockInterface</tt>, including the <tt>JFrame</tt> and all of its contents.
	 * </ul>
	 */
	private void setupInterface() {
		// Register Font
		try {
			final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, LockInterface.class.getResourceAsStream("/club/minimunch57/fonts/Montserrat-Regular.ttf")));
			ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, LockInterface.class.getResourceAsStream("/club/minimunch57/fonts/Montserrat-Bold.ttf")));
			ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, LockInterface.class.getResourceAsStream("/club/minimunch57/fonts/Montserrat-Italic.ttf")));
		} catch (FontFormatException | IOException e) {
			System.out.println("<!> Error setting fonts. (FFE|IOE setInt)");
			e.printStackTrace();
		}
		
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setBounds(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
		setResizable(false);
		setUndecorated(true);
		setCursor(Toolkit.getDefaultToolkit().createCustomCursor(new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB), new Point(0, 0), "CustomBlank"));
		
		contentPane = new JLayeredPane();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(null);
		contentPane.setRequestFocusEnabled(false);
		contentPane.setOpaque(false);
		this.setContentPane(contentPane);
		
		settingsPane = new JPanel();
		settingsPane.setLayout(null);
		settingsPane.setRequestFocusEnabled(false);
		settingsPane.setOpaque(false);
//		settingsPane.setBackground(new Color(0, 0, 0, 0));
		settingsPane.setVisible(false);
		settingsPane.setBounds(SCREEN_WIDTH/2 - 325, SCREEN_HEIGHT/2 - 160, 650, 320);
		contentPane.add(settingsPane, Integer.valueOf(9));
		
		settingsFPPane = new JPanel();
		settingsFPPane.setLayout(null);
		settingsFPPane.setRequestFocusEnabled(false);
		settingsFPPane.setOpaque(false);
//		settingsFPPane.setBackground(new Color(0, 0, 0, 0));
		settingsFPPane.setVisible(false);
		settingsFPPane.setBounds(SCREEN_WIDTH/2 - 325, SCREEN_HEIGHT/2 - 160, 650, 320);
		contentPane.add(settingsFPPane, Integer.valueOf(8));
		
		lblBackgroundImage = new JLabel("");
		lblBackgroundImage.setRequestFocusEnabled(false);
		lblBackgroundImage.setFocusable(false);
		lblBackgroundImage.setOpaque(true);
		lblBackgroundImage.setBounds(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
		contentPane.add(lblBackgroundImage, Integer.valueOf(1));
		
		shadowPane = new JPanel() {
			/** This <tt>JPanel</tt>'s unique serial. */
			private static final long serialVersionUID = 4227085301650309578L;
			/** The <tt>Color</tt> used for painting a partially transparent background. */
			private final Color alphaColor = new Color(0, 0, 0, 100);

			@Override
			protected void paintComponent(Graphics g) {
				//	Create Graphics
				Graphics g2d = (Graphics2D) g.create();
				
				//	Draw the Shadow Layer
				g2d.setColor(alphaColor);
				g2d.fillRect(0, 0, this.getWidth(), this.getHeight());
				
				//	Dispose of the Graphics2D Object
				g2d.dispose();
				
				super.paintComponent(g);
			}
		};
		shadowPane.setOpaque(false);
//		shadowPane.setBackground(new Color(0, 0, 0, 100));
		shadowPane.setVisible(false);
		shadowPane.setBounds(this.getBounds());
		shadowPane.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent me) {
				// Should be on the EDT, run async as starting point.
				CompletableFuture.runAsync(() -> {
					if(!popupPane.isHoldingFocus()) {
						popupPane.closePopup();
					}
				});
			}
		});
		contentPane.add(shadowPane, Integer.valueOf(10));
		
		popupPane = new NotificationPopup(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT), "", 0, false);
		popupPane.addMouseListener(new MouseAdapter() {});
		popupPane.setVisibleListener(new VisibleListener() {
			@Override
			public void isNowShown() {
				Helper.runSafelyAndWait(() -> {
					shadowPane.setVisible(true);
				});
			}

			@Override
			public void isNowHidden() {
				fingerprintScanner.cancelFingerprintScan();
				Helper.runSafelyAndWait(() -> {
					shadowPane.setVisible(false);
				});
			}
		});
		contentPane.add(popupPane, Integer.valueOf(11));
		
		btnSettings = new BeautifulButton("");
		btnSettings.setBackgroundColors(new Color(44, 62, 80), Color.BLACK);
		btnSettings.setOpaque(false);
		btnSettings.setBorderPainted(false);
		btnSettings.setContentAreaFilled(false);
		btnSettings.setFocusPainted(false);
		btnSettings.setRequestFocusEnabled(true);
		btnSettings.setIcon(iconGear);
		btnSettings.setBounds(SCREEN_WIDTH - iconGear.getIconWidth() - 30, 5, iconGear.getIconWidth() + 25, iconGear.getIconHeight() + 25);
		btnSettings.addActionListener((actionEvent) -> {
			// Should be on the EDT, run async.
			CompletableFuture.runAsync(() -> {
				//	Notification
				popupPane.reset("Place finger on fingerprint scanner...", 0, false);
				popupPane.setHoldsFocus(false);
				popupPane.displayPopup();
				
				//	Grant access if there are no registrations or an administrator's fingerprint was successfully scanned.
				if(fingerprintScanner.getFingerprintCount() == 0 || fingerprintScanner.scanFingerprintAdmin(popupPane)) {
					popupPane.closePopup();
					setUIScreen(Screen.SETTINGS);
				}
				else if(!fingerprintScanner.checkForScanCancellation()){
					popupPane.reset("Access denied.\nAdministrator access only.", 1500);
				}
			});
		});
		contentPane.add(btnSettings, Integer.valueOf(5));
		
		btnSettingsFP = new BeautifulButton("Fingerprint Settings");
		btnSettingsFP.setBackgroundColors(new Color(0, 146, 237), new Color(0, 58, 233));
		btnSettingsFP.setForeground(Color.WHITE);
		btnSettingsFP.setFont(new Font("Montserrat", Font.PLAIN, 22));
		btnSettingsFP.setIcon(iconFingerprint);
		btnSettingsFP.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnSettingsFP.setHorizontalTextPosition(SwingConstants.CENTER);
		btnSettingsFP.setIconTextGap(20);
		btnSettingsFP.setFocusPainted(false);
		btnSettingsFP.setRequestFocusEnabled(false);
		btnSettingsFP.setBounds(0, 0, 300, 320);
		btnSettingsFP.addActionListener((actionEvent) -> {
			setUIScreen(Screen.SETTINGS_FP);
		});
		settingsPane.add(btnSettingsFP);
		
		btnManualUnlocks = new BeautifulButton("Disable Manual Unlocks");
		btnManualUnlocks.setBackgroundColors(new Color(255, 32, 41), new Color(163, 0, 4));
		btnManualUnlocks.setForeground(Color.WHITE);
		btnManualUnlocks.setFont(new Font("Montserrat", Font.PLAIN, 22));
		btnManualUnlocks.setIcon(iconGearBig);
		btnManualUnlocks.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnManualUnlocks.setHorizontalTextPosition(SwingConstants.CENTER);
		btnManualUnlocks.setIconTextGap(10);
		btnManualUnlocks.setFocusPainted(false);
		btnManualUnlocks.setRequestFocusEnabled(false);
		btnManualUnlocks.setBounds(settingsPane.getWidth()/2 + 25, 0, 300, 150);
		btnManualUnlocks.addActionListener((actionEvent) -> {
			// Should be on the EDT, run async.
			CompletableFuture.runAsync(() -> {
				//	Enable or disable manual unlocks based on current state.
				if(manualUnlocksEnabled) {
					disableManualUnlocks(false);
				}
				else {
					countdownManager.stopCountdown(CountdownType.FAILED_UNLOCKS);
					countdownManager.stopCountdown(CountdownType.MANUAL_UNLOCKS);
					enableManualUnlocks();
				}
			});
		});
		settingsPane.add(btnManualUnlocks);
		
		btnExit = new BeautifulButton("Exit");
		btnExit.setBackgroundColors(new Color(245, 175, 25), new Color(241, 39, 17));
		btnExit.setForeground(Color.WHITE);
		btnExit.setFont(new Font("Montserrat", Font.PLAIN, 22));
		btnExit.setIcon(iconRemove);
		btnExit.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnExit.setHorizontalTextPosition(SwingConstants.CENTER);
		btnExit.setIconTextGap(10);
		btnExit.setFocusPainted(false);
		btnExit.setRequestFocusEnabled(false);
		btnExit.setBounds(settingsPane.getWidth()/2 + 25, 170, 300, 150);
		btnExit.addActionListener((actionEvent) -> {
			// Should be on the EDT, run async.
			CompletableFuture.runAsync(() -> {
				//	Prompt for confirmation then, if confirmed, exit.
				popupPane.reset("Are you sure you would like to exit?", 2, false);
				popupPane.setConfirmListener(new ConfirmListener() {
					@Override
					public void confirmationReceived(String input, boolean confirmed) {
						CompletableFuture.runAsync(() -> {
							if(confirmed) {
								Helper.runSafely(() -> btnExit.setEnabled(false));
								exitApplication();
							}
							popupPane.closePopup();
						});
					}
				});
				popupPane.displayPopup();
			});
		});
		settingsPane.add(btnExit);
		
		btnRegisterFP = new BeautifulButton("Register Fingerprint");
		btnRegisterFP.setBackgroundColors(new Color(0, 146, 237), new Color(0, 58, 233));
		btnRegisterFP.setForeground(Color.WHITE);
		btnRegisterFP.setFont(new Font("Montserrat", Font.BOLD, 22));
		btnRegisterFP.setIcon(iconFingerprint);
		btnRegisterFP.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnRegisterFP.setHorizontalTextPosition(SwingConstants.CENTER);
		btnRegisterFP.setIconTextGap(20);
		btnRegisterFP.setFocusPainted(false);
		btnRegisterFP.setRequestFocusEnabled(false);
		btnRegisterFP.setBounds(0, 0, 300, 320);
		btnRegisterFP.addActionListener((actionEvent) -> {
			// Should be on the EDT, run async.
			CompletableFuture.runAsync(() -> registerNewFingerprint());
		});
		settingsFPPane.add(btnRegisterFP);
		
		btnEditFP = new BeautifulButton("Edit");
		btnEditFP.setBackgroundColors(new Color(0, 237, 210), new Color(0, 174, 233));
		btnEditFP.setForeground(new Color(245, 250, 250));
		btnEditFP.setFont(new Font("Montserrat", Font.BOLD, 20));
		btnEditFP.setIcon(iconEdit);
		btnEditFP.setIconTextGap(10);
		btnEditFP.setRequestFocusEnabled(false);
		btnEditFP.setBounds(325, 187, 155, 133);
		btnEditFP.addActionListener((actionEvent) -> {
			// Should be on the EDT, run async.
			CompletableFuture.runAsync(() -> editSelectedFingerprint());
		});
		settingsFPPane.add(btnEditFP);
		
		btnRemoveFP = new BeautifulButton("Remove");
		btnRemoveFP.setBackgroundColors(new Color(245, 175, 25), new Color(241, 39, 17));
		btnRemoveFP.setForeground(Color.WHITE);
		btnRemoveFP.setFont(new Font("Montserrat", Font.BOLD, 20));
		btnRemoveFP.setIcon(iconRemove);
		btnRemoveFP.setIconTextGap(10);
		btnRemoveFP.setRequestFocusEnabled(false);
		btnRemoveFP.setBounds(495, 187, 155, 133);
		btnRemoveFP.addActionListener((actionEvent) -> {
			// Should be on the EDT, run async.
			CompletableFuture.runAsync(() -> removeSelectedFingerprint());
		});
		settingsFPPane.add(btnRemoveFP);

		listModel = new DefaultListModel<String>();
		fingerprintList = new JList<String>();
		fingerprintList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		fingerprintList.setModel(listModel);
		fingerprintList.setFont(new Font("Montserrat", Font.PLAIN, 18));
		fingerprintList.setOpaque(false);
		fingerprintList.setBorder(BorderFactory.createCompoundBorder(fingerprintList.getBorder(), BorderFactory.createEmptyBorder(10, 10, 10, 10)));
		fingerprintList.setBounds(325, 0, 325, 176);
		DragAdapter dragAdapter = new DragAdapter(fingerprintList);
        fingerprintList.addMouseListener(dragAdapter);
        fingerprintList.addMouseMotionListener(dragAdapter);
        
		JScrollPane scrollPane = new JScrollPane(fingerprintList) {
			/** This <tt>JScrollPane</tt>'s unique serial. */
			private static final long serialVersionUID = 8587192831246156905L;

			@Override
			public void paintComponent(Graphics g) {
				//	Create Graphics and Rendering Hints
				Graphics2D g2d = (Graphics2D) g.create();
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				
				//	Draw the Rounded Panel
				g2d.setColor(getBackground());
				g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
				
				//	Dispose of the Graphics2D Object
				g2d.dispose();
				super.paintComponent(g);
			}
		};
		scrollPane.setOpaque(false);
		scrollPane.getViewport().setOpaque(false);
		scrollPane.setBorder(null);
		scrollPane.setBackground(fingerprintList.getBackground());
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		scrollPane.setFocusable(false);
		scrollPane.setBounds(fingerprintList.getBounds());
		settingsFPPane.add(scrollPane);
		
		btnSettingsBack = new BeautifulButton("Back");
		btnSettingsBack.setBackgroundColors(new Color(124, 142, 160), new Color(40, 58, 76));
		btnSettingsBack.setForeground(Color.WHITE);
		btnSettingsBack.setFont(new Font("Montserrat", Font.BOLD, 14));
		btnSettingsBack.setFocusPainted(false);
		btnSettingsBack.setRequestFocusEnabled(false);
		btnSettingsBack.setVisible(false);
		btnSettingsBack.setBounds(SCREEN_WIDTH - 90, 10, 80, 50);
		btnSettingsBack.addActionListener((actionEvent) -> {
			if(currentScreen == Screen.SETTINGS) {
				setUIScreen(Screen.MAIN);
			}
			else if(currentScreen == Screen.SETTINGS_FP) {
				setUIScreen(Screen.SETTINGS);
			}
		});
		contentPane.add(btnSettingsBack, Integer.valueOf(4));
		
		lblActionStatus = new JLabel();
		lblActionStatus.setFont(new Font("Montserrat", Font.BOLD, 32));
		lblActionStatus.setFocusable(false);
		lblActionStatus.setHorizontalAlignment(SwingConstants.CENTER);
		lblActionStatus.setBounds(10, SCREEN_HEIGHT - 60, SCREEN_WIDTH - 20, 50);
		contentPane.add(lblActionStatus, Integer.valueOf(5));
		
		btnLock = new BeautifulButton("");
		btnLock.setOpaque(false);
		btnLock.setBorderPainted(false);
		btnLock.setContentAreaFilled(false);
		btnLock.setFocusPainted(false);
		btnLock.setRequestFocusEnabled(true);
		btnLock.setIcon(iconLockLocked);
		btnLock.setBounds(5, 5, iconLockLocked.getIconWidth() + 25, iconLockLocked.getIconHeight() + 25);
		btnLock.addActionListener((actionEvent) -> {
			// Should be on the EDT, run async.
			CompletableFuture.runAsync(() -> lockUnlockSystem());
		});
		btnLock.setEnabled(false);
		contentPane.add(btnLock, Integer.valueOf(5));
		
		btnOpen = new JButton("");
		btnOpen.setBorderPainted(false);
		btnOpen.setContentAreaFilled(false);
		btnOpen.setFocusable(false);
		btnOpen.setFocusPainted(false);
		btnOpen.setBounds(SCREEN_WIDTH/2-iconOpenLocked.getIconWidth()/2, SCREEN_HEIGHT/2-iconOpenLocked.getIconHeight()/2, iconOpenLocked.getIconWidth(), iconOpenLocked.getIconHeight());
		btnOpen.addActionListener((actionEvent) -> {
			CompletableFuture.runAsync(() -> {
				if(!doorOpenInProgress) {
					openDoor();
				}
			});
		});
		btnOpen.setEnabled(false);
		contentPane.add(btnOpen, Integer.valueOf(5));
		
		contentPane.moveToFront(shadowPane);
		contentPane.moveToFront(popupPane);
		setUILockState(systemLocked);
		UIManager.put("Button.select", new Color(0, 0, 0, 0));
	}
	
	/**
	 * <ul>
	 * <p>	<b><i>displayInterface</i></b>
	 * <p>	<code>private void displayInterface()</code>
	 * <p>	Displays the <tt>LockInterface</tt>, including the <tt>JFrame</tt> and all of its contents.
	 * </ul>
	 */
	private void displayInterface() {
		//	Display the LockInterface for Usage After Initialization.
		this.setVisible(true);
	}
	
	/**
	 * <ul>
	 * <p>	<b><i>setupRemoteClient</i></b>
	 * <p>	<code>private void setupRemoteClient()</code>
	 * <p>	Sets up the <tt>RemoteClient</tt> on a separate <tt>Thread</tt>.
	 * </ul>
	 */
	private void setupRemoteClient() {
		//	Setup remote client on separate thread.
		CompletableFuture.runAsync(() -> {
			//	Create Instance and Connect
			remoteClient = new RemoteClient();
			
			//	Setup Listener
			remoteClient.setMessageListener(new MessageListener() {
				@Override
				public void messageReceived(String message) {
					System.out.println("[INCOMING MESSAGE] --> " + message);
				}
				
				@Override
				public void broadcastReceived(String broadcastMessage) {
					System.out.println("[INCOMING BROADCAST] --> " + broadcastMessage);
				}
				
				@Override
				public void commandReceived(RemoteCMD command, String[] args) {
					switch(command) {
					case LOCK:
						//	Cancel any active open door cycles and lock the door.
						countdownManager.stopCountdown(CountdownType.OPEN_CYCLE);
						lockDoor();
						break;
					case UNLOCK:
						//	Save the timer's previous initial delay.
						final int previousDelay = openCycleLengthMS;
						//	The amount of time the door should remain unlocked (open).
						int openLength = 3;
						
						//	Change the amount of seconds the door is open for if it was passed with the command.
						if(args != null && args.length > 0) {
							try {
								openLength = Integer.parseInt(args[0]);
								if(openLength>0 && openLength<10) {
									openCycleLengthMS = openLength*1000;
								}
							} catch(NumberFormatException nfe) {
								//	Do nothing. The default open cycle length will be used.
							}
						}
						
						//	Open the door and set it back to its previous initial delay.
						doorOpenInProgress = true;
						unlockDoor(openLength, false);
						openCycleLengthMS = previousDelay;
						break;
					case SYSTEM_LOCK:
						if(!systemLocked) {
							lockSystem();
						}
						break;
					case SYSTEM_UNLOCK:
						if(systemLocked) {
							unlockSystem();
						}
						break;
					case MANUALUNLOCKS_DISABLE:
						if(manualUnlocksEnabled) disableManualUnlocks(false);
						break;
					case MANUALUNLOCKS_ENABLE:
						if(!manualUnlocksEnabled) enableManualUnlocks();
						break;
					case UNRECOGNIZED:
						//	Do Nothing.
						break;
					}
				}
				
				@Override
				public void connectionMessageReceived(String connectionMessage) {
					//	Do nothing for now.
				}
				
				@Override
				public void responseMessageReceived(String responseMessage) {
					//	Do nothing for now.
				}
			});
		});
	}
	
	/**
	 * <ul>
	 * <p>	<b><i>setupCountdownManager</i></b>
	 * <p>	<code>private void setupCountdownManager()</code>
	 * <p>	Sets up the <tt>CountdownManager</tt> used for managing countdowns.
	 * </ul>
	 */
	private void setupCountdownManager() {
		countdownManager = new CountdownManager() {
			@Override
			public void textUpdated(String text, CountdownType type) {
				if(text == null) {
					setActionStatusText(currentDefaultText);
				}
				else {
					setActionStatusText(text);
				}
			}
			
			@Override
			public void countdownDone(CountdownType type) {
				switch(type) {
					case STANDARD:
						break;
					case OPEN_CYCLE:
						//	Open/Close Cycle Countdown should be over, meaning the door should be closed/locked to fully complete the cycle.
						lockDoor();
						break;
					case MANUAL_UNLOCKS:
						//	Completion of a Manual Unlocks Countdown indicates that manual unlocks should be enabled again.
						enableManualUnlocks();
						break;
					case FAILED_UNLOCKS:
						//	Completion of this Countdown should mean that enough time has passed since the last failed unlock attempt to reset the count.
						consecutiveFailedUnlocks = 0;
						break;
					case BACKGROUND:
						//	Ignore for now. These countdowns run in the background and have no special text or function by default.
						break;
				}
			}
		};
	}
	
	/**
	 * <ul>
	 * <p>	<b><i>syncSettings</i></b>
	 * <p>	<code>private void syncSettings()</i></b>
	 * <p>	Syncs the information stored in the properties file with information presented on the user interface.
	 * </ul>
	 */
	private void syncSettings() {
		//	Adds Saved Fingerprints and Their Respective Nicknames to the ListModel used in the JList.
//		final String[] keys = fileManager.getPropertyKeys();
		refreshFingerprintList();
		
		//	Syncs the List of Current Administrators with the Fingerprint Scanner's.
		final String[] adminKeys = Arrays.stream(fileManager.getPropertyKeys()).filter(i -> i.startsWith("Admin")).sorted().toArray(String[]::new);
		for(int i = 0; i<adminKeys.length; i++) {
			final int fingerID = Integer.parseInt(adminKeys[i].replaceAll("Admin", ""));
			if(Boolean.parseBoolean(fileManager.getProperty(adminKeys[i]))) {
				fingerprintScanner.addFingerprintAdmin(fingerID);
			}
		}
	}
	
	/**
	 * <ul>
	 * <p>	<b><i>syncModels</i></b>
	 * <p>	<code>private void syncModels()</code>
	 * <p>	Syncs the fingerprint models and ensures that they are consistent across hardware.
	 * <p>	In order to sync the locally stored fingerprint models with the fingerprint scanner's, the scanner's models are cleared first.
	 * 		The <tt>FileManager</tt> then retrieves all of the stored fingerprint models and uploads them to the fingerprint scanner.
	 * </ul>
	 */
	private void syncModels() {
		fingerprintScanner.uploadFingerprintModels(fileManager.retrieveFingerprintModels(), true);
	}
	
	/**
	 * <ul>
	 * <p>	<b><i>createListString</i></b>
	 * <p>	<code>private String createListString(int fingerID)</code>
	 * <p>	Creates a <tt>String</tt> containing the appropriate text information about the specified fingerprint in the settings.
	 * <p>	This a helper method to simplify the repetitive process of concatenating multiple <tt>String</tt>s together for setting lines in the <tt>DefaultListModel</tt>.
	 * 		It creates a line of text with information about the fingerprint under the passed ID. 
	 * @param fingerID - an <code>int</code> for the ID of the fingerprint to use for the <tt>String</tt> creation.
	 * @return a <tt>String</tt> with the fingerprint information to be displayed in the fingerprint settings.
	 * </ul>
	 */
	private String createListString(int fingerID) {
		final String nickname = fileManager.getProperty("Fingerprint" + fingerID);
		String result = "FP #" + fingerID;
		if(Boolean.parseBoolean(fileManager.getProperty("Admin" + fingerID))) {
			result += " (A)";
		}
		else {
			result += " (EU: " + fileManager.getProperty("EmergencyUses" + fingerID) + ")";
		}
		
		if(nickname.trim().length()>=1) {
			result += " - " + fileManager.getProperty("Fingerprint" + fingerID);
		}
		
		return result;
	}
	
	/**
	 * <ul>
	 * <p>	<b><i>openDoor</i></b>
	 * <p>	<code>private void openDoor()</code>
	 * <p>	Opens the door based on the system's current lock state and the authorization of the attempting user if applicable.
	 * <p>	Note: This method is intended to be used by the designated Open button.
	 * </ul>
	 */
	private void openDoor() {
		//	Updates the boolean variable to be consistent with door opening progress.
		doorOpenInProgress = true;
		
		//	System is locked, but manual unlocks are enabled. Allow if Administrator or authorized Emergency Bypass.
		if(systemLocked && manualUnlocksEnabled) {
			//	Scan for a fingerprint. If it is authorized, unlock the door. Otherwise, notify and add to the counter.
			popupPane.reset("Place finger on fingerprint scanner...", 0, false);
			popupPane.setHoldsFocus(true);
			popupPane.displayPopup();
			int scanResultID = fingerprintScanner.scanFingerprint(popupPane);
			
			//	If fingerprint is recognized.
			if(scanResultID >= 0) {
				popupPane.closePopup();
				final int fingerID = scanResultID;
				//	The fingerprint is linked to an Administrator.
				if(fingerprintScanner.isFingerprintAdmin(fingerID)) {
					unlockDoor(true);
				}
				//	The fingerprint is not linked to an Administrator.
				else {
					//	The fingerprint has emergency uses left.
					if(Integer.parseInt(fileManager.getProperty("EmergencyUses" + fingerID)) > 0) {
						//	Check if the user would like to use an Emergency Use.
						popupPane.reset("Use your Emergency Use?\nThe system administrator will be notified!", 2, false);
						popupPane.setConfirmListener(new ConfirmListener() {
							@Override
							public void confirmationReceived(String input, boolean confirmed) {
								CompletableFuture.runAsync(() -> {
									if(confirmed) {
										unlockDoor(true);
										
										fileManager.setProperty("EmergencyUses" + fingerID, "0");
										syncSettings();
									}
									doorOpenInProgress = false;
									popupPane.closePopup();
								});
							}
						});
						popupPane.displayPopup();
					}
					//	The fingerprint has no emergency uses left.
					else {
						popupPane.reset("Access denied.\nYou are out of Emergency Uses!", 2000);
						doorOpenInProgress = false;
					}
				}
			}
			//	Fingerprint is not recognized.
			else if(scanResultID == -1) {
				consecutiveFailedUnlocks++;
				
				if(consecutiveFailedUnlocks>=5) {
					disableManualUnlocks(true);
				}
				
				popupPane.reset("Access denied.\nFingerprint not recognized.", 2000);
				doorOpenInProgress = false;
			}
			fingerprintScanner.checkForScanCancellation();
		}
		//	System is unlocked, so any open button press while manual unlocks are enabled starts the open cycle.
		else if(!systemLocked) {
			unlockDoor(true);
		}
	}
	
	/**
	 * <ul>
	 * <p>	<b><i>lockDoor</i></b>
	 * <p>	<code>private void lockDoor()</code>
	 * <p>	Locks the door.
	 * </ul>
	 */
	private void lockDoor() {
		System.out.println("# Locking the door...");
		
		//	Sends the deactivation signal to the relay, locking the door.
		relayLock.deactivateRelay();
		
		//	Updates the action status text.
		countdownManager.startCountdown("Locked...", -1, CountdownType.STANDARD);		
		
		//	Finally, updates the boolean variable to be consistent with door opening progress.
		doorOpenInProgress = false;
	}
	
	/**
	 * <ul>
	 * <p>	<b><i>unlockDoor</i></b>
	 * <p>	<code>private void unlockDoor(int s, boolean manual)</code>
	 * <p>	Unlocks the door for a specified number of seconds.
	 * <p>	Note: The passed int will be limited to the range 1-9. Passing an int outside this range means picking the valid min or max value respectively.
	 * @param s - An <code>int</code> for the number of seconds that the door should be unlocked for.
	 * @param manual - a <code>boolean</code> for whether or not the unlock was performed manually as opposed to being executed remotely.
	 * </ul>
	 */
	private void unlockDoor(int s, boolean manual) {
		System.out.println("# Unlocking the door...");
		
		//	Sends the activation signal to the relay, unlocking the door.
		relayLock.activateRelay();
		
		//	Starts the cycle countdown, updating the action status text over time.
		countdownManager.startCountdown("Unlocked for ", Math.max(1, Math.min(s, 9)), CountdownType.OPEN_CYCLE);
		
		//	Finally, resets the failed unlocks counter if the unlock was performed manually, not remotely.
		if(manual) consecutiveFailedUnlocks = 0;
	}
	
	/**
	 * <ul>
	 * <p>	<b><i>unlockDoor</i></b>
	 * <p>	<code>private void unlockDoor(boolean manual)</code>
	 * <p>	Unlocks the door for a default of three seconds.
	 * @param manual - a <code>boolean</code> for whether or not the unlock was performed manually as opposed to being executed remotely.
	 * </ul>
	 */
	private void unlockDoor(boolean manual) {
		unlockDoor(3, manual);
	}
	

	/**
	 * <ul>
	 * <p>	<b><i>lockUnlockSystem</i></b>
	 * <p>	<code>private void lockUnlockSystem()</code>
	 * <p>	Locks or unlocks the system based on its current state and the authorization of the attempting user.
	 * <p>	Note: This method is intended to be used by the designated Lock/Unlock button.
	 * </ul>
	 */
	private void lockUnlockSystem() {
		if(manualUnlocksEnabled) {
			//	Scan for a fingerprint. If it is authorized, unlock the door. Otherwise, notify and add to the counter.
			popupPane.reset("Place finger on fingerprint scanner...", 0, false);
			popupPane.setHoldsFocus(false);
			popupPane.displayPopup();
			final int scanResultID = fingerprintScanner.scanFingerprint(popupPane);
			if(scanResultID >= 0) {
				popupPane.closePopup();
				final int fingerID = scanResultID;
				//	The fingerprint is linked to an Administrator.
				if(fingerprintScanner.isFingerprintAdmin(fingerID)) {
					//	Lock/Unlock the system.
					if(systemLocked) {
						unlockSystem();
					}
					else {
						lockSystem();
					}
				}
				//	The fingerprint is not linked to an Administrator.
				else {
					popupPane.reset("Access denied.\nYou are not authorized.", 2000);
				}
			}
			//	Fingerprint is not recognized.
			else if(scanResultID == -1) {
				consecutiveFailedUnlocks++;
				
				if(consecutiveFailedUnlocks>=5) {
					disableManualUnlocks(true);
				}
				
				popupPane.reset("Access denied.\nFingerprint not recognized.", 2000);
			}
			fingerprintScanner.checkForScanCancellation();
		}
	}
	
	/**
	 * <ul>
	 * <p>	<b><i>lockSystem</i></b>
	 * <p>	<code>private void lockSystem()</code>
	 * <p>	Locks the system.
	 * <p>	When the system is locked, only administrators and authorized emergency bypass users may open the door.
	 * </ul>
	 */
	private void lockSystem() {
		System.out.println("# Locking the system...");
		
		//	Updates the system locked status.
		systemLocked = true;
		
		//	Updates GUI elements to match the current lock state.
		setUILockState(systemLocked);
	}
	
	/**
	 * <ul>
	 * <p>	<b><i>unlockSystem</i></b>
	 * <p>	<code>private void unlockSystem()</code>
	 * <p>	Unlocks the system.
	 * <p>	When the system is unlocked, opening the door does not require authorization.
	 * </ul>
	 */
	private void unlockSystem() {
		System.out.println("# Unlocking the system...");
		
		//	Updates the system locked status.
		systemLocked = false;
		
		//	Updates GUI elements to match the current lock state.
		setUILockState(systemLocked);
	}
	
	/**
	 * <ul>
	 * <p>	<b><i>setUIScreen</i></b>
	 * <p>	<code>private void setUIScreen(Screen screen)</code>
	 * <p>	Sets the user interface screen based on the passed value.
	 * <p>	The values are accessible by the enum <tt>Screen</tt>.
	 * @param screen - a <code>Screen</code> enum value representing the screen for the UI to display.
	 * </ul>
	 */
	private void setUIScreen(Screen screen) {
		Helper.runSafely(() -> {
			switch(screen) {
				case MAIN: {
					if(systemLocked) {
						lblBackgroundImage.setIcon(imageBackgroundLocked);
					}
					else {
						lblBackgroundImage.setIcon(imageBackgroundUnlocked);
					}
					btnOpen.setVisible(true);
					btnLock.setVisible(true);
					btnSettings.setVisible(true);
					lblActionStatus.setVisible(true);
					btnSettingsBack.setVisible(false);
					settingsPane.setVisible(false);
					break;
				}
				case SETTINGS: {
					CompletableFuture.runAsync(() -> {
						if(currentScreen == Screen.MAIN) refreshFingerprintList();
					}).join();
					lblBackgroundImage.setIcon(imageBackgroundSettings);
					btnOpen.setVisible(false);
					btnLock.setVisible(false);
					btnSettings.setVisible(false);
					lblActionStatus.setVisible(false);
					btnSettingsBack.setVisible(true);
					settingsPane.setVisible(true);
					settingsFPPane.setVisible(false);
					break;
				}
				case SETTINGS_FP: {
					settingsPane.setVisible(false);
					settingsFPPane.setVisible(true);
					break;
				}
			}
			currentScreen = screen;
		});
	}
	
	/**
	 * <ul>
	 * <p>	<b><i>setUILockState</i></b>
	 * <p>	<code>private void setUILockState(boolean isLocked)</code>
	 * <p>	Sets the user interface's lock state based on the passed <code>boolean</code>.
	 * <p>	The lock state simply refers to whether or not the system is locked.
	 * 		When the system is locked, only administrators and authorized emergency bypass users may open the door.
	 * 		When the system is unlocked, opening the door does not require authorization.
	 * 		If the passed <code>boolean</code> is <code>true</code> (the system is locked), then the icons and background will be set to their locked variants.
	 * 		Similarly, if the <code>boolean</code> is <code>false</code>, then the icons and background will be set to their unlocked variants.
	 * @param isLocked - a <code>boolean</code> for whether or not the system is locked.
	 * </ul>
	 */
	private void setUILockState(boolean isLocked) {
		Helper.runSafely(() -> {
			if(isLocked) {
				lblBackgroundImage.setIcon(imageBackgroundLocked);
				btnLock.setBackgroundColors(colorLocked1, colorLocked2);
				btnLock.setIcon(iconLockLocked);
				btnOpen.setIcon(iconOpenLocked);
				btnOpen.setPressedIcon(iconOpenLockedP);
			}
			else {
				lblBackgroundImage.setIcon(imageBackgroundUnlocked);
				btnLock.setBackgroundColors(colorUnlocked1, colorUnlocked2);
				btnLock.setIcon(iconLockUnlocked);
				btnOpen.setIcon(iconOpenUnlocked);
				btnOpen.setPressedIcon(iconOpenUnlockedP);
			}
		});
	}
	
	/**
	 * <ul>
	 * <p>	<b><i>setActionStatusText</i></b>
	 * <p>	<code>private void setActionStatusText(String text)</code>
	 * <p>	Sets the action status label's text on on the proper <tt>Thread</tt>.
	 * @param text - a <tt>String</tt> containing the new action status label text.
	 * </ul>
	 */
	private void setActionStatusText(String text) {
		//	Check if there would be ANY change to the action status label by setting the text.
		//	This check should help prevent queueing tons of updates that change nothing.
		if(!lblActionStatus.getText().equals(text)) {
			Helper.runSafely(() -> {
				lblActionStatus.setText(text);
			});
		}
	}
	
	/**
	 * <ul>
	 * <p>	<b><i>refreshFingerprintList</i></b>
	 * <p>	<code>private void refreshFingerprintList(boolean soft)</code>
	 * <p>	Refreshes and updates the fingerprint list shown in the fingerprint settings.
	 * 		This method allows for a soft refresh if <code>true</code> is passed.
	 * 		A soft refresh will only make changes to the fingerprint list if there are more or less IDs than before.
	 * 		Changes to a fingerprint's attributes will not be picked up by a soft refresh, and nothing will be changed.
	 * <p>	For example: A seventh Fingerprint is added. A soft refresh would notice a change in fingerprint count and re-do the list.
	 * 		In most cases, a soft refresh will not be enough and a hard refresh is required.
	 *		<b>If you would like to ensure the fingerprint list is fully up-to-date, pass <code>false</code>.</b>
	 * @param soft - <code>true</code> for a soft reset; <code>false</code> otherwise.
	 * </ul>
	 */
	private void refreshFingerprintList(boolean soft) {
		final String[] nameKeys = Arrays.stream(fileManager.getPropertyKeys()).filter(i -> i.startsWith("Fingerprint")).sorted().toArray(String[]::new);
		System.out.println(nameKeys.length + " EX: " + nameKeys[0] + " " + nameKeys[1]);
		System.out.println(listModel.size());
		if(!soft || listModel.size() != nameKeys.length) {
			Helper.runSafely(() -> listModel.clear());
			for(int i = 0; i<nameKeys.length; i++) {
				final int fingerID = Integer.parseInt(nameKeys[i].replaceAll("Fingerprint", ""));
				final String fingerText = createListString(fingerID);
				Helper.runSafely(() -> listModel.add(listModel.getSize(), fingerText));
			}
		}
	}
	
	/**
	 * <ul>
	 * <p>	<b><i>refreshFingerprintList</i></b>
	 * <p>	<code>private void refreshFingerprintList()</code>
	 * <p>	Refreshes and updates the fingerprint list shown in the fingerprint settings.
	 * 		This is a shorthand call to <code>refreshFingerprintList(boolean)</code>, defaulting to <code>false</code>.
	 * </ul>
	 */
	private void refreshFingerprintList() {
		refreshFingerprintList(false);
	}
	
	/**
	 * <ul>
	 * <p>	<b><i>registerNewFingerprint</i></b>
	 * <p>	<code>public void registerNewFingerpint()</code>
	 * <p>	Activates the fingerprint registration process.
	 * <p>	First, the fingerprint scanner attempts to register a new fingerprint.
	 * 		If the fingerprint already exists or the scanner is not able to scan the finger, then registration is cancelled.
	 * 		Otherwise, the fingerprint model is registered and stored on the scanner's internal memory.
	 * 		More importantly, the model is returned to this method and sent to the <tt>FileManager</tt> to save it to its own file.
	 * 		Finally, the <tt>LockInterface</tt> is updated with the new fingerprint.
	 * </ul>
	 */
	private void registerNewFingerprint() {
		//	Display Notification.
		popupPane.reset("Place finger on fingerprint scanner...", 0, false);
		popupPane.setHoldsFocus(true);
		popupPane.displayPopup();
		
		//	Registration -- Remember to run this asynchronously to avoid hanging parts of the application during the process.
		final Map.Entry<Integer, byte[]> model = fingerprintScanner.registerFingerprint(popupPane);
		if(model!=null) {
			fileManager.storeFingerprintModel(model);
			syncSettings();
			popupPane.reset("Fingerprint registered successfully under ID #" + fingerprintScanner.getLastRegistrantID() + "!", 2000);
		}
		else if(!fingerprintScanner.checkForScanCancellation()) {
			popupPane.reset("Fingerprint registration failed.\n\nThe finger may already be registered.\nMake sure you use the same finger for both scans!", 3000);
		}
	}
	
	/**
	 * <ul>
	 * <p>	<b><i>editSelectedFingerprint</i></b>
	 * <p>	<code>private void editSelectedFingerprint()</code>
	 * <p>	Goes through the edit process using the fingerprint that is currently selected in the <tt>JList</tt>.
	 * 		This method provides the user with multiple options in order to edit the selected fingerprint.
	 * <p>	Note: This method is intended to be used by the designated Edit Fingerprint button.
	 * </ul>
	 */
	private void editSelectedFingerprint() {
		//	Shows the fingerprint edit screen to edit administrator permissions, name, and emergency uses.
		if(!fingerprintList.isSelectionEmpty()) {
			final int fingerID = Integer.parseInt(fingerprintList.getSelectedValue().substring(4, fingerprintList.getSelectedValue().indexOf(" ", 3)));
			int buttons = 3;
			if(Boolean.parseBoolean(fileManager.getProperty("Admin" + fingerID))) {
				buttons = 2;
			}
			final int buttonsNeeded = buttons;
			
			popupPane.reset("Edit '" + fingerprintList.getSelectedValue() + "'", buttonsNeeded, false);
			popupPane.setHoldsFocus(false);
			Helper.runSafelyAndWait(() -> {
				final BeautifulButton adminButton = popupPane.getButton(0);
				adminButton.setText("Admin");
				adminButton.setBackgroundColors(BColors.BEAUTIFUL_BLUE, BColors.BEAUTIFUL_DARKBLUE);
				adminButton.removeActionListener(adminButton.getActionListeners()[0]);
				adminButton.addActionListener((actionEvent) -> {
					CompletableFuture.runAsync(() -> {
						popupPane.reset("Set Fingerprint #" + fingerID + " as an Administrator?", 2, false);
						popupPane.setConfirmListener(new ConfirmListener() {
							@Override
							public void confirmationReceived(String input, boolean confirmation) {
								CompletableFuture.runAsync(() -> {
									if(confirmation) {
										fileManager.setProperty("Admin" + fingerID, "true");
										final String fingerText = createListString(fingerID);
										Helper.runSafely(() -> listModel.set(fingerprintList.getSelectedIndex(), fingerText));
										fingerprintScanner.addFingerprintAdmin(fingerID);
									}
									else {
										fileManager.setProperty("Admin" + fingerID, "false");
										final String fingerText = createListString(fingerID);
										Helper.runSafely(() -> listModel.set(fingerprintList.getSelectedIndex(), fingerText));
										fingerprintScanner.removeFingerprintAdmin(fingerID);
									}
									fileManager.writeSettings();
									popupPane.closePopup();
								});
							}
						});
						popupPane.displayPopup();
					});
				});
				final BeautifulButton nameButton = popupPane.getButton(1);
				nameButton.setText("Name");
				nameButton.setBackgroundColors(BColors.BEAUTIFUL_SEAGREEN, BColors.BEAUTIFUL_SEABLUE);
				nameButton.removeActionListener(nameButton.getActionListeners()[0]);
				nameButton.addActionListener((actionEvent) -> {
					CompletableFuture.runAsync(() -> {
						popupPane.reset("Set nickname for Fingerprint #" + fingerID + ":", 2, true);
						popupPane.setConfirmListener(new ConfirmListener() {
							@Override
							public void confirmationReceived(String input, boolean confirmation) {
								if(confirmation) {
									fileManager.setProperty("Fingerprint" + fingerID, input.trim());
									final String fingerText = createListString(fingerID);
									Helper.runSafely(() -> listModel.set(fingerprintList.getSelectedIndex(), fingerText));
									fileManager.writeSettings();
								}
								popupPane.closePopup();
							}
						});
						popupPane.displayPopup();
					});
				});
				if(buttonsNeeded>2) {
					final BeautifulButton bypassButton = popupPane.getButton(2);
					bypassButton.setText("EUs");
					bypassButton.setBackgroundColors(BColors.BEAUTIFUL_ORANGE, BColors.BEAUTIFUL_RED);
					bypassButton.removeActionListener(bypassButton.getActionListeners()[0]);
					bypassButton.addActionListener((actionEvent) -> {
						CompletableFuture.runAsync(() -> {
							popupPane.reset("Reset Emergency Uses for Fingerprint #" + fingerID + "?", 2, false);
							popupPane.setConfirmListener(new ConfirmListener() {
								@Override
								public void confirmationReceived(String input, boolean confirmation) {
									if(confirmation) {
										fileManager.setProperty("EmergencyUses" + fingerID, "1");
										final String fingerText = createListString(fingerID);
										Helper.runSafely(() -> listModel.set(fingerprintList.getSelectedIndex(), fingerText));
										fileManager.writeSettings();
									}
									popupPane.closePopup();
								}
							});
							popupPane.displayPopup();
						});
					});
				}
			});
			popupPane.displayPopup();
		}
	}
	
	/**
	 * <ul>
	 * <p>	<b><i>removeSelectedFingerprint</i></b>
	 * <p>	<code>private void removeSelectedFingerprint()</code>
	 * <p>	Goes through the removal process using the fingerprint that is currently selected in the <tt>JList</tt>.
	 * 		This method does not remove the fingerprint without first prompting the user to confirm the removal.
	 * 		Additionally, the removal process will fail if the selected fingerprint is registered as an Administrator.
	 * <p>	Note: This method is intended to be used by the designated Remove Fingerprint button.
	 * </ul>
	 */
	private void removeSelectedFingerprint() {
		//	Shows confirmation then, if confirmed, removes the fingerprint from the system.
		if(!fingerprintList.isSelectionEmpty()) {
			final int fingerID = Integer.parseInt(fingerprintList.getSelectedValue().substring(4, fingerprintList.getSelectedValue().indexOf(" ", 3)));
			if(fingerprintScanner.isFingerprintAdmin(fingerID)) {
				popupPane.reset("Sorry, you cannot remove Administrator fingerprints.\nPlease demote the fingerprint first in order to remove it.", 3000);
			}
			else {
				final String displayText = "Do you really want to remove the following fingerprint?\n\n" + "Fingerprint #" + fingerID + " - \"" + fileManager.getProperty("Fingerprint" + fingerID) + "\"";
				popupPane.reset(displayText, 2, false);
				popupPane.setConfirmListener(new ConfirmListener() {
					@Override
					public void confirmationReceived(String input, boolean confirmation) {
						//	Run removal code on a separate Thread.
						CompletableFuture.runAsync(() -> {
							if(confirmation) {
								// Remove FP from Swing listModel component, then delete model from local list and reset scanner's internal memory.
								Helper.runSafely(() -> {
									listModel.remove(fingerprintList.getSelectedIndex());
								});
								fileManager.deleteFingerprintModel(fingerID);
								
								//	Remove model from scanner's internal memory.
								fingerprintScanner.uploadFingerprintModels(fileManager.retrieveFingerprintModels(), true);
							}
							popupPane.closePopup();
						});
					}
				});
				popupPane.displayPopup();
			}
		}
	}
	
	/**
	 * <ul>
	 * <p>	<b><i>enableManualUnlocks</i></b>
	 * <p>	<code>public void enableManualUnlocks()</code>
	 * <p>	Enables manual unlocks using the fingerprint sensor.
	 * 		Manual unlocks are usually disabled after there are a certain amount of failed unlock attempts using the fingerprint scanner.
	 * <p>	<i><b>Note:</b> This method resets the amount of consecutive failed unlock attempts back to zero, even if manually triggered by an administrator.</i>
	 * </ul>
	 */
	public void enableManualUnlocks() {
		//	Re-enable manual unlocking with the fingerprint scanner.
		System.out.println("<#> Manual unlocks have been enabled.");
		manualUnlocksEnabled = true;
		consecutiveFailedUnlocks = 0;
		
		//	Update Swing Components
		Helper.runSafelyAndWait(() -> {
			btnManualUnlocks.setText("Disable Manual Unlocks");
			btnManualUnlocks.setBackgroundColors(colorLocked1, colorLocked2);
			btnLock.setEnabled(true);
			btnOpen.setEnabled(true);
			
		});
		
		//	Reset default text if applicable.
		if(currentDefaultText.equalsIgnoreCase(DEFAULT_TEXT_SYSDISABLED)) {
			currentDefaultText = DEFAULT_TEXT;
			setActionStatusText(currentDefaultText);
		}
	}
	
	/**
	 * <ul>
	 * <p>	<b><i>disableManualUnlocks</i></b>
	 * <p>	<code>public void disableManualUnlocks(boolean autoEnable)</code>
	 * <p>	Disables manual unlocks using the fingerprint sensor.
	 * 		Manual unlocks are usually disabled after there are a certain amount of failed unlock attempts using the fingerprint scanner.
	 * @param autoEnable - a <code>boolean</code> for whether or not manual unlocks should be re-enabled automatically after a delay.
	 * </ul>
	 */
	public void disableManualUnlocks(boolean autoEnable) {
		//	Disable manual unlocking with the fingerprint scanner due to a certain amount of failed unlock attempts.
		System.out.println("<#> Manual unlocks have been disabled.");
		manualUnlocksEnabled = false;
		
		//	Update Swing Components
		Helper.runSafelyAndWait(() -> {
			btnManualUnlocks.setText("Enable Manual Unlocks");
			btnManualUnlocks.setBackgroundColors(colorUnlocked1, colorUnlocked2);
			btnLock.setEnabled(false);
			btnOpen.setEnabled(false);
		});
		
		//	Auto re-enable manual unlocks or set default text.
		if(autoEnable) {
			countdownManager.startCountdown("System disabled for ", 300, CountdownType.MANUAL_UNLOCKS);
		}
		else {
			currentDefaultText = DEFAULT_TEXT_SYSDISABLED;
			setActionStatusText(currentDefaultText);
		}
	}
	
	/**
	 * <ul>
	 * <p>	<b><i>exitApplication</i></b>
	 * <p>	<code>private void exitApplication()</code>
	 * <p>	Exits the application, closing the user interface and any connections to it.
	 * </ul>
	 */
	private void exitApplication() {
		System.out.println("<#> Exiting...");
		
		//	Disable manual unlocks and button presses.
		disableManualUnlocks(false);
		Helper.runSafely(() -> {
			btnSettings.setEnabled(false);
			btnSettingsBack.setEnabled(false);
			btnSettingsFP.setEnabled(false);
			btnRegisterFP.setEnabled(false);
			btnEditFP.setEnabled(false);
			btnRemoveFP.setEnabled(false);
		});
		
		// Disconnect from the communications server.
		remoteClient.disconnectFromServer();
		
		//	Close and release scanner and relay.
		if(fingerprintScanner.disconnectSensor()) {
			System.out.println("<#> Sensor disconnected.");
		}
		relayLock.prepareForShutdown();
		System.out.println("<#> Relay disconnected.");
		
		//	Exit
		System.out.println("<#> Closing the GUI...");
		System.exit(0);
	}
}