package eu.ibutler.affiliatenetwork.controllers;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;

import com.sun.net.httpserver.HttpExchange;

import eu.ibutler.affiliatenetwork.entity.FtlDataModel;
import eu.ibutler.affiliatenetwork.entity.FtlProcessor;
import eu.ibutler.affiliatenetwork.entity.LinkUtils;
import eu.ibutler.affiliatenetwork.entity.exceptions.FtlProcessingException;

@SuppressWarnings("restriction")
public class RegisterPageController extends AbstractHttpHandler {

	private static Logger log = Logger.getLogger(RegisterPageController.class.getName());
	
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		try(InputStream in = exchange.getRequestBody()) {}
		
		//create html
		String responseHtml;
		try {
			responseHtml = new FtlProcessor().createHtml(LinkUtils.REGISTER_PAGE_FTL, new FtlDataModel());
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
