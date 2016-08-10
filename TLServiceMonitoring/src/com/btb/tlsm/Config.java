package com.btb.tlsm;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Level;

/**
 * @author iskwon
 *
 */
public class Config {
	
	private static Properties properties = null;

	// Members for config properties
	private static String tlasAddr;
	private static String authUrl;
	private static String serialKey;
	private static String macAddress;
	private static String mmmIp;
	private static int mmmPort;

	// Log Config.
	private static String logFile = Constants.DEFAULT_LOG_FILE;
	private static Level logLevel = Level.INFO;

	public static boolean load() {
		InputStream is;
		String value;

		try {
			is = new FileInputStream("tlsm.properties");
			properties = new Properties();
			properties.load(is);
		} catch (Exception e) {
			e.printStackTrace();
			properties = null;
		}

		if (properties == null)
			return false;

		value = properties.getProperty("tlasAddr");
		if (value != null)
			tlasAddr = value;
		else
			System.err.println("'tlasAddr' is not specified in properties file.");

		value = properties.getProperty("authUrl");
		if (value != null)
			authUrl = value;
		else
			System.err.println("'authUrl' is not specified in properties file.");
		
		value = properties.getProperty("serialKey");
		if (value != null)
			serialKey = value;
		else
			System.err.println("'serialKey' is not specified in properties file.");
		
		value = properties.getProperty("macAddress");
		if (value != null)
			macAddress = value;
		else
			System.err.println("'macAddress' is not specified in properties file.");
		
		value = properties.getProperty("mmmIp");
		if (value != null)
			mmmIp = value;
		else
			System.err.println("'mmmIp' is not specified in properties file.");
		
		value = properties.getProperty("mmmPort");
		if (value != null)
			mmmPort = Integer.parseInt(value);
		else
			System.err.println("'mmmPort' is not specified in properties file.");
		
		value = properties.getProperty("logFile");
		if (value != null)
			logFile = value;

		value = properties.getProperty("logLevel");
		if (value != null) {
			if (value.equalsIgnoreCase("ALL")) 
				logLevel = Level.ALL;
			else if (value.equalsIgnoreCase("TRACE"))
				logLevel = Level.TRACE;
			else if (value.equalsIgnoreCase("DEBUG"))
				logLevel = Level.DEBUG;
			else if (value.equalsIgnoreCase("INFO")) 
				logLevel = Level.INFO;
			else if (value.equalsIgnoreCase("WARN")) 
				logLevel = Level.WARN;
			else if (value.equalsIgnoreCase("ERROR")) 
				logLevel = Level.ERROR;
			else if (value.equalsIgnoreCase("FATAL")) 
				logLevel = Level.FATAL;
			else if (value.equalsIgnoreCase("OFF")) 
				logLevel = Level.OFF;
		}
		else {
			logLevel = Level.WARN;
		}

		return true;
	}

	public static Properties getProperties() {
		return properties;
	}

	public static String getTlasAddr() {
		return tlasAddr;
	}
	
	public static String getAuthUrl() {
		return authUrl;
	}
	
	public static String getSerialKey() {
		return serialKey;
	}
	
	public static String getMacAddress() {
		return macAddress;
	}
	
	public static String getMmmIp() {
		return mmmIp;
	}
	
	public static int getMmmPort() {
		return mmmPort;
	}

	public static String getLogFile() {
		return logFile;
	}

	public static Level getLogLevel() {
		return logLevel;
	}
}
