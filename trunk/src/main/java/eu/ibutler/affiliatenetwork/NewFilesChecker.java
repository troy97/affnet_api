package eu.ibutler.affiliatenetwork;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import com.google.common.base.Throwables;

import eu.ibutler.affiliatenetwork.config.Config;
import eu.ibutler.affiliatenetwork.controllers.StatusEndpoint;
import eu.ibutler.affiliatenetwork.dao.exceptions.DbAccessException;
import eu.ibutler.affiliatenetwork.dao.impl.FileDaoImpl;
import eu.ibutler.affiliatenetwork.entity.FileTemplate;
import eu.ibutler.affiliatenetwork.entity.UploadedFile;
import eu.ibutler.affiliatenetwork.file.AbstractFileProcessor;
import eu.ibutler.affiliatenetwork.file.FileProcessingUtils;
import eu.ibutler.affiliatenetwork.file.TemplateCreator;

/**
 * Searches for unprocessed files in DB
 * and schedules them for processing by ExecutorService.
 * Unprocessed files are divided into groups according to shop they belong to.
 * Processing of one group of files is made by one thread in the oder the files were
 * uploaded.
 * @author Anton Lukashchuk
 *
 */
public class NewFilesChecker implements Runnable {

	private static Logger logger = Logger.getLogger(NewFilesChecker.class.getName());
	
	@Override
	public void run() {
		ExecutorService manager = Executors.newFixedThreadPool(4);
		logger.debug("Started");
		while(true) {
			try {
				List<UploadedFile> unprocessed = new FileDaoImpl().selectUnprocessed();
				if(unprocessed.size()>0) {logger.debug("Found " + unprocessed.size() + " unprocessed files, start processing...");}
				while(unprocessed.size()>0) {
					List<UploadedFile> fromOneShop = selectWithSameShopId(unprocessed);
					//make task with sequential processing of this list in one thread
					Runnable task = schedule(fromOneShop);
					manager.execute(task);
				}
			} catch (DbAccessException e) {
				logger.error("Error getting unprocessed files from DB. " + Throwables.getStackTraceAsString(e));
				StatusEndpoint.incrementErrors();
			}
			
			//how often service will check for new files
			try {
				Thread.sleep(Config.UNPROCESSED_PRICE_LIST_POLL_INTERVAL_SEC*1000);
				logger.debug("Check");
			} catch (InterruptedException e) {
				logger.warn("Interrupted by: " + e);
				StatusEndpoint.incrementWarnings();
				return;
			}
		}//while
	}//run

	/**
	 * Extracts UploadedFiles belonging to the same shop from given list.
	 * Given List is modified during execution of this method
	 * @param unprocessed
	 * @return
	 */
	private List<UploadedFile> selectWithSameShopId(List<UploadedFile> unprocessed) {
		List<UploadedFile> result = new LinkedList<>();
		int id = unprocessed.get(0).getShopId();
		Iterator<UploadedFile> iter = unprocessed.iterator();
		while (iter.hasNext()) {
			UploadedFile f = iter.next();
		    if (f.getShopId() == id) {
		    	result.add(f);
		        iter.remove();
		    }
		}
		return result;
	}
	
	
	private Runnable schedule(final List<UploadedFile> files) {
		Runnable task = new Runnable() {
			
			@Override
			public void run() {
				for(UploadedFile f : files) {
					logger.debug("Processing: " + f.getName() + " shop: " + f.getShopId() + " uploaded at: " + f.getUploadTime());
					AbstractFileProcessor processor = FileProcessingUtils.getProcessor(f.getExtension());

					UploadedFile file = processor.validateFile(f);
					if(file.isValid()) {
						try {
							processor.processFile(file);
						} catch (ProcessingException e) {
							logger.debug("Unable to process valid file. " + Throwables.getStackTraceAsString(e));
							return;
						}
					} else {
						logger.debug("Invalid file " + file.getFsPath() + " skip processing.");
						file.setProcessed(true);
						try {
							new FileDaoImpl().update(file);
						} catch (DbAccessException e) {
							logger.debug("Unable to update file entry. " + Throwables.getStackTraceAsString(e));
							return;
						}
					}
					logger.debug("Finished processing of: "  + f.getName());
				}
				//create template file containing all active products for this shop
				logger.debug("Creating template file for shop: " + files.get(0).getShopId());
				FileTemplate t = TemplateCreator.create(files.get(0).getShopId());
				logger.debug("Template created: " + t.getFsPath());
			}//run
		};//task
		return task;
	}


}
