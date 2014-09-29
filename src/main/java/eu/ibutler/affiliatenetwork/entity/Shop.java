package eu.ibutler.affiliatenetwork.entity;

public class Shop {
	
	private int dbId = 0;
	private String name = null;
	
	/**
	 * Constructor only used by DAO
	 * @param dbId assigned by DBMS
	 * @param name
	 */
	public Shop(int dbId, String name) {
		this.dbId = dbId;
		this.name = name;
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

	@Override
	public String toString() {
		return this.name;
	}
	
	

}
