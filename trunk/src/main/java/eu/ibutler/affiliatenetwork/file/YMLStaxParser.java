package eu.ibutler.affiliatenetwork.file;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.apache.log4j.Logger;

import com.google.common.base.Throwables;

import eu.ibutler.affiliatenetwork.ParsingException;

/**
 * This is xml parser that parses given Yandex .yml file into Offer object
 * @author Anton Lukashchuk
 *
 */
public class YMLStaxParser {
	
	private static Logger logger = Logger.getLogger(YMLStaxParser.class.getName());
	
	private static final String categoriesTag = "categories";
	private static final String categoryTag = "category";
	
	private static final String categoryIdTag = "categoryId"; //child of offerTag
	
	private static final String offersTag = "offers";
	private static final String offerTag = "offer";
	
	private static final String categoryIdAttr = "id";
	
	private static final String titleTag = "title";
	
	
	private Map<Integer, String> categories = null;
	private XMLEventReader eventReader = null;

	public YMLStaxParser(String ymlFilePath) throws ParsingException {
		init(ymlFilePath);
	}
	
	
	private void init(String ymlFile) throws ParsingException {
		try {
			// First, create a new XMLInputFactory
			XMLInputFactory inputFactory = XMLInputFactory.newInstance();
			// Setup a new eventReader
			InputStream in = new FileInputStream(ymlFile);
			eventReader = inputFactory.createXMLEventReader(in);


			//Create map <categoryId, category_name>
			this.categories = getCategories();
			//System.out.println(categories);


			//move down to offersTag
			while (eventReader.hasNext()) {
				XMLEvent event = eventReader.nextEvent();

				if (event.isStartElement()) {
					if (event.asStartElement().getName().getLocalPart().equals(offersTag)) {
						//System.out.println("offers tag found");
						break;
					}
				}
			}//now ready to read offers
			
		} catch (Exception e) {
			this.close();
			System.err.println(Throwables.getStackTraceAsString(e));
			throw new ParsingException(e);
		} 
	}//init


