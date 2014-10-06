package eu.ibutler.affiliatenetwork.controllers;

import static eu.ibutler.affiliatenetwork.utils.LinkUtils.*;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;

import com.sun.net.httpserver.HttpExchange;

import eu.ibutler.affiliatenetwork.entity.User;
import eu.ibutler.affiliatenetwork.http.session.HttpSession;
import eu.ibutler.affiliatenetwork.utils.FtlDataModel;
import eu.ibutler.affiliatenetwork.utils.FtlProcessingException;
import eu.ibutler.affiliatenetwork.utils.FtlProcessor;
import eu.ibutler.affiliatenetwork.utils.LinkUtils;

@SuppressWarnings("restriction")
public class UploadPageController extends AbstractHttpHandler {

	private static Logger log = Logger.getLogger(UploadPageController.class.getName());
	
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		//get and close inputStream
		try(InputStream in = exchange.getRequestBody()){}
		
		//get session and user object
		HttpSession session = (HttpSession) exchange.getAttribute(EXCHANGE_SESSION_ATTR_NAME);
		User user = (User) session.getAttribute(SESSION_USER_ATTR_NAME);
		
		//check if it's the first attempt to upload,
		//if not, put "wrong" notification to dataModel
		FtlDataModel ftlData = new FtlDataModel();
		String queryStr = exchange.getRequestURI().getQuery();
		if((queryStr != null) && queryStr.contains("wrong=true")) {
			ftlData.put("badFileFormat", "<font color=\"red\">" + cfg.get("badFileFormat") + "</font>");
		}
		ftlData.put("logoutPage", LinkUtils.LOGOUT_PAGE_CONTROLLER_FULL_URL);
		ftlData.put("downloadPage", LinkUtils.DOWNLOAD_PAGE_CONTROLLER_FULL_URL);
		ftlData.put("uploadPage", LinkUtils.UPLOAD_PAGE_CONTROLLER_FULL_URL);
		ftlData.put("cabinetPage", cfg.makeUrl("DOMAIN_NAME", "USER_CABINET_PAGE_URL"));
		ftlData.put("name", user.getEmail());
		ftlData.put("shopId", user.getShopId());
		
		//create upload page html
		String responseHtml;
		try {
			responseHtml = new FtlProcessor().createHtml(LinkUtils.UPLOAD_PAGE_FTL, ftlData);
		} catch (FtlProcessingException e) {
			sendRedirect(exchange, cfg.makeUrl("DOMAIN_NAME", "ERROR_PAGE_URL"));
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
