package eu.ibutler.affiliatenetwork.utils.csv;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import eu.ibutler.affiliatenetwork.config.Urls;
import eu.ibutler.affiliatenetwork.controllers.StatusEndpoint;
import eu.ibutler.affiliatenetwork.controllers.utils.FsPaths;
import eu.ibutler.affiliatenetwork.controllers.utils.Links;
import eu.ibutler.affiliatenetwork.dao.exceptions.DbAccessException;
import eu.ibutler.affiliatenetwork.dao.exceptions.UniqueConstraintViolationException;
import eu.ibutler.affiliatenetwork.dao.impl.FileDaoImpl;
import eu.ibutler.affiliatenetwork.dao.impl.FileTemplateDaoImpl;
import eu.ibutler.affiliatenetwork.dao.impl.ProductDaoImpl;
import eu.ibutler.affiliatenetwork.entity.FileTemplate;
import eu.ibutler.affiliatenetwork.entity.Product;
import eu.ibutler.affiliatenetwork.entity.UploadedFile;
import eu.ibutler.affiliatenetwork.utils.csv.exceptions.CSVProcessingException;
import eu.ibutler.affiliatenetwork.utils.csv.exceptions.InconsistentRecordException;
import eu.ibutler.affiliatenetwork.utils.csv.exceptions.ParsingException;

/**
 * This class different utility methods to work with CSV files
 * @author Anton Lukashchuk
 *
 */
public class CSVProcessor {
	
	//private static AppConfig cfg = AppConfig.getInstance();
	private static Logger logger = Logger.getLogger(CSVProcessor.class.getName());
	
	/**
	 * CSV file considered valid if it contains all mandatory columns
	 * @param file
	 * @return -1 if file is invalid or there's no records after header line
	 * 	 
	 */
/*	public int isValid(UploadedFile uploadedFile) {
		int result = 0;
		List<String> mandatoryColumns = CSVUtils.getMandatoryColumnNames();
		try ( CSVReader reader = new CSVReader(new FileReader(uploadedFile.getFsPath())) ) {
			//Headers must be the first line
			List<String> headers = new ArrayList<String>( Arrays.asList(reader.readNext()) );
			if(headers.containsAll(mandatoryColumns)) {
				while(reader.readNext() != null) {
					result++;
				}
			} else {
				logger.debug("Some mandatory columns are absent, headers are: " + headers + ";"
						+ " Mandatory columns are: " + mandatoryColumns);
			}
		} catch (Exception e) {
			logger.debug("Exception while validating file: " + e.getClass().getName());
		}
		return (result > 0) ? result : (-1);
	}*/

	/**
	 * This methods starts a new thread which parses CSV file into
	 * separate records, creates Product objects and inserts them into DB.
	 *  
	 * @param uploadedFile
	 * @param csvParser
	 * @throws ParsingException
	 * @throws DbAccessException
	 */
	public void process(final UploadedFile uploadedFile) {
		Thread insertProductsThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				logger.debug("Starting product parsing thread...");
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


			private List<Product> getProducts(final UploadedFile uploadedFile) throws ParsingException, InconsistentRecordException {
				List<Product> products = null;
				try{	
					products = new ArrayList<Product>();
					CSVParser csvParser = new CSVParser(uploadedFile.getFsPath());
					int count = 0;
					for(CSVRecord record : csvParser.parse()) {
						if(record.isConsistent()) {
							products.add( new Product(record, uploadedFile.getDbId(), uploadedFile.getShopId()) );
						} else {
							logger.debug("Incosistent CSV record found, line number: " + count + " throw exception.");
							throw new InconsistentRecordException("Incosistent CSV record found, line number: " + count);
						}
						count++;
					}
				} catch (ParsingException e) {
					logger.debug("Unable to extract all products from uploaded csv file: " + e.getClass().getName());
					throw e;
				}
				return products;
			}


			//create file_template for distributors
			/*TODO: refactor this part! #####################################################################################################
			 * add line with headers //done
			 * generate entries for CSV using CSVUtils constants
			 * obey header to value order!
			*/
			private FileTemplate createFileTemplate(final UploadedFile uploadedFile) throws IOException, DbAccessException {
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

			private String makeDistributorLink(Product p) {
				Map<String, String> linkQuery = new HashMap<>();
				linkQuery.put(Links.PRODUCT_ID_PARAM_NAME, ""+p.getDbId());
				linkQuery.put(Links.DISTRIBUTOR_ID_PARAM_NAME, "1");
				String distributorLink = Urls.DOMAIN_NAME + Urls.DISTRIBUTOR_CLICK_URL + Links.createQueryString(linkQuery);
				return distributorLink;
			}
			
		});//thread
		insertProductsThread.setName("parseCsvAndInsertProductsToDBFrom: " + uploadedFile.getName());
		insertProductsThread.start();
	}
	
	
	/**
	 * Create CSV file from given data, separator is comma(','), domain separator is '"'.
	 * @param headers
	 * @param data
	 * @return
	 * @throws IOException
	 */
	public String createCSV(List<String> headers, List<String[]> data) throws IOException {
		String result = null;
		new File(FsPaths.FILE_TEMPLATES_PATH + "/FileLists").mkdir();
		result = FsPaths.FILE_TEMPLATES_PATH + "/FileLists" + "/" + System.currentTimeMillis() + ".csv";
		
    	logger.debug("Creating temporary list file...");
		CSVWriter csvWriter = new CSVWriter(new FileWriter(result), ',', '\"');
		csvWriter.writeNext(headers.toArray(new String[0]));
		csvWriter.writeAll(data);
		csvWriter.close();
		logger.debug("Temporary list file created: " + result);
		return result;
	}
	
}
