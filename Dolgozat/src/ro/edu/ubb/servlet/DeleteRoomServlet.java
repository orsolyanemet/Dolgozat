package ro.edu.ubb.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ro.edu.ubb.entity.RoleType;
import ro.edu.ubb.service.RoomService;
import ro.edu.ubb.service.ServiceException;
import ro.edu.ubb.service.UserService;

/**
 * Servlet for delete room.
 * 
 * @author Nemet Orsolya
 *
 */
@WebServlet("/deleteroom.do")
public class DeleteRoomServlet extends HttpServlet {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private RoomService roomService=new RoomService();
	private UserService userService=new UserService();
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		RoleType roleType=userService.findUserRole(req.getSession().getAttribute("loggedUsername").toString());
		if(roleType==RoleType.ADMINISTRATOR) {
			dispatch("deleteroom.jsp", req, res);
		}
		else {
			dispatch("error.jsp",req,res);
		}
	}

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		try {
			PrintWriter out = res.getWriter();
			if(roomService.deleteRoom(req.getParameter("idToDelete"))) {
				out.println("{\"respons\": \"" + "OK" + "\"}");
			}
			else {
				out.println("{\"respons\": \"" + "ERROR" + "\"}");
			}
			out.flush();
		}
		catch(ServiceException e) {
			dispatch("error.jsp",req,res);
		}
	}

	public void dispatch(String jsp, HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		if (jsp != null) {
			RequestDispatcher rd = req.getRequestDispatcher(jsp);
			rd.forward(req, res);
		}
	}
}
