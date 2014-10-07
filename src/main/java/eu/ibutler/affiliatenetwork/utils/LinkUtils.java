package eu.ibutler.affiliatenetwork.utils;



public class LinkUtils {
	
	private static AppConfig properties = AppConfig.getInstance();
	
	//url-mapping
	public static final String STATUS_PAGE_URL = "/status";
	public static final String UPLOAD_PAGE_URL = "/upload";
	public static final String DOWNLOAD_CONTROLLER_URL = "/download";
	public static final String REGISTER_PAGE_URL = "/register";
	public static final String SIGNUP_PAGE_URL = "/signup";
	public static final String SIGNIN_PAGE_URL = "/signin";
	public static final String CHECK_REGISTER_URL = "/checkRegister";
	public static final String CHECK_SIGNUP_URL = "/checkSignup";
	public static final String CHECK_SIGNIN_URL = "/checkSignin";
	public static final String CHECK_LOGIN_URL = "/checkLogin"; 
	public static final String LOGIN_PAGE_URL = properties.get("LOGIN_PAGE_URL");
	public static final String LOGOUT_PAGE_URL = "/logout";
	public static final String ERROR_PAGE_URL = "/error";
	public static final String FILE_REQUEST_CONTROLLER_URL = "/";

	public static final String DOMAIN_NAME = "http://localhost:8080";
	
	//full url's for redirecting
	public static final String ERROR_PAGE_CONTROLLER_FULL_URL = "http://localhost:8080/error";
	public static final String LOGIN_PAGE_CONTROLLER_FULL_URL = "http://localhost:8080/login";
	public static final String LOGOUT_PAGE_CONTROLLER_FULL_URL = "http://localhost:8080/logout";
	public static final String UPLOAD_PAGE_CONTROLLER_FULL_URL = "http://localhost:8080/upload";
	public static final String STATUS_PAGE_CONTROLLER_FULL_URL = "http://localhost:8080/status";
	public static final String DOWNLOAD_PAGE_CONTROLLER_FULL_URL = "http://localhost:8080/download";
	public static final String CHECK_REGISTER_CONTROLLER_FULL_URL = "http://localhost:8080/checkRegister";
	public static final String CHECK_LOGIN_CONTROLLER_FULL_URL = "http://localhost:8080/checkLogin";
	public static final String REGISTER_PAGE_CONTROLLER_FULL_URL = "http://localhost:8080/register";
	public static final String SIGNUP_PAGE_CONTROLLER_FULL_URL = "http://localhost:8080/signup";
	public static final String CHECK_SIGNUP_CONTROLLER_FULL_URL = "http://localhost:8080/checkSignup";
	public static final String CHECK_SIGNIN_CONTROLLER_FULL_URL = "http://localhost:8080/checkSignin";
	
	//ftls
	public static final String LOGIN_PAGE_FTL = "loginPage.ftl";
	public static final String REGISTER_PAGE_FTL = "registerPage.ftl";
	public static final String ERROR_PAGE_FTL = "errorPage.ftl";
	public static final String DOWNLOAD_SUCCESS_FTL = "downloadSuccess.ftl";
	public static final String UPLOAD_PAGE_FTL = "uploadPage.ftl";
	public static final String SIGNUP_PAGE_FTL = "signUpPage.ftl";
	public static final String SIGNIN_PAGE_FTL = "signInPage.ftl";
	
	//attribute names
	public static final String EXCHANGE_SESSION_ATTR_NAME = "session";
	public static final String SESSION_USER_ATTR_NAME = "user";
	
	//parameter names
	public static final String EMAIL_PARAM = "email";
	public static final String PASSWORD_PARAM = "password";
	public static final String NAME_PARAM = "name";
	public static final String FIRST_NAME_PARAM = "nameFirst";
	public static final String LAST_NAME_PARAM = "nameLast";
	public static final String SHOP_NAME_PARAM = "shopName";
	public static final String SHOP_URL_PARAM = "shopUrl";
	
	public static final String WRONG_PARAM = "wrong";
	public static final String DUPLICATE_USER_PARAM = "duplicateUser";
	public static final String DUPLICATE_SHOP_PARAM = "duplicateShop";
	
	public static String createQueryString(String... paramNames) {
		StringBuilder result = new StringBuilder("?");
		for(String name : paramNames) {
			result.append(name + "=true&");
		}
		result.deleteCharAt(result.length()-1); //delete last "&"
		return result.toString();
	}
	

	
}
