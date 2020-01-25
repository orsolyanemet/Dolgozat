package ro.edu.ubb.dao;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;

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

	public JdbcRoomTypeDAO() {
		MongoConnectionManager.getInstance();
	}

	@Override
	public List<RoomType> getAllRoomTypes() {
		List<RoomType> roomTypes = new ArrayList<>();
		MongoClient connection = MongoConnectionManager.getConnection();
		MongoCollection<Document> collection = MongoConnectionManager
				.getCollection(MongoConnectionManager.getDatabase(connection), "RoomType");
		List<Document> roomTypesFromDB = (List<Document>) collection.find().into(new ArrayList<Document>());
		for (Document roomTypeFromDB : roomTypesFromDB) {
			RoomType roomType = new RoomType();
			List<RoomType> equivalentTo = new ArrayList<>();
			roomType.setIdRoomType(roomTypeFromDB.getObjectId("_id").toString());
			roomType.setRoomTypeName(roomTypeFromDB.getString("roomTypeName"));
			List<ObjectId> equivalentToFromDB = (List<ObjectId>) roomTypeFromDB.get("equivalentTo");
			if (equivalentToFromDB!=null) {
				for (ObjectId equivalent : equivalentToFromDB) {
					BasicDBObject whereQuery = new BasicDBObject();
					whereQuery.put("_id", equivalent);
					FindIterable<Document> document = collection.find(whereQuery);
					RoomType equivalentRoom = new RoomType();
					equivalentRoom.setIdRoomType(equivalent.toString());
					equivalentRoom.setRoomTypeName(document.first().get("roomTypeName").toString());
					equivalentTo.add(equivalentRoom);
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

	@Override
	public boolean deleteRoomType(String idRoomType) {
		// TODO Auto-generated method stub
		return false;
	}

}
