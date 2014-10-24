package eu.ibutler.affiliatenetwork.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import eu.ibutler.affiliatenetwork.http.session.HttpSession;

/**
 * Singleton class allows to read config.properties file
 * and performs some operations with this properties
 * @author Anton Lukashchuk
 *
 */
public class AppConfig {
	
	private static final String ENVIRONMENT_VAR_NAME = "APPLICATION_ENV";
	private static final String DEFAULT_ENVIRONMENT = "development";
	private static final String[] DEFAULT_CFG_LOCATIONS = {
		//development
		    //"/home/anton/workspaceJEE/SVN/AffiliateNetwork/affnet.properties",
		//stage, production
		"/etc/affiliateNetwork/affnet.properties",
		};
	
	private static Logger logger = null;
	private static AppConfig instance = null;
	private Properties config = new Properties();
	private String environment = null;
	
	
	
	/**
	 * Get instance of AppProperties
	 * @return AppProperties instance
	 * @throws IOException
	 */
	public static AppConfig getInstance() {
		if(instance == null) {
			instance = new AppConfig();
		}
		return instance;
	}
	
	/**
	 * Private constructor loads config file
	 */
	private AppConfig() {
		//load affnet.properties
		String configLocation = null;
		try {
			configLocation = this.loadConfig();
		} catch (IOException e) {
			System.err.println("Unable to read configuration file, exit.");
			System.exit(1);
		}
		
		//get application environment
		setEnvironment();
		System.out.println("Environment set to: " + this.environment);
		
		//configure logger
		PropertyConfigurator.configure(this.getWithEnv("logginConfigPath"));
		logger = Logger.getLogger(AppConfig.class.getName());
		
		logger.info("Environment set to: " + this.environment);
		logger.info("Config loaded from: " + configLocation);
		
		//configure sessions
		long sessionDefaultInactiveTimer = Long.valueOf(this.get("maxInactiveInterval"));
		HttpSession.setDefaultSessionInactiveInterval(sessionDefaultInactiveTimer*60*1000);
	}
	
	/**
	* Searches for config file in default locations and,
	* if not found, in CLASSPATH
	*
	* @throws IOException if unable to locate or read file.
	*/
	private String loadConfig() throws IOException {
		String result = null;
		InputStream in = null;
		for (String path : DEFAULT_CFG_LOCATIONS) {
			if (new File(path).exists()) {
				in = new FileInputStream(path);
				result = path;
				System.out.println("Config loaded from: " + path);
				break;
			}
		}
		//if config file was not found in default locations, load one from CLASSPATH
		if(in == null) {
			throw new IOException();
		}
		this.config.load(in);
		return result;
	}
	
	/**
	* @return one of manually set environment, system environment setting or
	* default environment
	*/
	private void setEnvironment() {
		if (this.environment == null) {
			Map<String, String> env = System.getenv();
			System.out.println("APPLICATION_ENV key: " + env.containsKey(ENVIRONMENT_VAR_NAME));
			if (env.containsKey(ENVIRONMENT_VAR_NAME)) {
				this.environment = env.get(ENVIRONMENT_VAR_NAME).toString();
			} else {
				this.environment = DEFAULT_ENVIRONMENT;
			}
		}
	}
	
	
	/**
	 * Get property value by its name
	 * @param propertyName
	 * @return value of property with given name or null if no such property name found
	 */
	public String get(String propertyName) {
		String result = this.config.getProperty(propertyName);
		if(result == null) {
			logger.warn("Property name: \"" + propertyName + "\" returned NULL value.");
		}
		return result;
	}
	
	/**
	 * Get property value by its name according to current environment
	 * @param propertyName
	 * @return value of property with given name or null if no such property name found
	 */
	public String getWithEnv(String propertyName) {
		String result = this.config.getProperty(this.environment + "." + propertyName);
		if(result == null) {
			logger.warn("Property name: \"" + propertyName + "\" returned NULL value.");
		}
		return result;
	}
	
}
