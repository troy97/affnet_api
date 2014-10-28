package eu.ibutler.affiliatenetwork.controllers;

import static eu.ibutler.affiliatenetwork.controllers.utils.Links.*;

import java.io.BufferedOutputStream;
import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;

import eu.ibutler.affiliatenetwork.config.Urls;
import eu.ibutler.affiliatenetwork.controllers.utils.FsPaths;
import eu.ibutler.affiliatenetwork.controllers.utils.Links;
import eu.ibutler.affiliatenetwork.dao.FileDao;
import eu.ibutler.affiliatenetwork.dao.exceptions.DaoException;
import eu.ibutler.affiliatenetwork.dao.impl.FileDaoImpl;
import eu.ibutler.affiliatenetwork.entity.Admin;
import eu.ibutler.affiliatenetwork.entity.UploadedFile;
import eu.ibutler.affiliatenetwork.entity.User;
import eu.ibutler.affiliatenetwork.http.parse.MultipartDownloader;
import eu.ibutler.affiliatenetwork.http.parse.exceptions.DownloadErrorException;
import eu.ibutler.affiliatenetwork.http.session.HttpSession;
import eu.ibutler.affiliatenetwork.utils.freemarker.FtlDataModel;
import eu.ibutler.affiliatenetwork.utils.freemarker.FtlProcessingException;
import eu.ibutler.affiliatenetwork.utils.freemarker.FtlProcessor;
import eu.ibutler.affiliatenetwork.validation.FileValidator;
import eu.ibutler.affiliatenetwork.validation.ValidationException;
import eu.ibutler.affiliatenetwork.validation.ValidationUtils;

/**
 * This handler tries to download a file sent from
 * upload page and renders "downloadSuccessfull" page if
 * process went OK, "errorPage" otherwise.
 * @author Anton Lukashchuk
 *
 */
@SuppressWarnings("restriction")
@WebController("/download")
public class FileDownloadController extends AbstractHttpHandler implements RestrictedAccess {
	
	@Override
	public void handleBody(HttpExchange exchange) throws IOException {
		logger.debug("Start file download request handling.");
		//request method must be POST
		if(!exchange.getRequestMethod().equals("POST")) {
			logger.error("Error, attempt to upload file not via POST");
			sendRedirect(exchange, Urls.fullURL(Urls.ERROR_PAGE_URL));
			return;
		}
		
		//Get file uploaded by user
		UploadedFile uploadedFile = null;
		try {
			//InputStream is closed inside
			uploadedFile = new MultipartDownloader().download(exchange, FsPaths.UPLOAD_FOLDER);
		} catch (DownloadErrorException d) {
			StatusEndpoint.incrementWarnings();
			logger.warn("Error downloading or saving file.");
			sendRedirect(exchange, Urls.fullURL(Urls.ERROR_PAGE_URL));
			return;
		} catch (ValidationException e) {
			logger.debug("Bad file extension or invalid content, redirect to referer." + " Message: " + e.getMessage());
			sendRedirect(exchange, Links.stripQuery(exchange.getRequestHeaders().getFirst("Referer")) + Links.createQueryString(Links.ERROR_PARAM_NAME));
			return;
		}
		
		//put file into DB
		FileDao fileDao = new FileDaoImpl();
		try {
			int dbId = fileDao.insertOne(uploadedFile);
			uploadedFile.setId(dbId);
		} catch (DaoException e) {
			StatusEndpoint.incrementErrors();
			logger.error("File was downloaded, but service failed to add entry about it into DB, redirect to error page. " + 
			"filePath: " + uploadedFile.getFsPath() + " Exception: " + e.getClass().getName() + " Message: " + e.getMessage());
			sendRedirect(exchange, Urls.fullURL(Urls.ERROR_PAGE_URL));
			return;
		} 
		
		logger.debug("New file downloaded to " + uploadedFile.getFsPath());
		
		//Parse, validate file, save all products to database and create template. Done in new thread. 
		FileValidator validator = ValidationUtils.getValidator(uploadedFile.getExtension());
		validator.validateFile(uploadedFile);
		
		//OK, generate response html
		FtlDataModel ftlData = new FtlDataModel();
		ftlData.put("logoutPage", Urls.fullURL(Urls.LOGOUT_PAGE_URL));
		
		//get session and user object (don't know if it's a user or admin)
		HttpSession session = (HttpSession) exchange.getAttribute(EXCHANGE_SESSION_ATTR_NAME);
		Object client = session.getAttribute(SESSION_USER_ATTR_NAME);
		if(client instanceof User) {
			User user = (User) client;
			ftlData.put("cabinetPage", Urls.fullURL(Urls.USER_CABINET_PAGE_URL));
			ftlData.put("name", user.getEmail());
		} else if(client instanceof Admin) {
			Admin admin = (Admin) client;
			ftlData.put("name", admin.getEmail());
		}
		
		ftlData.put("fileName", uploadedFile.getName());
		ftlData.put("uploadMoreLink", Links.stripQuery(exchange.getRequestHeaders().getFirst("Referer")));
		//ftlData.put("viewLastFiles", Urls.VIEW_LAST_FILES_PAGE_URL);
		String responseHtml;
		try {
			responseHtml = new FtlProcessor().createHtml(DOWNLOAD_SUCCESS_FTL, ftlData);
		} catch (FtlProcessingException e) {
			logger.error("Error creating \"download successfull\" page");
			responseHtml = "Downloaded Successfully";
		}
		byte[] responseBytes = responseHtml.getBytes("UTF-8");
		exchange.sendResponseHeaders(200, responseBytes.length);
		try(BufferedOutputStream out = new BufferedOutputStream(exchange.getResponseBody());) {
			out.write(responseBytes);
			out.flush();
		}
		logger.debug("Response sent. Return.");
	}


	
}
