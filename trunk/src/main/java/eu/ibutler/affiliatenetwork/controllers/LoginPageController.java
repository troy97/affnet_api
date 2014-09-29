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

/**
 * Handler responsible for login page
 * @author Anton
 *
 */
@SuppressWarnings("restriction")
public class LoginPageController extends AbstractHttpHandler {
	
	private static Logger log = Logger.getLogger(LoginPageController.class.getName());

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		
		try(InputStream in = exchange.getRequestBody()) {}
		
		//check if it's the first attempt to login,
		//if not, put "wrong" notification to dataModel
		FtlDataModel dataModel = new FtlDataModel();
		if(exchange.getRequestURI().getQuery() != null) {
			dataModel.put("wrongLoginPassword", "<font face=\"arial\" color=\"red\">wrong login/password pair, try again</font>");
		}
		
		//create html
		String responseHtml;
		try {
			responseHtml = new FtlProcessor().createHtml(LinkUtils.LOGIN_PAGE_FTL, dataModel);
		} catch (FtlProcessingException e) {
			log.error("Failed to create login page");
			sendRedirect(exchange, LinkUtils.ERROR_PAGE_CONTROLLER_FULL_URL);
			return;
		}	
		
		//send response
		try(BufferedOutputStream out = new BufferedOutputStream(exchange.getResponseBody())){
			byte[] responseBytes = responseHtml.getBytes();
			exchange.sendResponseHeaders(200, responseBytes.length);
			out.write(responseBytes);
			out.flush();
		}
	}
}
