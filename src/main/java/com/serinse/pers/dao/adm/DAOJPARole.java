package com.serinse.pers.dao.adm;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import com.serinse.pers.dao.DAOJPABase;
import com.serinse.pers.entity.adm.Role;

@Stateless
@LocalBean
public class DAOJPARole extends DAOJPABase<Role, Long> {

	public DAOJPARole() {
		super(Role.class);
	}

}