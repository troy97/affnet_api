package eu.ibutler.affiliatenetwork.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.FileSystems;
import java.nio.file.Path;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.sun.net.httpserver.HttpExchange;

import eu.ibutler.affiliatenetwork.utils.AppConfig;

/**
 * This controller is responsible for file upload to user.
 * @author anton
 *
 */
@SuppressWarnings("restriction")
public class FileRequestController extends AbstractHttpHandler {
	
	private static Logger log = Logger.getLogger(FileRequestController.class.getName());

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		//query string
		String query = exchange.getRequestURI().getPath();
		String queryStr = query.substring("/".length());
		Path queryPath = FileSystems.getDefault().getPath(queryStr);
		
		//path to bootstrap folder
		Path rootPath = AppConfig.getInstance().getServiceRootFsPath();
		Path bootstrapPath = rootPath.resolve("WebContent/bootstrap");
		
		//full path to file = bootstrap path + queryPath
		Path filePath = bootstrapPath.resolve(queryPath);
		
		File file = filePath.toFile();
		try (OutputStream os = exchange.getResponseBody()) {
			if (!file.exists() || file.isDirectory()
					|| !exchange.getRequestMethod().equalsIgnoreCase("GET")
					|| !filePath.startsWith(rootPath)) {
				byte[] response = new byte[0];
				exchange.sendResponseHeaders(404, response.length);
				os.write(response);
			} else {
				exchange.sendResponseHeaders(200, file.length());
				try (FileInputStream fis = new FileInputStream(file)) {
					IOUtils.copy(fis, os);
				} catch (IOException e) {
					log.debug("Unable to send file");
				}
			}
		} catch (IOException e) {
			log.debug("Output stream problem");
		}
	}
}
