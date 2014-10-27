package eu.ibutler.affiliatenetwork.entity;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;

import eu.ibutler.affiliatenetwork.config.AppConfig;


public class FileTemplate {

	private static AppConfig cfg = AppConfig.getInstance();
	private static Logger log = Logger.getLogger(UploadedFile.class.getName());
	
	private static final String FILE_EXTENSION = ".csv";
	
	private int id = 0;
	private String name = null;
	private String fsPath = null;
	private int productsCount = 0;
	private boolean active = false;
	private long size = 0L;
	private long compressedSize = 0L;
	private long createTime = 0L; //in milliseconds since EPOCH
	private int uploadedFileId = 0;
	private int webshopId = 0;
	

	/**
	 * This constructor only used by DAO
	 * @param id
	 * @param name
	 * @param fsPath
	 * @param productsCount
	 * @param active
	 * @param size
	 * @param compressedSize
	 * @param createTime
	 * @param uploadedFileId
	 * @param shopId
	 */
	public FileTemplate(int id, String name, String fsPath,
			int productsCount, boolean active, long size, long compressedSize,
			long createTime, int uploadedFileId, int shopId) {
		this.id = id;
		this.name = name;
		this.fsPath = fsPath;
		this.productsCount = productsCount;
		this.active = active;
		this.size = size;
		this.compressedSize = compressedSize;
		this.createTime = createTime;
		this.uploadedFileId = uploadedFileId;
		this.webshopId = shopId;
	}



	/**
	 * Public constructor
	 * @param tmpFilePath - path to file which was downloaded and stored on disk
	 * @param uploadTime (ms since EPOCH)
	 * @param extension ".zip" for example
	 * @param shopId
	 * @throws IOException 
	 */
	public FileTemplate(int uploadedFileId, int shopId) throws IOException {
		this.createTime = System.currentTimeMillis();
		String templatesFolder = cfg.getWithEnv("fileTemplatesPath");
		//create directory if it does not exist
		new File(templatesFolder).mkdir();
		//create name in format: createTimeMillis + [suffix].extension
		String correctName = "";
		String correctPath = "";
		int suffix = 0;
		boolean created = false;
		while(!created) {
			if(suffix == 0) {
				correctName = "" + this.createTime + FILE_EXTENSION;
			} else {
				correctName = "" + this.createTime + "_" + suffix + FILE_EXTENSION;
			}
			correctPath = templatesFolder + "/" + correctName;
			File f = new File(correctPath);
			created = f.createNewFile();
			if(!created) {
				log.debug("File \"" + correctName + "\" already exists, adding suffix...");
			}
			suffix++;
		}
		
		this.fsPath = correctPath;
		this.name = correctName;
		this.uploadedFileId = uploadedFileId;
		this.webshopId = shopId;
		
		log.debug("File \"" + this.fsPath + "\" created");
	}


	
	

	public int getId() {
		return id;
	}



	public void setDbId(int dbId) {
		this.id = dbId;
	}



	public String getName() {
		return name;
	}



	public void setName(String name) {
		this.name = name;
	}



	public String getFsPath() {
		return fsPath;
	}



	public void setFsPath(String fsPath) {
		this.fsPath = fsPath;
	}



	public int getProductsCount() {
		return productsCount;
	}



	public void setProductsCount(int productsCount) {
		this.productsCount = productsCount;
	}



	public boolean isActive() {
		return active;
	}



	public void setActive(boolean active) {
		this.active = active;
	}



	public long getSize() {
		return size;
	}



	public void setSize(long size) {
		this.size = size;
	}



	public long getCompressedSize() {
		return compressedSize;
	}



	public void setCompressedSize(long compressedSize) {
		this.compressedSize = compressedSize;
	}



	public long getCreateTime() {
		return createTime;
	}



	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}



	public int getUploadedFileDbId() {
		return uploadedFileId;
	}



	public void setUploadedFileDbId(int uploadedFileDbId) {
		this.uploadedFileId = uploadedFileDbId;
	}



	public int getShopId() {
		return webshopId;
	}



	public void setWebshopDbId(int webshopDbId) {
		this.webshopId = webshopDbId;
	}

	
	
}
