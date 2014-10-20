package eu.ibutler.affiliatenetwork.controllers;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;

import eu.ibutler.affiliatenetwork.config.AppConfig;
import eu.ibutler.affiliatenetwork.config.Urls;
import eu.ibutler.affiliatenetwork.dao.exceptions.DbAccessException;
import eu.ibutler.affiliatenetwork.dao.exceptions.NoSuchEntityException;
import eu.ibutler.affiliatenetwork.dao.exceptions.UniqueConstraintViolationException;
import eu.ibutler.affiliatenetwork.dao.impl.ClickDaoJdbc;
import eu.ibutler.affiliatenetwork.dao.impl.DistributorDaoMock;
import eu.ibutler.affiliatenetwork.dao.impl.ProductDaoImpl;
import eu.ibutler.affiliatenetwork.dao.impl.ShopDaoImpl;
import eu.ibutler.affiliatenetwork.entity.Click;
import eu.ibutler.affiliatenetwork.entity.Distributor;
import eu.ibutler.affiliatenetwork.entity.Product;
import eu.ibutler.affiliatenetwork.entity.Shop;
import eu.ibutler.affiliatenetwork.http.ParsingException;
import eu.ibutler.affiliatenetwork.http.parse.Parser;


/**
 * Controller that processes clicks made by distributor links
 * @author Anton Lukashchuk
 *
 */
@SuppressWarnings("restriction")
@WebController("/distributorClick")
public class DistributorClickController extends AbstractHttpHandler implements FreeAccess {

	@Override
	public void handleBody(HttpExchange exchange) throws IOException {
		
		//Only GET requests allowed 
		if(!exchange.getRequestMethod().equals("GET")) {
			logger.debug("Click not via GET");
			sendRedirect(exchange, Urls.fullURL(Urls.ERROR_PAGE_URL));
			return;
		}
		
		//Validate query parameters
		Map<String, String> params;
		try {
			params = Parser.parseQuery(exchange.getRequestURI().getQuery());
		} catch (ParsingException e) {
			logger.debug("Unable to parse query");
			sendRedirect(exchange, Urls.fullURL(Urls.ERROR_PAGE_URL));
			return;
		}
		if(!params.keySet().containsAll(Arrays.asList(Links.DISTRIBUTOR_ID_PARAM_NAME, Links.PRODUCT_ID_PARAM_NAME))) {
			logger.debug("Missing query parameters");
			sendRedirect(exchange, Urls.fullURL(Urls.ERROR_PAGE_URL));
			return;
		}
		
		//Request OK, process it
		
		int distributorId = 0;
		int productId = 0;
		try {
			distributorId = Integer.valueOf(params.get(Links.DISTRIBUTOR_ID_PARAM_NAME));
			productId = Integer.valueOf(params.get(Links.PRODUCT_ID_PARAM_NAME));
		} catch (NumberFormatException e) {
			logger.debug("Invalid query parameters");
			sendRedirect(exchange, Urls.fullURL(Urls.ERROR_PAGE_URL));
			return;
		}
		
		Distributor distrib = null;
		try {
			distrib = new DistributorDaoMock().selectById(distributorId);
		} catch (DbAccessException | NoSuchEntityException e) {
			logger.debug("Unable to extract distributor from DB: " + e.getClass().getName());
			sendRedirect(exchange, Urls.fullURL(Urls.ERROR_PAGE_URL));
			return;
		} 
		
		Product product = null;
		try {
			product = new ProductDaoImpl().selectById(productId);
		} catch (DbAccessException | NoSuchEntityException e) {
			logger.debug("Unable to extract product from DB: " + e.getClass().getName());
			sendRedirect(exchange, Urls.fullURL(Urls.ERROR_PAGE_URL));
			return;
		} 
		
		Shop shop = null;
		try {
			shop = new ShopDaoImpl().selectById(product.getShopDbId());
		} catch (DbAccessException | NoSuchEntityException e) {
			logger.debug("Unable to extract shop from DB: " + e.getClass().getName());
			sendRedirect(exchange, Urls.fullURL(Urls.ERROR_PAGE_URL));
			return;
		}
		
		Click click = new Click(product.getDbId(), shop.getDbId(), distrib.getId());
		try {
			new ClickDaoJdbc().insertOne(click);
			logger.info("New click inserted: " + click);
		} catch (DbAccessException | UniqueConstraintViolationException e) {
			logger.warn("Failed to insert Click into database: " + click + " : " + e.getClass().getName());
		}
		
		//OK, job done, redirect to real product page
		sendRedirect(exchange, product.getUrlPath());
		return;
	}

}
