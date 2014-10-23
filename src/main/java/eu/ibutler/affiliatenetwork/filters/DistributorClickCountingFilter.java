package eu.ibutler.affiliatenetwork.filters;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

import com.sun.net.httpserver.HttpExchange;

import eu.ibutler.affiliatenetwork.config.Urls;
import eu.ibutler.affiliatenetwork.controllers.utils.Links;

@SuppressWarnings("restriction")
public class DistributorClickCountingFilter extends AbstractFilter{
	
	private static AtomicLong count = new AtomicLong();

	@Override
	public String description() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void doFilterBody(HttpExchange exchange, Chain chain) throws IOException {
		String path = exchange.getRequestURI().getPath();
		if(path.contains(Urls.DISTRIBUTOR_CLICK_URL)) {
			count.incrementAndGet();
			exchange.setAttribute(Links.EXCHANGE_CLICK_COUNT_ATTR_NAME, count.get());
		}
		chain.doFilter(exchange);
	}

}
