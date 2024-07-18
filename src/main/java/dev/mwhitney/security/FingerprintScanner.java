package dev.mwhitney.security;

import java.io.IOException;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.SwingUtilities;

import dev.mwhitney.main.NotificationPopup;
import sk.mimac.fingerprint.FingerprintException;
import sk.mimac.fingerprint.FingerprintSensor;
import sk.mimac.fingerprint.adafruit.AdafruitSensor;

/**
 * 
 * @author Matthew Whitney
 *
 */
public class FingerprintScanner {
	
	/** A <tt>FingerprintSensor</tt> object that is responsible for communicating with the fingerprint scanner. */
	private static FingerprintSensor sensor;
	/** An <code>int</code> for the ID of the last registered fingerprint in the system. */
	private int lastRegisteredFingerprintID = -1;
	/** An <code>int</code> for the amount of fingerprints registered in the system. */
	private int fingerprintCount = 0;
	/** An <code>int[]</code> array containing the ID numbers of all registered fingerprints. */
	private List<Integer> fingerprintIDs = new ArrayList<Integer>();
	/** An <code>int[]</code> array containing the fingerprint ID numbers of system Administrators. */
	private List<Integer> fingerprintAdmins = new ArrayList<Integer>();
	
	/** A <code>boolean</code> for whether or not a scan is underway. */
	private boolean isScanning = false;
	/** A <code>boolean</code> for whether or not a scan is actively being cancelled. */
	private boolean cancellingScan = false;
	
	/**
	 * <ul>
	 * <p>	<b><i>FingerprintScanner</i></b>
	 * <p>	<code>public FingerprintScanner()</code>
	 * <p>	Creates a new <tt>FingerprintScanner</tt>.
	 * <p>	Note: There should only be one object of this class active at a time.
	 * </ul>
	 */
	public FingerprintScanner() {
		//	Creates the FingerprintSensor object and gives it the serial port to connect to later.
		//	For GPIO, used '/dev/ttyS0'
		sensor = new AdafruitSensor("/dev/ttyUSB0");
		connectSensor();
	}
	
