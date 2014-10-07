package eu.ibutler.affiliatenetwork.controllers;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;

import com.sun.net.httpserver.HttpExchange;

import eu.ibutler.affiliatenetwork.utils.FtlDataModel;
import eu.ibutler.affiliatenetwork.utils.FtlProcessingException;
import eu.ibutler.affiliatenetwork.utils.FtlProcessor;
import eu.ibutler.affiliatenetwork.utils.LinkUtils;

@SuppressWarnings("restriction")
@WebController("/register")
public class RegisterPageController extends AbstractHttpHandler implements FreeAccess {

	private static Logger log = Logger.getLogger(RegisterPageController.class.getName());
	
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		try(InputStream in = exchange.getRequestBody()) {}
		
		//create html
		String responseHtml;
		try {
			FtlDataModel ftlData = new FtlDataModel();
			ftlData.put("uploadPage", LinkUtils.UPLOAD_PAGE_CONTROLLER_FULL_URL);
			ftlData.put("loginPage", LinkUtils.LOGIN_PAGE_CONTROLLER_FULL_URL);
			ftlData.put("checkRegister", LinkUtils.CHECK_REGISTER_CONTROLLER_FULL_URL);
			responseHtml = new FtlProcessor().createHtml(LinkUtils.REGISTER_PAGE_FTL, ftlData);
		} catch (FtlProcessingException e) {
			log.error("Failed to create page");
			sendRedirect(exchange, LinkUtils.ERROR_PAGE_CONTROLLER_FULL_URL);
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
