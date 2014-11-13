package eu.ibutler.affiliatenetwork.http.session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.sun.net.httpserver.HttpExchange;

/**
 * Class for managing session on com.sun.net.httpserver
 * 
 * @author Anton Lukashchuk
 *
 */
public class SessionManager {
	
	private static Logger logger = Logger.getLogger(SessionManager.class.getName());
	
	static final int SESSION_ID_LENGTH = 16;
	
	/**
	 * Value of cookie Path attribute to be stored in clients browser
	 * Default is "/" means cookie will be sent to any path on this server 
	 */
	private static String cookiePath = "/";
	private static SessionManager singleton = null;
	private Map<String, HttpSession> sessions = new ConcurrentHashMap<String, HttpSession>();
	
	private SessionManager() {}
	
	/**
	 * Returns singleton instance
	 * @return SessionManager object
	 */
	public static SessionManager getInstance() {
		if(singleton == null) {
			singleton = new SessionManager();
		}
		return singleton;
	}
	
	public static void setCookiePath(String path) {
		cookiePath = path;
	}
	
	/**
	 * <p>Checks if there's a session for this exchange</p>
	 * <p>Returns HttpSession implementation if session was found</p>
	 * <p>Returns newly created Session object if there's no valid
	 * session ID and "create" is set to true</p>
	 * <p>Returns NULL if no session found and "create" is set to false</p>
	 * @param exchange
	 * @param create
	 * @return HttpSession implementation or NULL
	 */
	public HttpSession getSession(HttpExchange exchange, boolean create) {
		String jSessionId = parseSessionId(exchange);
		if(jSessionId == null || (!this.sessions.containsKey(jSessionId))) {
			if(create == true) {
				HttpSession freshSession = new HttpSession();
				addSession(freshSession);
				setCookieHeader(exchange, freshSession);
				return freshSession;
			} else {
				return null;
			}
		} 
		return this.sessions.get(jSessionId);
	}
	
	/**
	 * Add new session to sessions Map
	 * @param session
	 */
	private void addSession(HttpSession session) {
		this.sessions.put(session.getId(), session);
	}
	
	/**
	 * Parses jsessionid from given exchange object,
	 * returns jsessionid value or null
	 * @param exchange
	 * @return jsessionid or null
	 */
	private String parseSessionId(HttpExchange exchange) {
		String result = null;
		try {
			String cookieHeader = exchange.getRequestHeaders().getFirst("Cookie");
			String tmp = cookieHeader.split("JSESSIONID=")[1];
			result = tmp.substring(0, SESSION_ID_LENGTH);
		} catch (Exception e) {
			logger.debug("No JSESSIONID found");
			result = null;
		}
		return result;
	}
	
	/**
	 * Sets JSESSIONID cookie in response headers 
	 * for given session and path. Cookie life time is not set.
	 * @param exchange
	 */
	private void setCookieHeader(HttpExchange exchange, HttpSession session) {
		String cookie = "JSESSIONID=" + session.getId() + "; Path=" + cookiePath;
		exchange.getResponseHeaders().add("Set-Cookie", cookie);
	}
	
	/**
	 * Invalidates session and deletes it
	 * @param session to delete
	 */
	public void invalidateSession(HttpSession session) {
		session.invalidate();
		this.sessions.remove(session.getId());
	}
}
