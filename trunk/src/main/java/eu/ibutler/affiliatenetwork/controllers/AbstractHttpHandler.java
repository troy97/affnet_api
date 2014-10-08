package eu.ibutler.affiliatenetwork.controllers;
import java.io.IOException;
import java.io.OutputStream;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import eu.ibutler.affiliatenetwork.utils.AppConfig;

/**
 * This class adds different features to basic HttpHandler 
 * If You want to create new Handler, extend this class
 * @author Anton Lukashchuk
 *
 */
@SuppressWarnings("restriction")
public abstract class AbstractHttpHandler implements HttpHandler  {
	
	/**
	 * Application configuration class must be accessible in any handler
	 */
	protected static AppConfig cfg = AppConfig.getInstance();
	
	/**
	 * Method allows redirection to specified location
	 * don't forget to place "return;" in your context after invocation of this method
	 * @param exchange
	 * @param location
	 * @throws IOException
	 */
	protected void sendRedirect(HttpExchange exchange, String location) throws IOException {
		Headers responseHeaders = exchange.getResponseHeaders();
		responseHeaders.add("Location", location);
		exchange.sendResponseHeaders(302, 0);
		try(OutputStream out = exchange.getResponseBody()) {}
	}
	
	/**
	 * Send 403 forbidden http response.
	 * Don't forget to place "return;" in your context after invocation of this method.
	 * @param exchange
	 * @throws IOException
	 */
	protected void sendForbidden(HttpExchange exchange) throws IOException {
		String responseHtml = "403 Forbidden";
		exchange.sendResponseHeaders(403, responseHtml.getBytes("UTF-8").length);
		try(OutputStream out = exchange.getResponseBody()) {
			out.write(responseHtml.getBytes("UTF-8"));
			out.flush();
		}
	}
}
