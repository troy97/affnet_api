package eu.ibutler.affiliatenetwork.config;


public class Urls {

	private static AppConfig cfg = AppConfig.getInstance();
	
	//service root name
	public static final String DOMAIN_NAME = cfg.getWithEnv("DOMAIN_NAME");
	
	//url-mapping
	public static final String STATUS_PAGE_URL = cfg.get("STATUS_PAGE_URL");
	public static final String UPLOAD_PAGE_URL = cfg.get("UPLOAD_PAGE_URL");
	public static final String ADMIN_UPLOAD_PAGE_URL = cfg.get("ADMIN_UPLOAD_PAGE_URL");
	public static final String DOWNLOAD_CONTROLLER_URL = cfg.get("DOWNLOAD_CONTROLLER_URL");
	public static final String REGISTER_PAGE_URL = cfg.get("REGISTER_PAGE_URL");
	public static final String SIGNUP_PAGE_URL = cfg.get("SIGNUP_PAGE_URL");
	public static final String SIGNIN_PAGE_URL = cfg.get("SIGNIN_PAGE_URL");
	public static final String CHECK_REGISTER_URL = cfg.get("CHECK_REGISTER_URL");
	public static final String CHECK_SIGNUP_URL = cfg.get("CHECK_SIGNUP_URL");
	public static final String CHECK_SIGNIN_URL = cfg.get("CHECK_SIGNIN_URL");
	public static final String CHECK_LOGIN_URL = cfg.get("CHECK_LOGIN_URL");
	public static final String LOGIN_PAGE_URL = cfg.get("LOGIN_PAGE_URL");
	public static final String LOGOUT_PAGE_URL = cfg.get("LOGOUT_PAGE_URL");
	public static final String ERROR_PAGE_URL = cfg.get("ERROR_PAGE_URL");
	public static final String FILE_REQUEST_CONTROLLER_URL = cfg.get("FILE_REQUEST_CONTROLLER_URL");
	public static final String USER_CABINET_PAGE_URL = cfg.get("USER_CABINET_PAGE_URL");
	public static final String UPDATE_USER_PROFILE_PAGE_URL = cfg.get("UPDATE_USER_PROFILE_PAGE_URL");
	public static final String CHECK_UPDATE_PROFILE_URL = cfg.get("CHECK_UPDATE_PROFILE_URL");
	public static final String VIEW_LAST_FILES_PAGE_URL = cfg.get("VIEW_LAST_FILES_PAGE_URL");
	public static final String AFFILIATE_CLICK_URL = cfg.get("AFFILIATE_CLICK_URL");
	
	
	/**
	 * Adds DOMAIN_NAME in front of given string
	 * @param url
	 * @return DOMAIN_NAME + relative
	 */
	public static String fullURL(String url) {
		return DOMAIN_NAME + url;
	}
	
}
