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

import ro.edu.ubb.common.dao.RoomTypeDAO;
import ro.edu.ubb.entity.RoomType;
import ro.edu.ubb.util.MongoConnectionManager;

/**
 * Implementation of RoomTypeDAO.
 * 
 * @author Nemet Orsolya
 *
 */
public class JdbcRoomTypeDAO implements RoomTypeDAO {

	private MongoClient connection;
	private MongoCollection<Document> collection;
	private static final String EQUIVALENTTO = "equivalentTo";
	private static final String ROOMTYPENAME = "roomTypeName";

	public JdbcRoomTypeDAO() {
		MongoConnectionManager.getInstance();
		connection = MongoConnectionManager.getConnection();
		collection = MongoConnectionManager.getCollection(MongoConnectionManager.getDatabase(connection), "RoomType");
	}

	@Override
	public List<RoomType> getAllRoomTypes() {
		List<RoomType> roomTypes = new ArrayList<>();
		List<Document> roomTypesFromDB = (List<Document>) collection.find().into(new ArrayList<Document>());
		for (Document roomTypeFromDB : roomTypesFromDB) {
			RoomType roomType = new RoomType();
			List<RoomType> equivalentTo = new ArrayList<>();
			roomType.setIdRoomType(roomTypeFromDB.getObjectId("_id").toString());
			roomType.setRoomTypeName(roomTypeFromDB.getString(ROOMTYPENAME));
			List<ObjectId> equivalentToFromDB = (List<ObjectId>) roomTypeFromDB.get(EQUIVALENTTO);
			if (equivalentToFromDB != null) {
				for (ObjectId equivalent : equivalentToFromDB) {
					if(equivalent!=null) {
					equivalentTo.add(findEquivalentById(equivalent));
					}
				}
			}
			roomType.setEquivalentTo(equivalentTo);
			roomTypes.add(roomType);
		}
		return roomTypes;
	}

	@Override
	public void createRoomType(RoomType roomType) {
		List<ObjectId> equivalentRoomTypeList = new ArrayList<>();
		for (int i = 0; i < roomType.getEquivalentTo().size(); i++) {
			equivalentRoomTypeList.add(new ObjectId(
					findRoomTypeByName(roomType.getEquivalentTo().get(i).getRoomTypeName()).getIdRoomType()));
		}
		Document addRoomType = new Document("_id", new ObjectId());
		addRoomType.append(ROOMTYPENAME, roomType.getRoomTypeName());
		if (!equivalentRoomTypeList.isEmpty()) {
			addRoomType.append(EQUIVALENTTO, equivalentRoomTypeList);
		}
		collection.insertOne(addRoomType);
	}

	@Override
	public String createCheck(RoomType roomType) {
		createRoomType(roomType);
		if (findRoomTypeByName(roomType.getRoomTypeName()) != null) {
			return "OK";
		}
		return "NULL";
	}
	
	public List<ObjectId> findIdsOfAttributes(List<RoomType> equivalents) {
		List<ObjectId> equivalentAttributes=new ArrayList<>();
		for(RoomType equivalent: equivalents) {
			BasicDBObject query = new BasicDBObject();
			query.put("roomTypeName", equivalent.getRoomTypeName());
			FindIterable<Document> doc = collection.find(query);
			equivalentAttributes.add((ObjectId)doc.first().get("_id"));
		}
		return equivalentAttributes;
	}
	
	public List<String> getRoomTypeNames(List<RoomType> equivalents){
		List<String> equivalentRoomTypes=new ArrayList<>();
		for(RoomType equivalent: equivalents) {
			equivalentRoomTypes.add(findRoomTypeByName(equivalent.getRoomTypeName()).getRoomTypeName());
		}
		return equivalentRoomTypes;
	}

