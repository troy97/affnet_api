package eu.ibutler.affiliatenetwork.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.sun.net.httpserver.HttpExchange;

import eu.ibutler.affiliatenetwork.dao.UserDao;
import eu.ibutler.affiliatenetwork.dao.exceptions.DbAccessException;
import eu.ibutler.affiliatenetwork.dao.exceptions.NoSuchEntityException;
import eu.ibutler.affiliatenetwork.dao.impl.UserDaoImpl;
import eu.ibutler.affiliatenetwork.entity.LinkUtils;
import eu.ibutler.affiliatenetwork.entity.MailAndPassword;
import eu.ibutler.affiliatenetwork.entity.Encrypter;
import eu.ibutler.affiliatenetwork.entity.User;
import eu.ibutler.affiliatenetwork.entity.exceptions.ParsingException;
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
		
		if(!exchange.getRequestMethod().equals("POST")) {
			log.debug("Attempt to send credentials not via POST");
			sendRedirect(exchange, LinkUtils.LOGIN_PAGE_CONTROLLER_FULL_URL);
			return;
		}
		
		//int contentLength = Integer.valueOf(exchange.getRequestHeaders().getFirst("Content-Length"));
		
		String query;
		try(InputStream in = exchange.getRequestBody()) {
			byte[] bytes = IOUtils.toByteArray(in);
			query = new String(bytes, "UTF-8");
			log.debug("POST query string is: \"" + query + "\"");
		}
		
		User freshUser;
		MailAndPassword credentials = null;
		try {
			credentials = parseQuery(query);
			String encryptedPassword = Encrypter.encrypt(credentials.getPassword());
			UserDao dao = new UserDaoImpl();
			freshUser = dao.login(credentials.getMail(), encryptedPassword);
		} catch (NoSuchEntityException e) {
			log.info("Bad sign in attempt, entered credentials: login=\"" + credentials.getMail()
					+ "\", pass=\"" + credentials.getPassword() + "\"");
			sendRedirect(exchange, LinkUtils.LOGIN_CONTROLLER_FULL_URL_REPEAT);
			return;
		} catch (DbAccessException | ParsingException e) {
			log.error("Login failure, exception: " + e.getClass().getName());
			sendRedirect(exchange, LinkUtils.ERROR_PAGE_CONTROLLER_FULL_URL);
			return;
		}
		
		//login OK, create new Session and attach this user to it
		HttpSession session = (HttpSession) exchange.getAttribute(LinkUtils.EXCHANGE_SESSION_ATTR_NAME);
		SessionManager manager = SessionManager.getInstance();
		session = manager.getSession(exchange, true);
		session.setAttribute(LinkUtils.SESSION_USER_ATTR_NAME, freshUser);

		//redirect to upload page
		log.debug("Successfull signin of \"" + credentials.getMail() + "\"");
		sendRedirect(exchange, LinkUtils.UPLOAD_PAGE_CONTROLLER_FULL_URL);
		return;
	}
	
	/**
	 * Parse query string for email and password
	 * @param query
	 * @return LoginAndPassword
	 * @throws NoSuchEntityException
	 */
	private MailAndPassword parseQuery(String query) throws ParsingException {
		if(query == null || query.length()<("email=&password=".length()+1)) {
			log.info("No credentials provided");
			return new MailAndPassword("", "");
		}
		
		try {
			query = URLDecoder.decode(query, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			log.debug("URLDecoder error");
			throw new ParsingException();
		}
		
		String email = "";
		String password = "";
		try {
			String[] arr = query.split("&");
			email = (arr[0].split("="))[1];
			password = (arr[1].split("="))[1];
		} catch (Exception e) {
			log.debug("Can't parse query");
		}
		
		return new MailAndPassword(email, password);
	}
	
}
