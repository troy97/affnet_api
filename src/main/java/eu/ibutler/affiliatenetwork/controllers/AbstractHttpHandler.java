package eu.ibutler.affiliatenetwork.controllers;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

/**
 * This class adds different features to basic HttpHandler 
 * @author anton
 *
 */
@SuppressWarnings("restriction")
public abstract class AbstractHttpHandler implements HttpHandler  {
	
	/**
	 * Method allows redirectiong to specified location
	 * @param exchange
	 * @param location
	 * @throws IOException
	 */
	protected void sendRedirect(HttpExchange exchange, String location) throws IOException {
		Headers responseHeaders = exchange.getResponseHeaders();
		responseHeaders.add("Location", location);
		exchange.sendResponseHeaders(302, 0);
		BufferedOutputStream out = new BufferedOutputStream(exchange.getResponseBody());
		out.close();
	}
}
