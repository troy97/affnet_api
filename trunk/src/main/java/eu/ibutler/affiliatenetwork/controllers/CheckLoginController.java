package eu.ibutler.affiliatenetwork.controllers;

import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;

import com.sun.net.httpserver.HttpExchange;

import eu.ibutler.affiliatenetwork.dao.UserDao;
import eu.ibutler.affiliatenetwork.dao.exceptions.DbAccessException;
import eu.ibutler.affiliatenetwork.dao.exceptions.NoSuchEntityException;
import eu.ibutler.affiliatenetwork.dao.impl.UserDaoImpl;
import eu.ibutler.affiliatenetwork.entity.LinkUtils;
import eu.ibutler.affiliatenetwork.entity.LoginAndPassword;
import eu.ibutler.affiliatenetwork.entity.Encrypter;

/**
 * This handler doesn't have any view part, it only gets credentials
 * from login controller and verifies them with those stored in DB
 * If no match found then redirect back to login page issued
 * If match found then redirect to upload page issued
 * @author Anton
 *
 */
@SuppressWarnings("restriction")
public class CheckLoginController extends AbstractHttpHandler {

	private static final String UPLOAD_CONTROLLER_REDIRECT_URL = "http://localhost:8080/affiliatenetwork/upload";
	private static final String LOGIN_CONTROLLER_REDIRECT_URL = "http://localhost:8080/affiliatenetwork/login?wrong=true";
	
	private static Logger log = Logger.getLogger(CheckLoginController.class.getName());
	
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		
		try(InputStream in = exchange.getRequestBody()) {}
		
		try {
			LoginAndPassword credentials = parseQuery(exchange.getRequestURI().getQuery());
			String encryptedPassword = Encrypter.encrypt(credentials.getPassword());
			UserDao dao = new UserDaoImpl();
			dao.login(credentials.getLogin(), encryptedPassword);
		} catch (NoSuchEntityException e) {
			log.info("Bad sign in attempt");
			//render login page again with some "Wrong login/password!" notation
			sendRedirect(exchange, LOGIN_CONTROLLER_REDIRECT_URL);
			return;
		} catch (DbAccessException e) {
			log.error("Database access failure");
			sendRedirect(exchange, LinkUtils.ERROR_PAGE_CONTROLLER_FULL_URL);
			return;
		}
		
		//login OK, create Session for this user
		//and redirect to upload page
		log.debug("Successfull login");
		sendRedirect(exchange, UPLOAD_CONTROLLER_REDIRECT_URL);
		return;
	}
	
	/**
	 * !!!!MUST BE changed to POST!!!!
	 * Parse query string for login and password
	 * @param query
	 * @return
	 * @throws NoSuchEntityException
	 */
	private LoginAndPassword parseQuery(String query) throws NoSuchEntityException {
		//query string can't be shorter than "login=&password=".length()+1
		if(query == null || query.length()<("login=&password=".length()+1)) {
			log.error("No or wrong credentials provided");
			throw new NoSuchEntityException();
		}
		String login;
		String password;
		try {
			String[] arr = query.split("&");
			login = (arr[0].split("="))[1];
			password = (arr[1].split("="))[1];
		} catch (Exception e) {
			log.debug("Can't parse query");
			throw new NoSuchEntityException();
		}
		
		return new LoginAndPassword(login, password);
	}

}
