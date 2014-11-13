package eu.ibutler.affiliatenetwork.controllers;

import java.io.File;
import java.io.IOException;

import com.google.common.base.Throwables;
import com.sun.net.httpserver.HttpExchange;

import eu.ibutler.affiliatenetwork.file.FileUtils;
import eu.ibutler.affiliatenetwork.http.FileSender;
import eu.ibutler.affiliatenetwork.http.Parser;

/**
 * This controller is responsible for outputting list of files available for
 * download. Resulting file contains data describing available for download
 * product files.
 * Upon distributor's request, such file is created and sent via http multipart
 * @author Anton Lukashchuk
 *
 */
@WebController("/getActiveFilesList")
public class DistributorGetActiveFilesListController extends AbstractHttpHandler implements FreeAccess {

	@Override
	protected void handleBody(HttpExchange exchange) throws IOException {
		
		if(!exchange.getRequestMethod().equals("GET")) {
			logger.debug("Request not via GET");
			sendClientError(exchange);
			return;
		}
		
		int distributorID = 0;
		try {
			String query = exchange.getRequestURI().getQuery();
			String distributorIdStr = Parser.parseQuery(query).get("distributorId");
			distributorID = Integer.valueOf(distributorIdStr);
		} catch (Exception e) {
			logger.debug("Can't get distributor ID from query. " + Throwables.getStackTraceAsString(e));
			sendClientError(exchange);
			return;
		}
		
		String path;
		try {
			path = FileUtils.createActiveFilesList(distributorID);
		} catch (IOException e) {
			StatusEndpoint.incrementErrors();
			logger.error("Error creating file.");
			sendServerError(exchange);
			return;
		}
		
		//OutputStream of this exchange is closed inside
		try {
			new FileSender().send(path, "availableFilesList.csv", exchange);
			new File(path).delete();
		} catch (IOException e) {
			StatusEndpoint.incrementWarnings();
			logger.warn("Failed to send file.");
		}
	}

}
