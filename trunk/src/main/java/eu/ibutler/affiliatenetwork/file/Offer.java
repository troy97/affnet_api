package eu.ibutler.affiliatenetwork.file;

public class Offer {
	
	private String url = null;
	private String price = null;
	private String currencyId = null;
	private String category = null;
	private String picture = null;	
	private String description = null;	
	private String barcode = null;
	private String name = null;
	private String title = null;
	
	private String type = null;
	private String vendor = null;
	private String model = null;
	private String localDeliveryCost = null;
	
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	public String getPrice() {
		return price;
	}
	
	public void setPrice(String price) {
		this.price = price;
	}
	public String getCurrencyId() {
		return currencyId;
	}
	
	public void setCurrencyId(String currencyId) {
		this.currencyId = currencyId;
	}
	public String getCategory() {
		return category;
	}
	
	public void setCategory(String category) {
		this.category = category;
	}
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	public String getVendor() {
		return vendor;
	}
	
	public void setVendor(String vendor) {
		this.vendor = vendor;
	}
	public String getModel() {
		return model;
	}
	
	public void setModel(String model) {
		this.model = model;
	}

	public String getPicture() {
		return picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	

	public String getLocalDeliveryCost() {
		return localDeliveryCost;
	}

	public void setLocalDeliveryCost(String localDeliveryCost) {
		this.localDeliveryCost = localDeliveryCost;
	}

	@Override
	public String toString() {
		return "\n Offer [url=" + url + ", price=" + price + ", currencyId="
				+ currencyId + ", category=" + category + ", picture="
				+ picture + ", description=" + description + ", barcode="
				+ barcode + ", name=" + name + ", title=" + title + ", type="
				+ type + ", vendor=" + vendor + ", model=" + model
				+ ", localDeliveryCost=" + localDeliveryCost + "]";
	}





	
	
	
}
