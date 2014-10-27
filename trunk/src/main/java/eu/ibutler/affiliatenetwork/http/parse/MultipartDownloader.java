package eu.ibutler.affiliatenetwork.http.parse;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.commons.fileupload.MultipartStream;
import org.apache.commons.fileupload.MultipartStream.MalformedStreamException;
import org.apache.log4j.Logger;

import eu.ibutler.affiliatenetwork.entity.UploadedFile;
import eu.ibutler.affiliatenetwork.http.parse.exceptions.BadFileFormatException;
import eu.ibutler.affiliatenetwork.http.parse.exceptions.DownloadErrorException;
import eu.ibutler.affiliatenetwork.http.parse.exceptions.FileValidationException;
import eu.ibutler.affiliatenetwork.http.parse.exceptions.ParsingException;
import eu.ibutler.affiliatenetwork.utils.csv.CSVUtils;

/**
 * This class implements downloading of price-list file sent via http multipart 
 * @author anton
 *
 */
public class MultipartDownloader {
	
	private static Logger log = Logger.getLogger(MultipartDownloader.class.getName());
	
	private static final int INPUT_BUFF_SIZE = 4096;
	private static final Set<String> SUPPORTED_FILE_EXTENSIONS = new HashSet<>(Arrays.asList(new String[]{".csv", ".zip"}));
	
	/**
	 * Download single file
	 * @param InputStream. Note! this method doesn't close this InputStream.
	 * @param http boundary as byte array
	 * @param path to folder where the resulting file will be stored, for example "/home/userName/downloadedFiles"
	 * @return UploadedFile object
	 * @throws DownloadErrorException
	 * @throws BadFileFormatException
	 */
	public UploadedFile download(InputStream in, byte[] boundary, String folderPath) throws DownloadErrorException, BadFileFormatException, FileValidationException {
		log.debug("Starting multipart download.");
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
					shopId = getShopId(multipartStream);
					partNumber = 2; //next iteration will go on "else" branch
				} else {
					//download file	
					extension = checkFileFormat(header);
					byte[] firstLine = getFirstLine(multipartStream);
					validateFileHeader(firstLine);
					
					log.debug("File looks good, continue download...");
					//create unique temporary file name, later this file will be renamed to appropriate name format
					String tmpFileName = shopId + "_" + System.currentTimeMillis() + "_" + new Random().nextInt(512);
		        	tmpFilePath = folderPath + "/" + tmpFileName;
		        	File tmpFile = new File(tmpFilePath);
		        	FileOutputStream fileOut = new FileOutputStream(tmpFile);
		        	fileOut.write(firstLine);
		        	multipartStream.readBodyData(fileOut);
		        	fileOut.close();
		        	log.debug("Temporary file created: " + tmpFilePath);
				}
				nextPart = multipartStream.readBoundary();
			}//while
		} catch(IOException | ParsingException e) {
			log.debug("Error downloading file: " + e.getClass().getName());
			throw new DownloadErrorException();
		}	
		
		long uploadTime = System.currentTimeMillis();
		result = new UploadedFile(tmpFilePath, extension, uploadTime, shopId);
		log.debug("File downloaded successfully. Return.");
		return result;
	}

	
	
	
	/**
	 * Check first line of file for presence of correct CSV header
	 * @param firstLine
	 * @throws IOException
	 * @throws FileValidationException
	 */
	private void validateFileHeader(byte[] firstLine) throws IOException,
			FileValidationException {
		List<String> csvHeaders = CSVUtils.parseLine(firstLine, "UTF-8");
		if( !csvHeaders.containsAll(CSVUtils.getMandatoryColumnNames()) ) {
			log.debug("File is missing some mandatory data."
					+ " Header line is: " + csvHeaders + "; Mandatory columns are: " + CSVUtils.getMandatoryColumnNames() +
					". Reject file, throw exception...");
			throw new FileValidationException();
		}
	}

	
	/**
	 * Read shop id from multipartStream
	 * @param multipartStream
	 * @return
	 * @throws MalformedStreamException
	 * @throws IOException
	 * @throws UnsupportedEncodingException
	 */
	private int getShopId(MultipartStream multipartStream)
			throws MalformedStreamException, IOException,
			UnsupportedEncodingException {
		int shopId;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		multipartStream.readBodyData(out);
		String shopIdStr = new String(out.toByteArray(), "UTF-8");
		shopId = Integer.valueOf(shopIdStr);
		out.close();
		return shopId;
	}
	
	/**
	 * Get byte array from MultipartStream until first occurrence of 'LF' byte
	 * @return byte[] ending with 'LF'
	 * @throws IOException if there's no more data in given multipart straem
	 */
	private byte[] getFirstLine(MultipartStream mps) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte b = 0;
		while( b != MultipartStream.LF ) {
			b=mps.readByte();
			out.write(b);
		}
		return out.toByteArray();
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
		String fileName = parseFileName(header);
		log.debug("Original file name = " + fileName);
		String extension = fileName.substring(fileName.length() - ".xxx".length());
		if(SUPPORTED_FILE_EXTENSIONS.contains(extension.toLowerCase())) {
			result = extension.toLowerCase();
		} else {
			log.debug("Unsupported file extension: \"" + extension + "\" throw exception...");
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