	/**
	 * Reads next offer object 
	 * @param eventReader
	 * @return Offer or null if no more offer available
	 * @throws XMLStreamException
	 * @throws ParsingException 
	 */
	public Offer getNextOffer() throws ParsingException {
		boolean foundOfferTag = false;
		Offer offer = null;
		try {
			while (eventReader.hasNext()) {
				XMLEvent event = eventReader.nextEvent();

				if (event.isStartElement()) {
					StartElement startElement = event.asStartElement();

					// If we have an item element, we create a new item
					if (startElement.getName().getLocalPart().equals(offerTag)) {
						if(!foundOfferTag) {
							foundOfferTag = true;
							offer = new Offer();
							// We read the attributes from this tag and add the date
							// attribute to our object
							Iterator<Attribute> attributes = startElement.getAttributes();
							while (attributes.hasNext()) {
								Attribute attribute = attributes.next();
								if (attribute.getName().toString().equals(FileFormat.VENDORMODEL_TYPE_M)) {
									offer.setType(attribute.getValue());
									break;
								}

							}
							continue;
						} else {
							String msg = "Error, found second <offer> tag before closing tag </offer>.";
							logger.debug(msg);
							throw new ParsingException(msg);
						}
					}

					//mandatory
					
					if (startElement.getName().getLocalPart().equals(FileFormat.COLUMN_URL_PATH_M)) {
						event = eventReader.nextEvent();
						offer.setUrl(event.asCharacters().getData());
						continue;
					}
					if (startElement.getName().getLocalPart().equals(FileFormat.COLUMN_PRICE_M)) {
						event = eventReader.nextEvent();
						offer.setPrice(event.asCharacters().getData());
						continue;
					}
					if (startElement.getName().getLocalPart().equals(FileFormat.COLUMN_PRICE_CURRENCY_M)) {
						event = eventReader.nextEvent();
						offer.setCurrencyId(event.asCharacters().getData());
						continue;
					}
					if (startElement.getName().getLocalPart().equals(categoryIdTag)) {
						event = eventReader.nextEvent();
						offer.setCategory(event.asCharacters().getData());
						continue;
					}
					if (startElement.getName().getLocalPart().equals(FileFormat.SIMPLIFIED_NAME_M)) {
						event = eventReader.nextEvent();
						offer.setName(event.asCharacters().getData());
						continue;
					}
					if (startElement.getName().getLocalPart().equals(FileFormat.VENDORMODEL_VENDOR_M)) {
						event = eventReader.nextEvent();
						offer.setVendor(event.asCharacters().getData());
						continue;
					}
					if (startElement.getName().getLocalPart().equals(FileFormat.VENDORMODEL_MODEL_M)) {
						event = eventReader.nextEvent();
						offer.setModel(event.asCharacters().getData());
						continue;
					}
					
					//optional
					
					if (startElement.getName().getLocalPart().equals(FileFormat.COLUMN_PICTURE_O)) {
						event = eventReader.nextEvent();
						offer.setPicture(event.asCharacters().getData());
						continue;
					}
					if (startElement.getName().getLocalPart().equals(FileFormat.COLUMN_DESCRIPTION_O)) {
						event = eventReader.nextEvent();
						offer.setDescription(event.asCharacters().getData());
						continue;
					}
					if (startElement.getName().getLocalPart().equals(FileFormat.COLUMN_BARCODE_O)) {
						event = eventReader.nextEvent();
						offer.setBarcode(event.asCharacters().getData());
						continue;
					}
					if (startElement.getName().getLocalPart().equals(FileFormat.COLUMN_LOCAL_DELIVERY_COST_O)) {
						event = eventReader.nextEvent();
						offer.setLocalDeliveryCost(event.asCharacters().getData());
						continue;
					}
					if (startElement.getName().getLocalPart().equals(titleTag)) {
						event = eventReader.nextEvent();
						offer.setTitle(event.asCharacters().getData());
						continue;
					}
					

				}

				// If we reach the end of an item element, we add it to the list
				if (event.isEndElement()) {
					EndElement endElement = event.asEndElement();
					if (endElement.getName().getLocalPart().equals(offerTag)) {
						break;
					}
					if (endElement.getName().getLocalPart().equals(offersTag)) {
						offer = null;
						break;
					}
				}

			}//while
			if(offer != null) {
				offer.setCategory( this.categories.get(Integer.valueOf( offer.getCategory() )) );
			}
		} catch (XMLStreamException e) {
			throw new ParsingException(e);
		}
		return offer;
	}
	  
	/**
	 * Closes underlying reader
	 */
	public void close() {
		try {
			this.eventReader.close();
		} catch (XMLStreamException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Moves to categoriesTag, reads all categories till the </categoriesTag>
	 * and returns them as Map.
	 * @param eventReader
	 * @return
	 * @throws XMLStreamException
	 */
	private Map<Integer, String> getCategories() throws XMLStreamException {
		  Map<Integer, String> categories = new HashMap<>();
		  //move to categoriesTag
		  while (eventReader.hasNext()) {
			  XMLEvent event = eventReader.nextEvent();

			  if (event.isStartElement()) {
				  if (event.asStartElement().getName().getLocalPart().equals(categoriesTag)) {
					  //System.out.println("Categories tag found");
					  break;
				  }
			  }
		  }//found categories tag

		  //populate categories map
		  while (eventReader.hasNext()) {
			  Integer key = null;
			  String value = null;

			  XMLEvent event = eventReader.nextEvent();

			  //is start tag
			  if (event.isStartElement()) {
				  if (event.asStartElement().getName().getLocalPart().equals(categoryTag)) {
					  Iterator<Attribute> attributes = event.asStartElement().getAttributes();
					  while (attributes.hasNext()) {
						  Attribute attribute = attributes.next();
						  if (attribute.getName().toString().equals(categoryIdAttr)) {
							  key = Integer.valueOf(attribute.getValue());
						  }

					  }

					  event = eventReader.nextEvent();
					  value = event.asCharacters().getData();

					  categories.put(key, value);
				  }
			  }

			  // If I reach the </categories> tag
			  if (event.isEndElement()) {
				  EndElement endElement = event.asEndElement();
				  if (endElement.getName().getLocalPart().equals(categoriesTag)) {
					  break;
				  }
			  }

		  }//populate categories map
		  return categories;
	  }

} 
