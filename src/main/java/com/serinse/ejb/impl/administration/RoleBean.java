package com.serinse.ejb.impl.administration;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import com.serinse.ejb.AbstractBean;
import com.serinse.pers.dao.adm.DAOJPARole;
import com.serinse.pers.entity.adm.Role;

@Stateless
@LocalBean
public class RoleBean extends AbstractBean<Role>{
	

	@EJB DAOJPARole daojpaRole;
	
	@PostConstruct
	public void init( ){
		super.init(daojpaRole);
	}
	
}
