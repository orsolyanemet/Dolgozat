package ro.edu.ubb.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;

import ro.edu.ubb.common.dao.RequestDAO;
import ro.edu.ubb.entity.Request;
import ro.edu.ubb.entity.Room;
import ro.edu.ubb.entity.User;
import ro.edu.ubb.util.MongoConnectionManager;

/**
 * Implementation of RequestDAO.
 * 
 * @author Nemet Orsolya
 *
 */
public class JdbcRequestDAO implements RequestDAO {
	
	private MongoClient connection;
	private MongoCollection<Document> collection;
	private static final String IDROOM="idRoom";
	private static final String IDUSER="idUser";
	
	public JdbcRequestDAO() {
		MongoConnectionManager.getInstance();
		connection = MongoConnectionManager.getConnection();
		collection = MongoConnectionManager
				.getCollection(MongoConnectionManager.getDatabase(connection), "Reservation");
	}

	@Override
	public List<Request> getAllRequests() {
		List<Request> requests= new ArrayList<>();
		List<Document> requestsFromDB = (List<Document>) collection.find().into(new ArrayList<Document>());
		for (Document requestFromDB : requestsFromDB) {
			Request request = new Request();
			request.setIdRequest(requestFromDB.getObjectId("_id").toString());
			request.setReservationName(requestFromDB.getString("reservationName"));
			List<Date> timesFromDB = (List<Date>) requestFromDB.get("time");
			request.setFromTime(timesFromDB.get(0));
			request.setToTime(timesFromDB.get(1));
			request.setDuration(requestFromDB.getInteger("duration")); 
			request.setReservationType(requestFromDB.getString("reservationType"));
			Room room=new Room();
			room.setIdRoom(requestFromDB.getObjectId(IDROOM).toString());
			MongoCollection<Document> roomCollection = MongoConnectionManager
					.getCollection(MongoConnectionManager.getDatabase(connection), "Room");
			BasicDBObject whereQuery = new BasicDBObject();
			whereQuery.put("_id",requestFromDB.getObjectId(IDROOM));
			FindIterable<Document> document = roomCollection.find(whereQuery);
			room.setRoomName((document.first().get("roomName")).toString());
			request.setRoom(room);
			User user=new User();
			user.setIdUser(requestFromDB.getObjectId(IDUSER).toString());
			MongoCollection<Document> userCollection = MongoConnectionManager
					.getCollection(MongoConnectionManager.getDatabase(connection), "User");
			whereQuery = new BasicDBObject();
			whereQuery.put("_id",requestFromDB.getObjectId(IDUSER));
			document = userCollection.find(whereQuery);
			user.setFirstName((document.first().get("firstName")).toString());
			user.setLastName((document.first().get("lastName")).toString());
			request.setUser(user);
			requests.add(request);
		}
		return requests;
	}
	
	@Override
	public List<Request> getUserRequests(String username) {
		MongoCollection<Document> userCollection = MongoConnectionManager
				.getCollection(MongoConnectionManager.getDatabase(connection), "User");
		BasicDBObject whereQuery = new BasicDBObject();
		whereQuery.put("username",username);
		FindIterable<Document> document = userCollection.find(whereQuery);
		String idUser=document.first().get("_id").toString();	
		List<Request> requests= new ArrayList<>();
		whereQuery = new BasicDBObject();
		whereQuery.put(IDUSER,new ObjectId(idUser));	
		List<Document> requestsFromDB = (List<Document>) collection.find(whereQuery).into(new ArrayList<Document>());
		for (Document requestFromDB : requestsFromDB) {
			Request request = new Request();
			request.setIdRequest(requestFromDB.getObjectId("_id").toString());
			request.setReservationName(requestFromDB.getString("reservationName"));
			List<Date> timesFromDB = (List<Date>) requestFromDB.get("time");
			request.setFromTime(timesFromDB.get(0));
			request.setToTime(timesFromDB.get(1));
			request.setDuration(requestFromDB.getInteger("duration")); 
			request.setReservationType(requestFromDB.getString("reservationType"));
			Room room=new Room();
			room.setIdRoom(requestFromDB.getObjectId(IDROOM).toString());
			MongoCollection<Document> roomCollection = MongoConnectionManager
					.getCollection(MongoConnectionManager.getDatabase(connection), "Room");
			whereQuery = new BasicDBObject();
			whereQuery.put("_id",requestFromDB.getObjectId(IDROOM));
			document = roomCollection.find(whereQuery);
			room.setRoomName((document.first().get("roomName")).toString());
			request.setRoom(room);
			requests.add(request);
		}
		return requests;
	}

	@Override
	public Request createRequest(Request request) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean deleteRequest(String idRequest) {
		BasicDBObject theQuery = new BasicDBObject();
		theQuery.put("_id", new ObjectId(idRequest));
		return collection.deleteOne(theQuery).getDeletedCount()==1;
	}

}
