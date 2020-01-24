package ro.edu.ubb.dao;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

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
	private static final String ROLETYPE = "roleType";

	public JdbcUserDAO() {
		MongoConnectionManager.getInstance();
	}

	@Override
	public List<User> getAllUsers() {
		List<User> users = new ArrayList<>();
		MongoClient connection = MongoConnectionManager.getConnection();
		MongoCollection<Document> collection = MongoConnectionManager
				.getCollection(MongoConnectionManager.getDatabase(connection), "User");
		List<Document> usersFromDB = (List<Document>) collection.find().into(new ArrayList<Document>());
		for (Document userFromDB : usersFromDB) {
			if (userFromDB.getString(ROLETYPE).equals("USER")) {
				User user = new User();
				user.setUsername(userFromDB.getString("username"));
				user.setFirstName(userFromDB.getString("firstName"));
				user.setLastName(userFromDB.getString("lastName"));
				user.setEmail(userFromDB.getString("email"));
				users.add(user);
			}
		}
		return users;
	}

	@Override
	public User findByUsername(String username) {
		return null;
	}

	@Override
	public User findByEmail(String email) {
		return null;
	}

	@Override
	public RoleType findUserRole(String username) {
		MongoClient connection = MongoConnectionManager.getConnection();
		MongoCollection<Document> collection = MongoConnectionManager
				.getCollection(MongoConnectionManager.getDatabase(connection), "User");
		BasicDBObject whereQuery = new BasicDBObject();
		whereQuery.put(USERNAME, username);
		FindIterable<Document> document = collection.find(whereQuery);
		return RoleType.valueOf((document.first().get(ROLETYPE)).toString());
	}

	@Override
	public User createUser(User user) {
		return null;
	}

	@Override
	public String createCheck(User user) {
		return null;
	}

	@Override
	public void updateUser(User user) {
		// Majd implementalni fogom
	}

	@Override
	public boolean deleteUser(Integer idUser) {
		return false;
	}

	@Override
	public boolean validateUser(User user) {
		MongoClient connection = MongoConnectionManager.getConnection();
		MongoCollection<Document> collection = MongoConnectionManager
				.getCollection(MongoConnectionManager.getDatabase(connection), "User");
		BasicDBObject whereQuery = new BasicDBObject();
		whereQuery.put(USERNAME, user.getUsername());
		FindIterable<Document> document = collection.find(whereQuery);
		if (document.first() != null) {
			user.setRoleType(RoleType.valueOf((document.first().get(ROLETYPE).toString())));
			if (user.getRoleType().equals(RoleType.ADMINISTRATOR)) {
				return collection.find(
						Filters.and(Filters.eq(USERNAME, user.getUsername()), Filters.eq("pdUser", user.getPdUser())))
						.first() != null;
			} else {
				if (user.getRoleType().equals(RoleType.USER)) {
					return collection
							.find(Filters.and(Filters.eq(USERNAME, user.getUsername()),
									Filters.eq("pdUser",
											SecureData.convertHexToString(SecureData.hashPassword(user.getPdUser())))))
							.first() != null;
				}
			}
		}
		return false;
	}

}