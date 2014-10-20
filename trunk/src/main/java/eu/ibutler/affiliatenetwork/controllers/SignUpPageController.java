package eu.ibutler.affiliatenetwork.controllers;

import static eu.ibutler.affiliatenetwork.controllers.Links.*;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.apache.log4j.Logger;

import com.sun.net.httpserver.HttpExchange;

import eu.ibutler.affiliatenetwork.config.Urls;
import eu.ibutler.affiliatenetwork.http.ParsingException;
import eu.ibutler.affiliatenetwork.http.parse.Parser;
import eu.ibutler.affiliatenetwork.utils.FtlDataModel;
import eu.ibutler.affiliatenetwork.utils.FtlProcessingException;
import eu.ibutler.affiliatenetwork.utils.FtlProcessor;

@SuppressWarnings("restriction")
@WebController("/signUp")
public class SignUpPageController extends AbstractHttpHandler implements FreeAccess {

	private static Logger log = Logger.getLogger(SignUpPageController.class.getName());
	
	@Override
	public void handleBody(HttpExchange exchange) throws IOException {
		
		try(InputStream in = exchange.getRequestBody()) {}
		
		//check if it's the first attempt to login,
		//if not, put "wrong" notification to dataModel
		FtlDataModel ftlData = new FtlDataModel();
		String queryStr = exchange.getRequestURI().getQuery();
		checkErrorParams(ftlData, queryStr);
		
		//create html
		String responseHtml;
		try {
			ftlData.put("uploadPage", Urls.fullURL(Urls.UPLOAD_PAGE_URL));
			ftlData.put("signInPage", Urls.fullURL(Urls.SIGNIN_PAGE_URL));
			ftlData.put("checkSignUp", Urls.fullURL(Urls.CHECK_SIGNUP_URL));
			ftlData.put("email", EMAIL_PARAM_NAME);
			ftlData.put("password", PASSWORD_PARAM_NAME);
			ftlData.put("firstName", FIRST_NAME_PARAM_NAME);
			ftlData.put("lastName", LAST_NAME_PARAM_NAME);
			ftlData.put("shopName", SHOP_NAME_PARAM_NAME);
			ftlData.put("shopUrl", SHOP_URL_PARAM_NAME);
			responseHtml = new FtlProcessor().createHtml(Links.SIGNUP_PAGE_FTL, ftlData);
		} catch (FtlProcessingException e) {
			log.error("Failed to create page");
			sendRedirect(exchange, Urls.fullURL(Urls.ERROR_PAGE_URL));
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
			if(params.containsKey(DUPLICATE_SHOP_PARAM_NAME)) {
				ftlData.put("wrongData", cfg.get("duplicateShopMsg"));		
			} else if(params.containsKey(DUPLICATE_USER_PARAM_NAME)) {
				ftlData.put("wrongData", cfg.get("duplicateUserMsg"));	
			}  else if(params.containsKey(ERROR_PARAM_NAME)) {
				ftlData.put("wrongData", cfg.get("wrongSignUpInfo"));	
			} 
		} catch (ParsingException ignore) {}
	}

}
