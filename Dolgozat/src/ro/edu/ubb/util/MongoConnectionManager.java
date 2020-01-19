package ro.edu.ubb.util;
import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class MongoConnectionManager {
	private static final String HOST = "localhost";
	private static final Integer PORT = 27017;
	private static final String DATABASENAME = "Events";
	private static MongoConnectionManager cm;
	
	private MongoConnectionManager() {
		
	}
	
	public static MongoConnectionManager getInstance() {
		if (cm == null) {
			cm = new MongoConnectionManager();
		}
		return cm;
	}

	public static MongoClient getConnection() {
		return new MongoClient(HOST, PORT);
	}

	public static MongoDatabase getDatabase(MongoClient connection) {
		return connection.getDatabase(DATABASENAME);
	}

	public static MongoCollection<Document> getCollection(MongoDatabase db,String collectionName) {
		return db.getCollection(collectionName);
	}

}
