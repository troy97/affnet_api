package eu.ibutler.affiliatenetwork.controllers;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.sun.net.httpserver.HttpExchange;

import eu.ibutler.affiliatenetwork.dao.exceptions.DbAccessException;
import eu.ibutler.affiliatenetwork.dao.impl.ShopDaoImpl;
import eu.ibutler.affiliatenetwork.entity.FtlDataModel;
import eu.ibutler.affiliatenetwork.entity.FtlProcessor;
import eu.ibutler.affiliatenetwork.entity.LinkUtils;
import eu.ibutler.affiliatenetwork.entity.Shop;
import eu.ibutler.affiliatenetwork.entity.exceptions.FtlProcessingException;

@SuppressWarnings("restriction")
public class UploadPageController extends AbstractHttpHandler {

	private static Logger log = Logger.getLogger(UploadPageController.class.getName());
	
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		//get and close inputStream
		try(InputStream in = exchange.getRequestBody()){}
		
		//check if it's the first attempt to upload,
		//if not, put "wrong" notification to dataModel
		FtlDataModel ftlData = new FtlDataModel();
		String queryStr = exchange.getRequestURI().getQuery();
		if((queryStr != null) && queryStr.contains("wrong=true")) {
			ftlData.put("badFormatMessage", "<font color=\"red\">You tried to upload a file of usupported format, please try again</font>");
		}
		
		//create dataModel with list of Shops
		List<Shop> shops = new ArrayList<>();
		try {
			shops = new ShopDaoImpl().getAllShops();
		} catch (DbAccessException e1) {
			log.error("Unable to get shop list from DAO, DbAccessException");
			sendRedirect(exchange, LinkUtils.ERROR_PAGE_CONTROLLER_FULL_URL);
			return;
		}
		ftlData.put("shopList", shops);
		
		//create upload page html
		String responseHtml;
		try {
			ftlData.put("logoutPage", LinkUtils.LOGOUT_PAGE_CONTROLLER_FULL_URL);
			ftlData.put("statusPage", LinkUtils.STATUS_PAGE_CONTROLLER_FULL_URL);
			ftlData.put("downloadPage", LinkUtils.DOWNLOAD_PAGE_CONTROLLER_FULL_URL);
			ftlData.put("uploadPage", LinkUtils.UPLOAD_PAGE_CONTROLLER_FULL_URL);
			responseHtml = new FtlProcessor().createHtml(LinkUtils.UPLOAD_PAGE_FTL, ftlData);
		} catch (FtlProcessingException e) {
			sendRedirect(exchange, LinkUtils.ERROR_PAGE_CONTROLLER_FULL_URL);
			return;
		}
		
		//send response to outputStrem
		try(BufferedOutputStream out = new BufferedOutputStream(exchange.getResponseBody())){
			byte[] responseBytes = responseHtml.getBytes();
			exchange.sendResponseHeaders(200, responseBytes.length);
			out.write(responseBytes);
			out.flush();
		}
	}
	

}
