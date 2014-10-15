package eu.ibutler.affiliatenetwork.utils.csv;

import eu.ibutler.affiliatenetwork.utils.AppConfig;

/**
 * This class checks recently uploaded file for validity
 * @author Anton Lukashchuk
 *
 */
public class FileValidator {
	
	private static AppConfig cfg = AppConfig.getInstance();

	private String header = cfg.get("csvHeader"); 
	
	public boolean isValid(String path) {
		return true;
	}
	
}
