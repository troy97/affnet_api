package eu.ibutler.affiliatenetwork.controllers;

import java.util.Map;

import eu.ibutler.affiliatenetwork.config.AppConfig;

public class Links {
	
	//FTLs
	public static final String LOGIN_PAGE_FTL = "loginPage.ftl";
	public static final String REGISTER_PAGE_FTL = "registerPage.ftl";
	public static final String ERROR_PAGE_FTL = "errorPage.ftl";
	public static final String DOWNLOAD_SUCCESS_FTL = "downloadSuccess.ftl";
	public static final String UPLOAD_PAGE_FTL = "uploadPage.ftl";
	public static final String SIGNUP_PAGE_FTL = "signUpPage.ftl";
	public static final String SIGNIN_PAGE_FTL = "signInPage.ftl";
	public static final String USER_CABINET_FTL = "userCabinetPage.ftl";
	public static final String UPDATE_USER_PROFILE_FTL = "updateProfile.ftl";
	public static final String UPDATE_PROFILE_SUCCESS_FTL = "updateProfileSuccess.ftl";
	public static final String VIEW_LAST_FILES_FTL = "viewLastFiles.ftl";
	public static final String LOGOUT_PAGE_FTL = "logOutPage.ftl";
	
	
	//attribute names
	   //for exchange object
	public static final String EXCHANGE_SESSION_ATTR_NAME = "session";
	public static final String EXCHANGE_CLICK_COUNT_ATTR_NAME = "clickCount";
	   //for session object
	public static final String SESSION_USER_ATTR_NAME = "user";
	
	//query parameter names
		//credentials
	public static final String EMAIL_PARAM_NAME = "email";
	public static final String PASSWORD_PARAM_NAME = "password";
	public static final String NAME_PARAM_NAME = "name";
	public static final String FIRST_NAME_PARAM_NAME = "nameFirst";
	public static final String LAST_NAME_PARAM_NAME = "nameLast";
	public static final String SHOP_NAME_PARAM_NAME = "shopName";
	public static final String SHOP_URL_PARAM_NAME = "shopUrl";
		//params that indicate errors
	public static final String ERROR_PARAM_NAME = "wrong";
	public static final String INVALID_FILE_PARAM_NAME = "invalidFile";
	public static final String DUPLICATE_USER_PARAM_NAME = "duplicateUser";
	public static final String DUPLICATE_SHOP_PARAM_NAME = "duplicateShop";
		//params for distributor links
	public static final String PRODUCT_ID_PARAM_NAME = "productId";
	public static final String DISTRIBUTOR_ID_PARAM_NAME = "distributorId";
	public static final String DISTRIBUTOR_SUB_ID_PARAM_NAME = "distributorSubId";
	
	
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
	 * Creates string of type "?paramName1=true&paramName2=true..."
	 * @param paramNames
	 * @return
	 */
	public static String createQueryString(Map<String, String> data) {
		StringBuilder result = new StringBuilder("?");
		for(Map.Entry<String, String> entry : data.entrySet()) {
			result.append(entry.getKey() + "=" + entry.getValue() + "&");
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
