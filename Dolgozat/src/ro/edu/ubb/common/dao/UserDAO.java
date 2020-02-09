package ro.edu.ubb.common.dao;

import java.util.List;

import ro.edu.ubb.entity.RoleType;
import ro.edu.ubb.entity.User;

/**
 * DAO interface for user.
 * 
 * @author Nemet Orsolya
 *
 */
public interface UserDAO {
	List<User> getAllUsers();
	
	User findById(String idUser);

	User findByUsername(String username);

	User findByEmail(String email);

	RoleType findUserRole(String username);

	void createUser(User user);

	String createCheck(User user);

	boolean updateUser(User user);

	boolean deleteUser(String idUser);

	boolean validateUser(User user);
	
	boolean changePdUser(String username, String currentPd, String newPd);
}
