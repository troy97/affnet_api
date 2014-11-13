package eu.ibutler.affiliatenetwork;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import com.google.common.base.Throwables;

import eu.ibutler.affiliatenetwork.controllers.StatusEndpoint;
import eu.ibutler.affiliatenetwork.dao.exceptions.DbAccessException;
import eu.ibutler.affiliatenetwork.dao.impl.FileDaoImpl;
import eu.ibutler.affiliatenetwork.dao.impl.ShopSourceDaoImpl;
import eu.ibutler.affiliatenetwork.entity.ShopSource;
import eu.ibutler.affiliatenetwork.entity.UploadedFile;
import eu.ibutler.affiliatenetwork.file.UrlDownloader;

/**
 * This worker checks for shop sources that are either new or had last successful synch attempt 
 * more than "maxAge" time ago and schedules them for synchronization.
 */
public class ShopSynchWorker implements Runnable {

	private static Logger logger = Logger.getLogger(ShopSynchWorker.class.getName());
	
	@Override
	public void run() {
		logger.debug("Started.");
		ExecutorService manager = Executors.newFixedThreadPool(4);
		while(true) {
			
			ShopSourceDaoImpl dao = new ShopSourceDaoImpl();
			try {
				List<ShopSource> sourcesToSynch = dao.selectNew();
				//sourcesToSynch.addAll(dao.selectOlderThan(Config.SYNCH_ATTEMPT_MAX_AGE_DAYS*24*60*60*1000)); //insert max attempt age here
				sourcesToSynch.addAll(dao.selectOlderThan(60*1000)); //insert max attempt age here
				
				if(sourcesToSynch.size()>0) {logger.debug("Found unsynchronized sources, starting synch...");}
				for(ShopSource s : sourcesToSynch) {
					manager.execute(synch(s));
				}
			} catch (DbAccessException e) {
				StatusEndpoint.incrementErrors();
				e.printStackTrace();
			}
			
			//how often service will check for unsynchronized sources
			try {
				Thread.sleep(20*1000); //20 sec
				logger.debug("Check");
			} catch (InterruptedException e) {
				logger.warn("Interrupted by: " + e);
				StatusEndpoint.incrementWarnings();
				return;
			}
		}
	}//run
	
	private Runnable synch(final ShopSource s) {
		return new Runnable() {
			
			@Override
			public void run() {
				long timeStart = System.currentTimeMillis();
				boolean is_successful = false;
				String msg = synch0(s);
				if(msg == null) {
					is_successful = true;
					msg = "No errors.";
					
				}
				long timeStop = System.currentTimeMillis();
				
				try {
					ShopSourceDaoImpl dao = new ShopSourceDaoImpl();
					dao.insertSynch(is_successful, msg, timeStart, timeStop, s.getId());
					dao.updateSourceTime(System.currentTimeMillis(), s.getId());
				} catch (DbAccessException e) {
					StatusEndpoint.incrementErrors();
					e.printStackTrace();
				}
			}
			
			private String synch0(ShopSource s) {
				logger.debug("Synchronizing source with id " + s.getId());
				String result = null;
				try {
					UploadedFile file = new UrlDownloader().download(s);
					new FileDaoImpl().insertOne(file);
					logger.debug("Done.");
				} catch (Exception e) {
					logger.debug("Synch failure: " + Throwables.getStackTraceAsString(e));
					result = "Synch failure: " + e.getMessage();
					logger.debug(result);
				}
				return result;
			}
		};//runnable
	}
	
}
