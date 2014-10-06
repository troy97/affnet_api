package eu.ibutler.affiliatenetwork.controllers;

import static eu.ibutler.affiliatenetwork.utils.LinkUtils.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.sun.net.httpserver.HttpExchange;

import eu.ibutler.affiliatenetwork.dao.exceptions.DaoException;
import eu.ibutler.affiliatenetwork.dao.impl.ShopDaoImpl;
import eu.ibutler.affiliatenetwork.dao.impl.UserDaoImpl;
import eu.ibutler.affiliatenetwork.entity.Shop;
import eu.ibutler.affiliatenetwork.entity.User;
import eu.ibutler.affiliatenetwork.http.ParsingException;
import eu.ibutler.affiliatenetwork.http.parse.QueryParser;
import eu.ibutler.affiliatenetwork.http.session.HttpSession;
import eu.ibutler.affiliatenetwork.http.session.SessionManager;

/**
 * Check registration info of new webshop user,
 * create new User and new Shop if valid data was given
 * @author Anton Lukashchuk
 *
 */
@SuppressWarnings("restriction")
public class CheckSignUpController extends AbstractHttpHandler {
	
	private static Logger log = Logger.getLogger(CheckRegisterController.class.getName());

	@Override
	public void handle(HttpExchange exchange) throws IOException {

		if(!exchange.getRequestMethod().equals("POST")) {
			log.debug("Attempt to send credentials not via POST");
			sendRedirect(exchange, cfg.makeUrl("DOMAIN_NAME", "SIGNUP_PAGE_URL"));
			return;
			
		}
		
		String query;
		try(InputStream in = exchange.getRequestBody()) {
			byte[] bytes = IOUtils.toByteArray(in);
			query = new String(bytes, "UTF-8");
			log.debug("POST query string is: \"" + query + "\"");
			if(!(query.contains(EMAIL_PARAM) && query.contains(PASSWORD_PARAM)
					&& query.contains(FIRST_NAME_PARAM) && query.contains(LAST_NAME_PARAM)
					&& query.contains(SHOP_NAME_PARAM) && query.contains(SHOP_URL_PARAM))) {
				log.debug("Query doesn't contain email and/or password");
				sendRedirect(exchange, cfg.makeUrl("DOMAIN_NAME", "SIGNUP_PAGE_URL"));
				return;
			}
		}
		
		User freshUser;
		Shop freshShop;
		Map<String, String> registerInfo;
		try {
			registerInfo = QueryParser.parseQuery(query);
			int shopId = 0;
			freshShop = new Shop(registerInfo.get(SHOP_NAME_PARAM), registerInfo.get(SHOP_URL_PARAM));
			shopId = new ShopDaoImpl().insertShop(freshShop);
			freshShop.setDbId(shopId);
			freshUser = new User(registerInfo.get(EMAIL_PARAM), registerInfo.get(PASSWORD_PARAM), 
					registerInfo.get(FIRST_NAME_PARAM), registerInfo.get(LAST_NAME_PARAM), shopId);
			int userId = new UserDaoImpl().insertUser(freshUser);
			freshUser.setDbId(userId);
		} catch (DaoException e) {
			log.debug("Problem while inserting to DB");
			sendRedirect(exchange, cfg.makeUrl("DOMAIN_NAME", "ERROR_PAGE_URL"));
			return;
		} catch (ParsingException e) {
			log.debug("Bad registration data provided");
			sendRedirect(exchange, cfg.makeUrl("DOMAIN_NAME", "ERROR_PAGE_URL"));
			return;
		}
		
		log.info("Successfull webshop user registration email=\"" + registerInfo.get(EMAIL_PARAM) + "\"");
		
		//register OK, create new Session and attach this user to it
		SessionManager manager = SessionManager.getInstance();
		HttpSession session = manager.getSession(exchange, true);
		session.setAttribute(SESSION_USER_ATTR_NAME, freshUser);

		//redirect to upload page
		sendRedirect(exchange, cfg.makeUrl("DOMAIN_NAME", "UPLOAD_PAGE_URL"));
		return;
	}
	
}
