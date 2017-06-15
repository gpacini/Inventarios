package com.serinse.pers.dao.adm;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;

import com.serinse.pers.dao.DAOJPABase;
import com.serinse.pers.entity.adm.Permission;
import com.serinse.pers.entity.adm.PermissionEnum;

@Stateless
@LocalBean
public class DAOJPAPermission extends DAOJPABase<Permission, Long>{

	public DAOJPAPermission() {
		super(Permission.class);
	}
	
	@SuppressWarnings("unchecked")
	public List<Permission> getPermissionsByRole(String rolename){
		String sql = "SELECT p1 from Permission p1 where p1.role.name =:role";
		Query query = this.em.createQuery(sql);
		query.setParameter("role", rolename);
		return query.getResultList();
	}
	
	public boolean permissionByRoleExists(PermissionEnum type, String path, String role){
		String sql = "SELECT p1 from Permission p1 where p1.role.name = :role AND p1.type = :type AND p1.path = :path";
		Query query = this.em.createQuery(sql);
		query.setParameter("role", role);
		query.setParameter("type", type);
		query.setParameter("path", path);
		return query.getResultList().size() > 0;
	}

}
