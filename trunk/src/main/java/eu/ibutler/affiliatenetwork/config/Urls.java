package eu.ibutler.affiliatenetwork.config;

import eu.ibutler.affiliatenetwork.controllers.AdminUploadPageController;
import eu.ibutler.affiliatenetwork.controllers.CheckLoginController;
import eu.ibutler.affiliatenetwork.controllers.CheckRegisterController;
import eu.ibutler.affiliatenetwork.controllers.CheckSignInController;
import eu.ibutler.affiliatenetwork.controllers.CheckSignUpController;
import eu.ibutler.affiliatenetwork.controllers.CheckUpdateProfileController;
import eu.ibutler.affiliatenetwork.controllers.DistributorClickController;
import eu.ibutler.affiliatenetwork.controllers.DistributorFileRequestController;
import eu.ibutler.affiliatenetwork.controllers.DistributorGetActiveFileListController;
import eu.ibutler.affiliatenetwork.controllers.ErrorPageController;
import eu.ibutler.affiliatenetwork.controllers.FileDownloadController;
import eu.ibutler.affiliatenetwork.controllers.LoginPageController;
import eu.ibutler.affiliatenetwork.controllers.LogoutPageController;
import eu.ibutler.affiliatenetwork.controllers.RegisterPageController;
import eu.ibutler.affiliatenetwork.controllers.SignInPageController;
import eu.ibutler.affiliatenetwork.controllers.SignUpPageController;
import eu.ibutler.affiliatenetwork.controllers.StatusPageController;
import eu.ibutler.affiliatenetwork.controllers.UiFileRequestController;
import eu.ibutler.affiliatenetwork.controllers.UpdateUserProfileController;
import eu.ibutler.affiliatenetwork.controllers.UploadPageController;
import eu.ibutler.affiliatenetwork.controllers.UserCabinetPageController;
import eu.ibutler.affiliatenetwork.controllers.ViewLastFilesPageController;
import eu.ibutler.affiliatenetwork.controllers.WebController;
import eu.ibutler.affiliatenetwork.http.parse.exceptions.DownloadErrorException;


public class Urls {

	private static AppConfig cfg = AppConfig.getInstance();
	
	//service root name
	public static final String DOMAIN_NAME = cfg.getWithEnv("DOMAIN_NAME");
	
	//url-mapping
	public static final String STATUS_PAGE_URL = StatusPageController.class.getAnnotation(WebController.class).value();
	public static final String UPLOAD_PAGE_URL = UploadPageController.class.getAnnotation(WebController.class).value();
	public static final String ADMIN_UPLOAD_PAGE_URL = AdminUploadPageController.class.getAnnotation(WebController.class).value();
	public static final String DOWNLOAD_CONTROLLER_URL = FileDownloadController.class.getAnnotation(WebController.class).value();
	public static final String REGISTER_PAGE_URL = RegisterPageController.class.getAnnotation(WebController.class).value();
	public static final String SIGNUP_PAGE_URL = SignUpPageController.class.getAnnotation(WebController.class).value();
	public static final String SIGNIN_PAGE_URL = SignInPageController.class.getAnnotation(WebController.class).value();
	public static final String CHECK_REGISTER_URL = CheckRegisterController.class.getAnnotation(WebController.class).value();
	public static final String CHECK_SIGNUP_URL = CheckSignUpController.class.getAnnotation(WebController.class).value();
	public static final String CHECK_SIGNIN_URL = CheckSignInController.class.getAnnotation(WebController.class).value();
	public static final String CHECK_LOGIN_URL = CheckLoginController.class.getAnnotation(WebController.class).value();
	public static final String LOGIN_PAGE_URL = LoginPageController.class.getAnnotation(WebController.class).value();
	public static final String LOGOUT_PAGE_URL = LogoutPageController.class.getAnnotation(WebController.class).value();
	public static final String ERROR_PAGE_URL = ErrorPageController.class.getAnnotation(WebController.class).value();
	public static final String UI_FILE_REQUEST_CONTROLLER_URL = UiFileRequestController.class.getAnnotation(WebController.class).value();
	public static final String USER_CABINET_PAGE_URL = UserCabinetPageController.class.getAnnotation(WebController.class).value();
	public static final String UPDATE_USER_PROFILE_PAGE_URL = UpdateUserProfileController.class.getAnnotation(WebController.class).value();
	public static final String CHECK_UPDATE_PROFILE_URL = CheckUpdateProfileController.class.getAnnotation(WebController.class).value();
	public static final String VIEW_LAST_FILES_PAGE_URL = ViewLastFilesPageController.class.getAnnotation(WebController.class).value();
	public static final String DISTRIBUTOR_CLICK_URL = DistributorClickController.class.getAnnotation(WebController.class).value();
	public static final String DISTRIBUTOR_FILE_REQUEST_CONTROLLER_URL = DistributorFileRequestController.class.getAnnotation(WebController.class).value();
	public static final String DISTRIBUTOR_GET_ACTIVE_FILES_CONTROLLER_URL = DistributorGetActiveFileListController.class.getAnnotation(WebController.class).value();
	
	
	/**
	 * Adds DOMAIN_NAME in front of given string
	 * @param url
	 * @return DOMAIN_NAME + relative
	 */
	public static String fullURL(String url) {
		return DOMAIN_NAME + url;
	}
	
}
