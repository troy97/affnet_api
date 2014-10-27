package eu.ibutler.affiliatenetwork.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import com.sun.net.httpserver.HttpExchange;

import eu.ibutler.affiliatenetwork.config.AppConfig;
import eu.ibutler.affiliatenetwork.config.Urls;
import eu.ibutler.affiliatenetwork.controllers.utils.Links;
import eu.ibutler.affiliatenetwork.dao.exceptions.DbAccessException;
import eu.ibutler.affiliatenetwork.dao.exceptions.NoSuchEntityException;
import eu.ibutler.affiliatenetwork.dao.impl.FileDaoImpl;
import eu.ibutler.affiliatenetwork.dao.impl.FileTemplateDaoImpl;
import eu.ibutler.affiliatenetwork.entity.FileTemplate;
import eu.ibutler.affiliatenetwork.http.FileSender;
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
public class DistributorFileRequestController extends AbstractHttpHandler implements FreeAccess {

	@Override
	public void handleBody(HttpExchange exchange) throws IOException {
		
		//Only GET requests allowed 
		if(!exchange.getRequestMethod().equals("GET")) {
			logger.debug("Request not via GET");
			sendRedirect(exchange, Urls.fullURL(Urls.ERROR_PAGE_URL));
			return;
		}
		
		int distributorId = 0;
		int fileId = 0;
		try {
			Map<String, String> params = Parser.parseQuery(exchange.getRequestURI().getQuery());
			distributorId = Integer.valueOf(params.get(Links.DISTRIBUTOR_ID_PARAM_NAME));
			fileId = Integer.valueOf(params.get(Links.FILE_TEMPLATE_ID_PARAM_NAME));
		} catch (Exception e) {
			logger.debug("request without file ID " + e.getClass().getName());
			sendRedirect(exchange, Urls.fullURL(Urls.ERROR_PAGE_URL));
			return;
		}
		
		//file extract here
		//TODO: process file here to add distributor id into affnet_url dynamically 
		try {
			FileTemplate file = new FileTemplateDaoImpl().selectById(fileId);
			new FileSender().send(file.getFsPath(), "productsFile_"+fileId, exchange);
		} catch (DbAccessException | NoSuchEntityException e) {
			StatusEndpoint.incrementErrors();
			logger.error("Can't get requested file template");
			sendRedirect(exchange, Urls.fullURL(Urls.ERROR_PAGE_URL));
			return;
		}
		

	}

}
