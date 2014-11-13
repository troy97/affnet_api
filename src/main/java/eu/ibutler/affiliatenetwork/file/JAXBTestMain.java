package eu.ibutler.affiliatenetwork.file;

import java.util.ArrayList;
import java.util.List;

import eu.ibutler.affiliatenetwork.ParsingException;

public class JAXBTestMain {
	
	public static void main(String[] args) {
		
		YMLStaxParser parser = null;
		try {
			parser = new YMLStaxParser("/home/anton/Desktop/YML/YML_REAL.xml");
		} catch (ParsingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	      List<Offer> offers = new ArrayList<>();
	      Offer offer = new Offer();
	      
	      while(offer != null){
	    	  try {
				offer = parser.getNextOffer();
			} catch (ParsingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	  offers.add(offer);
	      }
	      parser.close();
	      System.out.println("Size: " + offers.size() + "  \n" + offers);
		
	}

}
