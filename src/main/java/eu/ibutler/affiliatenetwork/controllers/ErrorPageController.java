package eu.ibutler.affiliatenetwork.controllers;

import java.io.BufferedOutputStream;
import java.io.IOException;
//import java.io.InputStream;



import java.io.InputStream;

import org.apache.log4j.Logger;

import com.sun.net.httpserver.HttpExchange;
//import com.sun.net.httpserver.HttpHandler;







import eu.ibutler.affiliatenetwork.utils.FtlDataModel;
import eu.ibutler.affiliatenetwork.utils.FtlProcessingException;
import eu.ibutler.affiliatenetwork.utils.FtlProcessor;
import eu.ibutler.affiliatenetwork.utils.LinkUtils;

/**
 * This controller generates error page.
 * First tries to do it using FTL template and if failed,
 * generates static html
 * @author Anton
 *
 */
@SuppressWarnings("restriction")
@WebController("/error")
public class ErrorPageController extends AbstractHttpHandler implements FreeAccess {
	
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
		
		try(InputStream in = exchange.getRequestBody()){}
		
		FtlDataModel data = new FtlDataModel();
		FtlProcessor processor = new FtlProcessor();
		String responseHtml;
		try {
			data.put("mainPageLink", "<a href=\"" + LinkUtils.UPLOAD_PAGE_CONTROLLER_FULL_URL + "\">Upload page</a>");
			responseHtml = processor.createHtml(LinkUtils.ERROR_PAGE_FTL, data);
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
