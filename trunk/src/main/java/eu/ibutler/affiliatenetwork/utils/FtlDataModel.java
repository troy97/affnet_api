package eu.ibutler.affiliatenetwork.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Class represents data model for FTL FreMarker pages
 * @author anton
 *
 */
public class FtlDataModel {
	
	private Map<String, Object> root = new HashMap<>();

	public FtlDataModel() {

	}
	
	public void put(String key, Object value) {
		root.put(key, value);
	}
	
	public Map<String, Object> getModel() {
		return root;
	}

}
