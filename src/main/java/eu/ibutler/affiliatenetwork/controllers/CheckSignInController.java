package eu.ibutler.affiliatenetwork.controllers;

import static eu.ibutler.affiliatenetwork.utils.LinkUtils.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.sun.net.httpserver.HttpExchange;

import eu.ibutler.affiliatenetwork.dao.UserDao;
import eu.ibutler.affiliatenetwork.dao.exceptions.DbAccessException;
import eu.ibutler.affiliatenetwork.dao.exceptions.NoSuchEntityException;
import eu.ibutler.affiliatenetwork.dao.impl.UserDaoImpl;
import eu.ibutler.affiliatenetwork.entity.User;
import eu.ibutler.affiliatenetwork.http.ParsingException;
import eu.ibutler.affiliatenetwork.http.parse.QueryParser;
import eu.ibutler.affiliatenetwork.http.session.HttpSession;
import eu.ibutler.affiliatenetwork.http.session.SessionManager;
import eu.ibutler.affiliatenetwork.utils.Encrypter;
import eu.ibutler.affiliatenetwork.utils.LinkUtils;

@SuppressWarnings("restriction")
@WebController("/checkSignIn")
public class CheckSignInController extends AbstractHttpHandler implements FreeAccess {
	
	private static Logger log = Logger.getLogger(CheckSignInController.class.getName());

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		
		if(!exchange.getRequestMethod().equals("POST")) {
			log.debug("Attempt to send credentials not via POST");
			sendRedirect(exchange, cfg.makeUrl("DOMAIN_NAME", "SIGNIN_PAGE_URL"));
			return;
		}
		
		String query;
		try(InputStream in = exchange.getRequestBody()) {
			byte[] bytes = IOUtils.toByteArray(in);
			query = new String(bytes, "UTF-8");
			log.debug("POST query string is: \"" + query + "\"");
			if(!(query.contains(EMAIL_PARAM) && query.contains(PASSWORD_PARAM))) {
				log.debug("Query doesn't contain email and/or password");
				sendRedirect(exchange, cfg.makeUrl("DOMAIN_NAME", "SIGNIN_PAGE_URL"));
				return;
			}
		}
		
		User freshUser;
		Map<String, String> credentials = null;
		try {
			credentials = QueryParser.parseQuery(query);
			UserDao dao = new UserDaoImpl();
			String encryptedPassword = Encrypter.encrypt(credentials.get(PASSWORD_PARAM));
			freshUser = dao.selectUser(credentials.get(EMAIL_PARAM), encryptedPassword);
		} catch (NoSuchEntityException e) {
			log.info("Bad sign in attempt, entered credentials: email=\"" + credentials.get(EMAIL_PARAM)
					+ "\", pass=\"" + credentials.get(PASSWORD_PARAM) + "\"");
			sendRedirect(exchange, cfg.makeUrl("DOMAIN_NAME", "SIGNIN_PAGE_URL") + createQueryString(WRONG_PARAM));
			return;
		} catch (DbAccessException e) {
			log.error("Sign in failure, exception: " + e.getClass().getName());
			sendRedirect(exchange, cfg.makeUrl("DOMAIN_NAME", "ERROR_PAGE_URL"));
			return;
		} catch (ParsingException e) {
			sendRedirect(exchange, cfg.makeUrl("DOMAIN_NAME", "SIGNIN_PAGE_URL") + createQueryString(WRONG_PARAM));
			return;
		}
		
		//OK, create new Session and attach this user to it
		SessionManager manager = SessionManager.getInstance();
		HttpSession session = manager.getSession(exchange, true);
		session.setAttribute(SESSION_USER_ATTR_NAME, freshUser);

		//redirect to upload page
		log.debug("Successfull sign in of \"" + credentials.get("email") + "\"");
		sendRedirect(exchange, cfg.makeUrl("DOMAIN_NAME", "UPLOAD_PAGE_URL"));
		return;
	}
}
