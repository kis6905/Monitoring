package com.btb.lsgsm;

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
	private static String lsgAddr;
	private static String gwUrl;
	private static String commandSet;
	private static String serviceInfo;
	private static String terminalInfo;
	private static String userInfo;
	private static String requestBody;
	private static String mmmIp;
	private static int mmmPort;

	// Log Config.
	private static String logFile = Constants.DEFAULT_LOG_FILE;
	private static Level logLevel = Level.INFO;

	public static boolean load() {
		InputStream is;
		String value;

		try {
			is = new FileInputStream("lsgsm.properties");
			properties = new Properties();
			properties.load(is);
		} catch (Exception e) {
			e.printStackTrace();
			properties = null;
		}

		if (properties == null)
			return false;

		value = properties.getProperty("lsgAddr");
		if (value != null)
			lsgAddr = value;
		else
			System.err.println("'lsgAddr' is not specified in properties file.");

		value = properties.getProperty("gwUrl");
		if (value != null)
			gwUrl = value;
		else
			System.err.println("'gwUrl' is not specified in properties file.");
		
		value = properties.getProperty("commandSet");
		if (value != null)
			commandSet = value;
		else
			System.err.println("'commandSet' is not specified in properties file.");
		
		value = properties.getProperty("serviceInfo");
		if (value != null)
			serviceInfo = value;
		else
			System.err.println("'serviceInfo' is not specified in properties file.");
		
		value = properties.getProperty("terminalInfo");
		if (value != null)
			terminalInfo = value;
		else
			System.err.println("'terminalInfo' is not specified in properties file.");
		
		value = properties.getProperty("userInfo");
		if (value != null)
			userInfo = value;
		else
			System.err.println("'userInfo' is not specified in properties file.");
		
		value = properties.getProperty("requestBody");
		if (value != null)
			requestBody = value;
		else
			System.err.println("'requestBody' is not specified in properties file.");
		
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
	
	public static String getLsgAddr() {
		return lsgAddr;
	}
	
	public static String getGwUrl() {
		return gwUrl;
	}
	
	public static String getCommandSet() {
		return commandSet;
	}
	
	public static String getServiceInfo() {
		return serviceInfo;
	}
	
	public static String getTerminalInfo() {
		return terminalInfo;
	}
	
	public static String getUserInfo() {
		return userInfo;
	}
	
	public static String getRequestBody() {
		return requestBody;
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
