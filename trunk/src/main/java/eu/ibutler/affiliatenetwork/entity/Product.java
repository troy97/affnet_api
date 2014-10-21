package eu.ibutler.affiliatenetwork.entity;

import java.util.LinkedHashMap;
import java.util.Map;

import eu.ibutler.affiliatenetwork.utils.csv.CSVRecord;
import eu.ibutler.affiliatenetwork.utils.csv.CSVUtils;

public class Product {
	private long id = 0;
	
	private String urlPath = null;
	private String name = null;
	private String description = null;
	private String shortDescription = null;
	private String imageUrl = null;
	private double price = 0.0;
	private String priceCurrency  = null;
	private int    weight  = 0; //Grams
	private double shippingPrice  = 0.0;
	private String category  = null;
	private String ean  = null;
	
	
	private int fileId = 0;
	private int shopId = 0;
	
	/**
	 * Public constructor
	 * @param CSVRecord
	 * @param fileId
	 */
	public Product(CSVRecord record, int fileId, int shopId) {
		this.urlPath = record.get(CSVUtils.COLUMN_URL_PATH);
		this.name = record.get(CSVUtils.COLUMN_NAME);
		this.description = record.get(CSVUtils.COLUMN_DESCRIPTION);
		this.shortDescription = record.get(CSVUtils.COLUMN_SHORT_DESCRIPTION);
		this.imageUrl = record.get(CSVUtils.COLUMN_IMAGE_URL);
		this.price = Double.valueOf(record.get(CSVUtils.COLUMN_PRICE));
		this.priceCurrency = record.get(CSVUtils.COLUMN_PRICE_CURRENCY);
		this.weight = Integer.valueOf(record.get(CSVUtils.COLUMN_WEIGHT));
		this.shippingPrice = Double.valueOf(record.get(CSVUtils.COLUMN_SHIPPING_PRICE));
		this.category = record.get(CSVUtils.COLUMN_CATEGORY);
		this.ean = record.get(CSVUtils.COLUMN_EAN);
		
		this.fileId = fileId;
		this.shopId = shopId;
		//this.record = record;
	}
	
	/**
	 * This constructor is only used by DAO
	 * @param fields
	 */
	public Product(long id, String urlPath, String name, String description,
			String shortDescription, String imageUrl, double price,
			String priceCurrency, int weight, double shippingPrice,
			String category, String ean, int fileId, int shopId) {
		this.id = id;
		
		this.urlPath = urlPath;
		this.name = name;
		this.description = description;
		this.shortDescription = shortDescription;
		this.imageUrl = imageUrl;
		this.price = price;
		this.priceCurrency = priceCurrency;
		this.weight = weight;
		this.shippingPrice = shippingPrice;
		this.category = category;
		this.ean = ean;
		
		this.fileId = fileId;
		this.shopId = shopId;
	}
	
	public String[] asStringArray() {
		String[] result = new String[11];
		result[0] = ""+this.id;
		result[1] = this.name;
		result[2] = this.description;
		result[3] = this.shortDescription;
		result[4] = this.imageUrl;
		result[5] = ""+this.price;
		result[6] = this.priceCurrency;
		result[7] = ""+this.weight;
		result[8] = ""+this.shippingPrice;
		result[9] = this.category;
		result[10] = this.ean;
		return result;
	}
	
	@Deprecated
	public Map<String, String> fieldsAsMap() {
		Map<String, String> result = new LinkedHashMap<String, String>();
		result.put(CSVUtils.COLUMN_NAME, this.name);
		result.put(CSVUtils.COLUMN_DESCRIPTION, this.description);
		result.put(CSVUtils.COLUMN_SHORT_DESCRIPTION, this.shortDescription);
		result.put(CSVUtils.COLUMN_IMAGE_URL, this.imageUrl);
		result.put(CSVUtils.COLUMN_PRICE, ""+this.price);
		result.put(CSVUtils.COLUMN_PRICE_CURRENCY,this.priceCurrency);
		result.put(CSVUtils.COLUMN_WEIGHT, ""+this.weight);
		result.put(CSVUtils.COLUMN_SHIPPING_PRICE, ""+this.shippingPrice);
		result.put(CSVUtils.COLUMN_CATEGORY, this.category);
		result.put(CSVUtils.COLUMN_EAN, this.ean);
		return result;
	}

	public long getDbId() {
		return id;
	}

	public void setDbId(long dbId) {
		this.id = dbId;
	}

	public String getUrlPath() {
		return urlPath;
	}

	public void setUrlPath(String urlPath) {
		this.urlPath = urlPath;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getShortDescription() {
		return shortDescription;
	}

	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public String getPriceCurrency() {
		return priceCurrency;
	}

	public void setPriceCurrency(String priceCurrency) {
		this.priceCurrency = priceCurrency;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public double getShippingPrice() {
		return shippingPrice;
	}

	public void setShippingPrice(double shippingPrice) {
		this.shippingPrice = shippingPrice;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getEan() {
		return ean;
	}

	public void setEan(String ean) {
		this.ean = ean;
	}

	public int getFileDbId() {
		return fileId;
	}

	public void setFileDbId(int fileDbId) {
		this.fileId = fileDbId;
	}

	public int getShopDbId() {
		return shopId;
	}

	public void setWebshopDbId(int webshopDbId) {
		this.shopId = webshopDbId;
	}

	@Override
	public String toString() {
		return "Product [dbId=" + id + ", urlPath=" + urlPath + ", name="
				+ name + ", description=" + description + ", shortDescription="
				+ shortDescription + ", imageUrl=" + imageUrl + ", price="
				+ price + ", priceCurrency=" + priceCurrency + ", weight="
				+ weight + ", shippingPrice=" + shippingPrice + ", category="
				+ category + ", ean=" + ean + "]";
	}
	

}
