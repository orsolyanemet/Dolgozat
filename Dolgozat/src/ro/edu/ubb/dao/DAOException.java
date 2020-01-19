package ro.edu.ubb.dao;

/**
 * Exception for dao errors.
 * 
 * @author Nemet Orsolya
 *
 */
public class DAOException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public DAOException() {
		super();
	}
	
	public DAOException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    public DAOException(String arg0) {
        super(arg0);
    }
}
