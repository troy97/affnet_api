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
	
	//Mandatory CSV file column names
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
	
	
	/**
	 * Creates list of all column names, that MUST be present in csv file
	 * @return list of column names
	 */
	public static List<String> getAllMandatoryColumnNames() {
		List<String> result = new ArrayList<String>();
		try {
			Field[] allFields = CSVUtils.class.getDeclaredFields();
			for(Field field : allFields) {
				if(field.getName().contains("COLUMN_")) {
					result.add((String) field.get(null));
				}
			}
		} catch (Exception e) {
			logger.error("Unable to get list of Mandatory fields: " + e.getClass().getName());
		} 
		return result;
	}
	
}
