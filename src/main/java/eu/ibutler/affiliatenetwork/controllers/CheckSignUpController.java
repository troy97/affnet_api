package eu.ibutler.affiliatenetwork.controllers;

import static eu.ibutler.affiliatenetwork.controllers.Links.*;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.sun.net.httpserver.HttpExchange;

import eu.ibutler.affiliatenetwork.config.Urls;
import eu.ibutler.affiliatenetwork.dao.exceptions.DbAccessException;
import eu.ibutler.affiliatenetwork.dao.exceptions.UniqueConstraintViolationException;
import eu.ibutler.affiliatenetwork.dao.impl.ShopDaoImpl;
import eu.ibutler.affiliatenetwork.dao.impl.UserDaoImpl;
import eu.ibutler.affiliatenetwork.entity.Shop;
import eu.ibutler.affiliatenetwork.entity.User;
import eu.ibutler.affiliatenetwork.http.ParsingException;
import eu.ibutler.affiliatenetwork.http.parse.Parser;
import eu.ibutler.affiliatenetwork.http.session.HttpSession;
import eu.ibutler.affiliatenetwork.http.session.SessionManager;
import eu.ibutler.affiliatenetwork.jdbc.DbConnectionPool;
import eu.ibutler.affiliatenetwork.jdbc.JdbcUtils;

/**
 * Check registration info of new webshop user,
 * create new User and new Shop if valid data was given
 * @author Anton Lukashchuk
 *
 */
@SuppressWarnings("restriction")
@WebController("/checkSignUp")
public class CheckSignUpController extends AbstractHttpHandler implements FreeAccess {
	
	private static Logger log = Logger.getLogger(CheckRegisterController.class.getName());

	@Override
	public void handleBody(HttpExchange exchange) throws IOException {

		if(!exchange.getRequestMethod().equals("POST")) {
			log.debug("Attempt to send credentials not via POST");
			sendRedirect(exchange, Urls.fullURL(Urls.SIGNUP_PAGE_URL));
			return;
			
		}
		
		String query;
		try(InputStream in = exchange.getRequestBody()) {
			byte[] bytes = IOUtils.toByteArray(in);
			query = new String(bytes, "UTF-8");
			log.debug("POST query string is: \"" + query + "\"");
		}
		
		User freshUser;
		Shop freshShop;
		Map<String, String> registerInfo;
		Connection conn = null; //############### transaction manager crutch
		String redirectIfDuplicateUrl = Urls.fullURL(Urls.SIGNUP_PAGE_URL) + Links.createQueryString(Links.ERROR_PARAM_NAME);
		try {
			registerInfo = Parser.parseQuery(query);
			//check if mandatory parameters are present
			if(!registerInfo.keySet().containsAll(Arrays.asList(SHOP_NAME_PARAM_NAME, SHOP_URL_PARAM_NAME, EMAIL_PARAM_NAME, PASSWORD_PARAM_NAME))) {
				throw new ParsingException();
			}
			
			conn = getConnection(); //############### transaction manager crutch
			
			freshShop = new Shop(registerInfo.get(SHOP_NAME_PARAM_NAME), registerInfo.get(SHOP_URL_PARAM_NAME));
			try {
				int shopId = new ShopDaoImpl().insertOne(freshShop, conn);
				freshShop.setDbId(shopId);
			} catch (UniqueConstraintViolationException e) {
				redirectIfDuplicateUrl = Urls.fullURL(Urls.SIGNUP_PAGE_URL) + Links.createQueryString(Links.DUPLICATE_SHOP_PARAM_NAME);
				throw e;
			}
			
			freshUser = new User(registerInfo.get(EMAIL_PARAM_NAME), registerInfo.get(PASSWORD_PARAM_NAME), 
					registerInfo.get(FIRST_NAME_PARAM_NAME), registerInfo.get(LAST_NAME_PARAM_NAME), freshShop.getDbId());
			try {
				int userId = new UserDaoImpl().insertOne(freshUser, conn);
				freshUser.setDbId(userId);
			} catch (UniqueConstraintViolationException e) {
				redirectIfDuplicateUrl = Urls.fullURL(Urls.SIGNUP_PAGE_URL) + Links.createQueryString(Links.DUPLICATE_USER_PARAM_NAME);
				throw e;
			}
			commitAndClose(conn); //############### transaction manager crutch
		} catch (DbAccessException e) {
			rollbackAndClose(conn); //############### transaction manager crutch
			log.debug("Problem while inserting to DB, not Shop nor User were created, " + e.getClass().getName());
			sendRedirect(exchange, Urls.fullURL(Urls.ERROR_PAGE_URL));
			return;
		} catch (UniqueConstraintViolationException e) {
			rollbackAndClose(conn); //############### transaction manager crutch
			log.debug("Problem while inserting to DB, not Shop nor User were created, " + e.getClass().getName());
			sendRedirect(exchange, redirectIfDuplicateUrl);
			return;
		} catch (ParsingException e) {
			log.debug("Bad registration data provided");
			sendRedirect(exchange, Urls.fullURL(Urls.SIGNUP_PAGE_URL) + Links.createQueryString(Links.ERROR_PARAM_NAME));
			return;
		}
		
		log.info("Successfull webshop user registration email=\"" + registerInfo.get(EMAIL_PARAM_NAME) + "\"");
		
		//register OK, create new Session and attach this user to it
		SessionManager manager = SessionManager.getInstance();
		HttpSession session = manager.getSession(exchange, true);
		session.setAttribute(SESSION_USER_ATTR_NAME, freshUser);

		//redirect to upload page
		sendRedirect(exchange, Urls.fullURL(Urls.UPLOAD_PAGE_URL));
		return;
	}//handle()

	
	
	
	
	
	//############### transaction manager crutch
	private Connection getConnection() {
		Connection result = null;
		try {
			result = DbConnectionPool.getInstance().getConnection();
			result.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			result.setAutoCommit(false);
		} catch (SQLException e) {
			log.debug("Unable create connection");
		}
		return result;
	}
	
	//############### transaction manager crutch
	private void commitAndClose(Connection conn) {
		JdbcUtils.commit(conn);
		try {
			conn.setAutoCommit(true);
		} catch (SQLException ignore) {
			ignore.printStackTrace();
		}
		JdbcUtils.close(conn);
	}
	
	//############### transaction manager crutch
	private void rollbackAndClose(Connection conn) {
		JdbcUtils.rollback(conn);
		try {
			conn.setAutoCommit(true);
		} catch (SQLException ignore) {
			ignore.printStackTrace();
		}
		JdbcUtils.close(conn);
	}
	
}
