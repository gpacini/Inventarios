package com.serinse.web.filters;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.serinse.ejb.impl.administration.RoleBean;
import com.serinse.pers.entity.adm.Role;
import com.serinse.web.session.UserSessionBean;

public class PermissionFilter implements Filter{

	@Inject UserSessionBean userSessionBean;
	@Inject RoleBean roleBean;
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		HttpServletResponse res = (HttpServletResponse) response;
		HttpServletRequest req = (HttpServletRequest) request;
		
		
		String path = ((HttpServletRequest) request).getRequestURI();
		
		if( path.contains(".xhtml") ) 
			res.sendRedirect("/serinse/error/permissionDenied.jsf");
		if( path.contains("error/permissionDenied.jsf") || path.contains("login.jsf") || path.contains("/images/") || path.contains("/base.js")){
			chain.doFilter(request, response);
			return;
		}
		
		List<Role> roles = roleBean.findAllById();
		Collections.sort(roles);
		
		if( userSessionBean.hasRole(roles.get(0).getRoleName()) || userSessionBean.hasPermission(path)) {
			chain.doFilter(request, response);
		} else {
			res.sendRedirect("/serinse/error/permissionDenied.jsf");
			return;
		}
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

}
