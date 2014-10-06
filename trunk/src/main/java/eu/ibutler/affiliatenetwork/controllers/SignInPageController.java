package eu.ibutler.affiliatenetwork.controllers;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;

import com.sun.net.httpserver.HttpExchange;

import eu.ibutler.affiliatenetwork.utils.AppConfig;
import eu.ibutler.affiliatenetwork.utils.FtlDataModel;
import eu.ibutler.affiliatenetwork.utils.FtlProcessingException;
import eu.ibutler.affiliatenetwork.utils.FtlProcessor;
import eu.ibutler.affiliatenetwork.utils.LinkUtils;

@SuppressWarnings("restriction")
public class SignInPageController extends AbstractHttpHandler {
	private static AppConfig cfg = AppConfig.getInstance();
	private static Logger log = Logger.getLogger(SignInPageController.class.getName());		

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		try(InputStream in = exchange.getRequestBody()) {}
		
		//check if it's the first attempt to login,
		//if not, put "wrong" notification to dataModel
		FtlDataModel dataModel = new FtlDataModel();
		String queryStr = exchange.getRequestURI().getQuery();
		if((queryStr != null) && queryStr.contains(LinkUtils.WRONG_PARAM)) {
			dataModel.put("wrongCredentials", "<font color=\"red\">" + cfg.get("wrongCredentials") + "</font>");
		}
		
		//create html
		String responseHtml;
		try {
			dataModel.put("email", LinkUtils.EMAIL_PARAM);
			dataModel.put("password", LinkUtils.PASSWORD_PARAM);
			dataModel.put("checkSignIn", cfg.makeUrl("DOMAIN_NAME", "CHECK_SIGNIN_URL"));
			dataModel.put("signUpPage", cfg.makeUrl("DOMAIN_NAME", "SIGNUP_PAGE_URL"));
			responseHtml = new FtlProcessor().createHtml(cfg.get("SIGNIN_PAGE_FTL"), dataModel);
		} catch (FtlProcessingException e) {
			log.error("Failed to create login page");
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
