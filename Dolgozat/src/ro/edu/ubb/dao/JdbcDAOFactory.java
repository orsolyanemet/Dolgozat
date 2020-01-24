package ro.edu.ubb.dao;

import ro.edu.ubb.common.dao.DAOFactory;
import ro.edu.ubb.common.dao.RequestDAO;
import ro.edu.ubb.common.dao.RoomDAO;
import ro.edu.ubb.common.dao.RoomTypeDAO;
import ro.edu.ubb.common.dao.UserDAO;

/**
 * Extension of DAOFactory.
 * 
 * @author Nemet Orsolya
 *
 */
public class JdbcDAOFactory extends DAOFactory {

	@Override
	public UserDAO getUserDAO() {
		return new JdbcUserDAO();
	}

	@Override
	public RequestDAO getRequestDAO() {
		return new JdbcRequestDAO();
	}

	@Override
	public RoomTypeDAO getRoomTypeDAO() {
		return new JdbcRoomTypeDAO();
	}
	
	@Override
	public RoomDAO getRoomDAO() {
		return new JdbcRoomDAO();
	}

}
