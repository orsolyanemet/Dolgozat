package ro.edu.ubb.service;

import java.util.List;

import ro.edu.ubb.common.dao.DAOFactory;
import ro.edu.ubb.common.dao.RoomDAO;
import ro.edu.ubb.dao.DAOException;
import ro.edu.ubb.entity.Room;

/**
 * Service for room.
 * 
 * @author Nemet Orsolya
 *
 */
public class RoomService {

	private RoomDAO roomDAO;
	private DAOFactory daoFactory;

	public RoomService() {
		daoFactory = DAOFactory.getInstance();
		roomDAO = daoFactory.getRoomDAO();
	}

	public Room createRoom(Room room) {
		try {
			return roomDAO.createRoom(room);
		} catch (DAOException e) {
			throw new ServiceException("Insert room failed.");
		}
	}

	public String createCheck(Room room) {
		try {
			return roomDAO.createCheck(room);
		} catch (DAOException e) {
			throw new ServiceException("Create check room failed.");
		}
	}

	public void updateRoom(Room room) {
		try {
			roomDAO.updateRoom(room);
		} catch (DAOException e) {
			throw new ServiceException("Update room failed.");
		}
	}

	public boolean deleteRoom(Integer idRoom) {
		try {
			return roomDAO.deleteRoom(idRoom);
		} catch (DAOException e) {
			throw new ServiceException("Delete room failed.");
		}
	}

	public List<Room> getAllRooms() {
		try {
			return roomDAO.getAllRooms();
		} catch (DAOException e) {
			throw new ServiceException("Getting all rooms failed.");
		}
	}
}

