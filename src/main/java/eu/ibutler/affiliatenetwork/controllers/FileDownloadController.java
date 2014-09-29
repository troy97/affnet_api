package eu.ibutler.affiliatenetwork.controllers;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

import eu.ibutler.affiliatenetwork.dao.FileDao;
import eu.ibutler.affiliatenetwork.dao.exceptions.BadFileFormatException;
import eu.ibutler.affiliatenetwork.dao.exceptions.DbAccessException;
import eu.ibutler.affiliatenetwork.dao.exceptions.UniqueConstraintViolationException;
import eu.ibutler.affiliatenetwork.dao.impl.FileDaoImpl;
import eu.ibutler.affiliatenetwork.entity.AppProperties;
import eu.ibutler.affiliatenetwork.entity.FtlDataModel;
import eu.ibutler.affiliatenetwork.entity.FtlProcessor;
import eu.ibutler.affiliatenetwork.entity.MultipartDownloader;
import eu.ibutler.affiliatenetwork.entity.LinkUtils;
import eu.ibutler.affiliatenetwork.entity.UploadedFile;
import eu.ibutler.affiliatenetwork.entity.exceptions.DownloadErrorException;
import eu.ibutler.affiliatenetwork.entity.exceptions.FtlProcessingException;

@SuppressWarnings("restriction")
public class FileDownloadController extends AbstractHttpHandler {
	
	private static final String UPLOAD_PAGE_CONTROLLER_FULL_URL = "http://localhost:8080/affiliatenetwork/upload";
	private static final String UPLOAD_PAGE_CONTROLLER_FULL_URL_BAD_FILE = "http://localhost:8080/affiliatenetwork/upload?wrong=true";
	private static final String DOWNLOAD_SUCCESS_FTL = "downloadSuccess.ftl";
	
	
	private static Logger log = Logger.getLogger(FileDownloadController.class.getName());
	private static AppProperties properties = AppProperties.getInstance();

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
		
		UploadedFile uploadedFile = null;
		try {
			byte[] boundary = getBoundary(contentType);
			try(InputStream in = exchange.getRequestBody()) {
				uploadedFile = new MultipartDownloader().download(in, boundary, properties.getProperty("uploadPath"));
			}
		} catch (DownloadErrorException d) {
			log.error("Error downloading and saving file");
			sendRedirect(exchange, LinkUtils.ERROR_PAGE_CONTROLLER_FULL_URL);
			return;
		} catch (BadFileFormatException b) {
			log.debug("Attempt to upload a file of unsupported format");
			sendRedirect(exchange, UPLOAD_PAGE_CONTROLLER_FULL_URL_BAD_FILE);
			return;
		}

		//put uploaded file into DB
		FileDao fileDao = new FileDaoImpl();
		try {
			int dbId = fileDao.insertFile(uploadedFile);
			uploadedFile.setDbId(dbId);
		} catch (DbAccessException e) {
			log.debug("file was downloaded, but unable to save to db");
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
		ftlData.put("fileName", uploadedFile.getName());
		ftlData.put("uploadMoreLink", "<a href=" + UPLOAD_PAGE_CONTROLLER_FULL_URL + ">Upload another file</a>");
		String responseHtml;
		try {
			responseHtml = new FtlProcessor().createHtml(DOWNLOAD_SUCCESS_FTL, ftlData);
		} catch (FtlProcessingException e) {
			responseHtml = "Downloaded Successfully";
		}
		byte[] responseBytes = responseHtml.getBytes("UTF-8");
		exchange.sendResponseHeaders(200, responseBytes.length);
		try(BufferedOutputStream out = new BufferedOutputStream(exchange.getResponseBody());) {
			out.write(responseBytes);
			out.flush();
		}

	}
	
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
