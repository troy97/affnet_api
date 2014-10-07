package eu.ibutler.affiliatenetwork.controllers;

import static eu.ibutler.affiliatenetwork.utils.LinkUtils.*;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.sun.net.httpserver.HttpExchange;

import eu.ibutler.affiliatenetwork.dao.exceptions.DaoException;
import eu.ibutler.affiliatenetwork.dao.exceptions.DbAccessException;
import eu.ibutler.affiliatenetwork.dao.exceptions.NoSuchEntityException;
import eu.ibutler.affiliatenetwork.dao.exceptions.UniqueConstraintViolationException;
import eu.ibutler.affiliatenetwork.dao.impl.ShopDaoImpl;
import eu.ibutler.affiliatenetwork.dao.impl.UserDaoImpl;
import eu.ibutler.affiliatenetwork.entity.Shop;
import eu.ibutler.affiliatenetwork.entity.User;
import eu.ibutler.affiliatenetwork.http.ParsingException;
import eu.ibutler.affiliatenetwork.http.parse.QueryParser;
import eu.ibutler.affiliatenetwork.http.session.HttpSession;
import eu.ibutler.affiliatenetwork.http.session.SessionManager;
import eu.ibutler.affiliatenetwork.jdbc.DbConnectionPool;
import eu.ibutler.affiliatenetwork.jdbc.JdbcUtils;
import eu.ibutler.affiliatenetwork.utils.LinkUtils;

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
		}
		
		User freshUser;
		Shop freshShop;
		Map<String, String> registerInfo;
		Connection conn = null; //############### transaction manager crutch
		String redirectIfDuplicateUrl = cfg.makeUrl("DOMAIN_NAME", "SIGNUP_PAGE_URL") + LinkUtils.createQueryString(LinkUtils.WRONG_PARAM);
		try {
			registerInfo = QueryParser.parseQuery(query);
			//check if mandatory parameters are present
			if(!registerInfo.keySet().containsAll(Arrays.asList(SHOP_NAME_PARAM, SHOP_URL_PARAM, EMAIL_PARAM, PASSWORD_PARAM))) {
				throw new ParsingException();
			}
			
			conn = getConnection(); //############### transaction manager crutch
			
			freshShop = new Shop(registerInfo.get(SHOP_NAME_PARAM), registerInfo.get(SHOP_URL_PARAM));
			try {
				int shopId = new ShopDaoImpl().insertShop(freshShop, conn);
				freshShop.setDbId(shopId);
			} catch (UniqueConstraintViolationException e) {
				redirectIfDuplicateUrl = cfg.makeUrl("DOMAIN_NAME", "SIGNUP_PAGE_URL") + LinkUtils.createQueryString(LinkUtils.DUPLICATE_SHOP_PARAM);
				throw e;
			}
			
			freshUser = new User(registerInfo.get(EMAIL_PARAM), registerInfo.get(PASSWORD_PARAM), 
					registerInfo.get(FIRST_NAME_PARAM), registerInfo.get(LAST_NAME_PARAM), freshShop.getDbId());
			try {
				int userId = new UserDaoImpl().insertUser(freshUser, conn);
				freshUser.setDbId(userId);
			} catch (UniqueConstraintViolationException e) {
				redirectIfDuplicateUrl = cfg.makeUrl("DOMAIN_NAME", "SIGNUP_PAGE_URL") + LinkUtils.createQueryString(LinkUtils.DUPLICATE_USER_PARAM);
				throw e;
			}
			commitAndClose(conn); //############### transaction manager crutch
		} catch (DbAccessException e) {
			rollbackAndClose(conn); //############### transaction manager crutch
			log.debug("Problem while inserting to DB, not Shop nor User were created, " + e.getClass().getName());
			sendRedirect(exchange, cfg.makeUrl("DOMAIN_NAME", "ERROR_PAGE_URL"));
			return;
		} catch (UniqueConstraintViolationException e) {
			rollbackAndClose(conn); //############### transaction manager crutch
			log.debug("Problem while inserting to DB, not Shop nor User were created, " + e.getClass().getName());
			sendRedirect(exchange, redirectIfDuplicateUrl);
			return;
		} catch (ParsingException e) {
			log.debug("Bad registration data provided");
			sendRedirect(exchange, cfg.makeUrl("DOMAIN_NAME", "SIGNUP_PAGE_URL") + LinkUtils.createQueryString(LinkUtils.WRONG_PARAM));
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
