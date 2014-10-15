package eu.ibutler.affiliatenetwork.utils.csv;

import eu.ibutler.affiliatenetwork.utils.AppConfig;

public class CSVutils {

	private static AppConfig cfg = AppConfig.getInstance();
	
	//Mandatory CSV file column names
	public static final String COLUMN_URL_PATH = cfg.get("CSV_URL_PATH");
	public static final String COLUMN_NAME = cfg.get("CSV_NAME");
	public static final String COLUMN_DESCRIPTION = cfg.get("CSV_DESCRIPTION");
	
}
