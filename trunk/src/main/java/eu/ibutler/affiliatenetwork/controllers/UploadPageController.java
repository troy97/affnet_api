package eu.ibutler.affiliatenetwork.controllers;

import static eu.ibutler.affiliatenetwork.controllers.Links.*;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.apache.log4j.Logger;

import com.sun.net.httpserver.HttpExchange;

import eu.ibutler.affiliatenetwork.entity.User;
import eu.ibutler.affiliatenetwork.http.ParsingException;
import eu.ibutler.affiliatenetwork.http.parse.Parser;
import eu.ibutler.affiliatenetwork.http.session.HttpSession;
import eu.ibutler.affiliatenetwork.utils.FtlDataModel;
import eu.ibutler.affiliatenetwork.utils.FtlProcessingException;
import eu.ibutler.affiliatenetwork.utils.FtlProcessor;

@SuppressWarnings("restriction")
@WebController("/upload")
public class UploadPageController extends AbstractHttpHandler implements RestrictedAccess {

	private static Logger log = Logger.getLogger(UploadPageController.class.getName());
	
	@Override
	public void handleBody(HttpExchange exchange) throws IOException {
		//get and close inputStream
		try(InputStream in = exchange.getRequestBody()){}
		
		//get session and user object
		HttpSession session = (HttpSession) exchange.getAttribute(EXCHANGE_SESSION_ATTR_NAME);
		User user = (User) session.getAttribute(SESSION_USER_ATTR_NAME);
		
		//check if it's the first attempt to upload,
		//if not, put "wrong" notification to dataModel
		FtlDataModel ftlData = new FtlDataModel();
		String queryStr = exchange.getRequestURI().getQuery();
		checkErrorParams(ftlData, queryStr);
		ftlData.put("logoutPage", Links.LOGOUT_PAGE_CONTROLLER_FULL_URL);
		ftlData.put("downloadPage", Links.DOWNLOAD_PAGE_CONTROLLER_FULL_URL);
		ftlData.put("uploadPage", Links.UPLOAD_PAGE_CONTROLLER_FULL_URL);
		ftlData.put("cabinetPage", cfg.makeUrl("DOMAIN_NAME", "USER_CABINET_PAGE_URL"));
		ftlData.put("name", user.getEmail());
		ftlData.put("shopId", user.getShopId());
		
		//create upload page html
		String responseHtml;
		try {
			responseHtml = new FtlProcessor().createHtml(Links.UPLOAD_PAGE_FTL, ftlData);
		} catch (FtlProcessingException e) {
			log.debug("Failed to process FTL");
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
	
	/**
	 * Verify if some messages for User are to be added to FTL 
	 * @param ftlData
	 * @param queryStr
	 */
	private void checkErrorParams(FtlDataModel ftlData, String queryStr) {
		if(queryStr == null) {
			return;
		}
		try {
			Map<String, String> params = Parser.parseQuery(queryStr);
			if(params.containsKey(INVALID_FILE_PARAM_NAME)) {
				ftlData.put("badFileFormat", cfg.get("invalidFileMsg"));		
			}  else if(params.containsKey(ERROR_PARAM_NAME)) {
				ftlData.put("badFileFormat", cfg.get("badFileFormat"));	
			} 
		} catch (ParsingException ignore) {}
	}
	

}
