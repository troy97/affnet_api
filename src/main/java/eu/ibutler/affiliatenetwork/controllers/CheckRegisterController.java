package eu.ibutler.affiliatenetwork.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.sun.net.httpserver.HttpExchange;

import eu.ibutler.affiliatenetwork.dao.exceptions.DaoException;
import eu.ibutler.affiliatenetwork.dao.exceptions.NoSuchEntityException;
import eu.ibutler.affiliatenetwork.dao.impl.UserDaoImpl;
import eu.ibutler.affiliatenetwork.entity.LinkUtils;
import eu.ibutler.affiliatenetwork.entity.User;
import eu.ibutler.affiliatenetwork.session.HttpSession;
import eu.ibutler.affiliatenetwork.session.SessionManager;

@SuppressWarnings("restriction")
public class CheckRegisterController extends AbstractHttpHandler {
	
	private static Logger log = Logger.getLogger(CheckRegisterController.class.getName());

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		try(InputStream in = exchange.getRequestBody()) {}
		
/*		//It's possible to get here with session already attached, so need to verify that
		HttpSession session = (HttpSession) exchange.getAttribute(LinkUtils.EXCHANGE_SESSION_ATTR_NAME);
		if(session != null) {
			sendRedirect(exchange, LinkUtils.UPLOAD_CONTROLLER_REDIRECT_URL);
			return;
		}*/
		
		User freshUser;
		try {
			List<String> registerInfo = parseQuery(exchange.getRequestURI().getQuery());
			String name = registerInfo.get(0);
			String login = registerInfo.get(1);
			String password = registerInfo.get(2);
			String email = registerInfo.get(3);
			freshUser = new User(name, email, login, password);
			new UserDaoImpl().addUser(freshUser);
		} catch (DaoException e) {
			log.debug("Bad registration attempt");
			sendRedirect(exchange, LinkUtils.ERROR_PAGE_CONTROLLER_FULL_URL);
			return;
		}
		
		//register OK, create new Session and attach this user to it
		SessionManager manager = SessionManager.getInstance();
		HttpSession session = manager.getSession(exchange, true);
		session.setAttribute(LinkUtils.SESSION_USER_ATTR_NAME, freshUser);

		//redirect to upload page
		log.debug("Successfull login");
		sendRedirect(exchange, LinkUtils.UPLOAD_PAGE_CONTROLLER_FULL_URL);
		return;
	}

	/**
	 * Parse query string for registration info
	 * @param query
	 * @return
	 * @throws NoSuchEntityException
	 */
	private List<String> parseQuery(String query) throws NoSuchEntityException {
		try {
			query = URLDecoder.decode(query, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			log.debug("URLDecoder error");
		}
		List<String> result = new ArrayList<String>();
		if(query == null || query.length()<("name=&login=&password=&email=".length()+1)) {
			log.error("No or wrong credentials provided");
			throw new NoSuchEntityException();
		}
		String name;
		String login;
		String password;
		String email;
		try {
			String[] arr = query.split("&");
			name = (arr[0].split("="))[1];
			result.add(name);
			login = (arr[1].split("="))[1];
			result.add(login);
			password = (arr[2].split("="))[1];
			result.add(password);
			email = (arr[3].split("="))[1];
			result.add(email);
		} catch (Exception e) {
			log.debug("Can't parse query");
			throw new NoSuchEntityException();
		}
		
		return result;
	}
	
}
