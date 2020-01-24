package ro.edu.ubb.common.dao;

import java.util.List;

import ro.edu.ubb.entity.Request;

/**
 * DAO interface for request.
 * 
 * @author Nemet Orsolya
 *
 */
public interface RequestDAO {
	List<Request> getAllRequests();

	Request createRequest(Request request);
}
