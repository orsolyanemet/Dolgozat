package ro.edu.ubb.service;

import java.util.List;

import ro.edu.ubb.common.dao.DAOFactory;
import ro.edu.ubb.common.dao.RoomTypeDAO;
import ro.edu.ubb.dao.DAOException;
import ro.edu.ubb.entity.RoomType;
import ro.edu.ubb.entity.User;

/**
 * Service for room type.
 * 
 * @author Nemet Orsolya
 *
 */
public class RoomTypeService {

	private RoomTypeDAO roomTypeDAO;
	private DAOFactory daoFactory;

	public RoomTypeService() {
		daoFactory = DAOFactory.getInstance();
		roomTypeDAO = daoFactory.getRoomTypeDAO();
	}
	
	public RoomType findRoomTypeByName(String roomTypeName) {
		try {
			return roomTypeDAO.findRoomTypeByName(roomTypeName);
		} catch (DAOException e) {
			throw new ServiceException("Finding room type by name failed.");
		}
	}

	public void createRoomType(RoomType roomType) {
		try {
			roomTypeDAO.createRoomType(roomType);
		} catch (DAOException e) {
			throw new ServiceException("Insert room type failed.");
		}
	}

	public String createCheck(RoomType roomType) {
		try {
			return roomTypeDAO.createCheck(roomType);
		} catch (DAOException e) {
			throw new ServiceException("Create check room type failed.");
		}
	}

	public boolean updateRoomType(RoomType roomType) {
		try {
			return roomTypeDAO.updateRoomType(roomType);
		} catch (DAOException e) {
			throw new ServiceException("Update room type failed.");
		}
	}

	public boolean deleteRoomType(String idRoomType) {
		try {
			return roomTypeDAO.deleteRoomType(idRoomType);
		} catch (DAOException e) {
			throw new ServiceException("Delete room type failed.");
		}
	}

	public List<RoomType> getAllRoomTypes() {
		try {
			return roomTypeDAO.getAllRoomTypes();
		} catch (DAOException e) {
			throw new ServiceException("Getting all room types failed.");
		}
	}
}
