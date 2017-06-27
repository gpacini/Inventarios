package com.serinse.ejb.impl.inventory;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.primefaces.model.SortOrder;

import com.serinse.ejb.AbstractBean;
import com.serinse.pers.dao.inventory.DAOJPAUpdateLog;
import com.serinse.pers.entity.inventory.UpdateLog;

@Stateless
@LocalBean
public class UpdateLogBean extends AbstractBean<UpdateLog> {

	@EJB
	private DAOJPAUpdateLog daojpaUpdateLog;
	
	@PostConstruct
	public void init(){
		super.init(daojpaUpdateLog);
	}

	public int count(Map<String, Object> filters) {
        return daojpaUpdateLog.count(filters);
    }

    public List<UpdateLog> getResultList(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
    	return daojpaUpdateLog.getResultList(first, pageSize, sortField, sortOrder, filters);
    }
}
