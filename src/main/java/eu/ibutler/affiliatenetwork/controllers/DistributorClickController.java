package eu.ibutler.affiliatenetwork.controllers;

import java.io.IOException;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;

import eu.ibutler.affiliatenetwork.http.ParsingException;
import eu.ibutler.affiliatenetwork.http.parse.Parser;

@SuppressWarnings("restriction")
@WebController("/distributorClick")
public class DistributorClickController extends AbstractHttpHandler implements FreeAccess {

	@Override
	public void handleBody(HttpExchange exchange) throws IOException {
		//?affId=0&productId=34&manufacturerCode=hp34ghRHP
		if(!exchange.getRequestMethod().equals("GET")) {
			logger.debug("Click not with GET");
			sendRedirect(exchange, cfg.makeUrl("DOMAIN_NAME", "ERROR_PAGE_URL"));
			return;
		}
		
		Map<String, String> linkParams;
		try {
			linkParams = Parser.parseQuery(exchange.getRequestURI().getQuery());
		} catch (ParsingException e) {
			logger.debug("Unable to parse query");
			sendRedirect(exchange, cfg.makeUrl("DOMAIN_NAME", "ERROR_PAGE_URL"));
			return;
		}
		
		
		
	}

}
