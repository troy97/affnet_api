package eu.ibutler.affiliatenetwork.controllers;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;

import com.sun.net.httpserver.HttpExchange;

import eu.ibutler.affiliatenetwork.config.AppConfig;
import eu.ibutler.affiliatenetwork.config.Urls;
import eu.ibutler.affiliatenetwork.controllers.utils.Links;
import eu.ibutler.affiliatenetwork.utils.freemarker.FtlDataModel;
import eu.ibutler.affiliatenetwork.utils.freemarker.FtlProcessingException;
import eu.ibutler.affiliatenetwork.utils.freemarker.FtlProcessor;

/**
 * Handler responsible for login page
 * @author Anton
 *
 */
@SuppressWarnings("restriction")
@WebController("/login")
public class LoginPageController extends AbstractHttpHandler implements FreeAccess {
	
	private static AppConfig properties = AppConfig.getInstance();
	private static Logger log = Logger.getLogger(LoginPageController.class.getName());

	@Override
	public void handleBody(HttpExchange exchange) throws IOException {
		
		try(InputStream in = exchange.getRequestBody()) {}
		
		//check if it's the first attempt to login,
		//if not, put "wrong" notification to dataModel
		FtlDataModel dataModel = new FtlDataModel();
		String queryStr = exchange.getRequestURI().getQuery();
		if((queryStr != null) && queryStr.contains("wrong=true")) {
			dataModel.put("wrongCredentials", properties.get("wrongCredentials"));
		}
		
		//create html
		String responseHtml;
		try {
			dataModel.put("checkLogin", Urls.CHECK_LOGIN_URL);
			dataModel.put("registerPage", Urls.REGISTER_PAGE_URL);
			responseHtml = new FtlProcessor().createHtml(Links.LOGIN_PAGE_FTL, dataModel);
		} catch (FtlProcessingException e) {
			log.error("Failed to create login page");
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
}
