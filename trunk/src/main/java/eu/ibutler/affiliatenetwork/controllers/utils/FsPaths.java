package eu.ibutler.affiliatenetwork.controllers.utils;

import eu.ibutler.affiliatenetwork.config.AppConfig;

public class FsPaths {

	private static AppConfig cfg = AppConfig.getInstance();
	//private static Logger logger = Logger.getLogger(FsPaths.class.getName());
	
	public static final String UPLOAD_PATH = cfg.getWithEnv("uploadPath");
	public static final String FILE_TEMPLATES_PATH = cfg.getWithEnv("fileTemplatesPath");
	public static final String WEB_CONTENT_PATH = cfg.getWithEnv("WebContentPath");
	public static final String LOGGER_CONFIG_PATH = cfg.getWithEnv("logginConfigPath");
}
