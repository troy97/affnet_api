package eu.ibutler.affiliatenetwork.entity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.fileupload.MultipartStream;
import org.apache.log4j.Logger;

import eu.ibutler.affiliatenetwork.entity.exceptions.ParsingException;

/**
 * This class allows easy downloading of files sent via http multipart 
 * @author anton
 *
 */
public class HttpDownloader {
	
	private static Logger log = Logger.getLogger(HttpDownloader.class.getName());
	
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
	public String fileDownload(InputStream in, byte[] boundary, String folderPath) throws IOException {
		return fileDownload(in, boundary, folderPath, "");
	}
	
	/**
	 * Download single file
	 * @param InputStream. Note! this method doesn't close this InputStream.
	 * @param http boundary as byte array
	 * @param path to folder where the resulting file will be stored, for example "/home/userName/downloadedFiles"
	 * @param prefix which will be added to filename, if prefix is "prefix", and filename read from inputstream 
	 * is "Filename.jpg", then resulting filename will be "prefixFilename.jpg" and full path to file is
	 * "home/userName/downloadedFiles/prefixFilename.jpg"
	 * @return absolute path to file which was downloaded
	 */
	public String fileDownload(InputStream in, byte[] boundary, String folderPath, String fileNamePrefix) throws IOException {
		String pathToFile = null;
		
        //to do: add progress Notifier
        MultipartStream multipartStream = new MultipartStream(in, boundary, INPUT_BUFF_SIZE, null);

        boolean nextPart = multipartStream.skipPreamble();
        if(nextPart != true) {
        	
        }
        String header = multipartStream.readHeaders();
        System.out.println(header);
        String dataType=null;
		String fileName=null;
		try {
			dataType = parseContentType(header);
			fileName = parseFileName(header);
		} catch (ParsingException e) {
			log.info("Unsupported upload operation");
			//redirect...
		}
        System.out.println(dataType);
        if(dataType.contains(SUPPORTED_FILEFORMAT_1) || dataType.contains(SUPPORTED_FILEFORMAT_2)) {
        	pathToFile = folderPath + "/" + fileNamePrefix + fileName;
        	FileOutputStream fileOut = new FileOutputStream(new File(pathToFile));
        	multipartStream.readBodyData(fileOut);
        	fileOut.close();
        } else {
        	log.info("Upload of unsupported file format");
        	//redirect...
        }
        nextPart = multipartStream.readBoundary();
        if(nextPart == true) {
        	log.info("Some additional part in file upload process, skipped");
        }
		
		return pathToFile;
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
