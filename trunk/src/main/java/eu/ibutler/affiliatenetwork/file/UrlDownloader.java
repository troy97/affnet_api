package eu.ibutler.affiliatenetwork.file;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Random;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

import com.google.common.base.Throwables;

import eu.ibutler.affiliatenetwork.ValidationException;
import eu.ibutler.affiliatenetwork.config.Config;
import eu.ibutler.affiliatenetwork.config.FsPaths;
import eu.ibutler.affiliatenetwork.entity.ShopSource;
import eu.ibutler.affiliatenetwork.entity.UploadedFile;

/**
 * This class implements downloading of price-list file by given URL. 
 * Initial validation is performed when download starts.
 * @author Anton Lukashchuk
 *
 */
public class UrlDownloader {

	private static Logger logger = Logger.getLogger(UrlDownloader.class.getName());
	
	private static final int INPUT_BUFF_SIZE = 4096;
	
	/**
	 * Downloads data from the provided URL into a given location
	 * (location must contain a filename)
	 * @param url URL to download from
	 * @param downloadLocation full path with filename to download to
	 * @return
	 * @throws IOException 
	 */
	public UploadedFile download(ShopSource source) throws IOException, ValidationException {
		UploadedFile result = null;
		
		String url = source.getDownload_url();
		String extension = source.getFile_format();
		int shopId = source.getShop_id();
		
		
		URL fileUrl;
		try {
			fileUrl = new URL(url);
		} catch (MalformedURLException e) {
			logger.error( "Bad URL: " + url + ". Exception: " + e );
			return null;
		}
		
		AbstractFileProcessor validator = FileProcessingUtils.getProcessor(extension);
		if(validator == null) {
			String msg = "Unsupported file extension: " + extension;
			logger.debug(msg);
			throw new ValidationException(msg);
		}
		
		URLConnection conn = fileUrl.openConnection();
		
		//Set authorization header if necessary
		String credentials = getHttpAuthString(source);
		if(credentials != null) {
			conn.setRequestProperty("Authorization", "Basic " + credentials);
		}
		
		InputStream in = conn.getInputStream();
		byte[] firstLine = validator.validateDownload(in);
		
		String folderPath = FsPaths.UPLOAD_FOLDER;
		
		String tmpFilePath = saveToFile(folderPath, shopId, in, firstLine);
		result = new UploadedFile(tmpFilePath, extension, System.currentTimeMillis(), shopId);
		
		return result;
	}
	
	
	private String saveToFile(String folderPath, int shopId, InputStream in, byte[] firstLine)
			throws IOException {
		String tmpFilePath;
		try {
			//create unique temporary file name, later this file will be renamed to appropriate name format
			String tmpFileName = shopId + "_" + System.currentTimeMillis() + "_" + new Random().nextInt(512);
			tmpFilePath = folderPath + "/" + tmpFileName;
			File tmpFile = new File(tmpFilePath);
			
			FileOutputStream fileOut = new FileOutputStream(tmpFile);
			fileOut.write(firstLine);
			
			BufferedInputStream bis = new BufferedInputStream(in);
	        byte[] buffer = new byte[INPUT_BUFF_SIZE];
	        int count=0;
	        while((count = bis.read(buffer,0,1024)) != -1)
	        {
	            fileOut.write(buffer, 0, count);
	        }
			
			fileOut.close();
			bis.close();
			logger.debug("Temporary file created: " + tmpFilePath);
		} catch (Exception e) {
			logger.debug("Unable to save data to file: " + e);
			throw new IOException();
		}
		return tmpFilePath;
	}
	
	/**
	 * Returns Base64 encoded string ready to insert into header
	 * or null if no credentials were provided by shop
	 * @param shopId
	 * @return
	 */
	private String getHttpAuthString(ShopSource s) {
		String result = null;
		try {
			if(!s.isBasic_http_auth_required()) {
				return null;
			}
			String credentials = s.getBasic_http_auth_username() + ":" + s.getBasic_http_auth_password();
			result = Base64.encodeBase64String(credentials.getBytes(Config.ENCODING));
		} catch (Exception e) {
			logger.debug("Unable to extract credentials." + Throwables.getStackTraceAsString(e));
		}
		return result;
	}
	
}
