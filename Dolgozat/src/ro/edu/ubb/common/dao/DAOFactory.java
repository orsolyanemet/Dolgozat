package ro.edu.ubb.common.dao;

import ro.edu.ubb.dao.JdbcDAOFactory;

/**
 * DAO factory abstract class.
 * 
 * @author Nemet Orsolya
 *
 */
public abstract class DAOFactory {

	public static DAOFactory getInstance() {
		return new JdbcDAOFactory();
	}

	public abstract UserDAO getUserDAO();

	public abstract RequestDAO getRequestDAO();

	public abstract RoomTypeDAO getRoomTypeDAO();

	public abstract RoomDAO getRoomDAO();

}
