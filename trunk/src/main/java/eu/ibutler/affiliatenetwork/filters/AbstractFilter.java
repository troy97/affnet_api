package eu.ibutler.affiliatenetwork.filters;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.log4j.Logger;

import com.google.common.base.Throwables;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.Filter.Chain;

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
	protected Logger logger = Logger.getLogger(this.getClass().getName());
	
	/**
	 * Template method, full substitution of {@link Filter#doFilter(HttpExchange, Chain)},
	 * called inside of real doFilter() method.
	 * This is done with the only purpose of printing stack traces of exceptions, that
	 * might be thrown during processing exchange object
	 * @param exchange, chain
	 * @throws IOException
	 */
	public abstract void doFilterBody(HttpExchange exchange, Chain chain) throws IOException;
	
	@Override
	public void doFilter(HttpExchange exchange, Chain chain) throws IOException {
		try {
			this.doFilterBody(exchange, chain);
		} catch (Exception e) {
			logger.error(Throwables.getStackTraceAsString(e));
			throw e;
		}
	}
	
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
