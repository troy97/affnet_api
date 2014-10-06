package eu.ibutler.affiliatenetwork.controllers;

import static eu.ibutler.affiliatenetwork.utils.LinkUtils.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.sun.net.httpserver.HttpExchange;

import eu.ibutler.affiliatenetwork.dao.exceptions.DaoException;
import eu.ibutler.affiliatenetwork.dao.impl.AdminDaoImpl;
import eu.ibutler.affiliatenetwork.entity.Admin;
import eu.ibutler.affiliatenetwork.http.ParsingException;
import eu.ibutler.affiliatenetwork.http.parse.QueryParser;
import eu.ibutler.affiliatenetwork.http.session.HttpSession;
import eu.ibutler.affiliatenetwork.http.session.SessionManager;

@SuppressWarnings("restriction")
public class CheckRegisterController extends AbstractHttpHandler {
	
	private static Logger log = Logger.getLogger(CheckRegisterController.class.getName());

	@Override
	public void handle(HttpExchange exchange) throws IOException {

		if(!exchange.getRequestMethod().equals("POST")) {
			log.debug("Attempt to send credentials not via POST");
			sendRedirect(exchange, LOGIN_PAGE_CONTROLLER_FULL_URL);
			return;
		}
		
		String query;
		try(InputStream in = exchange.getRequestBody()) {
			byte[] bytes = IOUtils.toByteArray(in);
			query = new String(bytes, "UTF-8");
			log.debug("POST query string is: \"" + query + "\"");
		}
		
		Admin freshUser;
		Map<String, String> registerInfo;
		try {
			registerInfo = QueryParser.parseQuery(query);
			freshUser = new Admin(registerInfo.get(NAME_PARAM), registerInfo.get(EMAIL_PARAM), registerInfo.get(PASSWORD_PARAM));
			new AdminDaoImpl().insertAdmin(freshUser);
		} catch (DaoException | ParsingException e) {
			log.debug("Bad registration attempt");
			sendRedirect(exchange, ERROR_PAGE_CONTROLLER_FULL_URL);
			return;
		}
		
		log.info("Successfull registration email=\"" + registerInfo.get("email") + "\"");
		
		//register OK, create new Session and attach this user to it
		SessionManager manager = SessionManager.getInstance();
		HttpSession session = manager.getSession(exchange, true);
		session.setAttribute(SESSION_USER_ATTR_NAME, freshUser);

		//redirect to upload page
		sendRedirect(exchange, cfg.makeUrl("DOMAIN_NAME", "ADMIN_UPLOAD_PAGE_URL"));
		return;
	}

}
