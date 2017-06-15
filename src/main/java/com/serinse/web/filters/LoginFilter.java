package com.serinse.web.filters;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.serinse.web.session.UserSessionBean;

public class LoginFilter implements Filter {

	@Inject UserSessionBean userSessionBean;
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		HttpServletResponse res = (HttpServletResponse) response;
		HttpServletRequest req = (HttpServletRequest) request;
		
		String path = ((HttpServletRequest) request).getRequestURI();
		if( path.contains(".xhtml") ) {
			res.sendRedirect("/serinse/error/permissionDenied.jsf");
			return;
		}
		if( path.contains("login.jsf") || path.contains("rest/authentication") || path.contains("/images/") || path.contains("/base.js")){
			chain.doFilter(request, response);
			return;
		}
		
		if( !userSessionBean.isUserInSession() ){
			String queryString = req.getQueryString();
			String url = req.getRequestURL().toString();
			url += queryString != null ? "?" + queryString : "";
			if( url.contains(".jsf") && !url.contains("javax.faces.resource") ){
				userSessionBean.redirectLink(url);
			}
			res.sendRedirect("/serinse/login.jsf");
			return;
		}
		
		chain.doFilter(request, response);
		
	}

	@Override
	public void destroy() {
	}

	
	
}
