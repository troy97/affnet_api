package eu.ibutler.affiliatenetwork;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.Filter;

import eu.ibutler.affiliatenetwork.controllers.AdminUploadPageController;
import eu.ibutler.affiliatenetwork.controllers.CheckLoginController;
import eu.ibutler.affiliatenetwork.controllers.CheckRegisterController;
import eu.ibutler.affiliatenetwork.controllers.CheckSignInController;
import eu.ibutler.affiliatenetwork.controllers.CheckSignUpController;
import eu.ibutler.affiliatenetwork.controllers.CheckUpdateProfileController;
import eu.ibutler.affiliatenetwork.controllers.ErrorPageController;
import eu.ibutler.affiliatenetwork.controllers.FileDownloadController;
import eu.ibutler.affiliatenetwork.controllers.FileRequestController;
import eu.ibutler.affiliatenetwork.controllers.LoginPageController;
import eu.ibutler.affiliatenetwork.controllers.LogoutPageController;
import eu.ibutler.affiliatenetwork.controllers.RegisterPageController;
import eu.ibutler.affiliatenetwork.controllers.SignInPageController;
import eu.ibutler.affiliatenetwork.controllers.SignUpPageController;
import eu.ibutler.affiliatenetwork.controllers.StatusPageController;
import eu.ibutler.affiliatenetwork.controllers.UpdateUserProfileController;
import eu.ibutler.affiliatenetwork.controllers.UploadPageController;
import eu.ibutler.affiliatenetwork.controllers.UserCabinetPageController;
import eu.ibutler.affiliatenetwork.controllers.ViewLastFilesPageController;
import eu.ibutler.affiliatenetwork.filters.AuthenticationFilter;
import eu.ibutler.affiliatenetwork.filters.RequestCountingFilter;
import eu.ibutler.affiliatenetwork.http.session.HttpSession;
import eu.ibutler.affiliatenetwork.http.session.ValidationFilter;
import eu.ibutler.affiliatenetwork.utils.AppConfig;

/**
 * Entry point to Affiliate network service
 * Http server is started and configured here
 * @author Anton Lukashchuk
 *
 */
@SuppressWarnings("restriction")
public class MainClass {
	private static final long START_TIME = System.currentTimeMillis();
	
	private static AppConfig cfg = AppConfig.getInstance();
	private static Logger log = Logger.getLogger(StatusPageController.class.getName());
	
