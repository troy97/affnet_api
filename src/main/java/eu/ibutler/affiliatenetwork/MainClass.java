package eu.ibutler.affiliatenetwork;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import com.sun.net.httpserver.HttpServer;

import eu.ibutler.affiliatenetwork.http.session.HttpSession;
import eu.ibutler.affiliatenetwork.utils.AppConfig;

/**
 * Entry point to Affiliate network service
 * Http server is started and configured here
 * @author Anton Lukashchuk
 *
 */
@SuppressWarnings("restriction")
public class MainClass {
	private static final long START_TIME = System.currentTimeMillis();
	
	private static AppConfig cfg = AppConfig.getInstance();
	private static Logger log = Logger.getLogger(MainClass.class.getName());
	
	public static void main(String[] args) throws IOException {
		
		InetSocketAddress serverAddress = new InetSocketAddress("localhost", 8080);
		HttpServer server = HttpServer.create(serverAddress, 8);
		
		long sessionDefaultInactiveTimer = Long.valueOf(cfg.get("maxInactiveInterval"));
		HttpSession.setDefaultSessionInactiveInterval(sessionDefaultInactiveTimer*60*1000);
		
		new UrlMapper().setMappingAndFilters(server);
		
		//to do: maybe better to limit maximum thread number 
		server.setExecutor(Executors.newCachedThreadPool());
		server.start();
		
		log.info("HttpServer has started");
	}
	
	public static long getStartTime() {
		return START_TIME;
	}
	
}
