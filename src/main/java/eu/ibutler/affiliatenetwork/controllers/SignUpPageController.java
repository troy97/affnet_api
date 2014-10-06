package eu.ibutler.affiliatenetwork.controllers;

import static eu.ibutler.affiliatenetwork.utils.LinkUtils.*;


import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;

import com.sun.net.httpserver.HttpExchange;

import eu.ibutler.affiliatenetwork.utils.FtlDataModel;
import eu.ibutler.affiliatenetwork.utils.FtlProcessingException;
import eu.ibutler.affiliatenetwork.utils.FtlProcessor;

@SuppressWarnings("restriction")
public class SignUpPageController extends AbstractHttpHandler{

	private static Logger log = Logger.getLogger(SignUpPageController.class.getName());
	
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		
		try(InputStream in = exchange.getRequestBody()) {}
		
		//create html
		String responseHtml;
		try {
			FtlDataModel ftlData = new FtlDataModel();
			ftlData.put("uploadPage", cfg.makeUrl("DOMAIN_NAME", "UPLOAD_PAGE_URL"));
			ftlData.put("signInPage", cfg.makeUrl("DOMAIN_NAME", "SIGNIN_PAGE_URL"));
			ftlData.put("checkSignUp", cfg.makeUrl("DOMAIN_NAME", "CHECK_SIGNUP_URL"));
			ftlData.put("email", EMAIL_PARAM);
			ftlData.put("password", PASSWORD_PARAM);
			ftlData.put("firstName", FIRST_NAME_PARAM);
			ftlData.put("lastName", LAST_NAME_PARAM);
			ftlData.put("shopName", SHOP_NAME_PARAM);
			ftlData.put("shopUrl", SHOP_URL_PARAM);
			responseHtml = new FtlProcessor().createHtml(cfg.get("SIGNUP_PAGE_FTL"), ftlData);
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
