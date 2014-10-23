package eu.ibutler.affiliatenetwork;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import com.sun.net.httpserver.HttpServer;

import eu.ibutler.affiliatenetwork.config.AppConfig;
import eu.ibutler.affiliatenetwork.controllers.StatusEndpoint;
import eu.ibutler.affiliatenetwork.http.session.HttpSession;

/**
 * Entry point to Affiliate Network service
 * Http server (com.sun.net.httpserver) is configured started here
 * @author Anton Lukashchuk
 *
 */
@SuppressWarnings("restriction")
public class MainClass {
	
	private static AppConfig cfg = AppConfig.getInstance();
	private static Logger logger = Logger.getLogger(MainClass.class.getName());
	
	public static void main(String[] args) throws IOException {
		
		StatusEndpoint.init(System.currentTimeMillis());
		
		//Check presence of necessary folders on the file system
		new File(cfg.getWithEnv("uploadPath")).mkdir();
		new File(cfg.getWithEnv("fileTemplatesPath")).mkdir();
		if(!new File(cfg.getWithEnv("WebContentPath")).exists()) {
			logger.error("WebContent folder not found in " + cfg.getWithEnv("WebContentPath"));
			System.exit(1);
		}
		
		//configure and start server
		int port = Integer.valueOf(cfg.getWithEnv("port"));
		InetSocketAddress serverAddress = new InetSocketAddress(cfg.getWithEnv("hostname"), port);
		int backlog = Integer.valueOf(cfg.getWithEnv("serverBacklog"));
		HttpServer server = HttpServer.create(serverAddress, backlog);
		new UrlMapper().setMappingAndFilters(server);
		server.setExecutor(Executors.newCachedThreadPool());
		server.start();
		System.out.println("HttpServer has started on " + serverAddress);
		logger.info("HttpServer has started on " + serverAddress);
	}
	
}
