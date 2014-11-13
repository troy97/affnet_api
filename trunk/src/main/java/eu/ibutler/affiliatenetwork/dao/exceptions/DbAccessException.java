package eu.ibutler.affiliatenetwork.dao.exceptions;

@SuppressWarnings("serial")
public class DbAccessException extends DaoException {
	public DbAccessException() {
		super();
	}
	public DbAccessException(String message, Throwable cause) {
		super(message, cause);
	}
	public DbAccessException(String message) {
		super(message);
	}
	public DbAccessException(Throwable cause) {
		super(cause);
	}
}
