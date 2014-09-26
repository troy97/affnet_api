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
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import eu.ibutler.affiliatenetwork.entity.AppProperties;
import eu.ibutler.affiliatenetwork.entity.HttpDownloader;
import eu.ibutler.affiliatenetwork.entity.exceptions.ParsingException;

@SuppressWarnings("restriction")
public class FileDownloadController extends AbstractHttpHandler {
	
	private static Logger log = Logger.getLogger(FileDownloadController.class.getName());
	private static AppProperties properties = AppProperties.getInstance();

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		
		//request method must be POST
		if(!exchange.getRequestMethod().equals("POST")) {
			log.error("Error, attempt to upload file not via POST");
			//redirect here...
		}
		
		Headers headers = exchange.getRequestHeaders();
		
		String contentType = headers.getFirst("Content-Type");
		if(!contentType.contains("multipart/form-data")) {
			log.error("Error, no multipart/form-data to upload");
			//redirect here...
		}
		
		String boundaryStr = contentType.split("boundary=")[1];
		byte[] boundary = boundaryStr.getBytes("UTF-8");
		
	    //String contentLengthStr = headers.getFirst("Content-Length");
	    //int contentLength = Integer.valueOf(contentLengthStr);

		try(InputStream in = exchange.getRequestBody();) {
			HttpDownloader downloader = new HttpDownloader();
			String filePath = null;
			try {
				filePath = downloader.fileDownload(in, boundary, properties.getProperty("uploadPath"));
			} catch (Exception e) {
				log.error("Error downloading and saving file, exception: " + e.getClass().getCanonicalName());
				//redirect...
			}
			log.info("File was downloaded and stored to : \"" + filePath + "\"");
		}

		//OK generate html
		String responseHtml = "uploaded";
		byte[] responseBytes = responseHtml.getBytes("UTF-8");
		exchange.sendResponseHeaders(200, responseBytes.length);
		try(BufferedOutputStream out = new BufferedOutputStream(exchange.getResponseBody());) {
			out.write(responseBytes);
			out.flush();
		}

	}
	
}
