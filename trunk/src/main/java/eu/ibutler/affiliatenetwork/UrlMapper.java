package eu.ibutler.affiliatenetwork;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.reflections.Reflections;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.Filter;

import eu.ibutler.affiliatenetwork.controllers.AbstractHttpHandler;
import eu.ibutler.affiliatenetwork.controllers.RestrictedAccess;
import eu.ibutler.affiliatenetwork.controllers.WebController;
import eu.ibutler.affiliatenetwork.filters.DistributorClickCountingFilter;
import eu.ibutler.affiliatenetwork.filters.AuthenticationFilter;
import eu.ibutler.affiliatenetwork.filters.RequestCountingFilter;
import eu.ibutler.affiliatenetwork.http.session.ValidationFilter;

/**
 * Class allows dynamic mapping of controllers to their url's and filters
 * @author Anton Lukashchuk
 *
 */
@SuppressWarnings("restriction")
public class UrlMapper {
	private static Logger log = Logger.getLogger(UrlMapper.class.getName());
	
	//List of filters that don't process authentication  
	private List<Filter> noAuth = new ArrayList<>();
	//List of filters that responsible for user authentication
	private List<Filter> withAuth = new ArrayList<>();
    
	
	public UrlMapper() {
		//list no auth filters here
		this.noAuth.add(new RequestCountingFilter());
		this.noAuth.add(new DistributorClickCountingFilter());
		//list auth filters here
		this.withAuth.add(new ValidationFilter());
		this.withAuth.add(new AuthenticationFilter());
	}


	/**
	 * This method performs creation of HttpContext's for given server.
	 * All classes in "eu.ibutler.affiliatenetwork.controllers"
	 * that marked with "@WebController" annotation considered proper handlers.
	 * Particular URL for each handler is taken from its annotation "value()" parameter.  
	 * 
	 * The "noAuth" list of filters is added to each context.
	 * If handler class implements RestrictedAccess interface then "withAuth"
	 * collection of filters is also added to context.
	 * @param server instance of com.sun.net.httpserver.HttpServer to add contexts to.
	 */
	public void setMappingAndFilters(HttpServer server) {
		Reflections reflections = new Reflections("eu.ibutler.affiliatenetwork.controllers");
		Set<Class<?>> controllers = reflections.getTypesAnnotatedWith(WebController.class);
		for(Class<?> controller : controllers) {
			//get instance of controller class
			AbstractHttpHandler handler = null;
			try {
				handler = (AbstractHttpHandler) controller.newInstance();
			} catch (InstantiationException | IllegalAccessException ignore) {
				log.error("Failed to instantiate Controller " + ignore.getMessage());
			}
			
			//get corresponding url-mapping
			Annotation note = controller.getAnnotation(WebController.class);
			String url = ((WebController) note).value();
			
			//bind them together
			HttpContext context = server.createContext(url, handler);
			
			//add necessary filters
			context.getFilters().addAll(noAuth);
			List<Class<?>> interfaces = new ArrayList<Class<?>>(Arrays.asList(controller.getInterfaces()));
			if(interfaces.contains(RestrictedAccess.class)) {
				context.getFilters().addAll(withAuth);
			}
			
		}
		log.debug("Controllers mapping: OK");
	}
    

}
