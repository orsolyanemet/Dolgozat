package ro.edu.ubb.common.dao;

import java.util.List;

import ro.edu.ubb.entity.RoomType;

/**
 * DAO interface for room type.
 * 
 * @author Nemet Orsolya
 *
 */
public interface RoomTypeDAO {
	List<RoomType> getAllRoomTypes();

	RoomType createRoomType(RoomType roomType);

	String createCheck(RoomType roomType);

	void updateRoomType(RoomType roomType);

	boolean deleteRoomType(String idRoomType);
}

