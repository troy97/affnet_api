package eu.ibutler.affiliatenetwork.entity;

public class Click {

	private long id = 0;
	private long productId = 0;
	private String productName = null;
	private double productPrice = -1.0;
	private double shippingPrice = -1.0;
	private int shopId = 0;
	private int distributorId = 0;
	private int subId=-1;
	private long clickTime = 0;
	
	/**
	 * Public constructor
	 * @param productId
	 * @param shopId
	 * @param distributorId
	 */
	public Click(long productId,
			int shopId,
			int distributorId,
			int subId,
			String prodName,
			double prodPrice,
			double shipPrice) {
		this.productId = productId;
		this.productName = prodName;
		this.productPrice = prodPrice;
		this.shippingPrice = shipPrice;
		this.shopId = shopId;
		this.distributorId = distributorId;
		this.subId = subId;
		this.clickTime = System.currentTimeMillis();
	}
	
	/**
	 * DAO constructor
	 * @param id
	 * @param productId
	 * @param shopId
	 * @param distributorId
	 */
	public Click(long id,
			int productId,
			int shopId,
			int distributorId,
			int subId,
			String prodName,
			double prodPrice,
			double shipPrice) {
		this(productId, shopId, distributorId, subId, prodName, prodPrice, shipPrice);
		this.id = id;
	}

	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getProductId() {
		return productId;
	}

	public void setProductId(long productId) {
		this.productId = productId;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public double getProductPrice() {
		return productPrice;
	}

	public void setProductPrice(double productPrice) {
		this.productPrice = productPrice;
	}

	public double getShippingPrice() {
		return shippingPrice;
	}

	public void setShippingPrice(double shippingPrice) {
		this.shippingPrice = shippingPrice;
	}

	public int getShopId() {
		return shopId;
	}

	public void setShopId(int shopId) {
		this.shopId = shopId;
	}

	public int getDistributorId() {
		return distributorId;
	}

	public void setDistributorId(int distributorId) {
		this.distributorId = distributorId;
	}

	public int getSubId() {
		return subId;
	}

	public void setSubId(int subId) {
		this.subId = subId;
	}

	
	
	public long getClickTime() {
		return clickTime;
	}

	public void setClickTime(long clickTime) {
		this.clickTime = clickTime;
	}

	@Override
	public String toString() {
		return "Click [productId=" + productId + ", shopId=" + shopId
				+ ", distributorId=" + distributorId + "]";
	}
}
