package eu.ibutler.affiliatenetwork.controllers;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.sun.net.httpserver.HttpExchange;

/**
 * This controller creates status endpoint response
 * from StatusEndpoint class data
 * 
 * @author Anton Lukashchuk
 *
 */
@WebController("/status")
public class StatusPageController extends AbstractHttpHandler implements FreeAccess {
	
	/**
	 * @see com.sun.net.httpserver.HttpHandler
	 */
	@Override
	public void handleBody(HttpExchange exchange) throws IOException {
		try( InputStream in = exchange.getRequestBody(); ) {}
		String responseHtml = StatusEndpoint.updateAndGet();
		exchange.sendResponseHeaders(200, responseHtml.getBytes("UTF-8").length);
		try( BufferedOutputStream out = new BufferedOutputStream(exchange.getResponseBody()); ) {
			out.write(responseHtml.getBytes());
			out.flush();
		}	
	}//handler



}
