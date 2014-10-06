package eu.ibutler.affiliatenetwork.entity;

public class Shop {
	

	private int dbId = 0;
	private String name = null;
	private String url = null;
	
	/**
	 * Public constructor
	 * @param name
	 * @param url
	 */
	public Shop(String name, String url) {
		this.name = name;
		this.url = url;
	}
	
	/**
	 * Constructor only used by DAO
	 * @param dbId assigned by DBMS
	 * @param name
	 */
	public Shop(int dbId, String name, String url) {
		this(name, url);
		this.dbId = dbId;
	}

	public int getDbId() {
		return dbId;
	}

	public void setDbId(int dbId) {
		this.dbId = dbId;
	}

	public String getName() {
		return name;
	}
	
	public String getUrl() {
		return url;
	}

	public void setName(String name) {
		if(name != null) {
			this.name = name;
		}
	}

	public void setUrl(String url) {
		if(url != null) {
			this.url = url;
		}
	}

	@Override
	public String toString() {
		return this.name;
	}
	
	

}
