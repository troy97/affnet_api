package eu.ibutler.affiliatenetwork.http.parse;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Random;

import org.apache.commons.fileupload.MultipartStream;
import org.apache.commons.fileupload.MultipartStream.MalformedStreamException;
import org.apache.log4j.Logger;

import com.sun.net.httpserver.HttpExchange;

import eu.ibutler.affiliatenetwork.entity.UploadedFile;
import eu.ibutler.affiliatenetwork.http.parse.exceptions.BadFileFormatException;
import eu.ibutler.affiliatenetwork.http.parse.exceptions.DownloadErrorException;
import eu.ibutler.affiliatenetwork.http.parse.exceptions.ParsingException;
import eu.ibutler.affiliatenetwork.validation.FileValidator;
import eu.ibutler.affiliatenetwork.validation.ValidationException;
import eu.ibutler.affiliatenetwork.validation.ValidationUtils;

/**
 * This class implements downloading of price-list file sent via http multipart 
 * @author Anton Lukashchuk
 *
 */
@SuppressWarnings("restriction") 
public class MultipartDownloader {
	
	private static Logger logger = Logger.getLogger(MultipartDownloader.class.getName());
	
	private static final int INPUT_BUFF_SIZE = 4096;
	/**
	 * Download single file
	 * @param InputStream. Note! this method closes InputStream of given exchange.
	 * @param http boundary as byte array
	 * @param path to folder where the resulting file will be stored, for example "/home/userName/downloadedFiles"
	 * @return UploadedFile object
	 * @throws DownloadErrorException
	 * @throws BadFileFormatException
	 */
	public UploadedFile download(HttpExchange exchange, String folderPath) throws DownloadErrorException, ValidationException {
		logger.debug("Starting multipart download.");
		UploadedFile result = null;
		int shopId = 0;
		String extension = null;
		
		String contentType = exchange.getRequestHeaders().getFirst("Content-Type");
		if(!contentType.contains("multipart/form-data")) {
			logger.debug("No multipart/form-data to upload");
			throw new DownloadErrorException();
		}
		
		String tmpFilePath = null;
		try {
			byte[] boundary = getBoundary(contentType);
			InputStream in = exchange.getRequestBody();
			MultipartStream multipartStream = new MultipartStream(in, boundary, INPUT_BUFF_SIZE, null);
			boolean nextPart = multipartStream.skipPreamble();
			int partNumber = 1;
			while(nextPart) {
				String header = multipartStream.readHeaders();
				if(partNumber == 1) {
					//get shopId
					shopId = getShopId(multipartStream);
					partNumber = 2; //next iteration will go on "else" branch
				} else {
					//download file	
					extension = getExtension(header);
					
					FileValidator validator = ValidationUtils.getValidator(extension);
					if(validator == null) {
						logger.debug("Unsupported extension, there's no validator for extension: " + extension);
						throw new ValidationException("Unsupported file extension");
					}
					byte[] firstLine = validator.validateDownload(multipartStream);
					logger.debug("File looks good, continue download...");
					
					//create unique temporary file name, later this file will be renamed to appropriate name format
					String tmpFileName = shopId + "_" + System.currentTimeMillis() + "_" + new Random().nextInt(512);
		        	tmpFilePath = folderPath + "/" + tmpFileName;
		        	File tmpFile = new File(tmpFilePath);
		        	FileOutputStream fileOut = new FileOutputStream(tmpFile);
		        	fileOut.write(firstLine);
		        	multipartStream.readBodyData(fileOut);
		        	fileOut.close();
		        	logger.debug("Temporary file created: " + tmpFilePath);
				}
				nextPart = multipartStream.readBoundary();
			}//while
			in.close();
		} catch(IOException e) {
			logger.debug("Error downloading file: " + e.getClass().getName());
			throw new DownloadErrorException();
		}	
		
		long uploadTime = System.currentTimeMillis();
		result = new UploadedFile(tmpFilePath, extension, uploadTime, shopId);
		logger.debug("File downloaded successfully. Return.");
		return result;
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
			logger.error("Unable to get boundary from multipart headers, encoding problem");
			boundary = boundaryStr.getBytes();
		}
		return boundary;
	}

	
	/**
	 * Read shop id from multipartStream
	 * @param multipartStream
	 * @return
	 * @throws MalformedStreamException
	 * @throws IOException
	 * @throws UnsupportedEncodingException
	 */
	private int getShopId(MultipartStream multipartStream) throws IOException {
		int shopId;
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			multipartStream.readBodyData(out);
			String shopIdStr = new String(out.toByteArray(), "UTF-8");
			shopId = Integer.valueOf(shopIdStr);
			out.close();
		} catch (Exception e) {
			logger.debug("Unable to read shop id from multipartStream: " + e.getClass().getName());
			throw new IOException();
		}
		return shopId;
	}

	
	/**
	 * Parse file extension from given header
	 * @param header
	 * @return extension as ".xxx", actually last for letters of filename are returned
	 * @throws ValidationException if unable to get extension, or this extension is not supported
	 */
	private String getExtension(String header) throws ValidationException {
		String extension;
		try{
			String fileName = parseFileName(header);
			logger.debug("Original file name = " + fileName);
			extension = fileName.substring(fileName.length() - ".xxx".length()).toLowerCase();
		} catch (Exception e) {
			logger.debug("Unable to read file extension: " + e.getClass().getName());
			throw new ValidationException("Cannot read file extension.");
		}
		return extension;
	}
	
	
	/**
	 * Get filename from headers
	 */
	private String parseFileName(String headers) throws ParsingException {
		String result = null;
		try {
			String tmp = headers.split("filename=")[1]; //get string, begining after "filename="
			result = tmp.split("\"")[1]; //get string begining after first \" and ending before next \"
		} catch (Exception e) {
			logger.debug("Unable to parse filename");
			throw new ParsingException();
		}
		return result;
	}
	
}
