package eu.ibutler.affiliatenetwork.filters;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import com.sun.net.httpserver.HttpExchange;

public class RequestCountingFilter extends AbstractFilter{

	private static AtomicInteger requestCount = new AtomicInteger();
	
	@Override
	public String description() {
		return "This filter counts all requests that came to this service";
	}

	@Override
	public void doFilterBody(HttpExchange exchange, Chain chain) throws IOException {
		requestCount.incrementAndGet();
		chain.doFilter(exchange);
	}
	
	/**
	 * 
	 * @return request count
	 */
	public static int getRequestCounter() {
		return requestCount.get();
	}

}
