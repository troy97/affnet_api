package eu.ibutler.affiliatenetwork.controllers;

import static eu.ibutler.affiliatenetwork.utils.LinkUtils.*;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.apache.log4j.Logger;

import com.sun.net.httpserver.HttpExchange;

import eu.ibutler.affiliatenetwork.http.ParsingException;
import eu.ibutler.affiliatenetwork.http.parse.QueryParser;
import eu.ibutler.affiliatenetwork.utils.FtlDataModel;
import eu.ibutler.affiliatenetwork.utils.FtlProcessingException;
import eu.ibutler.affiliatenetwork.utils.FtlProcessor;
import eu.ibutler.affiliatenetwork.utils.LinkUtils;

@SuppressWarnings("restriction")
@WebController("/signUp")
public class SignUpPageController extends AbstractHttpHandler implements FreeAccess {

	private static Logger log = Logger.getLogger(SignUpPageController.class.getName());
	
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		
		try(InputStream in = exchange.getRequestBody()) {}
		
		//check if it's the first attempt to login,
		//if not, put "wrong" notification to dataModel
		FtlDataModel ftlData = new FtlDataModel();
		String queryStr = exchange.getRequestURI().getQuery();
		checkErrorParams(ftlData, queryStr);
		
		//create html
		String responseHtml;
		try {
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

	/**
	 * Verify if some messages for User are to be added to FTL 
	 * @param ftlData
	 * @param queryStr
	 */
	private void checkErrorParams(FtlDataModel ftlData, String queryStr) {
		try {
			Map<String, String> params = QueryParser.parseQuery(queryStr);
			if(params.containsKey(LinkUtils.DUPLICATE_SHOP_PARAM)) {
				ftlData.put("wrongData", "<font color=\"red\">" + cfg.get("duplicateShopMsg") + "</font>");		
			} else if(params.containsKey(LinkUtils.DUPLICATE_USER_PARAM)) {
				ftlData.put("wrongData", "<font color=\"red\">" + cfg.get("duplicateUserMsg") + "</font>");	
			}  else if(params.containsKey(LinkUtils.WRONG_PARAM)) {
				ftlData.put("wrongData", "<font color=\"red\">" + cfg.get("wrongSignUpInfo") + "</font>");	
			} 
		} catch (ParsingException ignore) {}
	}

}
