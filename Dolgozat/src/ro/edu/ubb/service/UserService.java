package ro.edu.ubb.service;

import java.util.List;

import ro.edu.ubb.common.dao.DAOFactory;
import ro.edu.ubb.common.dao.UserDAO;
import ro.edu.ubb.dao.DAOException;
import ro.edu.ubb.entity.RoleType;
import ro.edu.ubb.entity.User;


/**
 * Service for user.
 * 
 * @author Nemet Orsolya
 *
 */
public class UserService {

	private UserDAO userDAO;
	private DAOFactory daoFactory;

	public UserService() {
		daoFactory = DAOFactory.getInstance();
		userDAO = daoFactory.getUserDAO();
	}

	public User createUser(User user) {
		try {
			return userDAO.createUser(user);
		} catch (DAOException e) {
			throw new ServiceException("Insert user failed.");
		}
	}
	
	public String createCheck(User user) {
		try {
			return userDAO.createCheck(user);
		} catch (DAOException e) {
			throw new ServiceException("Create check user failed.");
		}
	}

	public void updateUser(User user) {
		try {
			userDAO.updateUser(user);
		} catch (DAOException e) {
			throw new ServiceException("Update user failed.");
		}
	}

	public boolean deleteUser(Integer idUser) {
		try {
			return userDAO.deleteUser(idUser);
		} catch (DAOException e) {
			throw new ServiceException("Delete user failed.");
		}
	}

	public boolean validateUser(User user) {
		try {
			return userDAO.validateUser(user);
		} catch (DAOException e) {
			throw new ServiceException("Validate user failed.");
		}
	}

	public User findByUsername(String username) {
		try {
			return userDAO.findByUsername(username);
		} catch (DAOException e) {
			throw new ServiceException("Finding user by username failed.");
		}
	}
	
	public User findByEmail(String email) {
		try {
			return userDAO.findByEmail(email);
		} catch (DAOException e) {
			throw new ServiceException("Finding user by email failed.");
		}
	}
	
	public RoleType findUserRole(String email) {
		try {
			return userDAO.findUserRole(email);
		} catch (DAOException e) {
			throw new ServiceException("Finding role by username failed.");
		}
	}

	public List<User> getAllUsers() {
		try {
			return userDAO.getAllUsers();
		} catch (DAOException e) {
			throw new ServiceException("Getting all users failed.");
		}
	}
}
