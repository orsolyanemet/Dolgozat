package ro.edu.ubb.common.dao;

import java.util.List;

import org.bson.types.ObjectId;

import ro.edu.ubb.entity.RoomType;

/**
 * DAO interface for room type.
 * 
 * @author Nemet Orsolya
 *
 */
public interface RoomTypeDAO {
	List<RoomType> getAllRoomTypes();

	void createRoomType(RoomType roomType);
	
	RoomType findRoomTypeByName(String roomTypeName);
	
	RoomType findEquivalentById(ObjectId idEquivalent);

	String createCheck(RoomType roomType);

	boolean updateRoomType(RoomType roomType);

	boolean deleteRoomType(String idRoomType);
}

