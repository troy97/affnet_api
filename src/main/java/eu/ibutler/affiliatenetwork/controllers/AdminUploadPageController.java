package eu.ibutler.affiliatenetwork.controllers;

import static eu.ibutler.affiliatenetwork.utils.LinkUtils.*;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.sun.net.httpserver.HttpExchange;

import eu.ibutler.affiliatenetwork.dao.exceptions.DbAccessException;
import eu.ibutler.affiliatenetwork.dao.impl.ShopDaoImpl;
import eu.ibutler.affiliatenetwork.entity.Admin;
import eu.ibutler.affiliatenetwork.entity.Shop;
import eu.ibutler.affiliatenetwork.http.session.HttpSession;
import eu.ibutler.affiliatenetwork.utils.FtlDataModel;
import eu.ibutler.affiliatenetwork.utils.FtlProcessingException;
import eu.ibutler.affiliatenetwork.utils.FtlProcessor;
import eu.ibutler.affiliatenetwork.utils.LinkUtils;

@SuppressWarnings("restriction")
public class AdminUploadPageController extends AbstractHttpHandler{

		private static Logger log = Logger.getLogger(UploadPageController.class.getName());
		
		@Override
		public void handle(HttpExchange exchange) throws IOException {
			//get and close inputStream
			try(InputStream in = exchange.getRequestBody()){}
			
			//get session
			HttpSession session = (HttpSession) exchange.getAttribute(EXCHANGE_SESSION_ATTR_NAME);
			Admin admin = (Admin) session.getAttribute(SESSION_USER_ATTR_NAME);
			
			//check if it's the first attempt to upload,
			//if not, put "wrong" notification to dataModel
			FtlDataModel ftlData = new FtlDataModel();
			String queryStr = exchange.getRequestURI().getQuery();
			if((queryStr != null) && queryStr.contains("wrong=true")) {
				ftlData.put("badFileFormat", "<font color=\"red\">" + cfg.get("badFileFormat") + "</font>");
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
			
			//create upload page html
			String responseHtml;
			try {
				ftlData.put("shopList", shops);
				ftlData.put("logoutPage", LinkUtils.LOGOUT_PAGE_CONTROLLER_FULL_URL);
				ftlData.put("statusPage", LinkUtils.STATUS_PAGE_CONTROLLER_FULL_URL);
				ftlData.put("downloadPage", LinkUtils.DOWNLOAD_PAGE_CONTROLLER_FULL_URL);
				ftlData.put("uploadPage", cfg.makeUrl("DOMAIN_NAME", "ADMIN_UPLOAD_PAGE_URL"));
				ftlData.put("name", admin.getEmail());
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
