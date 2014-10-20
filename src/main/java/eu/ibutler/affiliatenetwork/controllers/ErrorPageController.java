package eu.ibutler.affiliatenetwork.controllers;

import java.io.BufferedOutputStream;
import java.io.IOException;
//import java.io.InputStream;



import java.io.InputStream;

import org.apache.log4j.Logger;

import com.sun.net.httpserver.HttpExchange;
//import com.sun.net.httpserver.HttpHandler;










import eu.ibutler.affiliatenetwork.config.Urls;
import eu.ibutler.affiliatenetwork.http.session.HttpSession;
import eu.ibutler.affiliatenetwork.utils.FtlDataModel;
import eu.ibutler.affiliatenetwork.utils.FtlProcessingException;
import eu.ibutler.affiliatenetwork.utils.FtlProcessor;

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
														+ "</body>"
														+ "</html>";

	private static Logger log = Logger.getLogger(ErrorPageController.class.getName());
	
	@Override
	public void handleBody(HttpExchange exchange) throws IOException {
		
		try(InputStream in = exchange.getRequestBody()){}
		
		String responseHtml;
		try {
			FtlDataModel data = new FtlDataModel();
			FtlProcessor processor = new FtlProcessor();
			String link = exchange.getRequestHeaders().getFirst("Referer");
			if(link == null || link.equals("")) {
				link = Urls.fullURL(Urls.SIGNIN_PAGE_URL);
			}
			data.put("someLink", link);
			responseHtml = processor.createHtml(Links.ERROR_PAGE_FTL, data);
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
