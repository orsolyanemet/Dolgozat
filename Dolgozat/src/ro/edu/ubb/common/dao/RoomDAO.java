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

	void createRoom(Room room);
	
	Room findById(String idRoom);
	
	Room findRoomByName(String roomName);
	
	Room findRoom(String roomName, String location);

	String createCheck(Room room);

	boolean updateRoom(Room room);
	
	boolean updateRoomAttribute(Room room);

	boolean deleteRoom(String idRoom);
}
