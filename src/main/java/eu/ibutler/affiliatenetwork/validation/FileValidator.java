package eu.ibutler.affiliatenetwork.validation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.fileupload.MultipartStream;
import org.apache.log4j.Logger;

import au.com.bytecode.opencsv.CSVWriter;
import eu.ibutler.affiliatenetwork.config.Urls;
import eu.ibutler.affiliatenetwork.controllers.StatusEndpoint;
import eu.ibutler.affiliatenetwork.controllers.utils.Links;
import eu.ibutler.affiliatenetwork.dao.exceptions.DbAccessException;
import eu.ibutler.affiliatenetwork.dao.exceptions.UniqueConstraintViolationException;
import eu.ibutler.affiliatenetwork.dao.impl.FileDaoImpl;
import eu.ibutler.affiliatenetwork.dao.impl.FileTemplateDaoImpl;
import eu.ibutler.affiliatenetwork.dao.impl.ProductDaoImpl;
import eu.ibutler.affiliatenetwork.entity.FileTemplate;
import eu.ibutler.affiliatenetwork.entity.Product;
import eu.ibutler.affiliatenetwork.entity.UploadedFile;
import eu.ibutler.affiliatenetwork.utils.csv.CSVUtils;
import eu.ibutler.affiliatenetwork.utils.csv.exceptions.CSVProcessingException;
import eu.ibutler.affiliatenetwork.utils.csv.exceptions.InconsistentRecordException;
import eu.ibutler.affiliatenetwork.utils.csv.exceptions.ParsingException;

/**
 * This abstract class must be extended by classes that provide file validation and processing.
 * Each implementation of this interface MUST be marked with @FileExtension annotation.
 * @author Anton Lukashchuk
 *
 */
public abstract class FileValidator {
	
	protected Logger logger = Logger.getLogger(this.getClass().getName());
	
	/**
	 * Validates file during download, usually file can be validated on it's small piece (first line for example).
	 * This methods reads this first piece (particular piece depends on implementation) and checks it according to 
	 * requirements for concrete type of file.
	 * @param stream to read data from
	 * @return byte[] first piece read from stream, usually this piece is written to file before downloading remaining data.
	 * @throws ValidationException 
	 */
	public abstract byte[] validateDownload(MultipartStream stream) throws ValidationException;
	
	/**
	 * Fat method, that starts new thread and:
	 * 1) Parses given uploaded file for Products
	 * 2) Validates given file
	 * 3) Stores parsed products into DB
	 * 4) Creates file template
	 * @param file
	 * @return List<Products> found in given file
	 * @throws ValidationException if file contents is invalid (validity criteria is implementation dependent)
	 */
	public void validateFile(final UploadedFile uploadedFile) {
		Thread thread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				logger.debug("Starting file validation thread...");
				String validationMsg = "Validation OK";
				boolean isValid = true;
				
				//parse List of Products from given file
				List<Product> products = null;
				try {
					products = getProducts(uploadedFile);
				} catch (CSVProcessingException e) {
					isValid = false;
					validationMsg = e.getMessage();
					if(validationMsg == null) {
						validationMsg="Parsing error";
					}
				}
				
				uploadedFile.setValid(isValid);
				
				if(isValid) {
					uploadedFile.setProductsCount(products.size());
					try {
						//put products into DB
						try {
							new ProductDaoImpl().insertAll(products);
							logger.debug("Products parsed and saved to DB successfully");
						} catch (DbAccessException e) {
							logger.debug("Unable to save products to DB: " + e.getClass().getName());
							throw e;
						}

						//create template file for distributors
						FileTemplate file;
						try {
							file = createFileTemplate(uploadedFile);
						} catch (DbAccessException | IOException e) {
							logger.debug("Error creating template file");
							throw e;
						}

						//save template file to DB
						try {
							int id = new FileTemplateDaoImpl().insertOne(file);
							file.setId(id);
						} catch (DbAccessException | UniqueConstraintViolationException e) {
							logger.debug("Error saving template file entry to DB");
							throw e;
						}
						
						//All went OK
						uploadedFile.setActive(true);
					} catch (Exception e) {
						uploadedFile.setActive(false);
						validationMsg+=" Unable to activate due to processing problem.";
						StatusEndpoint.incrementWarnings();
						logger.warn("Error while processing valid file, leavimg it inactive");
					}
				} else {
					uploadedFile.setActive(false);
				}
				
				uploadedFile.setValidationMessage(validationMsg);
				
				try {
					new FileDaoImpl().update(uploadedFile);
				} catch (DbAccessException e) {
					logger.error("Unable update DB file entry after processing" + e.getClass().getName());
				}
			}//run
		});//thread
		thread.setName("CsvFileValidationThreadFor_" + uploadedFile.getName());
		thread.start();	
	}
	
	/**
	 * Template method used inside validateFile(). Parses products from given file,
	 * implementation depends on type of file
	 * @param uploadedFile
	 * @return
	 * @throws ParsingException
	 * @throws InconsistentRecordException
	 */
	protected abstract List<Product> getProducts(UploadedFile uploadedFile) throws ParsingException, InconsistentRecordException;
	
	//create file_template for distributors
	/*TODO: refactor this part! #####################################################################################################
	 * generate entries for CSV using CSVUtils constants
	 * obey header to value order!
	*/
	private FileTemplate createFileTemplate(UploadedFile uploadedFile) throws IOException, DbAccessException {
		FileTemplate result = null;
		try {
			List<Product> products = new ProductDaoImpl().selectByFileId(uploadedFile.getDbId());
			List<String[]> productsAsArr = new ArrayList<String[]>();
			productsAsArr.add(CSVUtils.getDistributorsColumnNames().toArray(new String[0]));
			for(Product product : products) {
				String distributorLink = makeDistributorLink(product);
				String[] p = product.asStringArray();
				String[] csvLine = Arrays.copyOf(p, p.length + 1);
				//add distributor link as the last column in file
				csvLine[csvLine.length-1] = distributorLink;
				productsAsArr.add(csvLine);
			}
			result = new FileTemplate(uploadedFile.getDbId(), uploadedFile.getShopId());
			CSVWriter csvWriter = new CSVWriter(new FileWriter(result.getFsPath()), ',', '\"');
			csvWriter.writeAll(productsAsArr);
			csvWriter.close();
			result.setProductsCount(productsAsArr.size()-1);
			result.setActive(true);
			result.setSize(new File(result.getFsPath()).length());
			logger.debug("File template created succesfully: " + result.getFsPath());
		} catch (IOException | DbAccessException e) {
			logger.debug("Unable to create template file: " + e.getClass().getName());
			throw e;
		}
		return result;
	}

	/**
	 * 
	 * @param p
	 * @return String containing distributor link to a particular product in AffNet DB
	 */
	private String makeDistributorLink(Product p) {
		Map<String, String> linkQuery = new HashMap<>();
		linkQuery.put(Links.PRODUCT_ID_PARAM_NAME, ""+p.getDbId());
		linkQuery.put(Links.DISTRIBUTOR_ID_PARAM_NAME, "1");
		String distributorLink = Urls.DOMAIN_NAME + Urls.DISTRIBUTOR_CLICK_URL + Links.createQueryString(linkQuery);
		return distributorLink;
	}
}
