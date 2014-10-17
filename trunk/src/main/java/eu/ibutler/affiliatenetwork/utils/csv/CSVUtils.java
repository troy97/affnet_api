package eu.ibutler.affiliatenetwork.utils.csv;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import eu.ibutler.affiliatenetwork.dao.impl.ProductDaoImpl;
import eu.ibutler.affiliatenetwork.utils.AppConfig;

public class CSVUtils {

	private static AppConfig cfg = AppConfig.getInstance();
	private static Logger logger = Logger.getLogger(CSVUtils.class.getName());
	
	//Mandatory column names in file uploaded by shop
	public static final String COLUMN_URL_PATH = cfg.get("CSV_URL_PATH");
	public static final String COLUMN_NAME = cfg.get("CSV_NAME");
	public static final String COLUMN_DESCRIPTION = cfg.get("CSV_DESCRIPTION");
	public static final String COLUMN_SHORT_DESCRIPTION = cfg.get("CSV_SHORT_DESCRIPTION");
	public static final String COLUMN_IMAGE_URL = cfg.get("CSV_IMAGE_URL");
	public static final String COLUMN_PRICE = cfg.get("CSV_PRICE");
	public static final String COLUMN_PRICE_CURRENCY = cfg.get("CSV_PRICE_CURRENCY");
	public static final String COLUMN_WEIGHT = cfg.get("CSV_WEIGHT");
	public static final String COLUMN_SHIPPING_PRICE = cfg.get("CSV_SHIPPING_PRICE");
	public static final String COLUMN_CATEGORY = cfg.get("CSV_CATEGORY");
	public static final String COLUMN_EAN = cfg.get("CSV_EAN");
	
	//Column names, that are to be present in file for distributors
	public static final String DISTRIBUTOR_COLUMN_PRODUCT_ID = cfg.get("CSV_PRODUCT_ID");
	public static final String DISTRIBUTOR_COLUMN_NAME = COLUMN_NAME;
	public static final String DISTRIBUTOR_COLUMN_DESCRIPTION = COLUMN_DESCRIPTION;
	public static final String DISTRIBUTOR_COLUMN_SHORT_DESCRIPTION = COLUMN_SHORT_DESCRIPTION;
	public static final String DISTRIBUTOR_COLUMN_IMAGE_URL = COLUMN_IMAGE_URL;
	public static final String DISTRIBUTOR_COLUMN_PRICE = COLUMN_PRICE;
	public static final String DISTRIBUTOR_COLUMN_PRICE_CURRENCY = COLUMN_PRICE_CURRENCY;
	public static final String DISTRIBUTOR_COLUMN_WEIGHT = COLUMN_WEIGHT;
	public static final String DISTRIBUTOR_COLUMN_SHIPPING_PRICE = COLUMN_SHIPPING_PRICE;
	public static final String DISTRIBUTOR_COLUMN_CATEGORY = COLUMN_CATEGORY;
	public static final String DISTRIBUTOR_COLUMN_EAN = COLUMN_EAN;	
	public static final String DISTRIBUTOR_COLUMN_AFFNET_URL_PATH = cfg.get("CSV_AFFNET_URL_PATH");
	
	
	/**
	 * Creates list of all column names, that MUST be present in csv file
	 * @return list of column names
	 */
	public static List<String> getMandatoryColumnNames() {
		return getFieldsStartingWith("COLUMN_");
	}
	
	/**
	 * Creates list of all column names, that are to be written to
	 * distributor's file
	 * @return list of column names
	 */
	public static List<String> getDistributorsColumnNames() {
		return getFieldsStartingWith("DISTRIBUTOR_COLUMN_");
	}
	
	
	private static List<String> getFieldsStartingWith (String prefix) {
		List<String> result = new ArrayList<String>();
		try {
			Field[] allFields = CSVUtils.class.getDeclaredFields();
			for(Field field : allFields) {
				if(field.getName().startsWith(prefix)) {
					result.add((String) field.get(null));
				}
			}
		} catch (Exception e) {
			logger.error("Unable to get list of fields: " + e.getClass().getName());
		} 
		return result;
	}
	
}
