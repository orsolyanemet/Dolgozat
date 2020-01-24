package ro.edu.ubb.common.dao;

import java.util.List;

import ro.edu.ubb.entity.Room;

/**
 * DAO interface for room.
 * 
 * @author Nemet Orsolya
 *
 */
public interface RoomDAO {
	List<Room> getAllRooms();

	Room createRoom(Room roomType);

	String createCheck(Room room);

	void updateRoom(Room room);

	boolean deleteRoom(Integer idRoom);
}
