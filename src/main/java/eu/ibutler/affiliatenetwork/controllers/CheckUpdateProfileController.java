package eu.ibutler.affiliatenetwork.controllers;

import static eu.ibutler.affiliatenetwork.controllers.Links.*;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.sun.net.httpserver.HttpExchange;

import eu.ibutler.affiliatenetwork.dao.exceptions.DbAccessException;
import eu.ibutler.affiliatenetwork.dao.exceptions.NoSuchEntityException;
import eu.ibutler.affiliatenetwork.dao.exceptions.UniqueConstraintViolationException;
import eu.ibutler.affiliatenetwork.dao.impl.ShopDaoImpl;
import eu.ibutler.affiliatenetwork.dao.impl.UserDaoImpl;
import eu.ibutler.affiliatenetwork.entity.Shop;
import eu.ibutler.affiliatenetwork.entity.User;
import eu.ibutler.affiliatenetwork.http.ParsingException;
import eu.ibutler.affiliatenetwork.http.parse.Parser;
import eu.ibutler.affiliatenetwork.http.session.HttpSession;
import eu.ibutler.affiliatenetwork.jdbc.DbConnectionPool;
import eu.ibutler.affiliatenetwork.jdbc.JdbcUtils;
import eu.ibutler.affiliatenetwork.utils.Encrypter;
import eu.ibutler.affiliatenetwork.utils.FtlDataModel;
import eu.ibutler.affiliatenetwork.utils.FtlProcessingException;
import eu.ibutler.affiliatenetwork.utils.FtlProcessor;

@SuppressWarnings("restriction")
@WebController("/checkUpdateProfile")
public class CheckUpdateProfileController extends AbstractHttpHandler implements RestrictedAccess {
	
	private static Logger log = Logger.getLogger(CheckUpdateProfileController.class.getName());

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		
		if(!exchange.getRequestMethod().equals("POST")) {
			log.debug("Attempt to send credentials not via POST");
			sendRedirect(exchange, cfg.makeUrl("DOMAIN_NAME", "UPDATE_USER_PROFILE_PAGE_URL") + createQueryString(ERROR_PARAM_NAME));
			return;
		}
		
		String query;
		try(InputStream in = exchange.getRequestBody()) {
			byte[] bytes = IOUtils.toByteArray(in);
			query = new String(bytes, "UTF-8");
			log.debug("POST query string is: \"" + query + "\"");
		}
		
		//get session and user object
		HttpSession session = (HttpSession) exchange.getAttribute(EXCHANGE_SESSION_ATTR_NAME);
		User oldUser = (User) session.getAttribute(SESSION_USER_ATTR_NAME);
		
		Shop oldShop;
		try {
			oldShop = new ShopDaoImpl().selectById(oldUser.getShopId());
		} catch (DbAccessException | NoSuchEntityException e1) {
			log.debug("Unable to get Shop object associated with given User");
			sendRedirect(exchange, cfg.makeUrl("DOMAIN_NAME", "ERROR_PAGE_URL"));
			return;
		}
		
		User freshUser = oldUser.clone(); //old User is stored in session, so don't change it, unless changes are saved to DB
		Shop freshShop = oldShop;
		Map<String, String> registerInfo;
		Connection conn = null; //############### transaction manager crutch
		String redirectIfDuplicateUrl = cfg.makeUrl("DOMAIN_NAME", "UPDATE_USER_PROFILE_PAGE_URL") + Links.createQueryString(Links.ERROR_PARAM_NAME);
		try {
			registerInfo = Parser.parseQuery(query);
			
			freshShop.setName(registerInfo.get(SHOP_NAME_PARAM_NAME));
			freshShop.setUrl(registerInfo.get(SHOP_URL_PARAM_NAME));
			
			freshUser.setEmail(registerInfo.get(EMAIL_PARAM_NAME));
			freshUser.setEncryptedPassword(Encrypter.encrypt(registerInfo.get(PASSWORD_PARAM_NAME)));
			freshUser.setFirstName(registerInfo.get(FIRST_NAME_PARAM_NAME));
			freshUser.setLastName(registerInfo.get(LAST_NAME_PARAM_NAME));

			conn = getConnection(); //############### transaction manager crutch
			
			try {
				new ShopDaoImpl().updateShop(freshShop);
			} catch(UniqueConstraintViolationException e) {
				redirectIfDuplicateUrl = cfg.makeUrl("DOMAIN_NAME", "UPDATE_USER_PROFILE_PAGE_URL") + Links.createQueryString(Links.DUPLICATE_SHOP_PARAM_NAME);
				throw e;
			}
			
			try {
				new UserDaoImpl().updateUser(freshUser);
			} catch(UniqueConstraintViolationException e) {
				redirectIfDuplicateUrl = cfg.makeUrl("DOMAIN_NAME", "UPDATE_USER_PROFILE_PAGE_URL") + Links.createQueryString(Links.DUPLICATE_USER_PARAM_NAME);
				throw e;
			}
			commitAndClose(conn); //############### transaction manager crutch
			session.setAttribute(SESSION_USER_ATTR_NAME, freshUser); // update user in session
		} catch (DbAccessException e) {
			rollbackAndClose(conn); //############### transaction manager crutch
			log.debug("Problem while inserting to DB");
			sendRedirect(exchange, cfg.makeUrl("DOMAIN_NAME", "ERROR_PAGE_URL"));
			return;
		} catch (ParsingException e) {
			log.debug("Bad registration data provided");
			sendRedirect(exchange, cfg.makeUrl("DOMAIN_NAME", "UPDATE_USER_PROFILE_PAGE_URL") + createQueryString(ERROR_PARAM_NAME));
			return;
		} catch (UniqueConstraintViolationException e) {
			rollbackAndClose(conn); //############### transaction manager crutch
			log.debug("Profile update failure: " + e.getClass().getName());
			sendRedirect(exchange, redirectIfDuplicateUrl);
			return;
		}
		
		log.info("Profile updated successfully email=\"" + registerInfo.get(EMAIL_PARAM_NAME) + "\"");
		
		//register OK, update user in session attributes
		session.setAttribute(SESSION_USER_ATTR_NAME, freshUser);

		//create OK page
		FtlDataModel ftlData = new FtlDataModel();
		ftlData.put("name", freshUser.getEmail());
		ftlData.put("cabinetPage", cfg.makeUrl("DOMAIN_NAME", "USER_CABINET_PAGE_URL"));
		ftlData.put("logoutPage", cfg.makeUrl("DOMAIN_NAME", "LOGOUT_PAGE_URL"));
		ftlData.put("userObject", freshUser);
		ftlData.put("shopObject", freshShop);
		
		String responseHtml;
		try {
			responseHtml = new FtlProcessor().createHtml(cfg.get("UPDATE_PROFILE_SUCCESS_FTL"), ftlData);
		} catch (FtlProcessingException e) {
			log.error("Failed to create page");
			sendRedirect(exchange, cfg.makeUrl("DOMAIN_NAME", "ERROR_PAGE_URL"));
			return;
		}
		
		//render page
		try(BufferedOutputStream out = new BufferedOutputStream(exchange.getResponseBody())){
			byte[] responseBytes = responseHtml.getBytes();
			exchange.sendResponseHeaders(200, responseBytes.length);
			out.write(responseBytes);
			out.flush();
		}
		
	}
	
	
	
	
	
	
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
