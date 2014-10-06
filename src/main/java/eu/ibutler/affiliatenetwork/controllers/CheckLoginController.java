package eu.ibutler.affiliatenetwork.controllers;

import static eu.ibutler.affiliatenetwork.utils.LinkUtils.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.sun.net.httpserver.HttpExchange;

import eu.ibutler.affiliatenetwork.dao.AdminDao;
import eu.ibutler.affiliatenetwork.dao.exceptions.DbAccessException;
import eu.ibutler.affiliatenetwork.dao.exceptions.NoSuchEntityException;
import eu.ibutler.affiliatenetwork.dao.impl.AdminDaoImpl;
import eu.ibutler.affiliatenetwork.entity.Admin;
import eu.ibutler.affiliatenetwork.http.ParsingException;
import eu.ibutler.affiliatenetwork.http.parse.QueryParser;
import eu.ibutler.affiliatenetwork.http.session.HttpSession;
import eu.ibutler.affiliatenetwork.http.session.SessionManager;
import eu.ibutler.affiliatenetwork.utils.AppConfig;
import eu.ibutler.affiliatenetwork.utils.Encrypter;
import eu.ibutler.affiliatenetwork.utils.LinkUtils;

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

	private static AppConfig cfg = AppConfig.getInstance();
	private static Logger log = Logger.getLogger(CheckLoginController.class.getName());
	
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		
		if(!exchange.getRequestMethod().equals("POST")) {
			log.debug("Attempt to send credentials not via POST");
			sendRedirect(exchange, cfg.makeUrl("DOMAIN_NAME", "LOGIN_PAGE_URL"));
			return;
		}
		
		String query;
		try(InputStream in = exchange.getRequestBody()) {
			byte[] bytes = IOUtils.toByteArray(in);
			query = new String(bytes, "UTF-8");
			log.debug("POST query string is: \"" + query + "\"");
			if(!(query.contains(EMAIL_PARAM) && query.contains(PASSWORD_PARAM))) {
				log.debug("Query doesn't contain email and/or password");
				sendRedirect(exchange, cfg.makeUrl("DOMAIN_NAME", "LOGIN_PAGE_URL"));
				return;
			}
		}
		
		Admin freshUser;
		Map<String, String> credentials = null;
		try {
			credentials = QueryParser.parseQuery(query);
			String encryptedPassword = Encrypter.encrypt(credentials.get(PASSWORD_PARAM));
			AdminDao dao = new AdminDaoImpl();
			freshUser = dao.selectAdmin(credentials.get(EMAIL_PARAM), encryptedPassword);
		} catch (NoSuchEntityException e) {
			log.info("Bad login attempt, entered credentials: login=\"" + credentials.get(EMAIL_PARAM)
					+ "\", pass=\"" + credentials.get(PASSWORD_PARAM) + "\"");
			sendRedirect(exchange, cfg.makeUrl("DOMAIN_NAME", "LOGIN_PAGE_URL", "WRONG_PARAM"));
			return;
		} catch (DbAccessException e) {
			log.error("Login failure, exception: " + e.getClass().getName());
			sendRedirect(exchange, cfg.makeUrl("DOMAIN_NAME", "ERROR_PAGE_URL"));
			return;
		} catch (ParsingException e) {
			sendRedirect(exchange, cfg.makeUrl("DOMAIN_NAME", "LOGIN_PAGE_URL", "WRONG_PARAM"));
			return;
		}
		
		//login OK, create new Session and attach this user to it
		HttpSession session = (HttpSession) exchange.getAttribute(LinkUtils.EXCHANGE_SESSION_ATTR_NAME);
		SessionManager manager = SessionManager.getInstance();
		session = manager.getSession(exchange, true);
		session.setAttribute(LinkUtils.SESSION_USER_ATTR_NAME, freshUser);

		//redirect to upload page
		log.debug("Successfull login of \"" + credentials.get(EMAIL_PARAM) + "\"");
		sendRedirect(exchange, cfg.makeUrl("DOMAIN_NAME", "ADMIN_UPLOAD_PAGE_URL"));
		return;
	}
	
}
