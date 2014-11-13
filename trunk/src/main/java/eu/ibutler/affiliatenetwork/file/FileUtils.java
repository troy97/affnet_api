package eu.ibutler.affiliatenetwork.file;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import au.com.bytecode.opencsv.CSVWriter;
import eu.ibutler.affiliatenetwork.config.FsPaths;
import eu.ibutler.affiliatenetwork.config.Urls;
import eu.ibutler.affiliatenetwork.controllers.Links;
import eu.ibutler.affiliatenetwork.dao.exceptions.DbAccessException;
import eu.ibutler.affiliatenetwork.dao.exceptions.NoSuchEntityException;
import eu.ibutler.affiliatenetwork.dao.impl.FileTemplateDaoImpl;
import eu.ibutler.affiliatenetwork.dao.impl.ShopDaoImpl;
import eu.ibutler.affiliatenetwork.entity.FileTemplate;

/**
 * Class contains utility methods to processing files
 * @author Anton Lukashchuk
 *
 */
public class FileUtils {
	
	private static Logger logger = Logger.getLogger(FileUtils.class.getName());
	
	/**
	 * Create csv file with active file template index
	 * @param distributorID
	 * @return file system path to created csv
	 * @throws IOException
	 */
	public static String createActiveFilesList(int distributorID) throws IOException {
		String result;
		try {
			List<FileTemplate> activeFiles;
			try {
				activeFiles = new FileTemplateDaoImpl().getAllActive();
			} catch (DbAccessException e) {
				logger.debug("Unable to get list of active file templates " + e.getClass().getName());
				throw e;
			}

			List<String> headers = FileFormat.getFileListColumns();
			List<String[]> data = createCSVData(activeFiles, distributorID);

			result = createCSV(headers, data);
		} catch (Exception e) {
			throw new IOException();
		}
		return result;
	}
	
	/**
	 * Creates arrays containing values for csv entries
	 * @param files
	 * @return
	 */
	private static List<String[]> createCSVData(List<FileTemplate> files, int distribId) {
		List<String[]> result = new ArrayList<>();
			for(FileTemplate f : files) {
			List<String> tmp = new ArrayList<>();
			tmp.add(String.valueOf(f.getId()));
			tmp.add(new Date(f.getCreateTime()).toString());
			tmp.add(String.valueOf(f.getProductsCount()));
			tmp.add(String.valueOf(f.getSize()));
			tmp.add(String.valueOf(f.getCompressedSize()));
			try {
				tmp.add(new ShopDaoImpl().selectById(f.getShopId()).getName());
			} catch (DbAccessException | NoSuchEntityException e) {
				logger.debug("Unable to get shop name " + e.getClass().getName());
				tmp.add("unknown");
			}
			tmp.add(String.valueOf(f.getShopId()));
			tmp.add(createDownloadUrl(distribId, f.getId()));
			result.add(tmp.toArray(new String[0]));
		}
		return result;
	}
	
	/**
	 * Create URL for downloading particular product file 
	 * @param distribId
	 * @param fileId
	 * @return
	 */
	private static String createDownloadUrl(int distribId, int fileId) {
		return Urls.fullURL(Urls.DISTRIBUTOR_FILE_REQUEST_CONTROLLER_URL) +
				"?" + Links.DISTRIBUTOR_ID_PARAM_NAME + "=" + distribId +
				"&" + Links.FILE_TEMPLATE_ID_PARAM_NAME + "=" + fileId;
		
	}
	
	private static String createCSV(List<String> headers, List<String[]> data) throws IOException {
		String result = null;
		new File(FsPaths.FILE_TEMPLATES_FOLDER + "/FileLists").mkdir();
		result = FsPaths.FILE_TEMPLATES_FOLDER + "/FileLists" + "/" + System.currentTimeMillis() + ".csv";

		logger.debug("Creating temporary list file...");
		CSVWriter csvWriter = new CSVWriter(new FileWriter(result), ',', '\"');
		csvWriter.writeNext(headers.toArray(new String[0]));
		csvWriter.writeAll(data);
		csvWriter.close();
		logger.debug("Temporary list file created: " + result);
		return result;
    }
	
}
