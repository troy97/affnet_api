package eu.ibutler.affiliatenetwork.file;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import eu.ibutler.affiliatenetwork.ProcessingException;
import eu.ibutler.affiliatenetwork.ValidationException;
import eu.ibutler.affiliatenetwork.config.Urls;
import eu.ibutler.affiliatenetwork.controllers.Links;
import eu.ibutler.affiliatenetwork.entity.Product;
import eu.ibutler.affiliatenetwork.entity.UploadedFile;

/**
 * This abstract class must be extended by classes that provide file validation and processing.
 * Each implementation of this interface MUST be marked with @FileExtension annotation.
 * @author Anton Lukashchuk
 *
 */
public abstract class AbstractFileProcessor {
	
	protected Logger logger = Logger.getLogger(this.getClass().getName());
	
	/**
	 * Validates file during download, usually file can be validated on it's small piece (first line for example).
	 * This methods reads this first piece (particular piece depends on implementation) and checks it according to 
	 * requirements for concrete type of file.
	 * @param stream to read data from
	 * @return byte[] first piece read from stream, usually this piece is written to file before downloading remaining data.
	 * @throws ValidationException 
	 */
	public abstract byte[] validateDownload(InputStream stream) throws ValidationException;
	
	/**
	 * Checks given file for validity according to requirements as of (https://partner.market.yandex.ru/legal/tt/#id1168965542227)
	 * and updates corresponding fields with validation message and validation flag.
	 * @param file
	 * @return UploadedFile with filled validation info 
	 * @throws ValidationException if file is invalid or validation process failed to complete.
	 */
	public abstract UploadedFile validateFile(UploadedFile file);
	
	/**
	 * Parses given file (file has to pass validation before) for products, stores products to DB and creates 
	 * Template file for distributor.
	 * @param file
	 * @throws ProcessingException
	 */
	public abstract void processFile(UploadedFile file) throws ProcessingException;
	
	protected final void validateProduct(Product p) throws ValidationException {
		if(p.getFileId() <= 0 ||
		   p.getShopId() <= 0 ||
		   p.getRealUrl() == null || p.getRealUrl().isEmpty() ||
		   p.getName() == null || p.getName().isEmpty() ||
		   p.getPrice()<0 ||
		   p.getCurrencyCode() == null || p.getCurrencyCode().isEmpty() || p.getCurrencyCode().length()>3 ||
		   p.getCategory() == null || p.getCategory().isEmpty()) {
			
		   throw new ValidationException("Invalid product field.");
		}//if
	}
}
