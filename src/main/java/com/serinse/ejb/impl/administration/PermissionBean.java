package com.serinse.ejb.impl.administration;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import com.serinse.ejb.AbstractBean;
import com.serinse.pers.dao.adm.DAOJPAPermission;
import com.serinse.pers.entity.adm.Permission;
import com.serinse.pers.entity.adm.Role;

@LocalBean
@Stateless
public class PermissionBean extends AbstractBean<Permission> implements Serializable{

	private static final long serialVersionUID = 12938612373L;
	
	@EJB DAOJPAPermission daojpaPermission;
	
	@PostConstruct
	public void init( ){
		super.init(daojpaPermission);
	}
	
	public List<Permission> getPermissionsByRole(String rolename){
		return daojpaPermission.getPermissionsByRole(rolename);
	}
	
	public boolean permissionByRoleExists(Permission permission, Role role){
		return daojpaPermission.permissionByRoleExists(permission.getType(), permission.getPath(), role.getRoleName());
	}
}
