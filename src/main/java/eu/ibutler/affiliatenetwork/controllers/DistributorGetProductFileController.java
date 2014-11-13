package eu.ibutler.affiliatenetwork.controllers;

import java.io.IOException;
import java.util.Map;

import com.google.common.base.Throwables;
import com.sun.net.httpserver.HttpExchange;

import eu.ibutler.affiliatenetwork.dao.exceptions.DbAccessException;
import eu.ibutler.affiliatenetwork.dao.exceptions.NoSuchEntityException;
import eu.ibutler.affiliatenetwork.dao.impl.FileTemplateDaoImpl;
import eu.ibutler.affiliatenetwork.entity.FileTemplate;
import eu.ibutler.affiliatenetwork.http.FileSender;
import eu.ibutler.affiliatenetwork.http.Parser;

/**
 * Controller responsible for output of particular 
 * product file with distributor links
 * @author Anton Lukashchuk
 *
 */
@WebController("/affiliateFileRequest")
public class DistributorGetProductFileController extends AbstractHttpHandler implements FreeAccess {

	@Override
	public void handleBody(HttpExchange exchange) throws IOException {
		
		if(!exchange.getRequestMethod().equals("GET")) {
			logger.debug("Request not via GET");
			sendClientError(exchange);
			return;
		}
		
		int distributorId = 0;
		int templateId = 0;
		try {
			Map<String, String> params = Parser.parseQuery(exchange.getRequestURI().getQuery());
			distributorId = Integer.valueOf(params.get(Links.DISTRIBUTOR_ID_PARAM_NAME));
			templateId = Integer.valueOf(params.get(Links.FILE_TEMPLATE_ID_PARAM_NAME));
		} catch (Exception e) {
			logger.debug("Can't get distributor or file ID from query " + Throwables.getStackTraceAsString(e));
			sendClientError(exchange);
			return;
		}
		
		//Extract file template here
		//For now send template as is (it refers to distributor #1). TODO: add processing to insert real distributor id
		FileTemplate file;
		try {
			file = new FileTemplateDaoImpl().selectById(templateId);
		} catch (DbAccessException | NoSuchEntityException e) {
			StatusEndpoint.incrementErrors();
			logger.error("Can't get requested file template");
			sendServerError(exchange);
			return;
		}
		
		try {
			new FileSender().send(file.getFsPath(), "productsFile_"+templateId+".csv", exchange);
		} catch (IOException e) {
			StatusEndpoint.incrementWarnings();
			logger.warn("Failed to send file.");
		}
		
	}//handleBody

}
