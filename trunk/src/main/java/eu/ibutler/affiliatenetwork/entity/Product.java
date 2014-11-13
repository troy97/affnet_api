package eu.ibutler.affiliatenetwork.entity;

public class Product {
	
	//DB markers
	private long id = 0;
	private int fileId = 0;
	private int shopId = 0;
	private boolean active = false;
	private boolean processing = false;
	
	//mandatory (not NULL in DB)
	private String realUrl = null;
	private String name = null;
	private double price = 0.0;
	private String currencyCode  = null;
	private String category  = null;
	//optional
	private String imageUrl  = null;
	private String description  = null;
	private String descriptionShort  = null;
	private String ean  = null;
	private double shippingPrice = -1.0;

	
	/**
	 * Public constructor
	 */
	public Product(int fileId,
				   int shopId,
				   //mandatory
				   String realUrl,
				   String name,
				   double price,
				   String currencyCode,
				   String category,
					//optional
				   String imageUrl,
				   String description,
				   String descriptionShort,
				   String ean,
				   double shippingPrice) {
		
		this.fileId = fileId;
		this.shopId = shopId;
		
		//mandatory
		this.realUrl = realUrl;
		this.name = name;
		this.price = price;
		this.currencyCode = currencyCode;
		this.category = category;
		//optional
		this.imageUrl = imageUrl;
		this.description = description;
		this.descriptionShort = descriptionShort;
		this.ean = ean;
		this.shippingPrice = shippingPrice;
	}
	
	/**
	 * This constructor is only used by DAO
	 * @param fields
	 */
	public Product(long id, //obtained from DB
				   int fileId,
				   int shopId,
				   boolean isActive,
				   boolean isProcessing,
				   //mandatory
				   String realUrl,
				   String name,
				   double price,
				   String currencyCode,
				   String category,
					//optional
				   String imageUrl,
				   String description,
				   String descriptionShort,
				   String ean,
				   double shippingPrice) {
		
		this(fileId,shopId,realUrl,name,price,currencyCode,category,imageUrl,description,descriptionShort,ean, shippingPrice);
		this.id = id;
		this.active = isActive;
		this.processing = isProcessing;
	}

	
	@Override
	public String toString() {
		return "Product [urlPath=" + realUrl + ", name=" + name + "]";
	}

	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getFileId() {
		return fileId;
	}

	public void setFileId(int fileId) {
		this.fileId = fileId;
	}

	public int getShopId() {
		return shopId;
	}

	public void setShopId(int shopId) {
		this.shopId = shopId;
	}

	public String getRealUrl() {
		return realUrl;
	}

	public void setRealUrl(String realUrl) {
		this.realUrl = realUrl;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public String getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescriptionShort() {
		return descriptionShort;
	}

	public void setDescriptionShort(String descriptionShort) {
		this.descriptionShort = descriptionShort;
	}

	public String getEan() {
		return ean;
	}

	public void setEan(String ean) {
		this.ean = ean;
	}

	public double getShippingPrice() {
		return shippingPrice;
	}

	public void setShippingPrice(double shippingPrice) {
		this.shippingPrice = shippingPrice;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public boolean isProcessing() {
		return processing;
	}

	public void setProcessing(boolean processing) {
		this.processing = processing;
	}
	
	
	
	
}
