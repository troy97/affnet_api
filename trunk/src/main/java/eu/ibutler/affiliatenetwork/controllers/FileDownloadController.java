package eu.ibutler.affiliatenetwork.controllers;

import static eu.ibutler.affiliatenetwork.controllers.Links.*;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

import eu.ibutler.affiliatenetwork.dao.FileDao;
import eu.ibutler.affiliatenetwork.dao.exceptions.BadFileFormatException;
import eu.ibutler.affiliatenetwork.dao.exceptions.DbAccessException;
import eu.ibutler.affiliatenetwork.dao.exceptions.UniqueConstraintViolationException;
import eu.ibutler.affiliatenetwork.dao.impl.FileDaoImpl;
import eu.ibutler.affiliatenetwork.dao.impl.ProductDaoImpl;
import eu.ibutler.affiliatenetwork.entity.Admin;
import eu.ibutler.affiliatenetwork.entity.Product;
import eu.ibutler.affiliatenetwork.entity.UploadedFile;
import eu.ibutler.affiliatenetwork.entity.User;
import eu.ibutler.affiliatenetwork.http.DownloadErrorException;
import eu.ibutler.affiliatenetwork.http.ParsingException;
import eu.ibutler.affiliatenetwork.http.parse.MultipartDownloader;
import eu.ibutler.affiliatenetwork.http.session.HttpSession;
import eu.ibutler.affiliatenetwork.utils.AppConfig;
import eu.ibutler.affiliatenetwork.utils.FtlDataModel;
import eu.ibutler.affiliatenetwork.utils.FtlProcessingException;
import eu.ibutler.affiliatenetwork.utils.FtlProcessor;
import eu.ibutler.affiliatenetwork.utils.csv.CSVRecord;
import eu.ibutler.affiliatenetwork.utils.csv.CsvParser;

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
	
	private static Logger log = Logger.getLogger(FileDownloadController.class.getName());

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		
		//request method must be POST
		if(!exchange.getRequestMethod().equals("POST")) {
			log.error("Error, attempt to upload file not via POST");
			sendRedirect(exchange, ERROR_PAGE_CONTROLLER_FULL_URL);
			return;
		}
		
		//Content-Type must be multipart/form-data
		Headers headers = exchange.getRequestHeaders();
		String contentType = headers.getFirst("Content-Type");
		if(!contentType.contains("multipart/form-data")) {
			log.error("Error, no multipart/form-data to upload");
			sendRedirect(exchange, Links.ERROR_PAGE_CONTROLLER_FULL_URL);
			return;
		}
		
		//Get file uploaded by user
		UploadedFile uploadedFile = null;
		try {
			byte[] boundary = getBoundary(contentType);
			try(InputStream in = exchange.getRequestBody()) {
				uploadedFile = new MultipartDownloader().download(in, boundary, cfg.get("uploadPath"));
			}
		} catch (DownloadErrorException d) {
			log.error("Error downloading and saving file");
			sendRedirect(exchange, Links.ERROR_PAGE_CONTROLLER_FULL_URL);
			return;
		} catch (BadFileFormatException b) {
			log.debug("Attempt to upload a file of unsupported format, redirect back");
			sendRedirect(exchange, Links.stripQuery(exchange.getRequestHeaders().getFirst("Referer")) + Links.createQueryString(Links.ERROR_PARAM_NAME));
			//sendRedirect(exchange, "http://localhost:8080/upload?wrong=true");
			return;
		}
		
		//validate file
		CsvParser csvParser = new CsvParser(uploadedFile.getFsPath());
		int productCount = 0;
		if((productCount = csvParser.isValid()) == -1) {
			log.debug("File is invalid");
		} else {
			log.debug("File is valid");
			uploadedFile.setValid(true);
			uploadedFile.setActive(true);//####################################################################################
			uploadedFile.setProductsCount(productCount);
		}
		
		//put file into DB and assign DB id
		FileDao fileDao = new FileDaoImpl();
		try {
			int dbId = fileDao.insertOne(uploadedFile);
			uploadedFile.setDbId(dbId);
		} catch (DbAccessException e) {
			log.error("File was downloaded, but service failed to save it to db");
			sendRedirect(exchange, Links.ERROR_PAGE_CONTROLLER_FULL_URL);
			return;
		} catch (UniqueConstraintViolationException e) {
			log.debug("There's such file already, updating upload time...");
			try {
				fileDao.updateUploadTime(uploadedFile);
			} catch (DbAccessException e1) {
				log.error("Unable to update upload time");
				sendRedirect(exchange, Links.ERROR_PAGE_CONTROLLER_FULL_URL);
				return;
			}
		}
		
		log.info("New file uploaded to " + uploadedFile.getFsPath());
		
		//Parse file and save all products to database. Done in new thread.
		if(uploadedFile.isValid()) {
			saveProductsToDB(uploadedFile, csvParser);
		}
		
		//OK, generate html
		FtlDataModel ftlData = new FtlDataModel();
		ftlData.put("logoutPage", Links.LOGOUT_PAGE_CONTROLLER_FULL_URL);
		
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
		ftlData.put("uploadMoreLink", Links.stripQuery(exchange.getRequestHeaders().getFirst("Referer")));
		String responseHtml;
		try {
			responseHtml = new FtlProcessor().createHtml(DOWNLOAD_SUCCESS_FTL, ftlData);
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
	 * This methods starts a new thread which parses uploaded CSV file into
	 * separate records, creates Product objects and inserts them into DB 
	 * @param uploadedFile
	 * @param csvParser
	 * @throws ParsingException
	 * @throws DbAccessException
	 */
	private void saveProductsToDB(final UploadedFile uploadedFile, final CsvParser csvParser) {
		Thread insertProducts = new Thread(new Runnable() {
			
			@Override
			public void run() {
				log.debug("Starting product parsing thread...");
				try{	
					List<Product> products = new ArrayList<Product>();
					for(CSVRecord record : csvParser.parse()) {
						if(record.isConsistent()) {
							products.add( new Product(record, uploadedFile.getDbId(), uploadedFile.getWebShopId()) );
						} else {
							log.debug("Incosistent csv record, skipping product creation");
						}
					}
					//put products into DB
					new ProductDaoImpl().insertAll(products);
					log.debug("Products parsed and saved to DB successfully");
				} catch (ParsingException e) {
					log.debug("Unable to extract Products from uploaded csv file");
				} catch (DbAccessException e) {
					log.error("Unable to save products to DB");
				}
			}//run
			
		});//thread
		insertProducts.setName("parseCsvAndInsertProductsToDBFrom: " + uploadedFile.getName());
		insertProducts.start();
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
