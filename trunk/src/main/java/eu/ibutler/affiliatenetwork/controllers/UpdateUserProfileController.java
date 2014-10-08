package eu.ibutler.affiliatenetwork.controllers;

import static eu.ibutler.affiliatenetwork.utils.LinkUtils.*;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.apache.log4j.Logger;

import com.sun.net.httpserver.HttpExchange;

import eu.ibutler.affiliatenetwork.dao.exceptions.DbAccessException;
import eu.ibutler.affiliatenetwork.dao.exceptions.NoSuchEntityException;
import eu.ibutler.affiliatenetwork.dao.impl.ShopDaoImpl;
import eu.ibutler.affiliatenetwork.entity.Shop;
import eu.ibutler.affiliatenetwork.entity.User;
import eu.ibutler.affiliatenetwork.http.ParsingException;
import eu.ibutler.affiliatenetwork.http.parse.Parser;
import eu.ibutler.affiliatenetwork.http.session.HttpSession;
import eu.ibutler.affiliatenetwork.utils.FtlDataModel;
import eu.ibutler.affiliatenetwork.utils.FtlProcessingException;
import eu.ibutler.affiliatenetwork.utils.FtlProcessor;

@SuppressWarnings("restriction")
@WebController("/updateProfile")
public class UpdateUserProfileController extends AbstractHttpHandler implements RestrictedAccess {
	
	private static Logger log = Logger.getLogger(UpdateUserProfileController.class.getName());

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		try(InputStream in = exchange.getRequestBody()) {}
		
		//get session and user object
		HttpSession session = (HttpSession) exchange.getAttribute(EXCHANGE_SESSION_ATTR_NAME);
		User user = (User) session.getAttribute(SESSION_USER_ATTR_NAME);
		Shop shop = null;
		try {
			shop = new ShopDaoImpl().selectById(user.getShopId());
		} catch (DbAccessException | NoSuchEntityException e) {
			log.debug("Unable to get Shop instance from DAO " + e.getClass().getName());
			sendRedirect(exchange, cfg.makeUrl("DOMAIN_NAME", "ERROR_PAGE_URL"));
			return;
		} 
		
		//check if it's the first attempt,
		//if not, put "wrong" notification to dataModel
		FtlDataModel ftlData = new FtlDataModel();
		String queryStr = exchange.getRequestURI().getQuery();
		checkErrorParams(ftlData, queryStr);
		
		//create html
		String responseHtml;
		try {
			ftlData.put("name", user.getEmail());
			
			ftlData.put("checkUpdate", cfg.makeUrl("DOMAIN_NAME", "CHECK_UPDATE_PROFILE_URL"));
			ftlData.put("cabinetPage", cfg.makeUrl("DOMAIN_NAME", "USER_CABINET_PAGE_URL"));
			ftlData.put("logoutPage", cfg.makeUrl("DOMAIN_NAME", "LOGOUT_PAGE_URL"));
			
			ftlData.put("email", EMAIL_PARAM);
			ftlData.put("password", PASSWORD_PARAM);
			ftlData.put("firstName", FIRST_NAME_PARAM);
			ftlData.put("lastName", LAST_NAME_PARAM);
			ftlData.put("shopName", SHOP_NAME_PARAM);
			ftlData.put("shopUrl", SHOP_URL_PARAM);
			
			ftlData.put("shopObject", shop);
			ftlData.put("userObject", user);
			responseHtml = new FtlProcessor().createHtml(cfg.get("UPDATE_USER_PROFILE_FTL"), ftlData);
		} catch (FtlProcessingException e) {
			log.error("Failed to create page");
			sendRedirect(exchange, cfg.makeUrl("DOMAIN_NAME", "ERROR_PAGE_URL"));
			return;
		}	
		
		//render login page
		try(BufferedOutputStream out = new BufferedOutputStream(exchange.getResponseBody())){
			byte[] responseBytes = responseHtml.getBytes();
			exchange.sendResponseHeaders(200, responseBytes.length);
			out.write(responseBytes);
			out.flush();
		}
	}
	
	
	/**
	 * Verify if some messages for User are to be added to FTL 
	 * @param ftlData
	 * @param queryStr
	 */
	private void checkErrorParams(FtlDataModel ftlData, String queryStr) {
		if(queryStr == null) {
			return;
		}
		try {
			Map<String, String> params = Parser.parseQuery(queryStr);
			if(params.containsKey(DUPLICATE_SHOP_PARAM)) {
				ftlData.put("wrongData", cfg.get("duplicateShopMsg"));		
			} else if(params.containsKey(DUPLICATE_USER_PARAM)) {
				ftlData.put("wrongData", cfg.get("duplicateUserMsg"));	
			}  else if(params.containsKey(WRONG_PARAM)) {
				ftlData.put("wrongData", cfg.get("wrongUpdateInfo"));	
			} 
		} catch (ParsingException ignore) {}
	}

}
