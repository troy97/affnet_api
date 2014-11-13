package eu.ibutler.affiliatenetwork.config;

import eu.ibutler.affiliatenetwork.controllers.DistributorClickController;
import eu.ibutler.affiliatenetwork.controllers.DistributorGetProductFileController;
import eu.ibutler.affiliatenetwork.controllers.StatusPageController;
import eu.ibutler.affiliatenetwork.controllers.WebController;


public class Urls {

	private static AppConfig cfg = AppConfig.getInstance();
	
	//service root name
	public static final String DOMAIN_NAME = cfg.getWithEnv("DOMAIN_NAME");
	
	//url-mapping
	public static final String STATUS_PAGE_URL = StatusPageController.class.getAnnotation(WebController.class).value();
	public static final String DISTRIBUTOR_CLICK_URL = DistributorClickController.class.getAnnotation(WebController.class).value();

	public static final String DISTRIBUTOR_FILE_REQUEST_CONTROLLER_URL = DistributorGetProductFileController.class.getAnnotation(WebController.class).value();;
	
	
	/**
	 * Adds DOMAIN_NAME in front of given string
	 * @param url
	 * @return DOMAIN_NAME + relative
	 */
	public static String fullURL(String url) {
		return DOMAIN_NAME + url;
	}
	
}