	public static void main(String[] args) throws IOException {
		
		InetSocketAddress serverAddress = new InetSocketAddress("localhost", 8080);
		HttpServer server = HttpServer.create(serverAddress, 8);
		
		long sessionDefaultInactiveTimer = Long.valueOf(cfg.get("maxInactiveInterval"))*60*1000;
		HttpSession.setDefaultSessionInactiveInterval(sessionDefaultInactiveTimer);
		
		//url-mapping
		HttpContext statusPageContext = server.createContext(cfg.get("STATUS_PAGE_URL"), new StatusPageController(START_TIME));
		statusPageContext.getFilters().add(new RequestCountingFilter());
		statusPageContext.getFilters().add(new ValidationFilter());
		statusPageContext.getFilters().add(new AuthenticationFilter());
		
		HttpContext registerPageContext = server.createContext(cfg.get("REGISTER_PAGE_URL"), new RegisterPageController());
		registerPageContext.getFilters().add(new RequestCountingFilter());
		//no AuthorithationFilter
		
		HttpContext checkRegisterContext = server.createContext(cfg.get("CHECK_REGISTER_URL"), new CheckRegisterController());
		checkRegisterContext.getFilters().add(new RequestCountingFilter());
		//no AuthorithationFilter
		
		HttpContext signUpContext = server.createContext(cfg.get("SIGNUP_PAGE_URL"), new SignUpPageController());
		signUpContext.getFilters().add(new RequestCountingFilter());
		//no AuthorithationFilter
		
		HttpContext checkSignUpContext = server.createContext(cfg.get("CHECK_SIGNUP_URL"), new CheckSignUpController());
		checkSignUpContext.getFilters().add(new RequestCountingFilter());
		//no AuthorithationFilter
		
		HttpContext fileRequestContext = server.createContext(cfg.get("FILE_REQUEST_CONTROLLER_URL"), new FileRequestController());
		fileRequestContext.getFilters().add(new RequestCountingFilter());
		//no AuthorithationFilter
		
		HttpContext loginPageContext = server.createContext(cfg.get("LOGIN_PAGE_URL"), new LoginPageController());
		loginPageContext.getFilters().add(new RequestCountingFilter());
		//no AuthorithationFilter
		
		HttpContext signInContext = server.createContext(cfg.get("SIGNIN_PAGE_URL"), new SignInPageController());
		signInContext.getFilters().add(new RequestCountingFilter());
		//no AuthorithationFilter
		
		HttpContext checkSignInContext = server.createContext(cfg.get("CHECK_SIGNIN_URL"), new CheckSignInController());
		checkSignInContext.getFilters().add(new RequestCountingFilter());
		//no AuthorithationFilter
		
		HttpContext logoutPageContext = server.createContext(cfg.get("LOGOUT_PAGE_URL"), new LogoutPageController());
		logoutPageContext.getFilters().add(new RequestCountingFilter());
		logoutPageContext.getFilters().add(new ValidationFilter());
		logoutPageContext.getFilters().add(new AuthenticationFilter());
		
		HttpContext checkLoginContext = server.createContext(cfg.get("CHECK_LOGIN_URL"), new CheckLoginController());
		checkLoginContext.getFilters().add(new RequestCountingFilter());
		//no AuthorithationFilter
		
		HttpContext uploadPageContext = server.createContext(cfg.get("UPLOAD_PAGE_URL"), new UploadPageController());
		uploadPageContext.getFilters().add(new RequestCountingFilter());
		uploadPageContext.getFilters().add(new ValidationFilter());
		uploadPageContext.getFilters().add(new AuthenticationFilter());
		
		HttpContext adminUploadPageContext = server.createContext(cfg.get("ADMIN_UPLOAD_PAGE_URL"), new AdminUploadPageController());
		adminUploadPageContext.getFilters().add(new RequestCountingFilter());
		adminUploadPageContext.getFilters().add(new ValidationFilter());
		adminUploadPageContext.getFilters().add(new AuthenticationFilter());
		
		HttpContext downloadControllerContext = server.createContext(cfg.get("DOWNLOAD_CONTROLLER_URL"), new FileDownloadController());
		downloadControllerContext.getFilters().add(new RequestCountingFilter());
		downloadControllerContext.getFilters().add(new ValidationFilter());
		downloadControllerContext.getFilters().add(new AuthenticationFilter());
		
		HttpContext errorPageContext = server.createContext(cfg.get("ERROR_PAGE_URL"), new ErrorPageController());
		errorPageContext.getFilters().add(new RequestCountingFilter());
		//no AuthorithationFilter
		
		HttpContext userCabinetControllerContext = server.createContext(cfg.get("USER_CABINET_PAGE_URL"), new UserCabinetPageController());
		userCabinetControllerContext.getFilters().add(new RequestCountingFilter());
		userCabinetControllerContext.getFilters().add(new ValidationFilter());
		userCabinetControllerContext.getFilters().add(new AuthenticationFilter());
		
		HttpContext updateProfileControllerContext = server.createContext(cfg.get("UPDATE_USER_PROFILE_PAGE_URL"), new UpdateUserProfileController());
		updateProfileControllerContext.getFilters().add(new RequestCountingFilter());
		updateProfileControllerContext.getFilters().add(new ValidationFilter());
		updateProfileControllerContext.getFilters().add(new AuthenticationFilter());
		
		HttpContext checkUpdateProfileControllerContext = server.createContext(cfg.get("CHECK_UPDATE_PROFILE_URL"), new CheckUpdateProfileController());
		checkUpdateProfileControllerContext.getFilters().add(new RequestCountingFilter());
		checkUpdateProfileControllerContext.getFilters().add(new ValidationFilter());
		checkUpdateProfileControllerContext.getFilters().add(new AuthenticationFilter());
		
		HttpContext viewLastFilesControllerContext = server.createContext(cfg.get("VIEW_LAST_FILES_PAGE_URL"), new ViewLastFilesPageController());
		viewLastFilesControllerContext.getFilters().add(new RequestCountingFilter());
		viewLastFilesControllerContext.getFilters().add(new ValidationFilter());
		viewLastFilesControllerContext.getFilters().add(new AuthenticationFilter());
		
		//to do: maybe better to limit maximum thread number 
		server.setExecutor(Executors.newCachedThreadPool());
		server.start();
		log.info("HttpServer has started");
	}
	
}
