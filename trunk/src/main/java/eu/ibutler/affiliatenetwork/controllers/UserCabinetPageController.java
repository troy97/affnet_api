package eu.ibutler.affiliatenetwork.controllers;

import static eu.ibutler.affiliatenetwork.controllers.Links.*;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;

import com.sun.net.httpserver.HttpExchange;

import eu.ibutler.affiliatenetwork.config.Urls;
import eu.ibutler.affiliatenetwork.entity.User;
import eu.ibutler.affiliatenetwork.http.session.HttpSession;
import eu.ibutler.affiliatenetwork.utils.FtlDataModel;
import eu.ibutler.affiliatenetwork.utils.FtlProcessingException;
import eu.ibutler.affiliatenetwork.utils.FtlProcessor;

@SuppressWarnings("restriction")
@WebController("/userCabinet")
public class UserCabinetPageController extends AbstractHttpHandler implements RestrictedAccess {
	
	private static Logger log = Logger.getLogger(UserCabinetPageController.class.getName());

	@Override
	public void handleBody(HttpExchange exchange) throws IOException {
		//get and close inputStream
		try(InputStream in = exchange.getRequestBody()){}
		
		//get session and user object
		HttpSession session = (HttpSession) exchange.getAttribute(EXCHANGE_SESSION_ATTR_NAME);
		User user = (User) session.getAttribute(SESSION_USER_ATTR_NAME);
		
		FtlDataModel ftlData = new FtlDataModel();
		ftlData.put("name", user.getEmail());
		
		ftlData.put("logoutPage", Urls.fullURL(Urls.LOGOUT_PAGE_URL));
		ftlData.put("uploadPage", Urls.fullURL(Urls.UPLOAD_PAGE_URL));
		ftlData.put("updateProfilePage", Urls.fullURL(Urls.UPDATE_USER_PROFILE_PAGE_URL));
		ftlData.put("viewLastFilesPage", Urls.fullURL(Urls.VIEW_LAST_FILES_PAGE_URL));
		
		ftlData.put("uploadPageLinkName", cfg.get("userUploadFileInvaitation"));
		ftlData.put("updateProfileLinkName", cfg.get("userUpdateProfileInvaitation"));
		ftlData.put("viewLastFilesLinkName", cfg.get("userLastUploadedFilesInvaitation"));
		
		//create upload page html
		String responseHtml;
		try {
			responseHtml = new FtlProcessor().createHtml(Links.USER_CABINET_FTL, ftlData);
		} catch (FtlProcessingException e) {
			log.debug("Error processing cabinet page FTL");
			sendRedirect(exchange, Urls.fullURL(Urls.ERROR_PAGE_URL));
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
