package eu.ibutler.affiliatenetwork.controllers;

import static eu.ibutler.affiliatenetwork.utils.LinkUtils.EXCHANGE_SESSION_ATTR_NAME;
import static eu.ibutler.affiliatenetwork.utils.LinkUtils.SESSION_USER_ATTR_NAME;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;

import com.sun.net.httpserver.HttpExchange;

import eu.ibutler.affiliatenetwork.dao.exceptions.DbAccessException;
import eu.ibutler.affiliatenetwork.dao.impl.FileDaoImpl;
import eu.ibutler.affiliatenetwork.entity.UploadedFile;
import eu.ibutler.affiliatenetwork.entity.User;
import eu.ibutler.affiliatenetwork.http.session.HttpSession;
import eu.ibutler.affiliatenetwork.utils.FtlDataModel;
import eu.ibutler.affiliatenetwork.utils.FtlProcessingException;
import eu.ibutler.affiliatenetwork.utils.FtlProcessor;

@SuppressWarnings("restriction")
@WebController("/viewLastFiles")
public class ViewLastFilesPageController extends AbstractHttpHandler implements RestrictedAccess {

	private static Logger log = Logger.getLogger(ViewLastFilesPageController.class.getName());
	
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		
		log.debug("ViewLastFilesPageController started");
		
		//get session and user object
		HttpSession session = (HttpSession) exchange.getAttribute(EXCHANGE_SESSION_ATTR_NAME);
		User user = (User) session.getAttribute(SESSION_USER_ATTR_NAME);
		
		List<UploadedFile> files;
		try {
			files = new FileDaoImpl().getLastNfiles(10, user.getShopId());
		} catch (DbAccessException e) {
			log.debug("Unable to extract file from DB");
			sendRedirect(exchange, cfg.makeUrl("DOMAIN_NAME", "ERROR_PAGE_URL"));
			return;
		}
		
		FtlDataModel ftlData = new FtlDataModel();
		ftlData.put("fileList", files);
		
		ftlData.put("name", user.getEmail());
		ftlData.put("logoutPage", cfg.makeUrl("DOMAIN_NAME", "LOGOUT_PAGE_URL"));
		ftlData.put("cabinetPage", cfg.makeUrl("DOMAIN_NAME", "USER_CABINET_PAGE_URL"));
		
		//create upload page html
		String responseHtml;
		try {
			responseHtml = new FtlProcessor().createHtml(cfg.get("VIEW_LAST_FILES_FTL"), ftlData);
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
