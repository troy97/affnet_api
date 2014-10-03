package eu.ibutler.affiliatenetwork.entity;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;

import eu.ibutler.affiliatenetwork.controllers.CheckLoginController;
import eu.ibutler.affiliatenetwork.controllers.CheckRegisterController;
import eu.ibutler.affiliatenetwork.controllers.ErrorPageController;
import eu.ibutler.affiliatenetwork.controllers.FileDownloadController;
import eu.ibutler.affiliatenetwork.controllers.FileRequestController;
import eu.ibutler.affiliatenetwork.controllers.LoginPageController;
import eu.ibutler.affiliatenetwork.controllers.LogoutPageController;
import eu.ibutler.affiliatenetwork.controllers.RegisterPageController;
import eu.ibutler.affiliatenetwork.controllers.StatusPageController;
import eu.ibutler.affiliatenetwork.controllers.UploadPageController;
import eu.ibutler.affiliatenetwork.filters.AuthenticationFilter;
import eu.ibutler.affiliatenetwork.filters.RequestCountingFilter;
import eu.ibutler.affiliatenetwork.session.HttpSession;
import eu.ibutler.affiliatenetwork.session.ValidationFilter;

/**
 * Entry point to Affiliate network service
 * Http server is started and configured here
 * @author Anton Lukashchuk
 *
 */
@SuppressWarnings("restriction")
public class MainClass {
	private static AppProperties properties = AppProperties.getInstance();
	private static final long START_TIME = System.currentTimeMillis();
	private static Logger log = Logger.getLogger(StatusPageController.class.getName());
	
	public static void main(String[] args) throws IOException {
		
		InetSocketAddress serverAddress = new InetSocketAddress("localhost", 8080);
		HttpServer server = HttpServer.create(serverAddress, 8);
		
		long sessionDefaultInactiveTimer = Long.valueOf(properties.getProperty("maxInactiveInterval"))*60*1000;
		HttpSession.setDefaultSessionInactiveInterval(sessionDefaultInactiveTimer);
		
		//url-mapping
		HttpContext statusPageContext = server.createContext(LinkUtils.STATUS_PAGE_URL, new StatusPageController(START_TIME));
		statusPageContext.getFilters().add(new RequestCountingFilter());
		statusPageContext.getFilters().add(new ValidationFilter());
		statusPageContext.getFilters().add(new AuthenticationFilter());
		
		HttpContext registerPageContext = server.createContext(LinkUtils.REGISTER_PAGE_URL, new RegisterPageController());
		registerPageContext.getFilters().add(new RequestCountingFilter());
		//no AuthorithationFilter
		
		HttpContext checkRegisterContext = server.createContext(LinkUtils.CHECK_REGISTER_URL, new CheckRegisterController());
		checkRegisterContext.getFilters().add(new RequestCountingFilter());
		//no AuthorithationFilter
		
		HttpContext fileRequestContext = server.createContext(LinkUtils.FILE_REQUEST_CONTROLLER_URL, new FileRequestController());
		fileRequestContext.getFilters().add(new RequestCountingFilter());
		//no AuthorithationFilter
		
		HttpContext loginPageContext = server.createContext(LinkUtils.LOGIN_PAGE_URL, new LoginPageController());
		loginPageContext.getFilters().add(new RequestCountingFilter());
		loginPageContext.getFilters().add(new ValidationFilter());
		loginPageContext.getFilters().add(new AuthenticationFilter());
		
		HttpContext logoutPageContext = server.createContext(LinkUtils.LOGOUT_PAGE_URL, new LogoutPageController());
		logoutPageContext.getFilters().add(new RequestCountingFilter());
		logoutPageContext.getFilters().add(new ValidationFilter());
		logoutPageContext.getFilters().add(new AuthenticationFilter());
		
		HttpContext checkLoginContext = server.createContext(LinkUtils.CHECK_LOGIN_URL, new CheckLoginController());
		checkLoginContext.getFilters().add(new RequestCountingFilter());
		checkLoginContext.getFilters().add(new ValidationFilter());
		checkLoginContext.getFilters().add(new AuthenticationFilter());
		
		HttpContext uploadPageContext = server.createContext(LinkUtils.UPLOAD_PAGE_URL, new UploadPageController());
		uploadPageContext.getFilters().add(new RequestCountingFilter());
		uploadPageContext.getFilters().add(new ValidationFilter());
		uploadPageContext.getFilters().add(new AuthenticationFilter());
		
		HttpContext downloadControllerContext = server.createContext(LinkUtils.DOWNLOAD_CONTROLLER_URL, new FileDownloadController());
		downloadControllerContext.getFilters().add(new RequestCountingFilter());
		downloadControllerContext.getFilters().add(new ValidationFilter());
		downloadControllerContext.getFilters().add(new AuthenticationFilter());
		
		HttpContext errorPageContext = server.createContext(LinkUtils.ERROR_PAGE_URL, new ErrorPageController());
		errorPageContext.getFilters().add(new RequestCountingFilter());
		//no AuthorithationFilter
		
		//to do: maybe better to limit maximum thread number 
		server.setExecutor(Executors.newCachedThreadPool());
		server.start();
		log.info("HttpServer has started");
	}
	
}
