package eu.ibutler.affiliatenetwork.file;

import java.io.InputStream;
import java.sql.Connection;

import com.google.common.base.Throwables;

import eu.ibutler.affiliatenetwork.ParsingException;
import eu.ibutler.affiliatenetwork.ProcessingException;
import eu.ibutler.affiliatenetwork.ValidationException;
import eu.ibutler.affiliatenetwork.dao.impl.FileDaoImpl;
import eu.ibutler.affiliatenetwork.dao.impl.ProductDaoImpl;
import eu.ibutler.affiliatenetwork.dao.utils.JdbcUtils;
import eu.ibutler.affiliatenetwork.entity.Product;
import eu.ibutler.affiliatenetwork.entity.UploadedFile;

@FileExtension(".xml")
public class YMLProcessor extends AbstractFileProcessor {

	@Override
	public byte[] validateDownload(InputStream stream) throws ValidationException {
		return new byte[0];
	}

	@Override
	public UploadedFile validateFile(UploadedFile file) {
		try {
			YMLStaxParser parser = new YMLStaxParser(file.getFsPath());
/*			Offer offer = null;
			while((offer=parser.getNextOffer()) != null) {
				count++;
				//validate each offer here, for now offer parsed without exception means it's OK ##################################################
				if(offer.getType() == null && offer.getName() == null) {
					throw new ParsingException("Type is simplified, but no name found.");
				} else if(offer.getType().equals("vendor.model") && (offer.getVendor() == null || offer.getModel() == null)) {
					throw new ParsingException("Type is vendor.model, but no vendor or model found.");
				} else if(offer.getType() != null && !offer.getType().equals("vendor.model") && offer.getName() == null && offer.getTitle() == null) {
					throw new ParsingException("Unknown type. " + offer.getType() + " " + offer.getName() + " " + offer.getTitle());
				} 
			}*/
			logger.debug("Initial validation OK: " + file.getFsPath());
			file.setValidationMessage("Validation OK.");
			file.setValid(true);
		} catch (ParsingException e) {
			logger.debug("Validation fail: " + Throwables.getStackTraceAsString(e));
			file.setValidationMessage("Initial validation failed.");
			file.setValid(false);
		}
		return file;
	}

	@Override
	public void processFile(UploadedFile file) throws ProcessingException {
		int lineCount = 0;
		ProductDaoImpl productDao = new ProductDaoImpl();
		try {
			productDao.setProcessing(file.getShopId(), true);
			YMLStaxParser parser = new YMLStaxParser(file.getFsPath());
			
			Offer offer = new Offer();
			while(offer != null) {
				lineCount++;
				Connection conn = JdbcUtils.getConnection();
				try {
					offer=parser.getNextOffer();
					if(offer == null) {break;}
					this.validateOffer();
					String pName = null;
					if(offer.getType() == null ) {
						pName = offer.getName();
					} else if(offer.getType().equals("vendor.model")) {
						pName = offer.getVendor() + " " + offer.getModel();
					} else if(offer.getName() != null) {
						pName = offer.getName();
					} else if(offer.getTitle() != null) {
						pName = offer.getTitle();
					}

					double localDeliveryCost = -1.0;
					try {
						localDeliveryCost = offer.getLocalDeliveryCost() == null ? (-1.0) : Double.valueOf(offer.getLocalDeliveryCost());
					} catch (NumberFormatException ignore) {}

					Product product = new Product(file.getId(), file.getShopId(), offer.getUrl(), pName, Double.valueOf(offer.getPrice()),
							offer.getCurrencyId(), offer.getCategory(), offer.getPicture(), offer.getDescription(), null, offer.getBarcode(),
							localDeliveryCost);
					super.validateProduct(product);

					Product existing = productDao.selectByUrl(product.getRealUrl(), product.getShopId());
					if(existing == null) {
						long id =new ProductDaoImpl().insertOne(product, conn);
						product.setId(id);
					} else {
						product = existing;
					}
					product.setActive(true);
					product.setProcessing(false);
					productDao.update(product, conn);
					JdbcUtils.commitAndClose(conn);
				} catch (Exception e) {
					logger.debug("Invalid product, offer number: " + lineCount + " Exception: " + Throwables.getStackTraceAsString(e));
					JdbcUtils.rollbackAndClose(conn);
				}
			}
			
			productDao.deactivateOld(file.getShopId());
			
			file.setProcessed(true);
			file.setActive(true);
			file.setProductsCount(lineCount);
			
			new FileDaoImpl().update(file);
		} catch (Exception e) {
			logger.debug("Fatal processing error, offer number: " + lineCount + "Exception:" + Throwables.getStackTraceAsString(e));
			throw new ProcessingException("Processing fail.", e);
		}
		
	}
	
	private void validateOffer() throws ValidationException {
		
	}

}
