package eu.ibutler.affiliatenetwork.utils.freemarker;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;

import eu.ibutler.affiliatenetwork.MainClass;
import eu.ibutler.affiliatenetwork.config.AppConfig;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.Version;


/**
 * Singleton class that contains configuration object for FreeMaker
 * @author anton
 *
 */
public class FreeMakerConfig {

	private static AppConfig properties = AppConfig.getInstance();
	private static Logger log = Logger.getLogger(FreeMakerConfig.class.getName());

	private static Configuration instance = null;

	private FreeMakerConfig() {}
	
	/**
	 * Get singleton instance of FreeeMaker configuration Object
	 * @return
	 */
	public static Configuration getConfig() {
		if(instance == null) {
			createConfig();
		}
		return instance;
	}
	
	private static synchronized void createConfig() {
		Configuration cfg = new Configuration();
		try {
			cfg.setDirectoryForTemplateLoading(new File(properties.getWithEnv("WebContentPath") + "/ftl"));
		} catch (IOException e) {
			log.error("Can't open FTL templates folder");
		}
		//cfg.setClassForTemplateLoading(MainClass.class, "ftl/");
		cfg.setObjectWrapper(new DefaultObjectWrapper());
		cfg.setDefaultEncoding("UTF-8");
		cfg.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);
		cfg.setIncompatibleImprovements(new Version(2, 3, 20));  // FreeMarker 2.3.20
		instance = cfg;
	}

	
}
