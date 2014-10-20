package eu.ibutler.affiliatenetwork.controllers;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;

import com.sun.net.httpserver.HttpExchange;

import eu.ibutler.affiliatenetwork.config.Urls;
import eu.ibutler.affiliatenetwork.utils.FtlDataModel;
import eu.ibutler.affiliatenetwork.utils.FtlProcessingException;
import eu.ibutler.affiliatenetwork.utils.FtlProcessor;

@SuppressWarnings("restriction")
@WebController("/register")
public class RegisterPageController extends AbstractHttpHandler implements FreeAccess {

	private static Logger log = Logger.getLogger(RegisterPageController.class.getName());
	
	@Override
	public void handleBody(HttpExchange exchange) throws IOException {
		try(InputStream in = exchange.getRequestBody()) {}
		
		//create html
		String responseHtml;
		try {
			FtlDataModel ftlData = new FtlDataModel();
			ftlData.put("uploadPage", Urls.fullURL(Urls.UPLOAD_PAGE_URL));
			ftlData.put("loginPage", Urls.fullURL(Urls.LOGIN_PAGE_URL));
			ftlData.put("checkRegister", Urls.fullURL(Urls.CHECK_REGISTER_URL));
			responseHtml = new FtlProcessor().createHtml(Links.REGISTER_PAGE_FTL, ftlData);
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

}
