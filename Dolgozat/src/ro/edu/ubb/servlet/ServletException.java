package ro.edu.ubb.servlet;

/**
 * Exception for servlet errors.
 *  
 * @author Nemet Orsolya
 *
 */
public class ServletException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public ServletException() {
		super();
	}
	
	public ServletException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    public ServletException(String arg0) {
        super(arg0);
    }
}
