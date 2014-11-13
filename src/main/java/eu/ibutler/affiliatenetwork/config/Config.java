package eu.ibutler.affiliatenetwork.config;

public class Config {
	
	private static AppConfig cfg = AppConfig.getInstance();
	
	public static final int SYNCH_ATTEMPT_MAX_AGE_DAYS = Integer.valueOf(cfg.get("synchAttemptMaxAge"));
	public static final int UNPROCESSED_PRICE_LIST_POLL_INTERVAL_SEC = Integer.valueOf(cfg.get("uprocessedFilesCheckInterval"));
	
	public static final int SERVER_PORT = Integer.valueOf(cfg.getWithEnv("port"));
	public static final String SERVER_HOSTNAME = cfg.getWithEnv("hostname");
	public static final int SERVER_BACKLOG = Integer.valueOf(cfg.getWithEnv("serverBacklog"));
	
	public static final String ENCODING = cfg.get("encoding");
	

}
