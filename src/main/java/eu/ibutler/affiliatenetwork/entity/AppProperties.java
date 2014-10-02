package eu.ibutler.affiliatenetwork.entity;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * Class allows to read config.properties file
 * @author Anton Lukashchuk
 *
 */
public class AppProperties {
	
	private final Path rootPath = FileSystems.getDefault().getPath("/home/anton/workspaceJEE/SVN/AffiliateNetwork/");
	private final Path propertyFilePath = rootPath.resolve("src/main/resources/config.properties");
			
	private static AppProperties singleton = null;
	private Properties properties = new Properties();
	
	private static Logger log = Logger.getLogger(AppProperties.class.getName());
	
	private AppProperties() {
		try {
			this.properties.load(new FileInputStream(propertyFilePath.toString()));
		} catch (IOException e) {
			log.error("config.properties unavailable");
			System.exit(0);
		}
	}
	
	/**
	 * 
	 * @return AppProperties instance
	 * @throws IOException
	 */
	public static AppProperties getInstance() {
		if(singleton == null) {
			singleton = new AppProperties();
		}
		return singleton;
	}
	
	/**
	 * Get property value by its name
	 * @param propertyName
	 * @return value of property with given name
	 */
	public String getProperty(String propertyName) {
		return this.properties.getProperty(propertyName);
	}
	
	public Path getRootPath() {
		return rootPath;
	}

}
