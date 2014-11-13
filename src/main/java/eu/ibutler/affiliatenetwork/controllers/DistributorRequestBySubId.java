package eu.ibutler.affiliatenetwork.controllers;

import java.io.IOException;
import java.util.Map;

import com.google.common.base.Throwables;
import com.sun.net.httpserver.HttpExchange;

import eu.ibutler.affiliatenetwork.http.Parser;

@WebController("/getBySubId")
public class DistributorRequestBySubId extends AbstractHttpHandler implements FreeAccess {

	@Override
	protected void handleBody(HttpExchange exchange) throws IOException {
		
		if(!exchange.getRequestMethod().equals("GET")) {
			logger.debug("Request not via GET");
			sendClientError(exchange);
			return;
		}
		
		int distributorId = 0;
		int subId = 0;
		try {
			Map<String, String> params = Parser.parseQuery(exchange.getRequestURI().getQuery());
			distributorId = Integer.valueOf(params.get(Links.DISTRIBUTOR_ID_PARAM_NAME));
			subId = Integer.valueOf(params.get(Links.SUB_ID_PARAM_NAME));
		} catch (Exception e) {
			logger.debug("Can't get distributor or sub ID from query: " + Throwables.getStackTraceAsString(e));
			sendClientError(exchange);
			return;
		}
		
		//get required info from DB here
		
	}

}
