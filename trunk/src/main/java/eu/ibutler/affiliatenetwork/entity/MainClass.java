package eu.ibutler.affiliatenetwork.entity;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;

import eu.ibutler.affiliatenetwork.controllers.CheckLoginController;
import eu.ibutler.affiliatenetwork.controllers.ErrorPageController;
import eu.ibutler.affiliatenetwork.controllers.FileDownloadController;
import eu.ibutler.affiliatenetwork.controllers.LoginPageController;
import eu.ibutler.affiliatenetwork.controllers.StatusPageController;
import eu.ibutler.affiliatenetwork.controllers.UploadPageController;
import eu.ibutler.affiliatenetwork.dao.FileDao;
import eu.ibutler.affiliatenetwork.dao.ShopDao;
import eu.ibutler.affiliatenetwork.dao.UserDao;
import eu.ibutler.affiliatenetwork.dao.exceptions.DbAccessException;
import eu.ibutler.affiliatenetwork.dao.exceptions.UniqueConstraintViolationException;
import eu.ibutler.affiliatenetwork.dao.impl.FileDaoImpl;
import eu.ibutler.affiliatenetwork.dao.impl.ShopDaoImpl;
import eu.ibutler.affiliatenetwork.dao.impl.UserDaoImpl;
import eu.ibutler.affiliatenetwork.filters.RequestCountingFilter;

/**
 * Entry point to Affiliate network service
 * Http server is started and configured here
 * @author Anton Lukashchuk
 *
 */
@SuppressWarnings("restriction")
public class MainClass {
	private static final long START_TIME = System.currentTimeMillis();
	
	private static final String STATUS_PAGE_URL = "/affiliatenetwork/status";
	private static final String UPLOAD_PAGE_URL = "/affiliatenetwork/upload";
	private static final String DOWNLOAD_CONTROLLER_URL = "/affiliatenetwork/download";
	private static final String LOGIN_PAGE_URL = "/affiliatenetwork/login";
	private static final String CHECK_LOGIN_URL = "/affiliatenetwork/checkLogin"; //better move this check to a simple class, not controller, and do all redirects from login page
	private static final String ERROR_PAGE_URL = "/affiliatenetwork/error";
	
	private static Logger log = Logger.getLogger(StatusPageController.class.getName());
	
	public static void main(String[] args) throws IOException {
		
		//##DAO TEST HERE###########################################################################
/*		UserDao dao = new UserDaoImpl();
		try {
			try {
				//int id = dao.addUser(new User("Oksana", "ksu@gmail.com", "ksu", "1111"));
				int id = dao.addUser(new User("Mike", "mike@gmail.com", "mike", "1111"));
				System.out.println(id);
			} catch (UniqueConstraintViolationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (DbAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
/*		ShopDao dao = new ShopDaoImpl();
		try {
			List<Shop> shops = dao.getAllShops();
			System.out.println(shops);
		} catch (DbAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
/*		FileDao dao = new FileDaoImpl();
		try {
			List<UploadedFile> files = dao.getAllFiles();
			System.out.println(files);
		} catch (DbAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		
		//###########################################################################
		
		InetSocketAddress serverAddress = new InetSocketAddress("localhost", 8080);
		HttpServer server = HttpServer.create(serverAddress, 8);
		
		//url-mapping
		HttpContext statusPageContext = server.createContext(STATUS_PAGE_URL, new StatusPageController(START_TIME));
		statusPageContext.getFilters().add(new RequestCountingFilter());
		
		HttpContext loginPageContext = server.createContext(LOGIN_PAGE_URL, new LoginPageController());
		loginPageContext.getFilters().add(new RequestCountingFilter());
		
		HttpContext checkLoginContext = server.createContext(CHECK_LOGIN_URL, new CheckLoginController());
		checkLoginContext.getFilters().add(new RequestCountingFilter());
		
		HttpContext uploadPageContext = server.createContext(UPLOAD_PAGE_URL, new UploadPageController());
		uploadPageContext.getFilters().add(new RequestCountingFilter());
		
		HttpContext downloadControllerContext = server.createContext(DOWNLOAD_CONTROLLER_URL, new FileDownloadController());
		downloadControllerContext.getFilters().add(new RequestCountingFilter());
		
		HttpContext errorPageContext = server.createContext(ERROR_PAGE_URL, new ErrorPageController());
		errorPageContext.getFilters().add(new RequestCountingFilter());
		
		//to do: maybe better to limit maximum thread number 
		server.setExecutor(Executors.newCachedThreadPool());
		server.start();
		log.info("HttpServer has started");
	}
	
}
