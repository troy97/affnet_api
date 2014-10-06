package eu.ibutler.affiliatenetwork.filters;

import java.io.BufferedOutputStream;
import java.io.IOException;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.Filter;

import eu.ibutler.affiliatenetwork.utils.AppConfig;

/**
 * This class adds different features to basic Filter 
 * If You want to create new Filter, extend this class
 * @author Anton Lukashchuk
 *
 */
@SuppressWarnings("restriction")
public abstract class AbstractFilter extends Filter  {
	
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
		BufferedOutputStream out = new BufferedOutputStream(exchange.getResponseBody());
		out.close();
	}
}
