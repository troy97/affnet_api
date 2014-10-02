package eu.ibutler.affiliatenetwork.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.apache.log4j.Logger;

import com.sun.net.httpserver.HttpExchange;

import eu.ibutler.affiliatenetwork.dao.UserDao;
import eu.ibutler.affiliatenetwork.dao.exceptions.DbAccessException;
import eu.ibutler.affiliatenetwork.dao.exceptions.NoSuchEntityException;
import eu.ibutler.affiliatenetwork.dao.impl.UserDaoImpl;
import eu.ibutler.affiliatenetwork.entity.LinkUtils;
import eu.ibutler.affiliatenetwork.entity.LoginAndPassword;
import eu.ibutler.affiliatenetwork.entity.Encrypter;
import eu.ibutler.affiliatenetwork.entity.User;
import eu.ibutler.affiliatenetwork.session.HttpSession;
import eu.ibutler.affiliatenetwork.session.SessionManager;

/**
 * This handler doesn't have any view part, it only gets credentials
 * from login controller and verifies them against those stored in DB.
 * If no match found then redirect back to login page issued.
 * If match found then redirect to upload page issued.
 * @author Anton
 *
 */
@SuppressWarnings("restriction")
public class CheckLoginController extends AbstractHttpHandler {

	private static Logger log = Logger.getLogger(CheckLoginController.class.getName());
	
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		
		try(InputStream in = exchange.getRequestBody()) {}
		
		User freshUser;
		try {
			LoginAndPassword credentials = parseQuery(exchange.getRequestURI().getQuery());
			String encryptedPassword = Encrypter.encrypt(credentials.getPassword());
			UserDao dao = new UserDaoImpl();
			freshUser = dao.login(credentials.getLogin(), encryptedPassword);
		} catch (NoSuchEntityException e) {
			log.info("Bad sign in attempt");
			//render login page again with some "Wrong login/password!" notation
			sendRedirect(exchange, LinkUtils.LOGIN_CONTROLLER_FULL_URL_REPEAT);
			return;
		} catch (DbAccessException e) {
			log.error("Database access failure");
			sendRedirect(exchange, LinkUtils.ERROR_PAGE_CONTROLLER_FULL_URL);
			return;
		}
		
		
		//login OK, create new Session and attach this user to it
		HttpSession session = (HttpSession) exchange.getAttribute(LinkUtils.EXCHANGE_SESSION_ATTR_NAME);
		SessionManager manager = SessionManager.getInstance();
		session = manager.getSession(exchange, true);
		session.setAttribute(LinkUtils.SESSION_USER_ATTR_NAME, freshUser);

		//redirect to upload page
		log.debug("Successfull login");
		sendRedirect(exchange, LinkUtils.UPLOAD_PAGE_CONTROLLER_FULL_URL);
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
		if(query == null || query.length()<("login=&password=".length()+1)) {
			log.error("No or wrong credentials provided");
			throw new NoSuchEntityException();
		}
		try {
			query = URLDecoder.decode(query, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			log.debug("URLDecoder error");
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
