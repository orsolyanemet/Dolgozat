package ro.edu.ubb.dao;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;

import ro.edu.ubb.common.dao.RoomDAO;
import ro.edu.ubb.entity.Room;
import ro.edu.ubb.entity.RoomType;
import ro.edu.ubb.util.MongoConnectionManager;

/**
 * Implementation of RoomDAO.
 * 
 * @author Nemet Orsolya
 *
 */
public class JdbcRoomDAO implements RoomDAO {
	
	private MongoClient connection;
	private MongoCollection<Document> collection;

	public JdbcRoomDAO() {
		MongoConnectionManager.getInstance();
		connection = MongoConnectionManager.getConnection();
		collection = MongoConnectionManager
				.getCollection(MongoConnectionManager.getDatabase(connection), "Room");
	}

	@Override
	public List<Room> getAllRooms() {
		List<Room> rooms = new ArrayList<>();
		List<Document> roomsFromDB = (List<Document>) collection.find().into(new ArrayList<Document>());
		for (Document roomFromDB : roomsFromDB) {
			Room room = new Room();
			List<RoomType> roomTypeList = new ArrayList<>();
			room.setIdRoom(roomFromDB.getObjectId("_id").toString());
			room.setRoomName(roomFromDB.getString("roomName"));
			room.setLocation(roomFromDB.getString("location"));
			List<ObjectId> roomTypesToFromDB = (List<ObjectId>) roomFromDB.get("roomTypeList");
			if (roomTypesToFromDB!=null) {
				for (ObjectId roomType : roomTypesToFromDB) {
					MongoCollection<Document> roomTypeCollection = MongoConnectionManager
							.getCollection(MongoConnectionManager.getDatabase(connection), "RoomType");
					BasicDBObject whereQuery = new BasicDBObject();
					whereQuery.put("_id", roomType);
					FindIterable<Document> document = roomTypeCollection.find(whereQuery);
					RoomType typeRoom = new RoomType();
					typeRoom.setIdRoomType(roomType.toString());
					typeRoom.setRoomTypeName(document.first().get("roomTypeName").toString());
					roomTypeList.add(typeRoom);
				}
			}
			room.setRoomTypeList(roomTypeList);
			rooms.add(room);
		}
		return rooms;
	}

	@Override
	public Room createRoom(Room roomType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String createCheck(Room room) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateRoom(Room room) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean deleteRoom(String idRoom) {
		MongoCollection<Document> reservationCollection = MongoConnectionManager
				.getCollection(MongoConnectionManager.getDatabase(connection), "Reservation");
		BasicDBObject theQuery = new BasicDBObject();
		theQuery.put("idRoom", new ObjectId(idRoom));
		reservationCollection.deleteMany(theQuery);
		theQuery = new BasicDBObject();
		theQuery.put("_id", new ObjectId(idRoom));
		return collection.deleteOne(theQuery).getDeletedCount()==1;
	}

}
