package ro.edu.ubb.dao;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

import ro.edu.ubb.common.dao.RoomDAO;
import ro.edu.ubb.entity.Room;
import ro.edu.ubb.entity.RoomType;
import ro.edu.ubb.service.RoomTypeService;
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
	private static final String ROOMNAME = "roomName";
	private static final String LOCATION = "location";
	private static final String ROOMTYPE = "RoomType";
	private static final String ROOMTYPENAME = "roomTypeName";
	private static final String ROOMTYPELIST = "roomTypeList";

	public JdbcRoomDAO() {
		MongoConnectionManager.getInstance();
		connection = MongoConnectionManager.getConnection();
		collection = MongoConnectionManager.getCollection(MongoConnectionManager.getDatabase(connection), "Room");
	}

	@Override
	public List<Room> getAllRooms() {
		List<Room> rooms = new ArrayList<>();
		List<Document> roomsFromDB = (List<Document>) collection.find().into(new ArrayList<Document>());
		for (Document roomFromDB : roomsFromDB) {
			Room room = new Room();
			List<RoomType> roomTypeList = new ArrayList<>();
			room.setIdRoom(roomFromDB.getObjectId("_id").toString());
			room.setRoomName(roomFromDB.getString(ROOMNAME));
			room.setLocation(roomFromDB.getString(LOCATION));
			List<ObjectId> roomTypesToFromDB = (List<ObjectId>) roomFromDB.get(ROOMTYPELIST);
			if (roomTypesToFromDB != null) {
				for (ObjectId roomType : roomTypesToFromDB) {
					MongoCollection<Document> roomTypeCollection = MongoConnectionManager
							.getCollection(MongoConnectionManager.getDatabase(connection), ROOMTYPE);
					BasicDBObject whereQuery = new BasicDBObject();
					whereQuery.put("_id", roomType);
					FindIterable<Document> document = roomTypeCollection.find(whereQuery);
					RoomType typeRoom = new RoomType();
					typeRoom.setIdRoomType(roomType.toString());
					typeRoom.setRoomTypeName(document.first().get(ROOMTYPENAME).toString());
					roomTypeList.add(typeRoom);
				}
			}
			room.setRoomTypeList(roomTypeList);
			rooms.add(room);
		}
		return rooms;
	}

	@Override
	public Room findById(String idRoom) {
		BasicDBObject whereQuery = new BasicDBObject();
		whereQuery.put("_id", new ObjectId(idRoom));
		FindIterable<Document> document = collection.find(whereQuery);
		if (document.first() != null) {
			Room room = new Room();
			room.setIdRoom(idRoom);
			room.setRoomName(document.first().getString(ROOMNAME));
			room.setLocation(document.first().getString(LOCATION));
			List<RoomType> roomTypeList = new ArrayList<>();
			List<ObjectId> roomTypesToFromDB = (List<ObjectId>) document.first().get(ROOMTYPELIST);
			if (roomTypesToFromDB != null) {
				for (ObjectId roomType : roomTypesToFromDB) {
					MongoCollection<Document> roomTypeCollection = MongoConnectionManager
							.getCollection(MongoConnectionManager.getDatabase(connection), ROOMTYPE);
					BasicDBObject query = new BasicDBObject();
					query.put("_id", roomType);
					FindIterable<Document> doc = roomTypeCollection.find(query);
					RoomType typeRoom = new RoomType();
					typeRoom.setIdRoomType(roomType.toString());
					typeRoom.setRoomTypeName(doc.first().get(ROOMTYPENAME).toString());
					roomTypeList.add(typeRoom);
				}
			}
			room.setRoomTypeList(roomTypeList);
			return room;
		}
		return null;
	}
	
	@Override
	public Room findRoomByName(String roomName) {
		BasicDBObject whereQuery = new BasicDBObject();
		whereQuery.put(ROOMNAME, roomName);
		FindIterable<Document> document = collection.find(whereQuery);
		if (document.first() != null) {
			Room room = new Room();
			room.setIdRoom(document.first().get("_id").toString());
			room.setRoomName(roomName);
			room.setLocation(document.first().getString(LOCATION));
			List<RoomType> roomTypeList = new ArrayList<>();
			List<ObjectId> roomTypesToFromDB = (List<ObjectId>) document.first().get(ROOMTYPELIST);
			if (roomTypesToFromDB != null) {
				for (ObjectId roomType : roomTypesToFromDB) {
					MongoCollection<Document> roomTypeCollection = MongoConnectionManager
							.getCollection(MongoConnectionManager.getDatabase(connection), ROOMTYPE);
					BasicDBObject query = new BasicDBObject();
					query.put("_id", roomType);
					FindIterable<Document> doc = roomTypeCollection.find(query);
					RoomType typeRoom = new RoomType();
					typeRoom.setIdRoomType(roomType.toString());
					typeRoom.setRoomTypeName(doc.first().get(ROOMTYPENAME).toString());
					roomTypeList.add(typeRoom);
				}
			}
			room.setRoomTypeList(roomTypeList);
			return room;
		}
		return null;
	}

	@Override
	public void createRoom(Room room) {
		List<ObjectId> roomTypeList = new ArrayList<>();
		JdbcRoomTypeDAO roomTypeDAO = new JdbcRoomTypeDAO();
		for (int i = 0; i < room.getRoomTypeList().size(); i++) {
			roomTypeList.add(new ObjectId(
					roomTypeDAO.findRoomTypeByName(room.getRoomTypeList().get(i).getRoomTypeName()).getIdRoomType()));
		}
		Document addRoom = new Document("_id", new ObjectId());
		addRoom.append(ROOMNAME, room.getRoomName()).append(LOCATION, room.getLocation()).append(ROOMTYPELIST,
				roomTypeList);
		collection.insertOne(addRoom);
	}

	@Override
	public String createCheck(Room room) {
		createRoom(room);
		if (findRoom(room.getRoomName(), room.getLocation()) != null) {
			return "OK";
		}
		return "NULL";
	}

	@Override
	public boolean updateRoom(Room room) {
		Room roomFromDatabase = findById(room.getIdRoom());
		if (roomFromDatabase.getRoomName().equals(room.getRoomName())
				&& roomFromDatabase.getLocation().equals(room.getLocation())) {
			return true;
		} else {
			return collection
					.updateOne(Filters.eq("_id", new ObjectId(room.getIdRoom())),
							new Document("$set",
									new Document(ROOMNAME, room.getRoomName()).append(LOCATION, room.getLocation())))
					.getModifiedCount() == 1;
		}
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
		return collection.deleteOne(theQuery).getDeletedCount() == 1;
	}

	@Override
	public Room findRoom(String roomName, String location) {
		BasicDBObject whereQuery = new BasicDBObject();
		whereQuery.put(ROOMNAME, roomName);
		FindIterable<Document> document = collection.find(whereQuery);
		if (document.first() != null) {
			Room room = new Room();
			room.setIdRoom(document.first().getObjectId("_id").toString());
			room.setRoomName(roomName);
			room.setLocation(location);
			List<RoomType> roomTypeList = new ArrayList<>();
			List<ObjectId> roomTypesToFromDB = (List<ObjectId>) document.first().get(ROOMTYPELIST);
			if (roomTypesToFromDB != null) {
				for (ObjectId roomType : roomTypesToFromDB) {
					MongoCollection<Document> roomTypeCollection = MongoConnectionManager
							.getCollection(MongoConnectionManager.getDatabase(connection), ROOMTYPE);
					BasicDBObject query = new BasicDBObject();
					query.put("_id", roomType);
					FindIterable<Document> doc = roomTypeCollection.find(query);
					RoomType typeRoom = new RoomType();
					typeRoom.setIdRoomType(roomType.toString());
					typeRoom.setRoomTypeName(doc.first().get(ROOMTYPENAME).toString());
					roomTypeList.add(typeRoom);
				}
			}
			room.setRoomTypeList(roomTypeList);
			return room;
		}
		return null;
	}

	/*public List<ObjectId> findIdsOfAttributes(List<RoomType> attributes) {
		List<ObjectId> roomAttributes = new ArrayList<>();
		MongoCollection<Document> roomTypeCollection = MongoConnectionManager
				.getCollection(MongoConnectionManager.getDatabase(connection), ROOMTYPE);
		for (RoomType roomType : attributes) {
			BasicDBObject query = new BasicDBObject();
			query.put("roomTypeName", roomType.getRoomTypeName());
			FindIterable<Document> doc = roomTypeCollection.find(query);
			roomAttributes.add((ObjectId) doc.first().get("_id"));
		}
		return roomAttributes;
	}
	
	public List<String> getRoomTypeNames(List<RoomType> roomTypes){
		List<String> roomTypeNames=new ArrayList<>();
		for(RoomType roomType: roomTypes) {
			roomTypeNames.add(findRoomTypeByName(roomType.getRoomTypeName()).getRoomTypeName());
		}
		return roomTypeNames;
	}*/

	@Override
	public boolean updateRoomAttribute(Room room) {
		JdbcRoomTypeDAO roomTypeDAO=new JdbcRoomTypeDAO();
		List<ObjectId> roomAttributes = roomTypeDAO.findIdsOfAttributes(room.getRoomTypeList());
		List<String> currentRoomAttributes= roomTypeDAO.getRoomTypeNames(findRoomByName(room.getRoomName()).getRoomTypeList());
		List<String> newRoomAttributes=roomTypeDAO.getRoomTypeNames(room.getRoomTypeList());
		if (currentRoomAttributes.equals(newRoomAttributes)) {
			return true;
		} else {
			return collection.updateOne(Filters.eq(ROOMNAME, room.getRoomName()),
					new Document("$set", new Document(ROOMTYPELIST, roomAttributes))).getModifiedCount() == 1;
		}
	}

}
