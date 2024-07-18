package dev.mwhitney.main;

import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 
 * @author Matthew Whitney
 *
 */
public class FileManager {

	/** The folder location for the Project O.N.E. files. */
	final private String folderLocation = System.getProperty("user.dir") + "/ProjectONE/";
	/** The file extension of custom Project O.N.E. files. */
	final private String fileExtension = ".onefp";
	/** The file location for the properties file which contains the settings for Project O.N.E. */
	final private String propsFileLocation = folderLocation + "settings.properties";
	
	/** The <tt>Properties</tt> object that handles the settings. */
	private Properties props = new Properties();
	/** The <tt>FileWriter</tt> object that is responsible for writing the settings to the properties file. */
	private FileWriter writer = null;
	
	/**
	 * <ul>
	 * <p>	<b><i>FileManager</i></b>
	 * <p>	<code>public FileManager()</code>
	 * <p>	Creates a new <tt>FileManager</tt>.
	 * </ul>
	 */
	public FileManager() {
		/*	Create or Load Properties */
		if (!propsFileExists()) {
			writeSettings();
		} else {
			loadSettings();
		}
	}
	
	/**
	 * <ul>
	 * <p>	<b><i>propsFileExists</i></b>
	 * <p>	<code>private boolean propsFileExists()</code>
	 * <p>	Checks if the properties file exists and returns the result as a <code>boolean</code>.
	 * @return <code>true</code> if the properties file exists; <code>false</code> otherwise.
	 * </ul>
	 */
	private boolean propsFileExists() {
		return new File(propsFileLocation).exists();
	}
	
	/**
	 * <ul>
	 * <p>	<b><i>createPropsFile</i></b>
	 * <p>	<code>private void createPropsFile()</code>
	 * <p>	Creates the properties file used to store settings and information.
	 * <p>	<b>WARNING:</b> This method forces the creation of a new, empty file.
	 * 		This means that if a properties file already exists, it will be overwritten.
	 * 		You can check for an existing properties file by comparing the <code>boolean</code> returned from the <code>propsFileExists()</code> method.
	 * </ul>
	 */
	private void createPropsFile() {
		try {
			final File file = new File(propsFileLocation);
			file.getParentFile().mkdirs();
			file.createNewFile();
		} catch (IOException ioe) {
			System.out.println("<!> Error creating properties file. (IOE creProFil)");
			ioe.printStackTrace();
		}
	}
	
	/**
	 * <ul>
	 * <p>	<b><i>writeSettings</i></b>
	 * <p>	<code>public void writeSettings()</code>
	 * <p>	Writes the current settings to the properties file.
	 * </ul>
	 */
	public void writeSettings() {
		try {
			if(!propsFileExists()) {
				createPropsFile();
			}
			writer = new FileWriter(propsFileLocation);
			props.store(writer, "Project O.N.E. Settings\nChanging these settings to invalid values may cause issues with the application.");
			writer.close();
		} catch (IOException ioe) {
			System.out.println("<!> Error writing settings. (IOE wriSet)");
			ioe.printStackTrace();
		}
	}
	
	/**
	 * <ul>
	 * <p>	<b><i>loadSettings</i></b>
	 * <p>	<code>private void loadSettings()</code>
	 * <p>	Loads the settings from the properties file.
	 * </ul>
	 */
	private void loadSettings() {
		//	Read from the active properties and apply settings accordingly.
		try {
			final FileReader reader = new FileReader(propsFileLocation);
			props.load(reader);
			reader.close();
		} catch(IOException ioe) {
			System.out.println("<!> Fatal error loading settings. (IOE loaSet)");
			ioe.printStackTrace();
			System.exit(0);
		}
	}
	
