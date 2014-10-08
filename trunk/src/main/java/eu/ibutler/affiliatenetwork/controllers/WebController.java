package eu.ibutler.affiliatenetwork.controllers;

import java.lang.annotation.*;

/**
 * This annotation is used to mark implementations of HttpHandler that
 * used with HttpServer to create HttpContext's.
 * Similar to "@WebServlet" annotation 
 * @author Anton Lukashchuk
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface WebController {
	
	//URL value
	public String value();

}
