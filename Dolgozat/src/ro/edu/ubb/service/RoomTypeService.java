package ro.edu.ubb.service;

import java.util.List;

import ro.edu.ubb.common.dao.DAOFactory;
import ro.edu.ubb.common.dao.RoomTypeDAO;
import ro.edu.ubb.dao.DAOException;
import ro.edu.ubb.entity.RoomType;

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

	public RoomType createRoomType(RoomType roomType) {
		try {
			return roomTypeDAO.createRoomType(roomType);
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

	public void updateRoomType(RoomType roomType) {
		try {
			roomTypeDAO.updateRoomType(roomType);
		} catch (DAOException e) {
			throw new ServiceException("Update room type failed.");
		}
	}

	public boolean deleteRoomType(Integer idRoomType) {
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
