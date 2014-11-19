package eu.ibutler.affiliatenetwork.controllers;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;

import eu.ibutler.affiliatenetwork.ParsingException;
import eu.ibutler.affiliatenetwork.dao.exceptions.DbAccessException;
import eu.ibutler.affiliatenetwork.dao.exceptions.NoSuchEntityException;
import eu.ibutler.affiliatenetwork.dao.exceptions.UniqueConstraintViolationException;
import eu.ibutler.affiliatenetwork.dao.impl.ClickDaoImpl;
import eu.ibutler.affiliatenetwork.dao.impl.DistributorDaoMock;
import eu.ibutler.affiliatenetwork.dao.impl.ProductDaoImpl;
import eu.ibutler.affiliatenetwork.dao.impl.ShopDaoImpl;
import eu.ibutler.affiliatenetwork.entity.Click;
import eu.ibutler.affiliatenetwork.entity.Distributor;
import eu.ibutler.affiliatenetwork.entity.Product;
import eu.ibutler.affiliatenetwork.entity.Shop;
import eu.ibutler.affiliatenetwork.http.Parser;


/**
 * Controller that processes clicks made by distributor links
 * @author Anton Lukashchuk
 *
 */
@WebController("/distributorClick")
public class DistributorClickController extends AbstractHttpHandler implements FreeAccess {

	@Override
	public void handleBody(HttpExchange exchange) throws IOException {
		
		if(!exchange.getRequestMethod().equals("GET")) {
			logger.debug("Click not via GET");
			sendClientError(exchange);
			return;
		}
		
		Map<String, String> params;
		try {
			params = Parser.parseQuery(exchange.getRequestURI().getQuery());
		} catch (ParsingException e) {
			logger.debug("Unable to parse query");
			sendClientError(exchange);
			return;
		}
		if(!params.keySet().containsAll(Arrays.asList(Links.DISTRIBUTOR_ID_PARAM_NAME, Links.PRODUCT_ID_PARAM_NAME))) {
			logger.debug("Missing query parameters");
			sendClientError(exchange);
			return;
		}
		
		int distributorId = 0;
		int productId = 0;
		int subId = -1;
		try {
			distributorId = Integer.valueOf(params.get(Links.DISTRIBUTOR_ID_PARAM_NAME));
			productId = Integer.valueOf(params.get(Links.PRODUCT_ID_PARAM_NAME));
			try {
				subId = Integer.valueOf(params.get(Links.SUB_ID_PARAM_NAME));
			} catch (Exception ignore) {}
		} catch (NumberFormatException e) {
			logger.debug("Invalid query parameters");
			sendClientError(exchange);
			return;
		}
		
		//Request OK, process it
		
		Product product = null;
		try {
			product = new ProductDaoImpl().selectById(productId);
		} catch (DbAccessException | NoSuchEntityException e) {
			logger.debug("Unable to extract product from DB: " + e.getClass().getName());
			StatusEndpoint.incrementErrors();
			sendServerError(exchange);
			return;
		} 
		
		Distributor distrib = null;
		try {
			distrib = new DistributorDaoMock().selectById(distributorId);
		} catch (DbAccessException | NoSuchEntityException e) {
			logger.error("Unable to extract distributor from DB: " + e.getClass().getName());
			StatusEndpoint.incrementErrors();
			emergencyRedirectToProductPage(exchange, product);
			return;
		} 
		
		Shop shop = null;
		try {
			shop = new ShopDaoImpl().selectById(product.getShopId());
		} catch (DbAccessException | NoSuchEntityException e) {
			logger.debug("Unable to extract shop from DB: " + e.getClass().getName());
			StatusEndpoint.incrementErrors();
			emergencyRedirectToProductPage(exchange, product);
			return;
		}
		
		Click click = new Click(product.getId(), shop.getId(), distrib.getId(), subId, product.getName(), product.getPrice(), product.getShippingPrice());
		try {
			long id = new ClickDaoImpl().insertOne(click);
			click.setId(id);
			logger.info("New click inserted: " + click);
		} catch (DbAccessException e) {
			logger.error("Failed to insert Click into database: " + click + " : " + e.getClass().getName());
			StatusEndpoint.incrementErrors();
			emergencyRedirectToProductPage(exchange, product);
			return;
		}
		
		//OK, job done, redirect to real product page and add Click id parameter before
		String productLink = product.getRealUrl();
		productLink = productLink.contains("?") ? (productLink += "&") : (productLink += "?");
		productLink+=Links.CLICK_ID_PARAM_NAME + "=" + click.getId();
		sendRedirect(exchange, productLink);
		return;
	}
	
	/**
	 * This method is called if some service error occurred and
	 * it's not right to show our error page to customer.  
	 * In this case redirect to product page without ClickId parameter
	 * @param exchange
	 * @param product
	 * @throws IOException
	 */
	private void emergencyRedirectToProductPage(HttpExchange exchange, Product product) throws IOException {
		sendRedirect(exchange, product.getRealUrl());
	}

}
