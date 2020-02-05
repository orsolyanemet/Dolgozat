package ro.edu.ubb.servlet;

import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet for logout.
 * 
 * @author Nemet Orsolya
 *
 */
@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -663150891209934794L;

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		try {
			doPost(req,res);
		} catch (Exception ex) {
			dispatch("/error.jsp", req, res);
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		try {
			res.setContentType("text/html");
			req.getSession().setAttribute("msgIncorrectData", "");
			req.getSession().setAttribute("msgPwdNotGiven", "");
			req.getSession().setAttribute("msgUsernameNotGiven", "");
			req.getSession().setAttribute("loggedUsername", "");
			req.getSession().setAttribute("AuthenticatedAdmin", null);
			req.getSession().setAttribute("AuthenticatedUser", null);
			req.getSession().setAttribute("accountType", "");
			dispatch("/login.jsp", req, res);
		} catch (Exception ex) {
			dispatch("/error.jsp", req, res);
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
