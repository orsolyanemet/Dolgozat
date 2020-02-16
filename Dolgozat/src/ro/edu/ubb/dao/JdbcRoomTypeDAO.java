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
import ro.edu.ubb.entity.RoleType;
import ro.edu.ubb.entity.RoomType;
import ro.edu.ubb.entity.User;
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
	private static final String EQUIVALENTTO="equivalentTo";

	public JdbcRoomTypeDAO() {
		MongoConnectionManager.getInstance();
		connection = MongoConnectionManager.getConnection();
		collection = MongoConnectionManager
				.getCollection(MongoConnectionManager.getDatabase(connection), "RoomType");
	}

	@Override
	public List<RoomType> getAllRoomTypes() {
		List<RoomType> roomTypes = new ArrayList<>();
		List<Document> roomTypesFromDB = (List<Document>) collection.find().into(new ArrayList<Document>());
		for (Document roomTypeFromDB : roomTypesFromDB) {
			RoomType roomType = new RoomType();
			List<RoomType> equivalentTo = new ArrayList<>();
			roomType.setIdRoomType(roomTypeFromDB.getObjectId("_id").toString());
			roomType.setRoomTypeName(roomTypeFromDB.getString("roomTypeName"));
			List<ObjectId> equivalentToFromDB = (List<ObjectId>) roomTypeFromDB.get(EQUIVALENTTO);
			if (equivalentToFromDB!=null) {
				for (ObjectId equivalent : equivalentToFromDB) {
					equivalentTo.add(findEquivalentById(equivalent));
				}
			}
			roomType.setEquivalentTo(equivalentTo);
			roomTypes.add(roomType);
		}
		return roomTypes;
	}

	@Override
	public RoomType createRoomType(RoomType roomType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String createCheck(RoomType roomType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateRoomType(RoomType roomType) {
		// TODO Auto-generated method stub

	}
	
	public void deleteAttributeFromRooms(String idRoomType) {
		MongoCollection<Document> roomCollection = MongoConnectionManager
				.getCollection(MongoConnectionManager.getDatabase(connection), "Room");
		List<Document> roomsFromDB = (List<Document>) roomCollection.find().into(new ArrayList<Document>());
		for (Document roomFromDB : roomsFromDB) {
			List<ObjectId> roomTypeListFromDB = (List<ObjectId>) roomFromDB.get("roomTypeList");
			if (roomTypeListFromDB!=null) {
				List<ObjectId> toUpdate=new ArrayList<>();
				for (int i=0;i<roomTypeListFromDB.size();i++) {
					if(!roomTypeListFromDB.get(i).equals(new ObjectId(idRoomType))) {
						toUpdate.add(roomTypeListFromDB.get(i));					
					}
				}
				roomCollection
						.updateOne(Filters.eq("_id", roomFromDB.get("_id")),
								new Document("$set",
										new Document("roomTypeList",toUpdate)));
			}
		}
	}
	
	public void deleteFromEquivalentTo(String idRoomType) {
		List<Document> roomTypesFromDB = (List<Document>) collection.find().into(new ArrayList<Document>());
		for (Document roomTypeFromDB : roomTypesFromDB) {
			List<ObjectId> equivalentToFromDB = (List<ObjectId>) roomTypeFromDB.get(EQUIVALENTTO);
			if (equivalentToFromDB!=null) {
				List<ObjectId> toUpdate=new ArrayList<>();
				for (int i=0;i<equivalentToFromDB.size();i++) {
					if(!equivalentToFromDB.get(i).equals(new ObjectId(idRoomType))) {
						toUpdate.add(equivalentToFromDB.get(i));					
					}
				}
				collection
						.updateOne(Filters.eq("_id", roomTypeFromDB.get("_id")),
								new Document("$set",
										new Document(EQUIVALENTTO,toUpdate)));
			}
		}
	}

	@Override
	public boolean deleteRoomType(String idRoomType) {
		deleteAttributeFromRooms(idRoomType);
		deleteFromEquivalentTo(idRoomType);
		BasicDBObject query = new BasicDBObject();
		query.put("_id", new ObjectId(idRoomType));
		return collection.deleteOne(query).getDeletedCount()==1;
	}

	@Override
	public RoomType findRoomTypeByName(String roomTypeName) {
		BasicDBObject whereQuery = new BasicDBObject();
		whereQuery.put("roomTypeName", roomTypeName);
		FindIterable<Document> document = collection.find(whereQuery);
		if (document.first() != null) {
			List<RoomType> equivalentTo = new ArrayList<>();
			RoomType roomType = new RoomType();
			roomType.setIdRoomType(document.first().getObjectId("_id").toString());
			roomType.setRoomTypeName(roomTypeName);
			List<ObjectId> equivalentToFromDB = (List<ObjectId>) document.first().get(EQUIVALENTTO);
			if (equivalentToFromDB!=null) {
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
		equivalent.setRoomTypeName(document.first().get("roomTypeName").toString());
		return equivalent;
	}

}
