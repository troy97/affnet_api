package eu.ibutler.affiliatenetwork.filters;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import com.sun.net.httpserver.HttpExchange;

import eu.ibutler.affiliatenetwork.http.session.HttpSession;
import eu.ibutler.affiliatenetwork.utils.LinkUtils;

/**
 * Filter that redirects to Sign In page if User is not Signed In  
 * @author Anton Lukashchuk
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
		
		if(session == null) {
			sendRedirect(exchange, cfg.makeUrl("DOMAIN_NAME", "SIGNIN_PAGE_URL"));
			return;
		} 
		
		chain.doFilter(exchange);
	}

}
