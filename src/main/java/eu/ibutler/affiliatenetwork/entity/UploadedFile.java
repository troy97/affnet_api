package eu.ibutler.affiliatenetwork.entity;

import java.util.Date;

public class UploadedFile {
	
	private int dbId = 0;
	private String name = null;
	private String fsPath = null;
	private long uploadTime = 0; //in milliseconds since EPOCH
	private int webshopId = 0;
	
	/**
	 * This constructor is only called by DAO
	 * @param dbId
	 * @param name
	 * @param fsPath
	 * @param uploadTime
	 * @param webshopId
	 */
	public UploadedFile(int dbId, String name, String fsPath, long uploadTime, int webshopId) {
		this.dbId = dbId;
		this.name = name;
		this.fsPath = fsPath;
		this.uploadTime = uploadTime;
		this.webshopId = webshopId;
	}
	
	/**
	 * Public constructor
	 * @param name
	 * @param fsPath
	 * @param webshopId
	 * @param uploadTime
	 */
	public UploadedFile(String name, String fsPath, long uploadTime, int webshopId) {
		this.name = name;
		this.fsPath = fsPath;
		this.webshopId = webshopId;
		this.uploadTime = uploadTime;
	}
	
	@Override
	public String toString() {
		return "UploadedFile [name=" + name + ", uploadTime=" + new Date(uploadTime)
				+ ", webshopId=" + webshopId + "]";
	}

	public String getName() {
		return name;
	}

	public String getFsPath() {
		return fsPath;
	}

	public long getUploadTime() {
		return uploadTime;
	}

	public int getWebshopId() {
		return webshopId;
	}

	public void setDbId(int dbId) {
		this.dbId = dbId;
	}
	
	
	

}
