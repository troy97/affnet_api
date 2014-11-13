package eu.ibutler.affiliatenetwork.controllers;


import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.sun.net.httpserver.HttpExchange;

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
		
	}

}
