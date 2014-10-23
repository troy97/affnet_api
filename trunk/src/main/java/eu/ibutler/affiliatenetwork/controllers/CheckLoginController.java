package eu.ibutler.affiliatenetwork.controllers;

import static eu.ibutler.affiliatenetwork.controllers.utils.Links.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.sun.net.httpserver.HttpExchange;

import eu.ibutler.affiliatenetwork.config.AppConfig;
import eu.ibutler.affiliatenetwork.config.Urls;
import eu.ibutler.affiliatenetwork.controllers.utils.Links;
import eu.ibutler.affiliatenetwork.dao.AdminDao;
import eu.ibutler.affiliatenetwork.dao.exceptions.DbAccessException;
import eu.ibutler.affiliatenetwork.dao.exceptions.NoSuchEntityException;
import eu.ibutler.affiliatenetwork.dao.impl.AdminDaoImpl;
import eu.ibutler.affiliatenetwork.entity.Admin;
import eu.ibutler.affiliatenetwork.http.parse.Parser;
import eu.ibutler.affiliatenetwork.http.parse.exceptions.ParsingException;
import eu.ibutler.affiliatenetwork.http.session.HttpSession;
import eu.ibutler.affiliatenetwork.http.session.SessionManager;
import eu.ibutler.affiliatenetwork.utils.Encrypter;

/**
 * This handler doesn't have any view part, it only gets credentials
 * from login controller and verifies them against those stored in DB.
 * If no match found then redirect back to login page is sent.
 * If match found then redirect to upload page is sent.
 * @author Anton Lukashchuk
 *
 */
@SuppressWarnings("restriction")
@WebController("/checkLogin")
public class CheckLoginController extends AbstractHttpHandler implements FreeAccess {

	private static Logger log = Logger.getLogger(CheckLoginController.class.getName());
	
	@Override
	public void handleBody(HttpExchange exchange) throws IOException {
		
		if(!exchange.getRequestMethod().equals("POST")) {
			log.debug("Attempt to send credentials not via POST");
			sendRedirect(exchange, Urls.fullURL(Urls.LOGIN_PAGE_URL));
			return;
		}
		
		String query;
		try(InputStream in = exchange.getRequestBody()) {
			byte[] bytes = IOUtils.toByteArray(in);
			query = new String(bytes, "UTF-8");
			log.debug("POST query string is: \"" + query + "\"");
		}
		
		Admin freshUser;
		Map<String, String> credentials = null;
		try {
			credentials = Parser.parseQuery(query);
			if(!credentials.keySet().containsAll(Arrays.asList(EMAIL_PARAM_NAME, PASSWORD_PARAM_NAME))) {
				throw new ParsingException();
			}
			String encryptedPassword = Encrypter.encrypt(credentials.get(PASSWORD_PARAM_NAME));
			AdminDao dao = new AdminDaoImpl();
			freshUser = dao.selectAdmin(credentials.get(EMAIL_PARAM_NAME), encryptedPassword);
		} catch (NoSuchEntityException e) {
			log.info("Bad login attempt, entered credentials: login=\"" + credentials.get(EMAIL_PARAM_NAME)
					+ "\", pass=\"" + credentials.get(PASSWORD_PARAM_NAME) + "\"");
			sendRedirect(exchange, Urls.fullURL(Urls.LOGIN_PAGE_URL) + createQueryString(ERROR_PARAM_NAME));
			return;
		} catch (DbAccessException e) {
			log.error("Login failure, exception: " + e.getClass().getName());
			sendRedirect(exchange, Urls.fullURL(Urls.ERROR_PAGE_URL));
			return;
		} catch (ParsingException e) {
			sendRedirect(exchange, Urls.fullURL(Urls.LOGIN_PAGE_URL) + createQueryString(ERROR_PARAM_NAME));
			return;
		}
		
		//login OK, create new Session and attach this user to it
		HttpSession session = (HttpSession) exchange.getAttribute(Links.EXCHANGE_SESSION_ATTR_NAME);
		SessionManager manager = SessionManager.getInstance();
		session = manager.getSession(exchange, true);
		session.setAttribute(Links.SESSION_USER_ATTR_NAME, freshUser);

		//redirect to upload page
		log.debug("Successfull login of \"" + credentials.get(EMAIL_PARAM_NAME) + "\"");
		sendRedirect(exchange, Urls.fullURL(Urls.ADMIN_UPLOAD_PAGE_URL));
		return;
	}
	
}