	@Override
	public boolean updateRoomType(RoomType roomType) {
		List<ObjectId> equivalentAttributes=findIdsOfAttributes(roomType.getEquivalentTo());
		List<String> currentEquivalentAttributes= getRoomTypeNames(findRoomTypeByName(roomType.getRoomTypeName()).getEquivalentTo());
		List<String> newEquivalentAttributes=getRoomTypeNames(roomType.getEquivalentTo());
		if(currentEquivalentAttributes.equals(newEquivalentAttributes)) {
			return true;
		}else {
		return collection
				.updateOne(Filters.eq(ROOMTYPENAME, roomType.getRoomTypeName()),
						new Document("$set", new Document(EQUIVALENTTO, equivalentAttributes)))
				.getModifiedCount() == 1;
		}
	}

	public void deleteAttributeFromRooms(String idRoomType) {
		MongoCollection<Document> roomCollection = MongoConnectionManager
				.getCollection(MongoConnectionManager.getDatabase(connection), "Room");
		List<Document> roomsFromDB = (List<Document>) roomCollection.find().into(new ArrayList<Document>());
		for (Document roomFromDB : roomsFromDB) {
			List<ObjectId> roomTypeListFromDB = (List<ObjectId>) roomFromDB.get("roomTypeList");
			if (roomTypeListFromDB != null) {
				List<ObjectId> toUpdate = new ArrayList<>();
				for (int i = 0; i < roomTypeListFromDB.size(); i++) {
					if (!roomTypeListFromDB.get(i).equals(new ObjectId(idRoomType))) {
						toUpdate.add(roomTypeListFromDB.get(i));
					}
				}
				roomCollection.updateOne(Filters.eq("_id", roomFromDB.get("_id")),
						new Document("$set", new Document("roomTypeList", toUpdate)));
			}
		}
	}

	public void deleteFromEquivalentTo(String idRoomType) {
		List<Document> roomTypesFromDB = (List<Document>) collection.find().into(new ArrayList<Document>());
		for (Document roomTypeFromDB : roomTypesFromDB) {
			List<ObjectId> equivalentToFromDB = (List<ObjectId>) roomTypeFromDB.get(EQUIVALENTTO);
			if (equivalentToFromDB != null) {
				List<ObjectId> toUpdate = new ArrayList<>();
				for (int i = 0; i < equivalentToFromDB.size(); i++) {
					if (!equivalentToFromDB.get(i).equals(new ObjectId(idRoomType))) {
						toUpdate.add(equivalentToFromDB.get(i));
					}
				}
				collection.updateOne(Filters.eq("_id", roomTypeFromDB.get("_id")),
						new Document("$set", new Document(EQUIVALENTTO, toUpdate)));
			}
		}
	}

	@Override
	public boolean deleteRoomType(String idRoomType) {
		deleteAttributeFromRooms(idRoomType);
		deleteFromEquivalentTo(idRoomType);
		BasicDBObject query = new BasicDBObject();
		query.put("_id", new ObjectId(idRoomType));
		return collection.deleteOne(query).getDeletedCount() == 1;
	}

	@Override
	public RoomType findRoomTypeByName(String roomTypeName) {
		BasicDBObject whereQuery = new BasicDBObject();
		whereQuery.put(ROOMTYPENAME, roomTypeName);
		FindIterable<Document> document = collection.find(whereQuery);
		if (document.first() != null) {
			List<RoomType> equivalentTo = new ArrayList<>();
			RoomType roomType = new RoomType();
			roomType.setIdRoomType(document.first().getObjectId("_id").toString());
			roomType.setRoomTypeName(roomTypeName);
			List<ObjectId> equivalentToFromDB = (List<ObjectId>) document.first().get(EQUIVALENTTO);
			if (equivalentToFromDB != null) {
				for (ObjectId equivalent : equivalentToFromDB) {
					equivalentTo.add(findEquivalentById(equivalent));
				}
				roomType.setEquivalentTo(equivalentTo);
			}
			return roomType;
		}
		return null;
	}

	@Override
	public RoomType findEquivalentById(ObjectId idEquivalent) {
		BasicDBObject whereQuery = new BasicDBObject();
		whereQuery.put("_id", idEquivalent);
		FindIterable<Document> document = collection.find(whereQuery);
		RoomType equivalent = new RoomType();
		equivalent.setIdRoomType(idEquivalent.toString());
		equivalent.setRoomTypeName(document.first().get(ROOMTYPENAME).toString());
		return equivalent;
	}

}
