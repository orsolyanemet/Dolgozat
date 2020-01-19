package ro.edu.ubb.service;

/**
 * Exception for service errors.
 * 
 * @author Nemet Orsolya
 *
 */
public class ServiceException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public ServiceException() {
		super();
	}
	
	public ServiceException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    public ServiceException(String arg0) {
        super(arg0);
    }
}
