package eu.ibutler.affiliatenetwork.controllers;

import static eu.ibutler.affiliatenetwork.controllers.Links.*;

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
import eu.ibutler.affiliatenetwork.http.parse.Parser;
import eu.ibutler.affiliatenetwork.http.session.HttpSession;
import eu.ibutler.affiliatenetwork.http.session.SessionManager;

@SuppressWarnings("restriction")
@WebController("/checkRegister")
public class CheckRegisterController extends AbstractHttpHandler implements FreeAccess {
	
	private static Logger log = Logger.getLogger(CheckRegisterController.class.getName());

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
		}
		
		Admin freshUser;
		Map<String, String> registerInfo;
		try {
			registerInfo = Parser.parseQuery(query);
			freshUser = new Admin(registerInfo.get(NAME_PARAM_NAME), registerInfo.get(EMAIL_PARAM_NAME), registerInfo.get(PASSWORD_PARAM_NAME));
			new AdminDaoImpl().insertAdmin(freshUser);
		} catch (DaoException | ParsingException e) {
			log.debug("Bad registration attempt");
			sendRedirect(exchange, cfg.makeUrl("DOMAIN_NAME", "ERROR_PAGE_URL"));
			return;
		}
		
		log.info("Successfull Administrator registration email=\"" + registerInfo.get("email") + "\"");
		
		//register OK, create new Session and attach this user to it
		SessionManager manager = SessionManager.getInstance();
		HttpSession session = manager.getSession(exchange, true);
		session.setAttribute(SESSION_USER_ATTR_NAME, freshUser);

		//redirect to upload page
		sendRedirect(exchange, cfg.makeUrl("DOMAIN_NAME", "ADMIN_UPLOAD_PAGE_URL"));
		return;
	}

}
