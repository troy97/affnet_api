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
		"/etc/affnet/affnet.properties", "/etc/affnet.properties",
		"/usr/local/etc/affnet.properties", "C:/affnet/affnet.properties",
		"C:/etc/affnet.properties" };
	private static Logger log = Logger.getLogger(AppConfig.class.getName());
	private static AppConfig instance = null;
	private Path rootPath = null;
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
		try {
			this.loadConfig();
		} catch (IOException e) {
			log.error("Unable to read configuration file");
			System.exit(0);
		}
		setEnvironment();
		System.err.println(this.environment);
		this.rootPath = FileSystems.getDefault().getPath(System.getProperty("user.dir"));
	}
	
	/**
	* Searches for config file in default locations and,
	* if not found, in CLASSPATH
	*
	* @throws IOException if unable to locate or read file.
	*/
	private void loadConfig() throws IOException {
		InputStream in = null;
		for (String path : DEFAULT_CFG_LOCATIONS) {
			if (new File(path).exists()) {
				in = new FileInputStream(path);
				log.debug("config loaded from: " + path);
				break;
			}
		}
		//if config file was not found in default locations, load one from CLASSPATH
		if(in == null) {
			in = Thread.currentThread().getContextClassLoader().getResourceAsStream("affnet.properties");
			log.debug("config loaded from CLASSPATH");
		}
		this.config.load(in);
	}
	
	/**
	* @return one of manually set environment, system environment setting or
	* default environment
	*/
	private void setEnvironment() {
		if (this.environment == null) {
			Map<String, String> env = System.getenv();
			System.err.println("APPLICATION_ENV key: " + env.containsKey(ENVIRONMENT_VAR_NAME));
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
			log.warn("Property name: \"" + propertyName + "\" returned NULL value.");
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
			log.warn("Property name: \"" + propertyName + "\" returned NULL value.");
		}
		return result;
	}
	
	
	/**
	 * Returns root folder path of this app in file system
	 * @return Path root folder
	 */
	public Path getServiceRootFsPath() {
		return this.rootPath;
	}

}
