package eu.ibutler.affiliatenetwork.validation;

import java.lang.annotation.Annotation;
import java.util.Set;

import org.apache.log4j.Logger;
import org.reflections.Reflections;

/**
 * Class contains utility methods for file validation package
 * @author Anton Lukashchuk
 *
 */
public class ValidationUtils {
	
	private static Logger logger = Logger.getLogger(ValidationUtils.class.getName());
	
	private ValidationUtils(){}
	
	/**
	 * Search through all FileValidator implementations
	 * for someone who can process file of given extension.
	 * @param extension
	 * @return FileValidator implementation for given extension or null if not found
	 */
	public static FileValidator getValidator(String extension) {
		FileValidator result=null;
		Reflections reflections = new Reflections("eu.ibutler.affiliatenetwork.validation");
		Set<Class<?>> validators = reflections.getTypesAnnotatedWith(FileExtension.class);
		for(Class<?> validator : validators) {
			//get extension supported by this validator
			Annotation note = validator.getAnnotation(FileExtension.class);
			String ext = ((FileExtension) note).value();
			if(ext.equals(extension)) {
				//get instance of concrete validator class
				try {
					result = (FileValidator) validator.newInstance();
				} catch (InstantiationException | IllegalAccessException ignore) {
					logger.error("Failed to instantiate FileValidator " + ignore.getClass().getName() + " " + ignore.getMessage());
				}
				break; 
			}
		}
		return result;
	}


}
