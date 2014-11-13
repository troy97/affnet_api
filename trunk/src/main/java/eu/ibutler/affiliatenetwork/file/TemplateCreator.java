package eu.ibutler.affiliatenetwork.file;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.google.common.base.Throwables;

import au.com.bytecode.opencsv.CSVWriter;
import eu.ibutler.affiliatenetwork.config.Urls;
import eu.ibutler.affiliatenetwork.controllers.Links;
import eu.ibutler.affiliatenetwork.dao.exceptions.DaoException;
import eu.ibutler.affiliatenetwork.dao.impl.FileTemplateDaoImpl;
import eu.ibutler.affiliatenetwork.dao.impl.ProductDaoImpl;
import eu.ibutler.affiliatenetwork.dao.utils.JdbcUtils;
import eu.ibutler.affiliatenetwork.entity.FileTemplate;
import eu.ibutler.affiliatenetwork.entity.Product;

/**
 * Utility class to create FileTemplate for given shop
 * @author Anton Lukashchuk
 *
 */
public class TemplateCreator {
	
	private static Logger logger = Logger.getLogger(TemplateCreator.class.getName());

	public static FileTemplate create(int shopId) {
		ResultSet productsRs = null;
		CSVWriter csvWriter = null;
		try {
			ProductDaoImpl productDao = new ProductDaoImpl();
			productsRs = productDao.selectAllByShop(shopId);
			FileTemplate result = new FileTemplate(shopId);
			csvWriter = new CSVWriter(new FileWriter(result.getFsPath()), ',', '\"');
			csvWriter.writeNext(OutputFormat.getHeader().toArray(new String[0]));
			int productCounter = 0;
			while(productsRs.next()) {
				Product p = productDao.extractOne(productsRs);
				csvWriter.writeNext(toStrArray(p));
				productCounter++;
			}
			csvWriter.close();
			result.setProductsCount(productCounter);
			result.setActive(true);
			result.setSize(new File(result.getFsPath()).length());
			
			int id = new FileTemplateDaoImpl().insertOne(result);
			result.setId(id);
			return result;
		} catch (DaoException | SQLException | IOException e) {
			logger.error("Error creating template: " + Throwables.getStackTraceAsString(e));
			return null;
		} finally {
			if(csvWriter != null) {
				try {
					csvWriter.close();
				} catch (IOException e) {
					logger.debug("Failed to close csv writer: " + Throwables.getStackTraceAsString(e));
				}
			}
			if(productsRs != null) {
				try {
					Statement stm = productsRs.getStatement();
					Connection conn = stm.getConnection();
					JdbcUtils.close(productsRs);
					JdbcUtils.close(stm);
					JdbcUtils.close(conn);
				} catch (SQLException ignore) {}
			}
		}//finally
	}
	
/*	private static Product extractOne(ResultSet rs) throws SQLException {
		return new Product( rs.getLong("id"),
							rs.getInt("file_id"),
							rs.getInt("shop_id"),
							rs.getBoolean("is_active"),
							rs.getBoolean("is_processing"),
							
							rs.getString("real_url"),
							rs.getString("name"),
							rs.getDouble("price"),
							rs.getString("currency_code"),
							rs.getString("category"),
							
							rs.getString("image_url"),
							rs.getString("description"),
							rs.getString("description_short"),
							rs.getString("ean"),
							rs.getDouble("shipping_price"));
	}*/
	
	/**
	 * 
	 * @param p
	 * @return String containing distributor link to a particular product in AffNet DB
	 */
	private static String makeDistributorLink(Product p) {
		Map<String, String> linkQuery = new HashMap<>();
		linkQuery.put(Links.PRODUCT_ID_PARAM_NAME, ""+p.getId());
		linkQuery.put(Links.DISTRIBUTOR_ID_PARAM_NAME, "1");
		String distributorLink = Urls.DOMAIN_NAME + Urls.DISTRIBUTOR_CLICK_URL + Links.createQueryString(linkQuery);
		return distributorLink;
	}
	
	private static String[] toStrArray(Product p) {
		String[] result = new String[OutputFormat.getHeader().size()];
		
		result[0] = String.valueOf(p.getId());
		result[1] = makeDistributorLink(p);
		result[2] = p.getName();
		result[3] = String.valueOf(p.getPrice());
		result[4] = p.getCurrencyCode();
		result[5] = p.getCategory();
		
		result[6] = p.getImageUrl() == null ? "" : p.getImageUrl();
		result[7] = p.getDescription() == null ? "" : p.getDescription();
		result[8] = p.getDescriptionShort() == null ? "" : p.getDescriptionShort();
		result[9] = p.getEan() == null ? "" : p.getEan();
		result[10] = (p.getShippingPrice() < 0) ? "" : String.valueOf(p.getShippingPrice());
		
		return result;
	}
}
