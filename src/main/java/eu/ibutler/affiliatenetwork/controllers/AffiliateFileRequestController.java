package eu.ibutler.affiliatenetwork.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.FileSystems;
import java.nio.file.Path;

import org.apache.commons.io.IOUtils;

import com.sun.net.httpserver.HttpExchange;

import eu.ibutler.affiliatenetwork.config.AppConfig;
import eu.ibutler.affiliatenetwork.config.Urls;
import eu.ibutler.affiliatenetwork.http.parse.Parser;
import eu.ibutler.affiliatenetwork.http.parse.exceptions.ParsingException;

/**
 * For now assume that Publisher requests all files from all shops
 * so search in DB all files whose "isActive" == true, zip them in 
 * one archive and send in response
 * @author Anton Lukashchuk
 *
 */
@SuppressWarnings("restriction")
@WebController("/affiliateFileRequest")
public class AffiliateFileRequestController extends AbstractHttpHandler implements FreeAccess {

	@Override
	public void handleBody(HttpExchange exchange) throws IOException {
		String filePath = "/home/anton/workspaceJEE/SVN/AffiliateNetwork/trashbin/testCSV.csv";
		File file = new File(filePath);
		
		//assume that affiliates request files using file dbId
		//so extract file from DB here
		int fileDbId = 0;
		try {
			String query = exchange.getRequestURI().getQuery();
			String fileDbIdStr = Parser.parseQuery(query).get("fileDbId");
			fileDbId = Integer.valueOf(fileDbIdStr);
		} catch (Exception e) {
			logger.debug("request without file ID " + e.getClass().getName());
			sendRedirect(exchange, Urls.fullURL(Urls.ERROR_PAGE_URL));
			return;
		}
		//file extract here
		//-----
		//extracted
		
		try (OutputStream os = exchange.getResponseBody()) {
			if (!file.exists() || file.isDirectory() || !exchange.getRequestMethod().equalsIgnoreCase("GET")) {
				byte[] response = new byte[0];
				exchange.sendResponseHeaders(404, response.length);
				os.write(response);
			} else {
				exchange.getResponseHeaders().set("Content-Type", "text/csv");
				exchange.getResponseHeaders().set("Content-Disposition", "filename=file.csv");
				exchange.sendResponseHeaders(200, file.length());
				try (FileInputStream fis = new FileInputStream(file)) {
					IOUtils.copy(fis, os);
				} catch (IOException e) {
					logger.debug("Unable to send file");
				}
			}
		} catch (IOException e) {
			logger.debug("Output stream problem");
		}
	}

}
