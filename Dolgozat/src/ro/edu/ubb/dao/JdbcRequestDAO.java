package ro.edu.ubb.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.Document;

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
	
	public JdbcRequestDAO() {
		MongoConnectionManager.getInstance();
	}

	@Override
	public List<Request> getAllRequests() {
		List<Request> requests= new ArrayList<>();
		MongoClient connection = MongoConnectionManager.getConnection();
		MongoCollection<Document> collection = MongoConnectionManager
				.getCollection(MongoConnectionManager.getDatabase(connection), "Reservation");
		List<Document> requestsFromDB = (List<Document>) collection.find().into(new ArrayList<Document>());
		for (Document requestFromDB : requestsFromDB) {
			Request request = new Request();
			request.setReservationName(requestFromDB.getString("reservationName"));
			List<Date> timesFromDB = (List<Date>) requestFromDB.get("time");
			request.setFromTime(timesFromDB.get(0));
			request.setToTime(timesFromDB.get(1));
			request.setDuration(requestFromDB.getInteger("duration")); 
			request.setReservationType(requestFromDB.getString("reservationType"));
			Room room=new Room();
			room.setIdRoom(requestFromDB.getObjectId("idRoom").toString());
			MongoCollection<Document> roomCollection = MongoConnectionManager
					.getCollection(MongoConnectionManager.getDatabase(connection), "Room");
			BasicDBObject whereQuery = new BasicDBObject();
			whereQuery.put("_id",requestFromDB.getObjectId("idRoom"));
			FindIterable<Document> document = roomCollection.find(whereQuery);
			room.setRoomName((document.first().get("roomName")).toString());
			request.setRoom(room);
			User user=new User();
			user.setIdUser(requestFromDB.getObjectId("idUser").toString());
			MongoCollection<Document> userCollection = MongoConnectionManager
					.getCollection(MongoConnectionManager.getDatabase(connection), "User");
			whereQuery = new BasicDBObject();
			whereQuery.put("_id",requestFromDB.getObjectId("idUser"));
			document = userCollection.find(whereQuery);
			user.setFirstName((document.first().get("firstName")).toString());
			user.setLastName((document.first().get("lastName")).toString());
			request.setUser(user);
			requests.add(request);
		}
		return requests;
	}

	@Override
	public Request createRequest(Request request) {
		// TODO Auto-generated method stub
		return null;
	}

}
