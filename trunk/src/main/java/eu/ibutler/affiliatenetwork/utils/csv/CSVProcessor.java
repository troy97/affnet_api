package eu.ibutler.affiliatenetwork.utils.csv;

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
import eu.ibutler.affiliatenetwork.controllers.Links;
import eu.ibutler.affiliatenetwork.dao.exceptions.DbAccessException;
import eu.ibutler.affiliatenetwork.dao.impl.ProductDaoImpl;
import eu.ibutler.affiliatenetwork.entity.FileTemplate;
import eu.ibutler.affiliatenetwork.entity.Product;
import eu.ibutler.affiliatenetwork.entity.UploadedFile;
import eu.ibutler.affiliatenetwork.http.ParsingException;

public class CSVProcessor {
	
	//private static AppConfig cfg = AppConfig.getInstance();
	private static Logger logger = Logger.getLogger(CSVProcessor.class.getName());
	
	/**
	 * CSV file considered valid if it contains all mandatory columns
	 * @param file
	 * @return -1 if file is invalid or 0 or more number that shows total entry number
	 */
	public int isValid(UploadedFile uploadedFile) {
		int result = -1;
		List<String> mandatoryColumns = CSVUtils.getMandatoryColumnNames();
		try ( CSVReader reader = new CSVReader(new FileReader(uploadedFile.getFsPath())) ) {
			//Headers must be the first line
			List<String> headers = new ArrayList<String>( Arrays.asList(reader.readNext()) );
			if(headers.containsAll(mandatoryColumns)) {
				result = 0;
				while(reader.readNext() != null) {
					result++;
				}
			} else {
				logger.debug("Some mandatory columns are absent");
			}
		} catch (Exception e) {
			logger.debug("Exception while validating file: " + e.getClass().getName());
		}
		return result;
	}

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
		Thread insertProducts = new Thread(new Runnable() {
			
			@Override
			public void run() {
				logger.debug("Starting product parsing thread...");
				
				//parse List of Products from given file
				List<Product> products = null;
				try{	
					products = new ArrayList<Product>();
					CSVParser csvParser = new CSVParser(uploadedFile.getFsPath());
					for(CSVRecord record : csvParser.parse()) {
						if(record.isConsistent()) {
							products.add( new Product(record, uploadedFile.getDbId(), uploadedFile.getWebShopId()) );
						} else {
							logger.debug("Incosistent CSV record, skipping product creation");
						}
					}
				} catch (ParsingException e) {
					logger.debug("Unable to extract Products from uploaded csv file: " + e.getClass().getName());
				}	
				
				//put products into DB
				try {
					new ProductDaoImpl().insertAll(products);
					logger.debug("Products parsed and saved to DB successfully");
				} catch (DbAccessException e) {
					logger.error("Unable to save products to DB: " + e.getClass().getName());
				}
				
				//create file_template for distributors
				/*TODO: refactor this part! #####################################################################################################
				 * add line with headers //done
				 * generate entries for CSV using CSVUtils constants
				 * obey header to value order!
				*/
				try {
					products = null; //allow it to be garbage collected
					products = new ProductDaoImpl().selectByFileId(uploadedFile.getDbId());
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
					FileTemplate template = new FileTemplate(uploadedFile.getDbId(), uploadedFile.getWebShopId());
					CSVWriter csvWriter = new CSVWriter(new FileWriter(template.getFsPath()), ',', '\"');
					csvWriter.writeAll(productsAsArr);
					csvWriter.close();
					logger.debug("File template created succesfully: " + template.getFsPath());
				} catch (IOException | DbAccessException e) {
					logger.error("Unable to create template file: " + e.getClass().getName());
				}
					
					//Save template to DB
					//TODO:
					
				
			}//run

			private String makeDistributorLink(Product p) {
				Map<String, String> linkQuery = new HashMap<>();
				linkQuery.put(Links.PRODUCT_ID_PARAM_NAME, ""+p.getDbId());
				linkQuery.put(Links.DISTRIBUTOR_ID_PARAM_NAME, "777");
				String distributorLink = Links.DOMAIN_NAME + Links.AFFILIATE_CLICK_URL + Links.createQueryString(linkQuery);
				return distributorLink;
			}
			
		});//thread
		insertProducts.setName("parseCsvAndInsertProductsToDBFrom: " + uploadedFile.getName());
		insertProducts.start();
	}
	
}
