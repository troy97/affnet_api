package eu.ibutler.affiliatenetwork;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import com.sun.net.httpserver.HttpServer;

import eu.ibutler.affiliatenetwork.http.session.HttpSession;
import eu.ibutler.affiliatenetwork.utils.AppConfig;
import eu.ibutler.affiliatenetwork.utils.DBserviceRunnable;
import eu.ibutler.affiliatenetwork.utils.csv.CSVUtils;

/**
 * Entry point to Affiliate Network service
 * Http server (com.sun.net.httpserver) is configured started here
 * @author Anton Lukashchuk
 *
 */
@SuppressWarnings("restriction")
public class MainClass {
	private static final long START_TIME = System.currentTimeMillis();
	
	private static AppConfig cfg = AppConfig.getInstance();
	private static Logger log = Logger.getLogger(MainClass.class.getName());
	
	public static void main(String[] args) throws IOException {
		
		int port = 8080;
		InetSocketAddress serverAddress = new InetSocketAddress("localhost", port);
		int backlog = 32;
		HttpServer server = HttpServer.create(serverAddress, backlog);
		
		long sessionDefaultInactiveTimer = Long.valueOf(cfg.get("maxInactiveInterval"));
		HttpSession.setDefaultSessionInactiveInterval(sessionDefaultInactiveTimer*60*1000);
		
		new UrlMapper().setMappingAndFilters(server);
		
		//to do: maybe better to limit maximum thread number 
		server.setExecutor(Executors.newCachedThreadPool());
		server.start();
		log.info("HttpServer has started");
		
		//Thread DBservice = new Thread(new DBserviceRunnable());
	}
	
	public static long getStartTime() {
		return START_TIME;
	}
	
}
