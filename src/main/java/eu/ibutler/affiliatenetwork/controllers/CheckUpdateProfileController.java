package eu.ibutler.affiliatenetwork.controllers;

import static eu.ibutler.affiliatenetwork.utils.LinkUtils.EMAIL_PARAM;
import static eu.ibutler.affiliatenetwork.utils.LinkUtils.EXCHANGE_SESSION_ATTR_NAME;
import static eu.ibutler.affiliatenetwork.utils.LinkUtils.FIRST_NAME_PARAM;
import static eu.ibutler.affiliatenetwork.utils.LinkUtils.LAST_NAME_PARAM;
import static eu.ibutler.affiliatenetwork.utils.LinkUtils.PASSWORD_PARAM;
import static eu.ibutler.affiliatenetwork.utils.LinkUtils.SESSION_USER_ATTR_NAME;
import static eu.ibutler.affiliatenetwork.utils.LinkUtils.SHOP_NAME_PARAM;
import static eu.ibutler.affiliatenetwork.utils.LinkUtils.SHOP_URL_PARAM;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.sun.net.httpserver.HttpExchange;

import eu.ibutler.affiliatenetwork.dao.exceptions.DaoException;
import eu.ibutler.affiliatenetwork.dao.exceptions.DbAccessException;
import eu.ibutler.affiliatenetwork.dao.exceptions.NoSuchEntityException;
import eu.ibutler.affiliatenetwork.dao.impl.ShopDaoImpl;
import eu.ibutler.affiliatenetwork.dao.impl.UserDaoImpl;
import eu.ibutler.affiliatenetwork.entity.Shop;
import eu.ibutler.affiliatenetwork.entity.User;
import eu.ibutler.affiliatenetwork.http.ParsingException;
import eu.ibutler.affiliatenetwork.http.parse.QueryParser;
import eu.ibutler.affiliatenetwork.http.session.HttpSession;
import eu.ibutler.affiliatenetwork.http.session.SessionManager;
import eu.ibutler.affiliatenetwork.utils.Encrypter;
import eu.ibutler.affiliatenetwork.utils.FtlDataModel;
import eu.ibutler.affiliatenetwork.utils.FtlProcessingException;
import eu.ibutler.affiliatenetwork.utils.FtlProcessor;

@SuppressWarnings("restriction")
public class CheckUpdateProfileController extends AbstractHttpHandler {
	
	private static Logger log = Logger.getLogger(CheckUpdateProfileController.class.getName());

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		if(!exchange.getRequestMethod().equals("POST")) {
			log.debug("Attempt to send credentials not via POST");
			sendRedirect(exchange, cfg.makeUrl("DOMAIN_NAME", "UPDATE_USER_PROFILE_PAGE_URL", "WRONG_PARAM"));
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
				sendRedirect(exchange, cfg.makeUrl("DOMAIN_NAME", "UPDATE_USER_PROFILE_PAGE_URL", "WRONG_PARAM"));
				return;
			}
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
		
		User freshUser = oldUser;
		Shop freshShop = oldShop;
		Map<String, String> registerInfo;
		try {
			registerInfo = QueryParser.parseQuery(query);
			
			freshShop.setName(registerInfo.get(SHOP_NAME_PARAM));
			freshShop.setUrl(registerInfo.get(SHOP_URL_PARAM));
			
			freshUser.setEmail(registerInfo.get(EMAIL_PARAM));
			freshUser.setEncryptedPassword(Encrypter.encrypt(registerInfo.get(PASSWORD_PARAM)));
			freshUser.setFirstName(registerInfo.get(FIRST_NAME_PARAM));
			freshUser.setLastName(registerInfo.get(LAST_NAME_PARAM));

			new ShopDaoImpl().updateShop(freshShop);
			new UserDaoImpl().updateUser(freshUser);
			
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
		
		//register OK, update user in session attributes
		session.setAttribute(SESSION_USER_ATTR_NAME, freshUser);

		//create OK page
		FtlDataModel ftlData = new FtlDataModel();
		ftlData.put("name", freshUser.getEmail());
		ftlData.put("cabinetPage", cfg.makeUrl("DOMAIN_NAME", "USER_CABINET_PAGE_URL"));
		ftlData.put("logoutPage", cfg.makeUrl("DOMAIN_NAME", "LOGOUT_PAGE_URL"));
		
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

}
