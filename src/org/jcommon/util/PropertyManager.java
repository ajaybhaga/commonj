package org.jcommon.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

/**
 * The PropertyManager class provides convenience methods for working with
 * property files.
 * <p>
 * Here is a code example: <code>PropertyManager pm = PropertyManager.getXMLPropertyManager(String resource);</code>
 * of how to create an instance of this class.
 * 
 * @see Properties
 * @see FileInputStream
 * @since 5.0
 * @author bcurtsin
 *
 */
public class PropertyManager {
	private Properties properties;
	
	/**
	 * Instantiates the PropertyManager object containing the properties loaded from 
	 * a Property File.
	 * 
	 * @param properties	contains the properties loaded from a Property File
	 */
	private PropertyManager(Properties properties) {
		this.properties = properties;
	}
	
	/**
	 * Returns the value associated with the provided key. If the key is not found
	 * then a null value will be returned.
	 * 
	 * @param key	key to lookup the value for
	 * @return 		value represented by the key
	 * @throws		NullPointerException if key is null
	 */
	public String getString(String key) {
		return properties.getProperty(key);
	}
	
	/**
	 * Returns the int value of the value represented by the provided key. A call
	 * is made to {@link #getString} which is in turn parsed to an int.
	 * 
	 * @param key	key to lookup the value for
	 * @return		value represented by the key
	 * @throws		NullPointerException if the key is null
	 */
	public int getInt(String key) {
		return Integer.parseInt(getString(key));
	}
	
	/**
	 * Returns the Integer value of the value represented by the provided key. A call
	 * is made to {@link #getString} which is in turn parsed to an Integer.
	 * 
	 * @param key	key to lookup the value for
	 * @return		value represented by the key
	 * @throws		NullPointerException if the key is null
	 */
	public Integer getInteger(String key) {
		Integer i = null;
		String s = getString(key);
		if (s != null) {
			i = new Integer(s);
		}
		return i;
	}
	
	/**
	 * Returns an instance of a PropertyManager containing Properties loaded from
	 * the resource parameter containing the path to an XML Properties File.
	 * 
	 * @param resource	the path to an XML Properties File
	 * @return			instance of a PropertyManager containing the loaded properties
	 * @throws InvalidPropertiesFormatException
	 * @throws IOException
	 */
	public static PropertyManager getXMLPropertyManager(String resource) throws InvalidPropertiesFormatException, IOException {
		Properties properties = new Properties();
		InputStream fs = null;
		try {
			fs = PropertyManager.class.getClassLoader().getResourceAsStream(resource);
			properties.loadFromXML(fs);
			return new PropertyManager(properties);
		} finally {
			if (fs != null) {
				fs.close();
			}
		}
	}
	
	/**
	 * Returns an instance of a PropertyManager containing the Properties loaded
	 * from the resource parameter containing the path to a Properties File.
	 * 
	 * @param resource	the path to a Properties File.
	 * @return			instance of a PropertyManager containing the loaded properties.
	 * @throws IOException 
	 */
	public static PropertyManager getPropertiesFilePropertyManger(String resource) throws IOException {
		Properties properties = new Properties();
		InputStream fs = null;
		try {
			fs = PropertyManager.class.getClassLoader().getResourceAsStream(resource);
			properties.load(fs);
			return new PropertyManager(properties);
		} finally {
			if (fs != null) {
				fs.close();
			}
		}
	}
}
