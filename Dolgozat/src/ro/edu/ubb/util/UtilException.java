package ro.edu.ubb.util;

/**
 * Exception for util errors.
 * 
 * @author Nemet Orsolya
 *
 */
public class UtilException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public UtilException() {
		super();
	}
	
	public UtilException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    public UtilException(String arg0) {
        super(arg0);
    }
}
