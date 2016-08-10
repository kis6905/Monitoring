package com.btb.mmm;

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
	private static int port;
	private static String sender;
	private static String password;
	private static String smtpHost;
	private static String principal;
	private static String deputy;
	
	// Log Config.
	private static String logFile = Constants.DEFAULT_LOG_FILE;
	private static Level logLevel = Level.INFO;
	
	public static boolean load() {
		InputStream is;
		String value;

		try {
			is = new FileInputStream("mmm.properties");
			properties = new Properties();
			properties.load(is);
		} catch (Exception e) {
			e.printStackTrace();
			properties = null;
		}

		if (properties == null)
			return false;
		
		value = properties.getProperty("port");
		if (value != null)
			port = Integer.parseInt(value);
		else
			System.err.println("'port' is not specified in properties file.");
		
		value = properties.getProperty("sender");
		if (value != null)
			sender = value;
		else
			System.err.println("'sender' is not specified in properties file.");
		
		value = properties.getProperty("password");
		if (value != null)
			password = value;
		else
			System.err.println("'password' is not specified in properties file.");
		
		value = properties.getProperty("smtpHost");
		if (value != null)
			smtpHost = value;
		else
			System.err.println("'smtpHost' is not specified in properties file.");
		
		value = properties.getProperty("principal");
		if (value != null)
			principal = value;
		else
			System.err.println("'principal' is not specified in properties file.");
		
		value = properties.getProperty("deputy");
		if (value != null)
			deputy = value;
		else
			System.err.println("'deputy' is not specified in properties file.");
			
		
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
	
	public static int getPort() {
		return port;
	}
	
	public static String getSender() {
		return sender;
	}
	
	public static String getPassword() {
		return password;
	}
	
	public static String getSmtpHost() {
		return smtpHost;
	}
	
	public static String getPrincipal() {
		return principal;
	}
	
	public static String getDeputy() {
		return deputy;
	}

	public static String getLogFile() {
		return logFile;
	}

	public static Level getLogLevel() {
		return logLevel;
	}
}
