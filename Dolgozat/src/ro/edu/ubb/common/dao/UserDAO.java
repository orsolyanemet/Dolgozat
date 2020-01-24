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

	User findByUsername(String username);

	User findByEmail(String email);

	RoleType findUserRole(String username);

	User createUser(User user);

	String createCheck(User user);

	void updateUser(User user);

	boolean deleteUser(Integer idUser);

	boolean validateUser(User user);
}
