package eu.ibutler.affiliatenetwork.utils.csv;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
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
import eu.ibutler.affiliatenetwork.utils.AppConfig;

public class CSVProcessor {
	
	private static AppConfig cfg = AppConfig.getInstance();
	private static Logger logger = Logger.getLogger(CSVProcessor.class.getName());
	
	/**
	 * CSV file considered valid if it contains all mandatory columns
	 * @param file
	 * @return -1 if file is invalid or 0 or more number that shows total entry number
	 */
	public int isValid(UploadedFile uploadedFile) {
		int result = -1;
		List<String> mandatoryColumns = CSVUtils.getAllMandatoryColumnNames();
		try ( CSVReader reader = new CSVReader(new FileReader(uploadedFile.getFsPath())) ) {
			//Headers must be the first line
			List<String> headers = new ArrayList<String>( Arrays.asList(reader.readNext()) );
			if(headers.containsAll(mandatoryColumns)) {
				result = 0;
				while(reader.readNext() != null) {
					result++;
				}
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
				try{	
					List<Product> products = new ArrayList<Product>();
					CSVParser csvParser = new CSVParser(uploadedFile.getFsPath());
					for(CSVRecord record : csvParser.parse()) {
						if(record.isConsistent()) {
							products.add( new Product(record, uploadedFile.getDbId(), uploadedFile.getWebShopId()) );
						} else {
							logger.debug("Incosistent csv record, skipping product creation");
						}
					}
					//put products into DB
					new ProductDaoImpl().insertAll(products);
					logger.debug("Products parsed and saved to DB successfully");
					
					//create file_template for distributors
					products = new ProductDaoImpl().selectByFileId(uploadedFile.getDbId());
					List<String[]> list = new ArrayList<String[]>();
					for(Product p : products) {
						Map<String, String> query = new HashMap<>();
						query.put(Links.PRODUCT_ID_PARAM_NAME, ""+p.getDbId());
						query.put(Links.DISTRIBUTOR_ID_PARAM_NAME, "777");
						String distributorLink = Links.DOMAIN_NAME + Links.AFFILIATE_CLICK_URL + Links.createQueryString(query);
						String[] pArr = p.asStringArray();
						String[] csvLine = Arrays.copyOf(pArr, pArr.length + 1);
						csvLine[csvLine.length-1] = distributorLink;
						list.add(csvLine);
					}
					FileTemplate template = new FileTemplate(uploadedFile.getDbId(), uploadedFile.getWebShopId());
					CSVWriter csvWriter = new CSVWriter(new FileWriter(template.getFsPath()), ',', '\"');
					csvWriter.writeAll(list);
					csvWriter.close();
					
				} catch (ParsingException e) {
					logger.debug("Unable to extract Products from uploaded csv file: " + e.getClass().getName());
				} catch (DbAccessException e) {
					logger.error("Unable to save products to DB: " + e.getClass().getName());
				} catch (IOException e) {
					logger.error("Unable to create file template: " + e.getClass().getName());
				}
			}//run
			
		});//thread
		insertProducts.setName("parseCsvAndInsertProductsToDBFrom: " + uploadedFile.getName());
		insertProducts.start();
	}
	
}
