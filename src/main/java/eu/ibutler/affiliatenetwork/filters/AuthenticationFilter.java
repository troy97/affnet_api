package eu.ibutler.affiliatenetwork.filters;

import java.io.BufferedOutputStream;
import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;

import eu.ibutler.affiliatenetwork.entity.LinkUtils;
import eu.ibutler.affiliatenetwork.session.HttpSession;

/**
 * Filter that performs login checks and redirects accordingly
 * If user is logged in and tries to log in again render appropriate notification
 * If user is not logged in and tries to get to login page - ok, pass through
 * If user is not logged in and tries to to get to any page accept login - redirect to login page
 * If user logged in and tries to get to any page accept login page - ok, pass through
 * @author Anton
 *
 */
@SuppressWarnings("restriction")
public class AuthenticationFilter extends AbstractFilter {

	@Override
	public String description() {
		return null;
	}

	@Override
	public void doFilter(HttpExchange exchange, Chain chain) throws IOException {
		HttpSession session = (HttpSession) exchange.getAttribute("session");
		String requestPath = exchange.getRequestURI().getPath();
		
		if(session == null) {
			if(requestPath.equals(LinkUtils.LOGIN_PAGE_URL) || requestPath.equals(LinkUtils.CHECK_LOGIN_URL)) {
				chain.doFilter(exchange);
			} else {
				sendRedirect(exchange, LinkUtils.LOGIN_PAGE_CONTROLLER_FULL_URL);
			}
			return;
		}
		
		//If user already logged in and wants to get to login page again
		if(requestPath.equals(LinkUtils.LOGIN_PAGE_URL) || requestPath.equals(LinkUtils.CHECK_LOGIN_URL)) {
			String responseHtml = "You're already logged in, choose another page";
			try(BufferedOutputStream out = new BufferedOutputStream(exchange.getResponseBody())){
				byte[] responseBytes = responseHtml.getBytes();
				exchange.sendResponseHeaders(200, responseBytes.length);
				out.write(responseBytes);
				out.flush();
			}
			return;
		}
		
		chain.doFilter(exchange);
	}

}
