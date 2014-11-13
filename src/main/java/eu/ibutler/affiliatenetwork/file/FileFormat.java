package eu.ibutler.affiliatenetwork.file;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import eu.ibutler.affiliatenetwork.config.AppConfig;

//Will be renamed later to YandexFormat
public class FileFormat {
	
	public enum Type{
		SIMPLIFIED ("simplified"),
		VENDORMODEL ("vendor.model");
		
        private String formatName; 
        private Type(String name) { 
            this.formatName = name; 
        } 
        
        @Override 
        public String toString(){ 
            return formatName; 
        }
		
	};

	private static AppConfig cfg = AppConfig.getInstance();
	private static Logger logger = Logger.getLogger(FileFormat.class.getName());
	
	//Column names in file uploaded by shop
		//Common columns for all formats
			//Mandatory
	public static final String COLUMN_URL_PATH_M = "url";
	public static final String COLUMN_PRICE_M = "price";
	public static final String COLUMN_PRICE_CURRENCY_M = "currencyId";
	public static final String COLUMN_CATEGORY_M = "category";
			//Optional (only those, that supported by my service)
	public static final String COLUMN_PICTURE_O = "picture";	
	public static final String COLUMN_DESCRIPTION_O = "description";	
	public static final String COLUMN_BARCODE_O = "barcode";	
	public static final String COLUMN_LOCAL_DELIVERY_COST_O = "local_delivery_cost";	
	
		//Simplified format
			//Mandatory
	public static final String SIMPLIFIED_NAME_M = "name";
			//Optional
	
	
		//vendor.model format
			//Mandatory
	public static final String VENDORMODEL_TYPE_M = "type";
	public static final String VENDORMODEL_VENDOR_M = "vendor";
	public static final String VENDORMODEL_MODEL_M = "model";
			//Optional (only those, that supported by my service)

	
	
	//For file template
	public static final String AFFNET_PRODUCT_ID_COLUMN_NAME = "affnet_id";
	
	
	
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
	 * Creates list of all column names, that MUST be present in csv file list
	 * @return list of column names
	 */
	public static List<String> getFileListColumns() {
		return getFieldsByMask("FILE_LIST_", "");
	}
	
	/**
	 * Creates list of all column names, that MUST be present in uploadable csv file
	 * @return list of column names
	 */
	public static List<String> getMandatoryColumnNames(Enum type) {
		List<String> result = new ArrayList<String>(getFieldsByMask("COLUMN_", "_M"));
		if(type == Type.SIMPLIFIED) {
			result.addAll(getFieldsByMask("SIMPLIFIED_", "_M"));
		} else if (type == Type.VENDORMODEL) {
			result.addAll(getFieldsByMask("VENDORMODEL_", "_M"));
		} else {
			return null;
		}
		return result;
	}
	
	/**
	 * Creates list of all column names, that MUST be present in uploadable csv file
	 * @return list of column names
	 */
	public static List<String> getOptionalColumnNames(Enum type) {
		List<String> result = new ArrayList<String>(getFieldsByMask("COLUMN_", "_O"));
		if(type == Type.SIMPLIFIED) {
			result.addAll(getFieldsByMask("SIMPLIFIED_", "_O"));
		} else if (type == Type.VENDORMODEL) {
			result.addAll(getFieldsByMask("VENDORMODEL_", "_O"));
		} else {
			return null;
		}
		return result;
	}
	
	/**
	 * Creates list of all column names, that are to be written to
	 * distributor's file
	 * @return list of column names
	 */
	public static List<String> getDistributorsColumnNames() {
		List<String> result = new ArrayList<String>(getFieldsByMask("COLUMN_", "_M"));
		result.addAll(getFieldsByMask("SIMPLIFIED_", "_M"));
		result.addAll(getFieldsByMask("VENDORMODEL_", "_M"));
		return result;
	}
	
	
	private static List<String> getFieldsByMask (String prefix, String suffix) {
		List<String> result = new ArrayList<String>();
		try {
			Field[] allFields = FileFormat.class.getDeclaredFields();
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
