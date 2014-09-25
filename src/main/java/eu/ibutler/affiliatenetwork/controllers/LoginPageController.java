package eu.ibutler.affiliatenetwork.controllers;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import eu.ibutler.affiliatenetwork.entity.FreeMakerConfig;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

@SuppressWarnings("restriction")
public class LoginPageController implements HttpHandler {
	
	private static final String LOGIN_PAGE_FTL = "loginPage.ftl";
	private static final String ERROR_PAGE_FTL = "errorPage.ftl";
	
	private static Logger log = Logger.getLogger(LoginPageController.class.getName());

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		
		BufferedInputStream in = new BufferedInputStream(exchange.getRequestBody());
		in.close();

		Template view = createView();
		if(view == null){
			//to do: fatal error, do something here, maybe generate static error page.
		}
		
		Map<String, Object> root = new HashMap<>();
		//check if it's not the first attempt to login
		if(exchange.getRequestURI().getQuery() != null) {
			root.put("wrongLoginPassword", "<font face=\"arial\" color=\"red\">wrong login/password pair, try again</font>");
		}
		
		//fill and send response
		try(BufferedOutputStream out = new BufferedOutputStream(exchange.getResponseBody())){
			StringWriter writer = new StringWriter();
			view.process(root, writer);
			String responseHtml = writer.toString();
			byte[] responseBytes = responseHtml.getBytes();
			exchange.sendResponseHeaders(200, responseBytes.length);
			out.write(responseBytes);
			out.flush();
		}catch(TemplateException e){
			log.error("Error merging view and model");
		}
	}

	private Template createView() {
		Configuration cfg = FreeMakerConfig.getInstance();
		Template view = null;
		try {
			view = cfg.getTemplate(LOGIN_PAGE_FTL);
		} catch (IOException e) {
			log.error("Can't open " + LOGIN_PAGE_FTL);
			try {
				view = cfg.getTemplate(ERROR_PAGE_FTL);
			} catch (IOException ee) {
				log.fatal("Can't open " + ERROR_PAGE_FTL);
			}
		}
		return view;
	}
	

}
