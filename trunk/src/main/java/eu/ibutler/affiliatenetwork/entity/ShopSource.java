package eu.ibutler.affiliatenetwork.entity;

public class ShopSource {
	
	private int id = 0;
	private int shop_id = 0;
	private String file_format = null;
	private String download_url = null;
	private boolean basic_http_auth_required = false;
	private String basic_http_auth_username = null;
	private String basic_http_auth_password = null;
	
	
	public ShopSource(int shop_id, String file_format, String download_url,
			boolean basic_http_auth_required, String basic_http_auth_username,
			String basic_http_auth_password) {
		this.shop_id = shop_id;
		this.file_format = file_format;
		this.download_url = download_url;
		this.basic_http_auth_required = basic_http_auth_required;
		this.basic_http_auth_username = basic_http_auth_username;
		this.basic_http_auth_password = basic_http_auth_password;
	}


	public ShopSource(int id, int shop_id, String file_format,
			String download_url, boolean basic_http_auth_required,
			String basic_http_auth_username, String basic_http_auth_password) {
		this(shop_id, file_format, download_url,
				basic_http_auth_required, basic_http_auth_username, basic_http_auth_password);
		this.id = id;
	}


	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public int getShop_id() {
		return shop_id;
	}


	public void setShop_id(int shop_id) {
		this.shop_id = shop_id;
	}


	public String getFile_format() {
		return file_format;
	}


	public void setFile_format(String file_format) {
		this.file_format = file_format;
	}


	public String getDownload_url() {
		return download_url;
	}


	public void setDownload_url(String download_url) {
		this.download_url = download_url;
	}


	public boolean isBasic_http_auth_required() {
		return basic_http_auth_required;
	}


	public void setBasic_http_auth_required(boolean basic_http_auth_required) {
		this.basic_http_auth_required = basic_http_auth_required;
	}


	public String getBasic_http_auth_username() {
		return basic_http_auth_username;
	}


	public void setBasic_http_auth_username(String basic_http_auth_username) {
		this.basic_http_auth_username = basic_http_auth_username;
	}


	public String getBasic_http_auth_password() {
		return basic_http_auth_password;
	}


	public void setBasic_http_auth_password(String basic_http_auth_password) {
		this.basic_http_auth_password = basic_http_auth_password;
	}
	
	
	
	
	

}
