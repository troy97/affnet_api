package eu.ibutler.affiliatenetwork.controllers;

import static eu.ibutler.affiliatenetwork.utils.LinkUtils.*;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;

import com.sun.net.httpserver.HttpExchange;

import eu.ibutler.affiliatenetwork.entity.User;
import eu.ibutler.affiliatenetwork.http.session.HttpSession;
import eu.ibutler.affiliatenetwork.utils.FtlDataModel;
import eu.ibutler.affiliatenetwork.utils.FtlProcessingException;
import eu.ibutler.affiliatenetwork.utils.FtlProcessor;

@SuppressWarnings("restriction")
public class UpdateUserProfileController extends AbstractHttpHandler{
	
	private static Logger log = Logger.getLogger(UpdateUserProfileController.class.getName());

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		try(InputStream in = exchange.getRequestBody()) {}
		
		//get session and user object
		HttpSession session = (HttpSession) exchange.getAttribute(EXCHANGE_SESSION_ATTR_NAME);
		User user = (User) session.getAttribute(SESSION_USER_ATTR_NAME);
		
		//check if it's the first attempt,
		//if not, put "wrong" notification to dataModel
		FtlDataModel ftlData = new FtlDataModel();
		String queryStr = exchange.getRequestURI().getQuery();
		if((queryStr != null) && queryStr.contains(WRONG_PARAM)) {
			ftlData.put("wrongData", "<font color=\"red\">" + cfg.get("wrongUpdateInfo") + "</font>");
		}
		
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

}
