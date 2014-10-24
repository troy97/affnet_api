package eu.ibutler.affiliatenetwork.controllers;

import java.io.IOException;
import java.util.List;

import com.sun.net.httpserver.HttpExchange;

import eu.ibutler.affiliatenetwork.config.Urls;
import eu.ibutler.affiliatenetwork.controllers.utils.Links;
import eu.ibutler.affiliatenetwork.dao.impl.FileTemplateDaoImpl;
import eu.ibutler.affiliatenetwork.entity.FileTemplate;
import eu.ibutler.affiliatenetwork.http.parse.Parser;
import eu.ibutler.affiliatenetwork.utils.csv.CSVProcessor;

@SuppressWarnings("restriction")
@WebController("/getActiveFilesList")
public class DistributorGetActiveFileListController extends AbstractHttpHandler implements FreeAccess {

	@Override
	protected void handleBody(HttpExchange exchange) throws IOException {
		//Only GET requests allowed 
		if(!exchange.getRequestMethod().equals("GET")) {
			logger.debug("Request not via GET");
			sendRedirect(exchange, Urls.fullURL(Urls.ERROR_PAGE_URL));
			return;
		}
		
		String query = exchange.getRequestURI().getQuery();
		String distributorIdStr = Parser.getParam(query, Links.DISTRIBUTOR_ID_PARAM_NAME);
		int distributorId = Integer.valueOf(distributorIdStr);
		
		//Create csv with active file template index
		//List<FileTemplate> activeFiles = new FileTemplateDaoImpl().getAllActive();
		
		
		
		CSVProcessor processor = new CSVProcessor();
		
		
		
		
		
	}

}
