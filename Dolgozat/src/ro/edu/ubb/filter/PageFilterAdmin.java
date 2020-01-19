package ro.edu.ubb.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Filter for admin pages.
 * 
 * @author Nemet Orsolya
 *
 */
public class PageFilterAdmin implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
			throws IOException, ServletException {
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		HttpServletResponse httpServletResponse = (HttpServletResponse) response;
		Boolean auth = false;
		if (httpServletRequest.getSession().getAttribute("AuthenticatedAdmin") != null) {
			auth = true;
		}
		if (auth) {
			filterChain.doFilter(request, response);
		} else {
			httpServletResponse.sendRedirect(httpServletRequest.getContextPath() + "/login.jsp");
		}
	}

}
