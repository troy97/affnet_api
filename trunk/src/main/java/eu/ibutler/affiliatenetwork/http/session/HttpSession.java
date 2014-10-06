package eu.ibutler.affiliatenetwork.http.session;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.RandomStringUtils;

/**
 * Session class
 * All time variables are stored in milliseconds
 * @author anton
 *
 */
public class HttpSession {
	private final long creationTime;
	private final String JSESSIONID;
	
	/**
	 * Time (milliseconds since EPOCH) of receiving last request, containing jsessionid of this session,
	 * set by ValidationFilter only.
	 */
	private long lastAccessedTime = 0;
	
	/**
	 * Default maximum session inactivity time, can be setup on program startUp
	 * with setDefaultSessionInactiveInterval() 
	 */
	private static long defaultInactiveInterval = 30*60*1000; //30 minutes
	
	/**
	 * If session was inactive for more than specified amount of time,
	 * next request with this session ID will invalidate and delete this session. 
	 * This field allows each session to have it's own inactivity interval.
	 */
	private long maxInactiveInterval = defaultInactiveInterval;
	
	private Map<String, Object> attributes = new ConcurrentHashMap<>();
	
	public HttpSession() {
		this.creationTime = System.currentTimeMillis();
		this.JSESSIONID = RandomStringUtils.randomAlphanumeric(SessionManager.SESSION_ID_LENGTH);
	}

	/**
	 * Time (milliseconds since EPOCH) when constructor of this session object was invoked
	 * @return
	 */
	public long getCreationTime() {
		return this.creationTime;
	}

	/**
	 * Get JSESSIONID of this session
	 * @return JSESSIONID
	 */
	public String getId() {
		return this.JSESSIONID;
	}

	/**
	 * Returns time (milliseconds since EPOCH)
	 * since last request with this jsessionid
	 * @return time, 0 means this session is new
	 */
	public long getLastAccessedTime() {
		return this.lastAccessedTime;
	}
	
	void setLastAccessedTime(long timeInMilis) {
		this.lastAccessedTime = timeInMilis;
	}

	public void setMaxInactiveInterval(int interval) {
		this.maxInactiveInterval = interval;
	}

	public long getMaxInactiveInterval() {
		return this.maxInactiveInterval;
	}

	public Object getAttribute(String name) {
		return this.attributes.get(name);
	}

	public Set<String> getAttributeNames() {
		return this.attributes.keySet();
	}

	public void setAttribute(String name, Object value) {
		this.attributes.put(name, value);
	}

	public void removeAttribute(String name) {
		this.attributes.remove(name);
	}

	void invalidate() {
		this.attributes = null;
	}

	/**
	 * Session considered new until first request containing
	 * jsessionid of this session arrives. Upon receiving such request ValidationFilter
	 * will set lastAccessedTime to non-zero value. This ensures that session is considered
	 * new till some browser has proper cookie to request this session object
	 * @return true is session is new, false otherwise
	 */
	public boolean isNew() {
		return (this.lastAccessedTime == 0);
	}
	
	public static void setDefaultSessionInactiveInterval(long milliseconds) {
		defaultInactiveInterval = milliseconds;
	}
}
