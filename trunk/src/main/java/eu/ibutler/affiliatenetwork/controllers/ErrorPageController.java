package eu.ibutler.affiliatenetwork.controllers;

import java.io.BufferedOutputStream;
import java.io.IOException;
//import java.io.InputStream;


import org.apache.log4j.Logger;

import com.sun.net.httpserver.HttpExchange;
//import com.sun.net.httpserver.HttpHandler;


import eu.ibutler.affiliatenetwork.entity.FtlDataModel;
import eu.ibutler.affiliatenetwork.entity.FtlProcessor;
import eu.ibutler.affiliatenetwork.entity.exceptions.FtlProcessingException;

@SuppressWarnings("restriction")
public class ErrorPageController extends AbstractHttpHandler {
	
	private static final String ERROR_PAGE_FTL = "errorPage.ftl";
	private static final String STATIC_HTML_ERROR_PAGE = "<html>"
														+ "<body>"
														+ "<h3>Something really bad happened on our server:(</h3>"
														+ "Press your browsers back button, and try again</br>"
														+ "###Main page link here###"
														+ "</body>"
														+ "</html>";

	private static Logger log = Logger.getLogger(FileDownloadController.class.getName());
	//private static AppProperties properties = AppProperties.getInstance();
	
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		
/*		InputStream in = exchange.getRequestBody();
		in.close();*/
		
		FtlDataModel data = new FtlDataModel();
		FtlProcessor processor = new FtlProcessor();
		String responseHtml;
		try {
			data.put("mainPageLink", "<font face=\"arial\" color=\"red\">Main page link</font>");
			responseHtml = processor.createHtml(ERROR_PAGE_FTL, data);
		} catch (FtlProcessingException e) {
			log.error("Failed to create error page");
			responseHtml = STATIC_HTML_ERROR_PAGE;
		}
		
		//render response
		try(BufferedOutputStream out = new BufferedOutputStream(exchange.getResponseBody())){
			byte[] responseBytes = responseHtml.getBytes();
			exchange.sendResponseHeaders(200, responseBytes.length);
			out.write(responseBytes);
			out.flush();
		}
		
	}


}
