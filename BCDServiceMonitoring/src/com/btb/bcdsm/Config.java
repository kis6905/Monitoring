package com.btb.bcdsm;

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
	private static String sipsAddr;
	private static String monitoringUrl;
	private static String makerId;
	private static String password;
	private static String serverIp;
	private static String mmmIp;
	private static int mmmPort;

	// Log Config.
	private static String logFile = Constants.DEFAULT_LOG_FILE;
	private static Level logLevel = Level.INFO;

	public static boolean load() {
		InputStream is;
		String value;

		try {
			is = new FileInputStream("bcdsm.properties");
			properties = new Properties();
			properties.load(is);
		} catch (Exception e) {
			e.printStackTrace();
			properties = null;
		}

		if (properties == null)
			return false;

		value = properties.getProperty("sipsAddr");
		if (value != null)
			sipsAddr = value;
		else
			System.err.println("'sipsAddr' is not specified in properties file.");

		value = properties.getProperty("monitoringUrl");
		if (value != null)
			monitoringUrl = value;
		else
			System.err.println("'monitoringUrl' is not specified in properties file.");
		
		value = properties.getProperty("makerId");
		if (value != null)
			makerId = value;
		else
			System.err.println("'makerId' is not specified in properties file.");
		
		value = properties.getProperty("password");
		if (value != null)
			password = value;
		else
			System.err.println("'password' is not specified in properties file.");
		
		value = properties.getProperty("serverIp");
		if (value != null)
			serverIp = value;
		else
			System.err.println("'serverIp' is not specified in properties file.");
		
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

	public static String getSipsAddr() {
		return sipsAddr;
	}

	public static String getMonitoringUrl() {
		return monitoringUrl;
	}
	
	public static String getMakerId() {
		return makerId;
	}
	
	public static String getPassword() {
		return password;
	}
	
	public static String getServerIp() {
		return serverIp;
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