	/**
	 * <ul>
	 * <p>	<b><i>retrieveFingerprintModels</i></b>
	 * <p>	<code>public Map{@literal <Integer, byte[]>} retrieveFingerprintModels()</code>
	 * <p>	Retrieves all of the fingerprint models stored on the local disk.
	 * @return a <tt>Map{@literal <Integer, byte[]>}</tt> containing the fingerprint models.
	 * </ul>
	 */
	public Map<Integer, byte[]> retrieveFingerprintModels() {
		Map<Integer, byte[]> fingerprintModels = new HashMap<Integer, byte[]>(1);
		final File[] fileList = new File(folderLocation).listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.isFile() && pathname.getName().startsWith("fingerprint");
			}
		});
		//	Reads each fingerprint file and puts the models into a Map.
		for(File fingerprintFile : fileList) {
			try {
				fingerprintModels.put(Integer.parseInt(fingerprintFile.getName().replaceFirst("fingerprint", "").replace(fileExtension, "")), Files.readAllBytes(Paths.get(fingerprintFile.getCanonicalPath())));
			} catch (IOException ioe) {
				System.out.println("<!> Error reading fingerprint model files. (IOE retFinMod)");
				ioe.printStackTrace();
			}
		}
		return fingerprintModels;
	}
	
	/**
	 * <ul>
	 * <p>	<b><i>storeFingerprintModel</i></b>
	 * <p>	<code>public void storeFingerprintModel(Map.Entry{@literal <Integer, byte[]>} fingerprintModel)</code>
	 * <p>	Stores the passed fingerprint model to the local disk.
	 * @param fingerprintModel - a <tt>Map.Entry{@literal <Integer, byte[]>}</tt> containing the registered fingerprint model and its position number.
	 * </ul>
	 */
	public void storeFingerprintModel(Map.Entry<Integer, byte[]> fingerprintModel) {
		setProperty("Fingerprint" + fingerprintModel.getKey(), "");
		setProperty("EmergencyUses" + fingerprintModel.getKey(), "1");
		setProperty("Admin" + fingerprintModel.getKey(), "false");
		writeSettings();
		
		// Write individual fingerprint model to its own file.
		try {
			Files.write(Paths.get(folderLocation + "fingerprint" + fingerprintModel.getKey() + fileExtension), fingerprintModel.getValue());
		} catch (IOException ioe) {
			System.out.println("<!> Error writing fingerprint model files. (IOE stoFinMod)");
			ioe.printStackTrace();
		}
	}
	
	/**
	 * <ul>
	 * <p>	<b><i>storeFingerprintModels</i></b>
	 * <p>	<code>public void storeFingerprintModels(Map{@literal <Integer, byte[]>} fingerprintModels)</code>
	 * <p>	Stores all of the registered fingerprint models to the local disk.
	 * @param fingerprintModels - a <tt>Map{@literal <Integer, byte[]>}</tt> containing the registered fingerprint models.
	 * </ul>
	 */
	public void storeFingerprintModels(Map<Integer, byte[]> fingerprintModels) {
		//	Write each fingerprint model to its own file.
		for(Map.Entry<Integer, byte[]> fingerprintModel : fingerprintModels.entrySet()) {
			storeFingerprintModel(fingerprintModel);
		}
	}
	
	/**
	 * <ul>
	 * <p>	<b><i>deleteFingerprintModel</i></b>
	 * <p>	<code>public void deleteFingerprintModel(int fingerID)</code>
	 * <p>	Stores the passed fingerprint model to the local disk.
	 * @param fingerprintModel - a <tt>Map.Entry{@literal <Integer, byte[]>}</tt> containing the registered fingerprint model and its position number.
	 * </ul>
	 */
	public void deleteFingerprintModel(int fingerID) {
		try {
			Files.delete(Paths.get(folderLocation + "fingerprint" + fingerID + fileExtension));
		} catch (IOException ioe) {
			System.out.println("<!> Error deleting fingerprint model files. (IOE delFinMod)");
			ioe.printStackTrace();
		}
		
		props.remove("Fingerprint" + fingerID);
		props.remove("EmergencyUses" + fingerID);
		props.remove("Admin" + fingerID);
		writeSettings();
	}
	
	/**
	 * <ul>
	 * <p>	<b><i>getPropertyKeys</i></b>
	 * <p>	<code>public String[] getPropertyKeys()</code>
	 * <p>	Gets all of this <tt>FileManager</tt>'s properties keys.
	 * @return a <tt>String[]</tt> array containing all of the properties keys.
	 * </ul>
	 */
	public String[] getPropertyKeys() {
		return (String[]) props.keySet().toArray(new String[0]);	
	}
	
	/**
	 * <ul>
	 * <p>	<b><i>getProperty</i></b>
	 * <p>	<code>public String getProperty(String propertyKey)</code>
	 * <p>	Gets the property stored under the passed key name.
	 * @param propertyKey - the key or name of the property to get.
	 * @return the passed property key's respective value in the form of a <tt>String</tt>.
	 * </ul>
	 */
	public String getProperty(String propertyKey) {
		return props.getProperty(propertyKey);
	}
	
	/**
	 * <ul>
	 * <p>	<b><i>setProperty</i></b>
	 * <p>	<code>public void setProperty(String propertyKey, String propertyValue)</code>
	 * <p>	Sets a property with the passed <b>key</b> and <b>value</b>.
	 * @param propertyKey - the property key or name.
	 * @param propertyValue - the property key's value.
	 * </ul>
	 */
	public void setProperty(String propertyKey, String propertyValue) {
		props.setProperty(propertyKey, propertyValue);
	}
}