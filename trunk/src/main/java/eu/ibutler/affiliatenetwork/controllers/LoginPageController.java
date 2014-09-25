package eu.ibutler.affiliatenetwork.controllers;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;

import org.apache.log4j.Logger;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import eu.ibutler.affiliatenetwork.entity.FtlDataModel;
import eu.ibutler.affiliatenetwork.entity.FtlProcessor;
import eu.ibutler.affiliatenetwork.entity.exceptions.FtlProcessingException;

@SuppressWarnings("restriction")
public class LoginPageController extends AbstractHttpHandler {
	
	private static final String LOGIN_PAGE_FTL = "loginPage.ftl";
	private static final String ERROR_PAGE_CONTROLLER_FULL_URL = "http://localhost:8080/affiliatenetwork/error";
	
	private static Logger log = Logger.getLogger(LoginPageController.class.getName());

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		
		BufferedInputStream in = new BufferedInputStream(exchange.getRequestBody());
		in.close();
		
		//check if it's the first attempt to login,
		//if not, put "wrong" notification to dataModel
		FtlDataModel dataModel = new FtlDataModel();
		if(exchange.getRequestURI().getQuery() != null) {
			dataModel.put("wrongLoginPassword", "<font face=\"arial\" color=\"red\">wrong login/password pair, try again</font>");
		}
		
		FtlProcessor processor = new FtlProcessor();
		String responseHtml;
		try {
			responseHtml = processor.createHtml(LOGIN_PAGE_FTL, dataModel);
		} catch (FtlProcessingException e) {
			log.error("Fail to create login page");
			sendRedirect(exchange, ERROR_PAGE_CONTROLLER_FULL_URL);
			return;
		}	
		
		try(BufferedOutputStream out = new BufferedOutputStream(exchange.getResponseBody())){
			byte[] responseBytes = responseHtml.getBytes();
			exchange.sendResponseHeaders(200, responseBytes.length);
			out.write(responseBytes);
			out.flush();
		}
	}
}
