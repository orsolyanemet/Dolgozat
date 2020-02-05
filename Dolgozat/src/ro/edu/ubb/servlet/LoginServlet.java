package ro.edu.ubb.servlet;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ro.edu.ubb.entity.RoleType;
import ro.edu.ubb.entity.User;
import ro.edu.ubb.service.UserService;

/**
 * Servlet for login.
 * 
 * @author Nemet Orsolya
 *
 */
public class LoginServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -663150891209934794L;
	private static final String MSGINCORRECTDATA="msgIncorrectData";
	private static UserService userService = new UserService();
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		req.getSession().setAttribute(MSGINCORRECTDATA, "");
		req.getSession().setAttribute("msgPwdNotGiven", "");
		req.getSession().setAttribute("msgUsernameNotGiven", "");
		req.getSession().setAttribute("loggedUsername", "");
		req.getSession().setAttribute("AuthenticatedAdmin","");
		req.getSession().setAttribute("AuthenticatedUser","");
		req.getSession().setAttribute("accountType","");
	}

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException,IOException {
		String username = req.getParameter("username");
		String pdUser = req.getParameter("pdUser");
		User user = new User();
		user.setUsername(username);
		user.setPdUser(pdUser);
		req.getSession().setAttribute(MSGINCORRECTDATA, "");
		if (userService.validateUser(user)) {
			req.getSession().setAttribute(MSGINCORRECTDATA, "");
			req.getSession().setAttribute("loggedUsername", user.getUsername());
			RoleType roleType=userService.findUserRole(user.getUsername());
			if(roleType==RoleType.ADMINISTRATOR) {
				req.getSession().setAttribute("AuthenticatedAdmin",user);
				req.getSession().setAttribute("accountType", "Administrator");
				dispatch("roomschedule.jsp", req, res);
			}
			else if(roleType==RoleType.USER){
				req.getSession().setAttribute("AuthenticatedUser",user);
				req.getSession().setAttribute("accountType", "User");
				dispatch("roomschedule.jsp", req, res);
			}else {
				dispatch("error.jsp", req, res);
			}
		} else {
			req.getSession().setAttribute(MSGINCORRECTDATA, "Incorrect username or password!");
			dispatch("login.jsp", req, res);
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
