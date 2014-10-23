package eu.ibutler.affiliatenetwork.controllers;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;

import com.sun.net.httpserver.HttpExchange;

import eu.ibutler.affiliatenetwork.config.Urls;
import eu.ibutler.affiliatenetwork.controllers.utils.Links;
import eu.ibutler.affiliatenetwork.http.session.HttpSession;
import eu.ibutler.affiliatenetwork.http.session.SessionManager;
import eu.ibutler.affiliatenetwork.utils.freemarker.FtlDataModel;
import eu.ibutler.affiliatenetwork.utils.freemarker.FtlProcessingException;
import eu.ibutler.affiliatenetwork.utils.freemarker.FtlProcessor;

/**
 * Controller to handle logout process.
 * Invalidates session, deletes it and renders 
 * logout page
 * @author anton
 *
 */
@SuppressWarnings("restriction")
@WebController("/logout")
public class LogoutPageController extends AbstractHttpHandler implements RestrictedAccess {

	private static Logger log = Logger.getLogger(LogoutPageController.class.getName());
	
	@Override
	public void handleBody(HttpExchange exchange) throws IOException {
		
		try(InputStream in = exchange.getRequestBody()) {}
		
		log.debug("Starting logout");
		HttpSession session = (HttpSession) exchange.getAttribute(Links.EXCHANGE_SESSION_ATTR_NAME);
		if(session == null) {
			//nothing to logout
			return;
		}
		
		log.debug("invalidating");
		SessionManager manager = SessionManager.getInstance();
		manager.invalidateSession(session);
		
		log.debug("responding");
		
		//create html
		String responseHtml;
		try {
			FtlDataModel ftlData = new FtlDataModel();
			ftlData.put("goodByeMessage", cfg.get("goodByeMessage"));
			ftlData.put("userSignInPage", Urls.SIGNIN_PAGE_URL);
			responseHtml = new FtlProcessor().createHtml(Links.LOGOUT_PAGE_FTL, ftlData);
		} catch (FtlProcessingException e) {
			log.error("Failed to create page");
			sendRedirect(exchange, Urls.fullURL(Urls.ERROR_PAGE_URL));
			return;
		}	
		
		//render login page
		try(BufferedOutputStream out = new BufferedOutputStream(exchange.getResponseBody())){
			byte[] responseBytes = responseHtml.getBytes();
			exchange.sendResponseHeaders(200, responseBytes.length);
			out.write(responseBytes);
			out.flush();
		}
	}

}
