package eu.ibutler.affiliatenetwork.controllers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.sun.net.httpserver.HttpExchange;

import eu.ibutler.affiliatenetwork.config.Urls;
import eu.ibutler.affiliatenetwork.controllers.utils.Links;
import eu.ibutler.affiliatenetwork.dao.exceptions.DbAccessException;
import eu.ibutler.affiliatenetwork.dao.exceptions.NoSuchEntityException;
import eu.ibutler.affiliatenetwork.dao.impl.FileTemplateDaoImpl;
import eu.ibutler.affiliatenetwork.dao.impl.ShopDaoImpl;
import eu.ibutler.affiliatenetwork.entity.FileTemplate;
import eu.ibutler.affiliatenetwork.http.FileSender;
import eu.ibutler.affiliatenetwork.http.parse.Parser;
import eu.ibutler.affiliatenetwork.utils.csv.CSVProcessor;
import eu.ibutler.affiliatenetwork.utils.csv.CSVUtils;

/**
 * This controller is responsible for creating list of files available for
 * download. File has csv format and contains data describing available for download
 * product files.
 * Upon distributor's request, such file is created and sent via http multipart
 * @author Anton Lukashchuk
 *
 */
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
		
		int distributorId = 0;
		try {
			String query = exchange.getRequestURI().getQuery();
			String distributorIdStr = Parser.parseQuery(query).get("distributorId");
			distributorId = Integer.valueOf(distributorIdStr);
		} catch (Exception e) {
			logger.debug("request without file ID " + e.getClass().getName());
			sendRedirect(exchange, Urls.fullURL(Urls.ERROR_PAGE_URL));
			return;
		}
		
		//Create csv with active file template index
		List<FileTemplate> activeFiles;
		try {
			activeFiles = new FileTemplateDaoImpl().getAllActive();
		} catch (DbAccessException e) {
			StatusEndpoint.incrementErrors();
			logger.error("Unable to get list of active file templates " + e.getClass().getName());
			sendRedirect(exchange, Urls.fullURL(Urls.ERROR_PAGE_URL));
			return;
		}
		
		List<String> headers = CSVUtils.getFileListColumns();
		List<String[]> data = createCSVData(activeFiles, distributorId);
		
		CSVProcessor processor = new CSVProcessor();
		String path = processor.createCSV(headers, data);
		
		//OutputStream of this exchange is closed inside
		new FileSender().send(path, "availableFilesList.csv", exchange);
		new File(path).delete();
	}
	
	/**
	 * Creates arrays containing values for csv entries
	 * @param files
	 * @return
	 */
	private List<String[]> createCSVData(List<FileTemplate> files, int distribId) {
		List<String[]> result = new ArrayList<>();
			for(FileTemplate f : files) {
			List<String> tmp = new ArrayList<>();
			tmp.add(String.valueOf(f.getId()));
			tmp.add(new Date(f.getCreateTime()).toString());
			tmp.add(String.valueOf(f.getProductsCount()));
			tmp.add(String.valueOf(f.getSize()));
			tmp.add(String.valueOf(f.getCompressedSize()));
			try {
				tmp.add(new ShopDaoImpl().selectById(f.getShopId()).getName());
			} catch (DbAccessException | NoSuchEntityException e) {
				StatusEndpoint.incrementWarnings();
				logger.warn("Unable to shop name " + e.getClass().getName());
				tmp.add("unknown");
			}
			tmp.add(String.valueOf(f.getShopId()));
			tmp.add(createDownloadUrl(distribId, f.getId()));
			result.add(tmp.toArray(new String[0]));
		}
		return result;
	}

	private String createDownloadUrl(int distribId, int fileId) {
		return Urls.fullURL(Urls.DISTRIBUTOR_FILE_REQUEST_CONTROLLER_URL) +
				"?" + Links.DISTRIBUTOR_ID_PARAM_NAME + "=" + distribId +
				"&" + Links.FILE_TEMPLATE_ID_PARAM_NAME + "=" + fileId;
		
	}

}
