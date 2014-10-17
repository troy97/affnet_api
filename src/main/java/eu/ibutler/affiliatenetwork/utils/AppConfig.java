package eu.ibutler.affiliatenetwork.utils;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * Singleton class allows to read config.properties file
 * and performs some operations with this properties
 * @author Anton Lukashchuk
 *
 */
public class AppConfig {
	
	private Path rootPath = null;
			
	private static AppConfig instance = null;
	private Properties properties = new Properties();
	
	private static Logger log = Logger.getLogger(AppConfig.class.getName());
	
	private AppConfig() {
		try {
			properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("config.properties"));
			this.rootPath = FileSystems.getDefault().getPath(this.properties.getProperty("serviceRootPath"));
		} catch (IOException e) {
			log.error("config.properties unavailable");
			System.exit(0);
		}
	}
	
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
	 * Get property value by its name
	 * @param propertyName
	 * @return value of property with given name or null if no such property name found
	 */
	public String get(String propertyName) {
		String result = this.properties.getProperty(propertyName);
		if(result == null) {
			log.warn("Property name: \"" + propertyName + "\" returned NULL value.");
		}
		return result;
	}
	
	/**
	 * Creates URL by concatenating many url parts,
	 * be sure, that You gave url-part property names as parameters 
	 * of this method.
	 * @param urlParts property names
	 * @return URL created from parts
	 */
	public String makeUrl(String... urlParts) {
		StringBuilder result = new StringBuilder();
		for(String partName : urlParts) {
			String value = this.properties.getProperty(partName);
			if(value == null) {
				log.warn("Property name: \"" + partName + "\" returned NULL value.");
			}
			result.append(this.properties.getProperty(partName));
		}
		return result.toString();
	}
	
	/**
	 * Returns root folder path of this app in file system
	 * @return Path root folder
	 */
	public Path getServiceRootFsPath() {
		return rootPath;
	}

}
