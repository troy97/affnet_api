package eu.ibutler.affiliatenetwork.filters;

import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;

@SuppressWarnings("restriction")
public class TestFilter extends AbstractFilter {

	@Override
	public void doFilterBody(HttpExchange exchange, Chain chain)
			throws IOException {
		System.out.println("testFilter pass");
		chain.doFilter(exchange);
	}

	@Override
	public String description() {
		// TODO Auto-generated method stub
		return null;
	}

}
