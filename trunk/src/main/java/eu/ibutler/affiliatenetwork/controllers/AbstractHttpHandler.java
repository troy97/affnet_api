package eu.ibutler.affiliatenetwork.controllers;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.log4j.Logger;

import com.google.common.base.Throwables;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import eu.ibutler.affiliatenetwork.config.AppConfig;

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
	protected Logger logger = Logger.getLogger(this.getClass().getName());
	
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
	
	/**
	 * Template method, full substitution of {@link HttpHandler#handle(HttpExchange)},
	 * called inside of real handle() method.
	 * This is done with the only purpose of printing stack traces of exceptions, that
	 * might be thrown during processing exchange object
	 * @param exchange
	 * @throws IOException
	 */
	protected abstract void handleBody(HttpExchange exchange) throws IOException;
	
	/**
	 * @see HttpHandler#handle(HttpExchange)
	 */
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		try {
			this.handleBody(exchange);
		} catch (Exception e) {
			logger.error(Throwables.getStackTraceAsString(e));
			throw e;
		}
	}
	
	
}
