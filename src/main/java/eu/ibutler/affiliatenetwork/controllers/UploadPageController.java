package eu.ibutler.affiliatenetwork.controllers;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.StringWriter;

import org.apache.log4j.Logger;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import eu.ibutler.affiliatenetwork.entity.FreeMakerConfig;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

@SuppressWarnings("restriction")
public class UploadPageController implements HttpHandler {

	private static final String UPLOAD_PAGE_FTL = "uploadPage.ftl";
	private static final String ERROR_PAGE_FTL = "errorPage.ftl";
	
	private static Logger log = Logger.getLogger(LoginPageController.class.getName());
	
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		BufferedInputStream in = new BufferedInputStream(exchange.getRequestBody());
		in.close();

		Configuration cfg = FreeMakerConfig.getInstance();
		Template view = null;
		try {
			view = cfg.getTemplate(UPLOAD_PAGE_FTL);
		} catch (IOException e) {
			log.error("Can't open " + UPLOAD_PAGE_FTL);
			try {
				view = cfg.getTemplate(ERROR_PAGE_FTL);
			} catch (IOException ee) {
				log.fatal("Can't open " + ERROR_PAGE_FTL);
			}
		}
		if(view == null){
			log.error("Error creating view");
		}
		
		try(BufferedOutputStream out = new BufferedOutputStream(exchange.getResponseBody())){
			StringWriter writer = new StringWriter();
			view.process(null, writer);
			String responseHtml = writer.toString();
			byte[] responseBytes = responseHtml.getBytes();
			exchange.sendResponseHeaders(200, responseBytes.length);
			out.write(responseBytes);
			out.flush();
		}catch(TemplateException e){
			log.error("Error merging view and model");
		}
	}
	

}
