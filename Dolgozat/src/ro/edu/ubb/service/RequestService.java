package ro.edu.ubb.service;

import java.util.List;

import ro.edu.ubb.common.dao.DAOFactory;
import ro.edu.ubb.common.dao.RequestDAO;
import ro.edu.ubb.dao.DAOException;
import ro.edu.ubb.entity.Request;

/**
 * Service for request.
 * 
 * @author Nemet Orsolya
 *
 */
public class RequestService {

	private RequestDAO requestDAO;
	private DAOFactory daoFactory;

	public RequestService() {
		daoFactory = DAOFactory.getInstance();
		requestDAO = daoFactory.getRequestDAO();
	}

	public Request createRequest(Request request) {
		try {
			return requestDAO.createRequest(request);
		} catch (DAOException e) {
			throw new ServiceException("Insert request failed.");
		}
	}

	public List<Request> getAllRequests() {
		try {
			return requestDAO.getAllRequests();
		} catch (DAOException e) {
			throw new ServiceException("Getting all requests failed.");
		}
	}
}
