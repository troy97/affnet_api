package eu.ibutler.affiliatenetwork.entity;

public class Click {

	private long id = 0;
	private long productId = 0;
	private int shopId = 0;
	private int distributorId = 0;
	private int subId=-1;
	
	/**
	 * Public constructor
	 * @param productId
	 * @param shopId
	 * @param distributorId
	 */
	public Click(long productId, int shopId, int distributorId, int subId) {
		this.productId = productId;
		this.shopId = shopId;
		this.distributorId = distributorId;
		this.subId = subId;
	}
	
	/**
	 * DAO constructor
	 * @param id
	 * @param productId
	 * @param shopId
	 * @param distributorId
	 */
	public Click(long id, int productId, int shopId, int distributorId, int subId) {
		this(productId, shopId, distributorId, subId);
		this.id = id;
	}

	
	/*
	 * Setters
	 */

	public void setId(long id) {
		this.id = id;
	}

	/*
	 * Getters
	 */
	
	public long getId() {
		return id;
	}
	
	
	public long getProductId() {
		return productId;
	}


	public int getShopId() {
		return shopId;
	}

	public int getDistributorId() {
		return distributorId;
	}

	@Override
	public String toString() {
		return "Click [productId=" + productId + ", shopId=" + shopId
				+ ", distributorId=" + distributorId + "]";
	}

	public int getSubId() {
		return subId;
	}
	
	
	
}
