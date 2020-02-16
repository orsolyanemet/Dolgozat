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

import ro.edu.ubb.common.dao.UserDAO;
import ro.edu.ubb.entity.RoleType;
import ro.edu.ubb.entity.User;
import ro.edu.ubb.util.MongoConnectionManager;
import ro.edu.ubb.util.SecureData;

/**
 * Implementation of UserDAO.
 * 
 * @author Nemet Orsolya
 *
 */
public class JdbcUserDAO implements UserDAO {

	private static final String USERNAME = "username";
	private static final String FIRSTNAME = "firstName";
	private static final String LASTNAME = "lastName";
	private static final String EMAIL = "email";
	private static final String ROLETYPE = "roleType";
	private static final String PDUSER = "pdUser";
	private MongoClient connection;
	private MongoCollection<Document> collection;

	public JdbcUserDAO() {
		MongoConnectionManager.getInstance();
		connection = MongoConnectionManager.getConnection();
		collection = MongoConnectionManager.getCollection(MongoConnectionManager.getDatabase(connection), "User");
	}

	@Override
	public List<User> getAllUsers() {
		List<User> users = new ArrayList<>();
		List<Document> usersFromDB = (List<Document>) collection.find().into(new ArrayList<Document>());
		for (Document userFromDB : usersFromDB) {
			if (userFromDB.getString(ROLETYPE).equals("USER")) {
				User user = new User();
				user.setIdUser(userFromDB.getObjectId("_id").toString());
				user.setUsername(userFromDB.getString(USERNAME));
				user.setFirstName(userFromDB.getString(FIRSTNAME));
				user.setLastName(userFromDB.getString(LASTNAME));
				user.setEmail(userFromDB.getString(EMAIL));
				users.add(user);
			}
		}
		return users;
	}
	
	@Override
	public User findById(String idUser) {
		BasicDBObject whereQuery = new BasicDBObject();
		whereQuery.put("_id", new ObjectId(idUser));
		FindIterable<Document> document = collection.find(whereQuery);
		if (document.first() != null) {
			User user = new User();
			user.setIdUser(idUser);
			user.setUsername(document.first().getString(USERNAME));
			user.setFirstName(document.first().getString(FIRSTNAME));
			user.setLastName(document.first().getString(LASTNAME));
			user.setEmail(document.first().getString(EMAIL));
			user.setRoleType(RoleType.valueOf((document.first().get(ROLETYPE)).toString()));
			return user;
		}
		return null;
	}

	@Override
	public User findByUsername(String username) {
		BasicDBObject whereQuery = new BasicDBObject();
		whereQuery.put(USERNAME, username);
		FindIterable<Document> document = collection.find(whereQuery);
		if (document.first() != null) {
			User user = new User();
			user.setUsername(username);
			user.setFirstName(document.first().getString(FIRSTNAME));
			user.setLastName(document.first().getString(LASTNAME));
			user.setEmail(document.first().getString(EMAIL));
			user.setRoleType(RoleType.valueOf((document.first().get(ROLETYPE)).toString()));
			return user;
		}
		return null;
	}

	@Override
	public User findByEmail(String email) {
		BasicDBObject whereQuery = new BasicDBObject();
		whereQuery.put(EMAIL, email);
		FindIterable<Document> document = collection.find(whereQuery);
		if (document.first() != null) {
			User user = new User();
			user.setEmail(email);
			user.setFirstName(document.first().getString(FIRSTNAME));
			user.setLastName(document.first().getString(LASTNAME));
			user.setUsername(document.first().getString(USERNAME));
			user.setRoleType(RoleType.valueOf((document.first().get(ROLETYPE)).toString()));
			return user;
		}
		return null;
	}

	@Override
	public RoleType findUserRole(String username) {
		BasicDBObject whereQuery = new BasicDBObject();
		whereQuery.put(USERNAME, username);
		FindIterable<Document> document = collection.find(whereQuery);
		return RoleType.valueOf((document.first().get(ROLETYPE)).toString());
	}

	@Override
	public void createUser(User user) {
		Document addUser = new Document("_id", new ObjectId());
		addUser.append(USERNAME, user.getUsername()).append(FIRSTNAME, user.getFirstName())
				.append(LASTNAME, user.getLastName()).append(EMAIL, user.getEmail())
				.append(PDUSER, SecureData.convertHexToString(SecureData.hashPassword(user.getPdUser())))
				.append(ROLETYPE, user.getRoleType().toString());
		collection.insertOne(addUser);
	}

	@Override
	public String createCheck(User user) {
		createUser(user);
		if (findByUsername(user.getUsername())!= null) {
			return "OK";
		}
		return "NULL";
	}

	@Override
	public boolean updateUser(User user) {
		User userFromDatabase=findById(user.getIdUser());
		if(userFromDatabase.getLastName().equals(user.getLastName()) && userFromDatabase.getEmail().equals(user.getEmail())) {
			return true;
		}
		else {
		return collection
		.updateOne(Filters.eq("_id", new ObjectId(user.getIdUser())),
				new Document("$set",
						new Document(LASTNAME,user.getLastName()).append(EMAIL, user.getEmail()))).getModifiedCount()==1;
		}
	}

	@Override
	public boolean deleteUser(String idUser) {
		MongoCollection<Document> reservationCollection = MongoConnectionManager
				.getCollection(MongoConnectionManager.getDatabase(connection), "Reservation");
		BasicDBObject theQuery = new BasicDBObject();
		theQuery.put("idUser", new ObjectId(idUser));
		reservationCollection.deleteMany(theQuery);
		theQuery = new BasicDBObject();
		theQuery.put("_id", new ObjectId(idUser));
		return collection.deleteOne(theQuery).getDeletedCount() == 1;
	}

	@Override
	public boolean validateUser(User user) {
		BasicDBObject whereQuery = new BasicDBObject();
		whereQuery.put(USERNAME, user.getUsername());
		FindIterable<Document> document = collection.find(whereQuery);
		if (document.first() != null) {
			user.setRoleType(RoleType.valueOf((document.first().get(ROLETYPE).toString())));
			if (user.getRoleType().equals(RoleType.ADMINISTRATOR)) {
				return collection.find(
						Filters.and(Filters.eq(USERNAME, user.getUsername()), Filters.eq(PDUSER, user.getPdUser())))
						.first() != null;
			} else {
				if (user.getRoleType().equals(RoleType.USER)) {
					return collection
							.find(Filters.and(Filters.eq(USERNAME, user.getUsername()),
									Filters.eq(PDUSER,
											SecureData.convertHexToString(SecureData.hashPassword(user.getPdUser())))))
							.first() != null;
				}
			}
		}
		return false;
	}

	@Override
	public boolean changePdUser(String username, String currentPd, String newPd) {
		BasicDBObject whereQuery = new BasicDBObject();
		whereQuery.put(USERNAME, username);
		FindIterable<Document> document = collection.find(whereQuery);
		String pdUser = document.first().get(PDUSER).toString();
		if (pdUser.equals(SecureData.convertHexToString(SecureData.hashPassword(currentPd)))) {
			return collection
					.updateOne(Filters.eq(USERNAME, username),
							new Document("$set",
									new Document(PDUSER,
											SecureData.convertHexToString(SecureData.hashPassword(newPd)))))
					.getModifiedCount() == 1;
		}
		return false;
	}

}