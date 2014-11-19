package eu.ibutler.affiliatenetwork.controllers;


import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import com.sun.net.httpserver.HttpExchange;

import eu.ibutler.affiliatenetwork.dao.exceptions.DbAccessException;
import eu.ibutler.affiliatenetwork.dao.exceptions.NoSuchEntityException;
import eu.ibutler.affiliatenetwork.dao.impl.ClickDaoImpl;
import eu.ibutler.affiliatenetwork.dao.impl.OrderDaoImpl;
import eu.ibutler.affiliatenetwork.dao.impl.ProductDaoImpl;
import eu.ibutler.affiliatenetwork.entity.Click;
import eu.ibutler.affiliatenetwork.entity.Product;

@WebController("/test")
public class TestController extends AbstractHttpHandler implements FreeAccess {

	@Override
	public void handleBody(HttpExchange exchange) throws IOException {
		try(InputStream in = exchange.getRequestBody()){}
		System.out.println("TestController Pass");
		
		
		//render login page
		try(BufferedOutputStream out = new BufferedOutputStream(exchange.getResponseBody())){
			exchange.sendResponseHeaders(200, "TestController".getBytes().length);
			out.write("TestController".getBytes());
			out.flush();
		}

		
		long twoMonth = TimeUnit.DAYS.toMillis(60);
		int weeks = 8;
		long timeStart = System.currentTimeMillis()+TimeUnit.DAYS.toMillis(3)-twoMonth; // 2 month ago
		long createdAt = timeStart;

		
		//fill clicks
		ClickDaoImpl clickDao = new ClickDaoImpl();
		ProductDaoImpl productDao = new ProductDaoImpl();
		Random r = new Random();
		long clickTime = timeStart;
		for(int i = 0; i<3000; i++) {
			int productId = 1+r.nextInt(400);
			try {
				Product product = productDao.selectById(productId);
				Click click = new Click(product.getId(), product.getShopId(), 1, r.nextInt(10000),
						product.getName(), product.getPrice(), product.getShippingPrice());
				clickTime += twoMonth/3000;
				click.setClickTime(clickTime);
				clickDao.insertOne(click);
			} catch (Exception e) {
				System.out.println(productId + " " + e);
			}
		}
		
		System.out.println("################################################################################");
		
		//fill orders
		int count = 0;
		for(int w = 0; w<weeks; w++) {
			for(int i = 1; i<=7; i++) {
				try {
					createdAt+=TimeUnit.DAYS.toMillis(1);
					ordersDay(createdAt, i, count++);
				} catch (Exception e) {
					System.out.println(e);
				}
			}
		}
		
		System.out.println("########## Mocking Finished ############");
	}
	
	
	private void ordersDay(long dayStartTime, int dayNumber, int count) throws DbAccessException, NoSuchEntityException {
		ProductDaoImpl productDao = new ProductDaoImpl();
		OrderDaoImpl orderDao = new OrderDaoImpl();
		int currencyId = 1;
		int distributorId = 1;
		
		Random r = new Random();
		long day = TimeUnit.DAYS.toMillis(1)/2;
		
		int orders = 5 + r.nextInt(8-dayNumber);
		
		long buyInterval = day/orders;
		
		long createdAt = dayStartTime;
		
		
		for(int i = 0; i<orders; i++) {
			
			int productId = 1+r.nextInt(400);
			Product product = productDao.selectById(productId);
			String status;
			int s = r.nextInt(3);
			switch(s){
			case 0: status="Open";
			break;
			case 1: status="Cancelled";
			break;
			case 2: status="Confirmed";
			break;
			default: status = "Open";
			}
			
			
			//System.out.println(count + " " + new Timestamp(createdAt));
			createdAt += buyInterval;
			
			orderDao.insertOne(product.getId(),
					distributorId,
					r.nextInt(10000),
					r.nextInt(3000),
					status,
					product.getPrice(),
					currencyId,
					product.getPrice(),
					product.getName(),
					createdAt,
					createdAt
			);
		}
	}

}
