package eu.ibutler.affiliatenetwork.controllers;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;

import org.apache.log4j.Logger;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import eu.ibutler.affiliatenetwork.dao.exceptions.DbAccessException;
import eu.ibutler.affiliatenetwork.dao.exceptions.NoSuchUserException;
import eu.ibutler.affiliatenetwork.dao.impl.UserDaoMock;
import eu.ibutler.affiliatenetwork.entity.LoginAndPassword;
import eu.ibutler.affiliatenetwork.entity.PasswordEncrypter;
import eu.ibutler.affiliatenetwork.entity.User;

@SuppressWarnings("restriction")
public class CheckLoginController extends AbstractHttpHandler {

	private static final String UPLOAD_CONTROLLER_REDIRECT_URL = "http://localhost:8080/affiliatenetwork/upload";
	private static final String LOGIN_CONTROLLER_REDIRECT_URL = "http://localhost:8080/affiliatenetwork/login?wrong=true";
	
	private static Logger log = Logger.getLogger(CheckLoginController.class.getName());
	
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		
		BufferedInputStream in = new BufferedInputStream(exchange.getRequestBody());
		in.close();
		
		User user = null;
		LoginAndPassword credentials = null;
		try {
			credentials = parseQuery(exchange.getRequestURI().getQuery());
			UserDaoMock dao = new UserDaoMock();
			String encryptedPassword = PasswordEncrypter.encrypt(credentials.getPassword());
			user = dao.login(credentials.getLogin(), encryptedPassword);
		} catch (NoSuchUserException e) {
			log.info("Bad sign in attempt");
		} catch (DbAccessException e) {
			log.error("Database access failure");
		}
		
		if(user == null) {
			//render login page again with some "Wrong login/password!" notation
			log.debug("User = null");
			sendRedirect(exchange, LOGIN_CONTROLLER_REDIRECT_URL);
			return;
		} else {
			//login OK, create Session for this user
			//and redirect to upload CSV page
			log.debug("Have a USER!:)");
			sendRedirect(exchange, UPLOAD_CONTROLLER_REDIRECT_URL);
			return;
		}
		
	}
	
	private LoginAndPassword parseQuery(String query) throws NoSuchUserException {
		//query string can't be shorter than "login=&password=".length()+1
		if(query == null || query.length()<("login=&password=".length()+1)) {
			log.error("No or wrong credentials provided");
			throw new NoSuchUserException();
		}
		String login;
		String password;
		try {
			String[] arr = query.split("&");
			login = (arr[0].split("="))[1];
			password = (arr[1].split("="))[1];
		} catch (Exception e) {
			log.debug("Can't parse query");
			throw new NoSuchUserException();
		}
		
		return new LoginAndPassword(login, password);
	}

}