	/**
	 * <ul>
	 * <p>	<b><i>connectSensor</i></b>
	 * <p>	<code>private boolean connectSensor()</code>
	 * <p>	Connects to the fingerprint scanner.
	 * @return <code>true</code> if the connection attempt was successful; <code>false</code> otherwise.
	 * </ul>
	 */
	private boolean connectSensor() {
		try {
			sensor.connect();
		} catch (FingerprintException fe) {
			System.out.println("<!> Error connecting sensor. (FE conSen)");
			fe.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * <ul>
	 * <p>	<b><i>disconnectSensor</i></b>
	 * <p>	<code>public boolean disconnectSensor()</code>
	 * <p>	Disconnects the fingerprint scanner.
	 * <p>	<b>WARNING:</b> This method closes the connection to the fingerprint scanner.
	 * 		It is only intended to be used before exiting the application.
	 * @return <code>true</code> if the connection was successfully closed; <code>false</code> otherwise.
	 * </ul>
	 */
	public boolean disconnectSensor() {
		try {
			sensor.close();
		} catch (IOException ioe) {
			System.out.println("<!> Error disconnecting sensor. (IOE disSen)");
			ioe.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * <ul>
	 * <p>	<b><i>scanFingerprint</i></b>
	 * <p>	<code>public int scanFingerprint()</code>
	 * <p>	Scans for an authorized fingerprint.
	 * <p>	Checks for a finger on the scanner every 100 milliseconds.
	 * 		Once a finger is detected, it is scanned and compared.
	 * 		If the fingerprint is recognized; the method returns its ID number.
	 * 		If the fingerprint is not recognized; the method returns <code>-1</code>.
	 * 		If the scan was cancelled; the method returns <code>-2</code>.
	 * @param statusNotification - the <tt>NotificationPopup</tt> to display status notifications on.
	 * @return the scanned fingerprint's ID number if it is registered; <code>-1</code> otherwise. Returns <code>-2</code> if the scan was cancelled.
	 * </ul>
	 */
	public int scanFingerprint(NotificationPopup statusNotification) {
		isScanning = true;
		
		try {
			//	Wait for a finger to placed on the scanner, then scan the fingerprint.
			while (!sensor.hasFingerprint()) {
				System.out.println("Checked for finger...");
				if(cancellingScan) {
					System.out.println("...I'm supposed to cancel!");
					return -2;
				}
				Thread.sleep(100);
			}
			
			//	Check if the fingerprint is authorized.
			final Integer fingerID = sensor.searchFingerprint();
			if (fingerID != null) {
				//	The fingerprint is known and authorized.
				System.out.println("## Scanned: Fingerprint ID #" + fingerID);
				isScanning = false;
				return fingerID;
			}
		} catch (FingerprintException fe) {
			System.out.println("<!> Error scanning fingerprint. (FE scaFin)");
			fe.printStackTrace();
		} catch (InterruptedException ie) {
			System.out.println("<!> Error scanning fingerprint. (IE scaFin)");
			ie.printStackTrace();
		}
		//	The fingerprint has not been authorized.
		isScanning = false;
		return -1;
	}
	
	/**
	 * <ul>
	 * <p>	<b><i>scanFingerprintAdmin</i></b>
	 * <p>	<code>public boolean scanFingerprintAdmin()</code>
	 * <p>	Scans for an authorized, Administrator-level fingerprint.
	 * <p>	Checks for a finger on the scanner every 100 milliseconds.
	 * 		Once a finger is detected, it is scanned and compared.
	 * 		If the fingerprint is authorized and designated as an Administrator; the method returns <code>true</code>.
	 * 		If the fingerprint has not been authorized or is not designated as an Administrator; the method returns <code>false</code>.
	 * @param statusNotification - the <tt>NotificationPopup</tt> to display status notifications on.
	 * @return <code>true</code> if the fingerprint is authorized and an Administrator; <code>false</code> otherwise.
	 * </ul>
	 */
	public boolean scanFingerprintAdmin(NotificationPopup statusNotification) {
		isScanning = true;
		
		try {
			//	Wait for a finger to placed on the scanner, then scan the fingerprint.
			while (!sensor.hasFingerprint()) {
				System.out.println("Checked for finger...");
				if(cancellingScan) {
					System.out.println("...I'm supposed to cancel!");
					return false;
				}
				Thread.sleep(100);
			}
			
			//	Check if the fingerprint is authorized.
			final Integer fingerID = sensor.searchFingerprint();
			if (fingerID != null && fingerprintAdmins.contains(fingerID)) {
				//	The fingerprint is known and authorized.
				System.out.println("## Scanned Admin: Fingerprint ID #" + fingerID);
				isScanning = false;
				return true;
			}
		} catch (FingerprintException fe) {
			System.out.println("<!> Error scanning admin fingerprint. (FE scaFinAdm)");
			fe.printStackTrace();
		} catch (InterruptedException ie) {
			System.out.println("<!> Error scanning admin fingerprint. (IE scaFinAdm)");
			ie.printStackTrace();
		}
		//	The fingerprint has not been authorized or is not an Administrator.
		isScanning = false;
		return false;
	}
	
	/**
	 * <ul>
	 * <p>	<b><i>cancelFingerprintScan</i></b>
	 * <p>	<code>public void cancelFingerprintScan()</code>
	 * <p>	Calls for a cancellation of any ongoing fingerprint scan.
	 * </ul>
	 */
	public void cancelFingerprintScan() {
		if(isScanning) {
			isScanning = false;
			cancellingScan = true;
		}
	}
	
	/**
	 * <ul>
	 * <p>	<b><i>checkForScanCancellation</i></b>
	 * <p>	<code>public boolean checkForScanCancellation()</code>
	 * <p>	Checks for an ongoing fingerprint scan cancellation.
	 * 		If there is a fingerprint scan being cancelled, this method will finalize the cancellation process.
	 * @return <code>true</code> if there is a fingerprint scan being cancelled; <code>false</code> otherwise.
	 * </ul>
	 */
	public boolean checkForScanCancellation() {
		final boolean isCancelling = cancellingScan;
		cancellingScan = false;
		return isCancelling;
	}
	
	/**
	 * <ul>
	 * <p>	<b><i>registerFingerprint</i></b>
	 * <p>	<code>public byte[] registerFingerprint(NotificationPopup statusNotification)</code>
	 * <p>	Execute the fingerprint registration process.
	 * <p>	This process requires the user to place their finger on the scanner, remove it, then place it again.
	 * 		After completing both scans, a fingerprint model is created and stored on the fingerprint scanner's internal memory.
	 * 		Additionally, the model is returned, so it may be stored in a file on the system drive.
	 * @param statusNotification - the <tt>NotificationPopup</tt> to display status notifications on.
	 * @return model - a <code>byte[]</code> array containing the fingerprint model if the registration process was successful.
	 * 		Returns <code>null</code> if registration failed.
	 * </ul>
	 */
	public Entry<Integer, byte[]> registerFingerprint(NotificationPopup statusNotification) {
		byte[] model = null;
		Integer fingerID;
		try {
			//	Wait for a finger to placed on the scanner, then perform the first scan of the fingerprint.
			while(!sensor.hasFingerprint()) {
				Thread.sleep(100);
			}
			
			//	If the fingerprint already exists, notify user and cancel registration.
			fingerID = sensor.searchFingerprint();
			if(fingerID!=null) {
				System.out.println("<##> REGISTRATION --> Fingerprint already registered under ID #" + fingerID + "!");
				return null;
			}
			
			//	Perform the second scan of the fingerprint and create the model.
			SwingUtilities.invokeLater(() -> {
				//	It is not guaranteed that the first scan will remain as the first scan given the while() loop below.
				//	However, to prevent confusing the user, this terminology is being used anyway.
				statusNotification.setNotificationText("First scan completed.\nRemove finger from scanner.\nThen place it again for the second scan...");
			});
			
			//	Perform another scan, overwriting the previous scan if the fingerprint is still on the sensor.
			while(sensor.hasFingerprint()) {
				Thread.sleep(50);
			}
			
			//	Finally, continuously attempt to create the fingerprint model from the final scan.
			while (model == null) {
				Thread.sleep(100);
				model = sensor.createModel();
			}
			
			//	Ensures that the fingerprint is stored under a nonexistent ID.
			fingerID = 0;
			for(int newFingerID = 0; fingerprintIDs.contains(newFingerID); newFingerID++) {
				fingerID = newFingerID + 1;
			}
			
			//	Store the model on the fingerprint scanner.
			lastRegisteredFingerprintID = fingerID;
			fingerprintCount++;
			fingerprintIDs.add(fingerID);
			sensor.saveStoredModel(fingerID);
			System.out.println("## Registration Completed for Finger #" + fingerID + ".");
		} catch (FingerprintException fe) {
			System.out.println("<!> Error registering fingerprint. (FE regFin)");
			System.out.println(fe.getStackTrace()[0]);
			return null;
		} catch (InterruptedException ie) {
			System.out.println("<!> Error registering fingerprint. (IE regFin)");
			ie.printStackTrace();
			return null;
		}
		//	Return the fingerprint model for file storage.
		return new SimpleEntry<Integer, byte[]>(fingerID, model);
	}
	
	/**
	 * <ul>
	 * <p>	<b><i>uploadFingerprintModels</i></b>
	 * <p>	<code>public boolean uploadFingerprintModels(Map{@literal<Integer, byte[]>} fingerprintModels)</code>
	 * <p>	Sends all of the passed fingerprint models to the fingerprint scanner. These models are saved on the scanner's internal memory.
	 * @param fingerprintModels - a set of fingerprint models and their respective numerical positions.
	 * @return <code>true</code> if the models were uploaded successfully; <code>false</code> otherwise.
	 * </ul>
	 */
	public boolean uploadFingerprintModels(Map<Integer, byte[]> fingerprintModels) {
		//	Save every model onto the fingerprint scanner with their correct positions.
		try {
			for (Map.Entry<Integer, byte[]> fingerprintModel : fingerprintModels.entrySet()) {
				sensor.saveModel(fingerprintModel.getValue(), fingerprintModel.getKey());
				fingerprintIDs.add(fingerprintModel.getKey());
			}
			setFingerprintCount(fingerprintModels.size());
			return true;
		} catch (FingerprintException fe) {
			System.out.println("<!> Error uploading fingerprints to sensor. (FE uplFinMod)");
			fe.printStackTrace();
		}
		return false;
	}
	
	/**
	 * <ul>
	 * <p>	<b><i>uploadFingerprintModels</i></b>
	 * <p>	<code>public boolean uploadFingerprintModels(Map{@literal<Integer, byte[]>} fingerprintModels, boolean overwrite)</code>
	 * <p>	Sends all of the passed fingerprint models to the fingerprint scanner. These models are saved on the scanner's internal memory.
	 * @param fingerprintModels - a set of fingerprint models and their respective numerical positions.
	 * @param overwrite - <code>true</code> if the sensor is to replace its entire fingerprint list with the new models; <code>false</code> otherwise.
	 * @return <code>true</code> if the models were uploaded successfully; <code>false</code> otherwise.
	 * </ul>
	 */
	public boolean uploadFingerprintModels(Map<Integer, byte[]> fingerprintModels, boolean overwrite) {
		if(overwrite) {
			try {
				sensor.clearAllSaved();
				fingerprintIDs.clear();
			} catch (FingerprintException fe) {
				System.out.println("<!> Error uploading fingerprints to sensor. (FE uplFinMod2)");
				fe.printStackTrace();
			}
		}
		
		return uploadFingerprintModels(fingerprintModels);
	}
	
	/**
	 * <ul>
	 * <p>	<b><i>getFingerprintCount</i></b>
	 * <p>	<code>public int getFingerprintCount()</code>
	 * <p>	Gets the number of fingerprints registered in the system.
	 * @return fingerprintCount - the number of registered fingerprints.
	 * </ul>
	 */
	public int getFingerprintCount() {
		return fingerprintCount;
	}
	
	/**
	 * <ul>
	 * <p>	<b><i>setFingerprintCount</i></b>
	 * <p>	<code>public void setFingerprintCount(int numberOfPrints)</code>
	 * <p>	Sets the number of fingerprints registered in the system.
	 * <p>	Note: The <code>uploadFingerprintModels</code> method calls on this method.
	 * 		Therefore, depending on the reasoning, it may not be necessary.
	 * @param numberOfPrints - the number of registered fingerprints.
	 * </ul>
	 */
	public void setFingerprintCount(int numberOfPrints) {
		fingerprintCount = numberOfPrints;
	}
	
	/**
	 * <ul>
	 * <p>	<b><i>getLastRegistrantID</i></b>
	 * <p>	<code>public int getLastRegistrantID()</code>
	 * <p>	Gets the ID of the fingerprint most recently registered in the system.
	 * <p>	Note: The last registered fingerprint ID is not carried over between sessions.
	 * 		Therefore, this method will return <code>-1</code> if no registration has taken place during this session.
	 * @return the ID of the last registered fingerprint or <code>-1</code> if no registration has taken place during this session.
	 * </ul>
	 */
	public int getLastRegistrantID() {
		return lastRegisteredFingerprintID;
	}
	
	/**
	 * <ul>
	 * <p>	<b><i>getFingerprintIDs</i></b>
	 * <p>	<code>public int[] getFingerprintIDs()</code>
	 * <p>	Gets an array of ID numbers for all registered fingerprints in the system.
	 * @return an <code>int[]</code> array containing the fingerprint IDs.
	 * </ul>
	 */
	public int[] getFingerprintIDs() {
		return fingerprintIDs.stream().mapToInt(i -> i).toArray();
	}
	
	/**
	 * <ul>
	 * <p>	<b><i>setFingerprintIDs</i></b>
	 * <p>	<code>public void setFingerprintIDs(int[] fingerprintIDArray)</code>
	 * <p>	Sets the array of registered fingerprint IDs.
	 * @param fingerprintIDArray - an <code>int[]</code> array of registered fingerprint IDs.
	 * </ul>
	 */
	public void setFingerprintIDs(Integer[] fingerprintIDArray) {
		fingerprintIDs = Arrays.asList(fingerprintIDArray);
	}
	
	/**
	 * <ul>
	 * <p>	<b><i>isFingerprintAdmin</i></b>
	 * <p>	<code>public boolean isFingerprintAdmin(int fingerID)</code>
	 * <p>	Checks if the fingerprint under the passed ID number is an administrator.
	 * @param fingerID - the ID number of the fingerprint to perform the check on.
	 * @return <code>true</code> if the fingerprint is an administrator; <code>false</code> otherwise.
	 * </ul>
	 */
	public boolean isFingerprintAdmin(int fingerID) {
		return fingerprintAdmins.contains(fingerID);
	}
	
	/**
	 * <ul>
	 * <p>	<b><i>getFingerprintAdmins</i></b>
	 * <p>	<code>public int[] getFingerprintAdmins()</code>
	 * <p>	Get an array containing the fingerprint ID numbers of administrators.
	 * @return an <code>int[]</code> array containing the administrators' fingerprint IDs.
	 * </ul>
	 */
	public int[] getFingerprintAdmins() {
		return fingerprintAdmins.stream().mapToInt(i -> i).toArray();
	}
	
	/**
	 * <ul>
	 * <p>	<b><i>setFingerprintAdmins</i></b>
	 * <p>	<code>public void[] setFingerprintAdmins(Integer[] admins)</code>
	 * <p>	Sets the fingerprint administrators ID list.
	 * @param an <code>Integer[]</code> array containing the administrators' fingerprint IDs.
	 * </ul>
	 */
	public void setFingerprintAdmins(Integer[] admins) {
		fingerprintAdmins = Arrays.asList(admins);
	}
	
	/**
	 * <ul>
	 * <p>	<b><i>addFingerprintAdmin</i></b>
	 * <p>	<code>public void[] addFingerprintAdmin(int fingerID)</code>
	 * <p>	Adds the fingerprint to the administrators ID list.
	 * @param fingerID - the ID number of the fingerprint to add.
	 * </ul>
	 */
	public void addFingerprintAdmin(int fingerID) {
		if(!isFingerprintAdmin(fingerID)) {
			fingerprintAdmins.add(fingerID);
		}
	}
	
	/**
	 * <ul>
	 * <p>	<b><i>removeFingerprintAdmin</i></b>
	 * <p>	<code>public void[] removeFingerprintAdmin(int fingerID)</code>
	 * <p>	Removes the fingerprint from the administrators ID list.
	 * @param fingerID - the ID number of the fingerprint to remove.
	 * </ul>
	 */
	public void removeFingerprintAdmin(int fingerID) {
		if(isFingerprintAdmin(fingerID)) {
			fingerprintAdmins.remove(fingerprintAdmins.indexOf(fingerID));
		}
	}
}
