package eu.ibutler.affiliatenetwork.file;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to mark implementations of FileValidator that
 * used during downloading of files by MultiPart downloader
 * @author Anton Lukashchuk
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface FileExtension {
	
	//Extension value in ".xxx" format
	public String value();

}
