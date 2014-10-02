package eu.ibutler.affiliatenetwork.controllers;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;

import com.sun.net.httpserver.HttpExchange;

import eu.ibutler.affiliatenetwork.entity.LinkUtils;
import eu.ibutler.affiliatenetwork.session.HttpSession;
import eu.ibutler.affiliatenetwork.session.SessionManager;

@SuppressWarnings("restriction")
public class LogoutPageController extends AbstractHttpHandler {

	private static Logger log = Logger.getLogger(LogoutPageController.class.getName());
	
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		
		try(InputStream in = exchange.getRequestBody()) {}
		
		log.debug("Starting logout");
		HttpSession session = (HttpSession) exchange.getAttribute(LinkUtils.EXCHANGE_SESSION_ATTR_NAME);
		if(session == null) {
			//nothing to logout
			return;
		}
		log.debug("invalidating");
		SessionManager manager = SessionManager.getInstance();
		manager.invalidateSession(session);
		
		log.debug("responding");
		//create html
		String responseHtml = "<h2>Logged out</h2>";
		
		//render login page
		try(BufferedOutputStream out = new BufferedOutputStream(exchange.getResponseBody())){
			byte[] responseBytes = responseHtml.getBytes();
			exchange.sendResponseHeaders(200, responseBytes.length);
			out.write(responseBytes);
			out.flush();
		}
	}

}
