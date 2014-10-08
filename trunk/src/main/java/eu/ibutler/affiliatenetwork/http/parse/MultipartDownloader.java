package eu.ibutler.affiliatenetwork.http.parse;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import org.apache.commons.fileupload.MultipartStream;
import org.apache.log4j.Logger;

import eu.ibutler.affiliatenetwork.dao.exceptions.BadFileFormatException;
import eu.ibutler.affiliatenetwork.entity.UploadedFile;
import eu.ibutler.affiliatenetwork.http.DownloadErrorException;
import eu.ibutler.affiliatenetwork.http.ParsingException;

/**
 * This class implements downloading of price-list file sent via http multipart 
 * @author anton
 *
 */
public class MultipartDownloader {
	
	private static Logger log = Logger.getLogger(MultipartDownloader.class.getName());
	
	private static final int INPUT_BUFF_SIZE = 4096;
	private static final String SUPPORTED_FILEFORMAT_1 = "text/csv";
	private static final String SUPPORTED_FILEFORMAT_2 = "application/zip";
	
	/**
	 * Download single file
	 * @param InputStream. Note! this method doesn't close this InputStream.
	 * @param http boundary as byte array
	 * @param path to folder where the resulting file will be stored, for example "/home/userName/downloadedFiles"
	 * @return absolute path to file which was downloaded
	 */
	public UploadedFile download(InputStream in, byte[] boundary, String folderPath) throws DownloadErrorException, BadFileFormatException {
		return download(in, boundary, folderPath, "");
	}
	
	/**
	 * Download single file
	 * @param InputStream. Note! this method doesn't close this InputStream.
	 * @param http boundary as byte array
	 * @param path to folder where the resulting file will be stored, for example "/home/userName/downloadedFiles"
	 * @param prefix which will be added to filename, if prefix is "prefix", and filename read from inputstream 
	 * is "Filename.jpg", then resulting filename will be "prefixFilename.jpg" and full path to file is
	 * "home/userName/downloadedFiles/prefixFilename.jpg"
	 * @return UploadedFile object
	 * @throws DownloadErrorException
	 * @throws BadFileFormatException
	 */
	public UploadedFile download(InputStream in, byte[] boundary, String folderPath, String fileNamePrefix) throws DownloadErrorException, BadFileFormatException {
		UploadedFile result = null;
		
		int shopId = 0;
		String extension = null;
		String tmpFilePath = null;
		try {
			MultipartStream multipartStream = new MultipartStream(in, boundary, INPUT_BUFF_SIZE, null);
			boolean nextPart = multipartStream.skipPreamble();
			int partNumber = 1;
			while(nextPart) {
				String header = multipartStream.readHeaders();
				if(partNumber == 1) {
					//get shopId
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					multipartStream.readBodyData(out);
					String shopIdStr = new String(out.toByteArray(), "UTF-8");
					shopId = Integer.valueOf(shopIdStr);
					out.close();
					partNumber = 2; //next iteration will go on "else" branch
				//download ile	
				} else {
					extension = checkFileFormat(header);
					//create unique temporary file name, later this file will be renamed to appropriate name format
					String tmpFileName = shopId + "_" + System.currentTimeMillis() + "_" + new Random().nextInt(512);
		        	tmpFilePath = folderPath + "/" + tmpFileName;
		        	log.debug("Creating temporary file: " + tmpFilePath);
		        	File tmpFile = new File(tmpFilePath);
		        	FileOutputStream fileOut = new FileOutputStream(tmpFile);
		        	multipartStream.readBodyData(fileOut);
		        	fileOut.close();
				}
				nextPart = multipartStream.readBoundary();
			}//while
		} catch(IOException | ParsingException e) {
			log.debug("Error downloading file: " + e.getClass().getName());
			throw new DownloadErrorException();
		}	
		
		long uploadTime = System.currentTimeMillis();
		result = new UploadedFile(tmpFilePath, extension, uploadTime, shopId);
		return result;
	}

	/**
	 * 
	 * @param tmpFileName
	 * @param header
	 * @throws BadFileFormatException
	 * @throws DownloadErrorException
	 */
	private String checkFileFormat(String header) throws BadFileFormatException, ParsingException {
		String result = null;
		String dataType = parseContentType(header);
		String origFileName = parseFileName(header);
		
		if(!(dataType.contains(SUPPORTED_FILEFORMAT_1) || dataType.contains(SUPPORTED_FILEFORMAT_2))) {
			log.debug("Unsupported format");
			throw new BadFileFormatException();
		}
		
		if(origFileName.endsWith(".zip")) {
			result = ".zip";
		} else if (origFileName.endsWith(".csv")) {
			result = ".csv";
		} else {
			log.debug("Unsupported format");
			throw new BadFileFormatException();
		}
		
		return result;
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
			log.debug("Unable to parse filename");
			throw new ParsingException();
		}
		return result;
	}
	
	/**
	 * Get content type from headers
	 */
	private String parseContentType(String headers) throws ParsingException {
		String result = "noname";
		try {
			String tmp = headers.split("Content-Type: ")[1];
			result = tmp.split("\r")[0];
		} catch (Exception e) {
			log.debug("Unable to parse ContentType");
			throw new ParsingException();
		}
		return result;
	}

}
