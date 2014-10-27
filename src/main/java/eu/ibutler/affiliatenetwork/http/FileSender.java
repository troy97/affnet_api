package eu.ibutler.affiliatenetwork.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.sun.net.httpserver.HttpExchange;

import eu.ibutler.affiliatenetwork.controllers.StatusEndpoint;
import eu.ibutler.affiliatenetwork.controllers.utils.FsPaths;

/**
 * Contains methods for sending files
 * @author Anton Lukashchuk
 *
 */
@SuppressWarnings("restriction")
public class FileSender {
	
	private static Logger logger = Logger.getLogger(FsPaths.class.getName());
	
	/**
	 * Sends file located on the file system via http.
	 * NOTE that this method closes outputstream of given exchange
	 * @param path to file on disk
	 * @param name of file for user
	 * @param exchange object from HttpHandler
	 */
	public void send(String path, String name, HttpExchange exchange) {
		try (OutputStream os = exchange.getResponseBody()) {
			File file = new File(path);
			if (!file.exists() || file.isDirectory()) {
				byte[] response = new byte[0];
				exchange.sendResponseHeaders(404, response.length);
				os.write(response);
			} else {
				exchange.getResponseHeaders().set("Content-Type", "text/csv");
				exchange.getResponseHeaders().set("Content-Disposition", "filename=\"" + name + "\"");
				exchange.sendResponseHeaders(200, file.length());
				try (FileInputStream fis = new FileInputStream(file)) {
					IOUtils.copy(fis, os);
				} catch (IOException e) {
					StatusEndpoint.incrementWarnings();
					logger.warn("Unable to send file");
				}
			}
		} catch (IOException e) {
			logger.debug("Output stream problem");
		}
	}

}
