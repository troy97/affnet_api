package eu.ibutler.affiliatenetwork.entity;

public class Click {

	private long id = 0;
	private long productId = 0;
	private int shopId = 0;
	private int distributorId = 0;
	
	/**
	 * Public constructor
	 * @param productId
	 * @param shopId
	 * @param distributorId
	 */
	public Click(long productId, int shopId, int distributorId) {
		this.productId = productId;
		this.shopId = shopId;
		this.distributorId = distributorId;
	}
	
	/**
	 * DAO constructor
	 * @param id
	 * @param productId
	 * @param shopId
	 * @param distributorId
	 */
	public Click(long id, int productId, int shopId, int distributorId) {
		this(productId, shopId, distributorId);
		this.id = id;
	}

	
	/*
	 * Setters
	 */

	public void setId(int id) {
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
	
	
	
}
