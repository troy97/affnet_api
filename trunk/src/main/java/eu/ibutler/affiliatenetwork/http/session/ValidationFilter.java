package eu.ibutler.affiliatenetwork.http.session;

import java.io.IOException;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.Filter;

/**
 * All requests to server pages, that require session feature must pass through this filter.
 * This filter must be first in a chain of filters/handlers that use sessions.
 * 
 * Filter verifies if any session object is linked with this exchange object,
 * if so - attaches "session" attribute with HttpSession object to exchange and passes this request further along chain
 * if not - attaches NULL to exchange under "session" attribute name
 * @author Anton Lukashchuk
 *
 */
@SuppressWarnings("restriction")
public class ValidationFilter extends Filter{
	
	private static Logger log = Logger.getLogger(ValidationFilter.class.getName());

	@Override
	public String description() {
		return "Cheks if some JSESSIONID cookie attached to HttpExchange object"
				+ "creates and attaches session object to HttpExchange attributes under name \"session\"";
	}
	
	@Override
	public void doFilter(HttpExchange exchange, Chain chain) throws IOException {
		SessionManager manager = SessionManager.getInstance();
		HttpSession session = manager.getSession(exchange, false);
		session = sessionService(session);
		
		exchange.setAttribute("session", session);
		chain.doFilter(exchange);
	}
	
	/**
	 * If session is NULL do nothing, return NULL
	 * If session is new, set lastAccessedTime to current time and return
	 * If session expired, invalidate it and return NULL
	 * If session is not expired update its lastAccessTime with current value and return
	 * @param session
	 * @return
	 */
	private HttpSession sessionService(HttpSession session) {
		if(session==null) {
			return null;
		} else if(session.isNew()) {
			session.setLastAccessedTime(System.currentTimeMillis());
			return session;
		} 
		long sessionInactive = System.currentTimeMillis() - session.getLastAccessedTime();
		if(sessionInactive > session.getMaxInactiveInterval()) {
			SessionManager.getInstance().invalidateSession(session);
			log.debug("Session " + session.getId() +", invalidated due to long inactivity");
			return null;
		} else {
			session.setLastAccessedTime(System.currentTimeMillis());
		}
		return session;
	}

}
