package eu.ibutler.affiliatenetwork.controllers;

import eu.ibutler.affiliatenetwork.utils.AppConfig;



public class Links {
	
	private static AppConfig cfg = AppConfig.getInstance();
	
	//service root name
	public static final String DOMAIN_NAME = cfg.get("DOMAIN_NAME");
	
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

	
	//full url's for redirects
	public static final String ERROR_PAGE_CONTROLLER_FULL_URL = DOMAIN_NAME + ERROR_PAGE_URL;
	public static final String LOGIN_PAGE_CONTROLLER_FULL_URL = DOMAIN_NAME + LOGIN_PAGE_URL;
	public static final String LOGOUT_PAGE_CONTROLLER_FULL_URL = DOMAIN_NAME + LOGOUT_PAGE_URL;
	public static final String UPLOAD_PAGE_CONTROLLER_FULL_URL = DOMAIN_NAME + UPLOAD_PAGE_URL;
	public static final String STATUS_PAGE_CONTROLLER_FULL_URL = DOMAIN_NAME + STATUS_PAGE_URL;
	public static final String DOWNLOAD_PAGE_CONTROLLER_FULL_URL = DOMAIN_NAME + DOWNLOAD_CONTROLLER_URL;
	public static final String CHECK_REGISTER_CONTROLLER_FULL_URL = DOMAIN_NAME + CHECK_REGISTER_URL;
	public static final String CHECK_LOGIN_CONTROLLER_FULL_URL = DOMAIN_NAME + CHECK_LOGIN_URL;
	public static final String REGISTER_PAGE_CONTROLLER_FULL_URL = DOMAIN_NAME + REGISTER_PAGE_URL;
	public static final String SIGNUP_PAGE_CONTROLLER_FULL_URL = DOMAIN_NAME + SIGNUP_PAGE_URL;
	public static final String CHECK_SIGNUP_CONTROLLER_FULL_URL = DOMAIN_NAME + CHECK_SIGNUP_URL;
	public static final String CHECK_SIGNIN_CONTROLLER_FULL_URL = DOMAIN_NAME + CHECK_SIGNIN_URL;
	
	//FTLs
	public static final String LOGIN_PAGE_FTL = "loginPage.ftl";
	public static final String REGISTER_PAGE_FTL = "registerPage.ftl";
	public static final String ERROR_PAGE_FTL = "errorPage.ftl";
	public static final String DOWNLOAD_SUCCESS_FTL = "downloadSuccess.ftl";
	public static final String UPLOAD_PAGE_FTL = "uploadPage.ftl";
	public static final String SIGNUP_PAGE_FTL = "signUpPage.ftl";
	public static final String SIGNIN_PAGE_FTL = "signInPage.ftl";
	
	//attribute names
	   //for exchange object
	public static final String EXCHANGE_SESSION_ATTR_NAME = "session";
	public static final String EXCHANGE_CLICK_COUNT_ATTR_NAME = "clickCount";
	   //for session object
	public static final String SESSION_USER_ATTR_NAME = "user";
	
	//query parameter names
	public static final String EMAIL_PARAM_NAME = "email";
	public static final String PASSWORD_PARAM_NAME = "password";
	public static final String NAME_PARAM_NAME = "name";
	public static final String FIRST_NAME_PARAM_NAME = "nameFirst";
	public static final String LAST_NAME_PARAM_NAME = "nameLast";
	public static final String SHOP_NAME_PARAM_NAME = "shopName";
	public static final String SHOP_URL_PARAM_NAME = "shopUrl";
	
	public static final String ERROR_PARAM_NAME = "wrong";
	public static final String INVALID_FILE_PARAM_NAME = "invalidFile";
	public static final String DUPLICATE_USER_PARAM_NAME = "duplicateUser";
	public static final String DUPLICATE_SHOP_PARAM_NAME = "duplicateShop";
	
	
	/**
	 * Adds LinkUtils.DOMAIN_NAME in front of given string
	 * @param relative
	 * @return DOMAIN_NAME + relative
	 */
	public static String fullURL(String relative) {
		return DOMAIN_NAME + relative;
	}
	
	/**
	 * Creates string of type "?paramName1=true&paramName2=true..."
	 * @param paramNames
	 * @return
	 */
	public static String createQueryString(String... paramNames) {
		StringBuilder result = new StringBuilder("?");
		for(String name : paramNames) {
			result.append(name + "=true&");
		}
		result.deleteCharAt(result.length()-1); //delete last "&"
		return result.toString();
	}
	
	/**
	 * Creates html "a href" from given uri and link name
	 * @param uri
	 * @param linkName
	 * @return a tag link
	 */
	public static String wrapWithA(String uri, String linkName) {
		return "<a href=\"" + uri + "\">" + linkName + "</a>";
	}
	
	/**
	 * Strips query string from given url or does nothing
	 * if no query string was attached
	 * @param urlWithQuery
	 * @return url without query string
	 */
	public static String stripQuery(String urlWithQuery) {
		String result = urlWithQuery;
		if(urlWithQuery.contains("?")) {
			result = urlWithQuery.split("\\?")[0];
		}
		return result;
	}
	
}
