package eu.ibutler.affiliatenetwork.entity;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;

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

	private static AppProperties properties = AppProperties.getInstance();
	private static Logger log = Logger.getLogger(FreeMakerConfig.class.getName());

	private static Configuration singletonCfg = null;

	private FreeMakerConfig() {}
	
	/**
	 * Get singleton instance of FreeeMaker configuration Object
	 * @return
	 */
	public static Configuration getInstance() {
		if(singletonCfg == null) {
			singletonCfg = new Configuration();
			
			// Specify the data source where the template files come from. Here I set a
			// plain directory for it, but non-file-system are possible too:
			try {
				singletonCfg.setDirectoryForTemplateLoading(new File(properties.getProperty("ftlFolderPath")));
			} catch (IOException e) {
				log.error("Can't open FTL templates folder");
			}

			// Specify how templates will see the data-model.
			singletonCfg.setObjectWrapper(new DefaultObjectWrapper());

			// Set your preferred charset template files are stored in. UTF-8 is
			// a good choice in most applications:
			singletonCfg.setDefaultEncoding("UTF-8");

			// Sets how errors will appear. Here we assume we are developing HTML pages.
			// For production systems TemplateExceptionHandler.RETHROW_HANDLER is better.
			singletonCfg.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);

			// At least in new projects, specify that you want the fixes that aren't
			// 100% backward compatible too (these are very low-risk changes as far as the
			// 1st and 2nd version number remains):
			singletonCfg.setIncompatibleImprovements(new Version(2, 3, 20));  // FreeMarker 2.3.20
		}
		return singletonCfg;
	}

	
}
