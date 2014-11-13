package eu.ibutler.affiliatenetwork.file;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import eu.ibutler.affiliatenetwork.config.AppConfig;

public class OutputFormat {

	private static AppConfig cfg = AppConfig.getInstance();
	private static Logger logger = Logger.getLogger(OutputFormat.class.getName());
	
	//Column names in output file
		//Mandatory
	public static final String COLUMN_PRODUCT_ID_M = "affnet_id";
	
	public static final String COLUMN_AFFNET_URL_M = "affnet_url";
	public static final String COLUMN_NAME_M = "name";
	public static final String COLUMN_PRICE_M = "price";
	public static final String COLUMN_CURRENCY_CODE_M = "currency_code";
	public static final String COLUMN_CATEGORY_M = "category";
		//Optional
	public static final String COLUMN_IMAGE_URL_O = "image_url";
	public static final String COLUMN_DESCRIPTION_O = "description";
	public static final String COLUMN_DESCRIPTION_SHORT_O = "description_short";
	public static final String COLUMN_EAN_O = "ean";
	public static final String COLUMN_SHIPPING_PRICE_O = "shipping_price";
	
	
	
	//Column names, that are to be present in new files list for distributors
	public static final String FILE_LIST_ID = "file_id";
	public static final String FILE_LIST_CREATED_AT = "created_at";
	public static final String FILE_LIST_PRODUCTS_COUNT = "products_count";
	public static final String FILE_LIST_FILE_SIZE = "file_size_bytes";
	public static final String FILE_LIST_COMPRESSED_FILE_SIZE = "compressed_file_size_bytes";
	public static final String FILE_LIST_SHOP_NAME = "shop_name";
	public static final String FILE_LIST_SHOP_id = "shop_id";
	public static final String FILE_LIST_DOWNLOAD_URL = "download_url";
	
	
	/**
	 * Creates header for output csv file
	 * @return list of column names
	 */
	public static List<String> getHeader() {
		return new ArrayList<String>(getFieldsByMask("COLUMN_", ""));
	}
	
	/**
	 * Creates list of all column names, that MUST be present in output csv file
	 * @return list of column names
	 */
	public static List<String> getMandatoryColumnNames() {
		return new ArrayList<String>(getFieldsByMask("COLUMN_", "_M"));
	}
	
	/**
	 * Creates list of optional column names, in output csv file
	 * @return list of column names
	 */
	public static List<String> getOptionalColumnNames() {
		return new ArrayList<String>(getFieldsByMask("COLUMN_", "_O"));
	}

	/**
	 * Creates list of all column names, that MUST be present in output csv file list
	 * @return list of column names
	 */
	public static List<String> getFileListColumns() {
		return getFieldsByMask("FILE_LIST_", "");
	}
	
	
	private static List<String> getFieldsByMask (String prefix, String suffix) {
		List<String> result = new ArrayList<String>();
		try {
			Field[] allFields = OutputFormat.class.getDeclaredFields();
			for(Field field : allFields) {
				if(field.getName().startsWith(prefix) && field.getName().endsWith(suffix)) {
					result.add((String) field.get(null));
				}
			}
		} catch (Exception e) {
			logger.error("Unable to get list of fields: " + e);
		} 
		return result;
	}
	
}
