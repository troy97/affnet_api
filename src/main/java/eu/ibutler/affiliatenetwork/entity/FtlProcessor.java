package eu.ibutler.affiliatenetwork.entity;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import eu.ibutler.affiliatenetwork.controllers.LoginPageController;
import eu.ibutler.affiliatenetwork.entity.exceptions.FtlProcessingException;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class FtlProcessor {
	
	private static Logger log = Logger.getLogger(LoginPageController.class.getName());
	
	/**
	 * Merges *.ftl file and data model producing static html
	 * @param ftlName - name of ftl file in ftl folder (path to folder stored in config.properties)
	 * @param data
	 * @return
	 * @throws FtlProcessingException
	 */
	public String createHtml(String ftlName, FtlDataModel data) throws FtlProcessingException {
		String resultHtml = null;
		
		Template view;
		try {
			view = createView(ftlName);
		} catch (IOException e1) {
			log.error("Error creating template from FreeMarker configuration object");
			throw new FtlProcessingException();
		}

		//create html from template and data model
		try{
			StringWriter writer = new StringWriter();
			view.process(data.getModel(), writer);
			resultHtml = writer.toString();
		}catch(TemplateException | IOException e){
			log.error("Error while merging view and model");
			throw new FtlProcessingException();
		}
		
		return resultHtml;
	}
	
	private Template createView(String ftlName) throws IOException {
		Configuration cfg = FreeMakerConfig.getInstance();
		Template view = null;
		view = cfg.getTemplate(ftlName);
		return view;
	}

}
