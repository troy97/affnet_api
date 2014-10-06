package eu.ibutler.affiliatenetwork.controllers;

import static eu.ibutler.affiliatenetwork.utils.LinkUtils.EXCHANGE_SESSION_ATTR_NAME;
import static eu.ibutler.affiliatenetwork.utils.LinkUtils.SESSION_USER_ATTR_NAME;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.apache.log4j.Logger;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

import eu.ibutler.affiliatenetwork.dao.FileDao;
import eu.ibutler.affiliatenetwork.dao.exceptions.BadFileFormatException;
import eu.ibutler.affiliatenetwork.dao.exceptions.DbAccessException;
import eu.ibutler.affiliatenetwork.dao.exceptions.UniqueConstraintViolationException;
import eu.ibutler.affiliatenetwork.dao.impl.FileDaoImpl;
import eu.ibutler.affiliatenetwork.entity.Admin;
import eu.ibutler.affiliatenetwork.entity.UploadedFile;
import eu.ibutler.affiliatenetwork.entity.User;
import eu.ibutler.affiliatenetwork.http.DownloadErrorException;
import eu.ibutler.affiliatenetwork.http.parse.MultipartDownloader;
import eu.ibutler.affiliatenetwork.http.session.HttpSession;
import eu.ibutler.affiliatenetwork.utils.AppConfig;
import eu.ibutler.affiliatenetwork.utils.FtlDataModel;
import eu.ibutler.affiliatenetwork.utils.FtlProcessingException;
import eu.ibutler.affiliatenetwork.utils.FtlProcessor;
import eu.ibutler.affiliatenetwork.utils.LinkUtils;

/**
 * This handler tries to download a file sent from
 * upload page and renders "downloadSuccessfull" page if
 * process went OK, "errorPage" otherwise.
 * @author Anton Lukashchuk
 *
 */
@SuppressWarnings("restriction")
public class FileDownloadController extends AbstractHttpHandler {
	
	private static Logger log = Logger.getLogger(FileDownloadController.class.getName());
	private static AppConfig properties = AppConfig.getInstance();

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		
		//request method must be POST
		if(!exchange.getRequestMethod().equals("POST")) {
			log.error("Error, attempt to upload file not via POST");
			sendRedirect(exchange, LinkUtils.ERROR_PAGE_CONTROLLER_FULL_URL);
			return;
		}
		
		//Content-Type must be multipart/form-data
		Headers headers = exchange.getRequestHeaders();
		String contentType = headers.getFirst("Content-Type");
		if(!contentType.contains("multipart/form-data")) {
			log.error("Error, no multipart/form-data to upload");
			sendRedirect(exchange, LinkUtils.ERROR_PAGE_CONTROLLER_FULL_URL);
			return;
		}
		
		//Get file uploaded by user
		UploadedFile uploadedFile = null;
		try {
			byte[] boundary = getBoundary(contentType);
			try(InputStream in = exchange.getRequestBody()) {
				uploadedFile = new MultipartDownloader().download(in, boundary, properties.get("uploadPath"));
			}
		} catch (DownloadErrorException d) {
			log.error("Error downloading and saving file");
			sendRedirect(exchange, LinkUtils.ERROR_PAGE_CONTROLLER_FULL_URL);
			return;
		} catch (BadFileFormatException b) {
			log.debug("Attempt to upload a file of unsupported format, redirect back");
			sendRedirect(exchange, exchange.getRequestHeaders().getFirst("Referer") + "?wrong=true");
			return;
		}

		//put uploaded file into DB
		FileDao fileDao = new FileDaoImpl();
		try {
			int dbId = fileDao.insertFile(uploadedFile);
			uploadedFile.setDbId(dbId);
		} catch (DbAccessException e) {
			log.error("File was downloaded, but service failed to save it to db");
			sendRedirect(exchange, LinkUtils.ERROR_PAGE_CONTROLLER_FULL_URL);
			return;
		} catch (UniqueConstraintViolationException e) {
			log.debug("There's such file already, updating upload time...");
			try {
				fileDao.updateUploadTime(uploadedFile);
			} catch (DbAccessException e1) {
				log.error("Unable to update upload time");
				sendRedirect(exchange, LinkUtils.ERROR_PAGE_CONTROLLER_FULL_URL);
				return;
			}
		}
		
		log.info("New file uploaded to " + uploadedFile.getFsPath());
		
		
		//OK, generate html
		FtlDataModel ftlData = new FtlDataModel();
		ftlData.put("logoutPage", LinkUtils.LOGOUT_PAGE_CONTROLLER_FULL_URL);
		ftlData.put("statusPage", LinkUtils.STATUS_PAGE_CONTROLLER_FULL_URL);
		ftlData.put("uploadPage", LinkUtils.UPLOAD_PAGE_CONTROLLER_FULL_URL);
		
		//get session and user object (don't know if it's a user or admin)
		HttpSession session = (HttpSession) exchange.getAttribute(EXCHANGE_SESSION_ATTR_NAME);
		Object client = session.getAttribute(SESSION_USER_ATTR_NAME);
		if(client instanceof User) {
			User user = (User) client;
			ftlData.put("cabinetPage", cfg.makeUrl("DOMAIN_NAME", "USER_CABINET_PAGE_URL"));
			ftlData.put("name", user.getEmail());
		} else if(client instanceof Admin) {
			Admin admin = (Admin) client;
			ftlData.put("name", admin.getEmail());
		}
		
		ftlData.put("fileName", uploadedFile.getName());
		ftlData.put("uploadMoreLink", "<a href=" + exchange.getRequestHeaders().getFirst("Referer") + ">Upload another file</a>");
		String responseHtml;
		try {
			responseHtml = new FtlProcessor().createHtml(LinkUtils.DOWNLOAD_SUCCESS_FTL, ftlData);
		} catch (FtlProcessingException e) {
			log.error("Error creating download successfull page");
			responseHtml = "Downloaded Successfully";
		}
		byte[] responseBytes = responseHtml.getBytes("UTF-8");
		exchange.sendResponseHeaders(200, responseBytes.length);
		try(BufferedOutputStream out = new BufferedOutputStream(exchange.getResponseBody());) {
			out.write(responseBytes);
			out.flush();
		}

	}
	
	/**
	 * Parse boundary header 
	 * @param contentTypeHeader
	 * @return boundary as byte array
	 */
	private byte[] getBoundary(String contentTypeHeader) {
		String boundaryStr = contentTypeHeader.split("boundary=")[1];
		byte[] boundary;
		try {
			boundary = boundaryStr.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			log.error("Unable to get boundary from multipart headers, encoding problem");
			boundary = boundaryStr.getBytes();
		}
		return boundary;
	}
	
}
