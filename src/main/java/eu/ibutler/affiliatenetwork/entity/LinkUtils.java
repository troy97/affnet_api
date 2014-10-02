package eu.ibutler.affiliatenetwork.entity;


public class LinkUtils {
	
	//url-mapping
	public static final String STATUS_PAGE_URL = "/status";
	public static final String UPLOAD_PAGE_URL = "/upload";
	public static final String DOWNLOAD_CONTROLLER_URL = "/download";
	public static final String REGISTER_PAGE_URL = "/register";
	public static final String CHECK_REGISTER_URL = "/checkRegister";
	public static final String LOGIN_PAGE_URL = "/login";
	public static final String LOGOUT_PAGE_URL = "/logout";
	public static final String CHECK_LOGIN_URL = "/checkLogin"; 
	public static final String ERROR_PAGE_URL = "/error";
	public static final String FILE_REQUEST_CONTROLLER_URL = "/";

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
	
	//repeat redirect
	public static final String LOGIN_CONTROLLER_FULL_URL_REPEAT = "http://localhost:8080/login?wrong=true";
	public static final String UPLOAD_CONTROLLER_FULL_URL_REPEAT = "http://localhost:8080/upload?wrong=true";
	
	//ftls
	public static final String LOGIN_PAGE_FTL = "loginPage.ftl";
	public static final String REGISTER_PAGE_FTL = "registerPage.ftl";
	public static final String ERROR_PAGE_FTL = "errorPage.ftl";
	public static final String DOWNLOAD_SUCCESS_FTL = "downloadSuccess.ftl";
	public static final String UPLOAD_PAGE_FTL = "uploadPage.ftl";
	
	//attribute names
	public static final String EXCHANGE_SESSION_ATTR_NAME = "session";
	public static final String SESSION_USER_ATTR_NAME = "user";
	
	

	

	
	
	
	

	
}
