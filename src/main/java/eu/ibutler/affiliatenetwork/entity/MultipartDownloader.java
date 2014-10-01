package eu.ibutler.affiliatenetwork.entity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.fileupload.MultipartStream;
import org.apache.log4j.Logger;

import eu.ibutler.affiliatenetwork.dao.exceptions.BadFileFormatException;
import eu.ibutler.affiliatenetwork.dao.exceptions.DbAccessException;
import eu.ibutler.affiliatenetwork.dao.exceptions.NoSuchEntityException;
import eu.ibutler.affiliatenetwork.dao.impl.ShopDaoImpl;
import eu.ibutler.affiliatenetwork.entity.exceptions.DownloadErrorException;
import eu.ibutler.affiliatenetwork.entity.exceptions.ParsingException;

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
		String shopName = null;
		String fileName = null;
		String filePath = null;
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
					try {
						shopName = new ShopDaoImpl().selectById(shopId).getName();
					} catch (DbAccessException e) {
						e.printStackTrace();
					} catch (NoSuchEntityException ignore) {
						//NOP
					}
					out.close();
					partNumber = 2; //next iteration will go on "else" branch
				} else {
					//download file
			        String dataType=null;
					try {
						dataType = parseContentType(header);
						fileName = parseFileName(header);
						fileName = shopName + "_" + fileName;
					} catch (ParsingException e) {
						log.debug("Headers parsing error");
						throw new DownloadErrorException();
					}
					if(!(dataType.contains(SUPPORTED_FILEFORMAT_1) || dataType.contains(SUPPORTED_FILEFORMAT_2))) {
						log.debug("Unsupported format");
						throw new BadFileFormatException();
					}
					if(!(fileName.endsWith(".zip") || fileName.endsWith(".csv"))) {
						log.debug("Unsupported format");
						throw new BadFileFormatException();
					}
		        	filePath = folderPath + "/" + fileNamePrefix + fileName;
		        	FileOutputStream fileOut = new FileOutputStream(new File(filePath));
		        	multipartStream.readBodyData(fileOut);
		        	fileOut.close();
				}
				nextPart = multipartStream.readBoundary();
			}//while
		} catch(IOException e) {
			log.debug("IO error");
			throw new DownloadErrorException();
		}	
		
		result = new UploadedFile(fileName, filePath, System.currentTimeMillis(), shopId);
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
