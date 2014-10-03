package eu.ibutler.affiliatenetwork.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.sun.net.httpserver.HttpExchange;

import eu.ibutler.affiliatenetwork.dao.exceptions.DaoException;
import eu.ibutler.affiliatenetwork.dao.impl.UserDaoImpl;
import eu.ibutler.affiliatenetwork.entity.LinkUtils;
import eu.ibutler.affiliatenetwork.entity.User;
import eu.ibutler.affiliatenetwork.entity.exceptions.ParsingException;
import eu.ibutler.affiliatenetwork.session.HttpSession;
import eu.ibutler.affiliatenetwork.session.SessionManager;

@SuppressWarnings("restriction")
public class CheckRegisterController extends AbstractHttpHandler {
	
	private static Logger log = Logger.getLogger(CheckRegisterController.class.getName());

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
		Map<String, String> registerInfo;
		try {
			registerInfo = parseQuery(query);
			freshUser = new User(registerInfo.get("name"), registerInfo.get("email"), registerInfo.get("password"));
			new UserDaoImpl().addUser(freshUser);
		} catch (DaoException | ParsingException e) {
			log.debug("Bad registration attempt");
			sendRedirect(exchange, LinkUtils.ERROR_PAGE_CONTROLLER_FULL_URL);
			return;
		}
		
		log.info("Successfull registration email=\"" + registerInfo.get("email") + "\"");
		
		//register OK, create new Session and attach this user to it
		SessionManager manager = SessionManager.getInstance();
		HttpSession session = manager.getSession(exchange, true);
		session.setAttribute(LinkUtils.SESSION_USER_ATTR_NAME, freshUser);

		//redirect to upload page
		sendRedirect(exchange, LinkUtils.UPLOAD_PAGE_CONTROLLER_FULL_URL);
		return;
	}

	/**
	 * Parse query string for registration info
	 * @param query
	 * @return Map<String, String>
	 * @throws ParsingException if failed to parse query
	 */
	private Map<String, String> parseQuery(String query) throws ParsingException {
		if(query == null || query.length()<("name=&login=&password=&email=".length()+1)) {
			log.error("No or wrong credentials provided");
			throw new ParsingException();
		}
		
		try {
			query = URLDecoder.decode(query, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			log.debug("URLDecoder error");
			throw new ParsingException();
		}
		
		Map<String, String> result = null;
		String name;
		String password;
		String email;
		try {
			result = new HashMap<>();
			String[] arr = query.split("&");
			name = (arr[0].split("="))[1];
			result.put("name", name);
			password = (arr[1].split("="))[1];
			result.put("password", password);
			email = (arr[2].split("="))[1];
			result.put("email", email);
		} catch (Exception e) {
			log.debug("Can't parse query");
			throw new ParsingException();
		}
		
		return result;
	}
	
}
