package eu.ibutler.affiliatenetwork.entity;

import java.io.File;
import java.util.Date;

import org.apache.log4j.Logger;

import eu.ibutler.affiliatenetwork.utils.AppConfig;

public class UploadedFile {
	
	private static AppConfig properties = AppConfig.getInstance();
	private static Logger log = Logger.getLogger(UploadedFile.class.getName());
	
	private int dbId = 0;
	private String extension = null;
	private String name = null;
	private String fsPath = null;
	private long uploadTime = 0; //in milliseconds since EPOCH
	private int webShopId = 0;
	private long size = 0;
	private boolean active = false;
	private boolean valid = false;
	private int productsCount = 0;
	
	
	/**
	 * This constructor is only called by DAO
	 * @param dbId
	 * @param name
	 * @param fsPath
	 * @param uploadTime
	 * @param webshopId
	 */
	public UploadedFile(int dbId, String name, String fsPath, long uploadTime, int shopId, boolean isActive, boolean isValid, int productsCount, long fileSize) {
		this.dbId = dbId;
		this.name = name;
		this.fsPath = fsPath;
		this.uploadTime = uploadTime;
		this.extension = name.substring(name.length()-".zip".length());
		this.webShopId = shopId;
		this.size = fileSize;
		this.active = isActive;
		this.valid = isValid;
		this.productsCount = productsCount;
	}
	

	/**
	 * Public constructor
	 * @param tmpFile
	 * @param uploadTime2
	 * @param extension
	 * @param uploadTime
	 */
	public UploadedFile(String fileOnDiskPath, String extension, long uploadTime, int shopId) {
		
		String correctName = "" + shopId + "_" + uploadTime + extension;
		String correctPath = properties.get("uploadPath") + "/" + correctName;
		
		File tmpFile = new File(fileOnDiskPath);
		//rename existing temporary file to a correct fileNameFormat
		boolean isRenamed = tmpFile.renameTo(new File(correctPath));
		if(!isRenamed) {
			log.error("Can't rename file");
		}
		
		this.fsPath = correctPath;
		this.name = correctName;
		this.extension = extension;
		this.uploadTime = uploadTime;
		this.webShopId = shopId;
		this.size = new File(fsPath).length();
		
	}
	
	@Override
	public String toString() {
		return "UploadedFile [name=" + name + ", uploadTime=" + new Date(uploadTime)
				+ ", webShopId=" + webShopId + "]";
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

	public String getExtension() {
		return extension;
	}

	public int getWebShopId() {
		return webShopId;
	}

	public void setDbId(int dbId) {
		this.dbId = dbId;
	}
	
	public long getSize() {
		return size;
	}
	
	public boolean isActive() {
		return active;
	}
	
	public boolean isValid() {
		return valid;
	}

	public int getProductsCount() {
		return productsCount;
	}

}
