package com.serinse.ejb.impl.inventory;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.primefaces.model.SortOrder;

import com.serinse.ejb.AbstractBean;
import com.serinse.pers.dao.inventory.DAOJPARequisition;
import com.serinse.pers.entity.inventory.Delivery;
import com.serinse.pers.entity.inventory.Requisition;

@Stateless
@LocalBean
public class RequisitionBean extends AbstractBean<Requisition>{
	
	@EJB
	private DAOJPARequisition daojpaRequisition;

	@PostConstruct
	public void init(){
		super.init(daojpaRequisition);
	}
	
	public Requisition findByCode(String consecutive){
		return daojpaRequisition.findByConsecutive(consecutive);
	}
	
	public Requisition findByPhysicalRequisition(String code){
		return daojpaRequisition.findByPhysicalRequisition(code);
	}
	
	public int count(Map<String, String> filters) {
        return daojpaRequisition.count(filters);
    }

    public List<Delivery> getResultList(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
    	return daojpaRequisition.getResultList(first, pageSize, sortField, sortOrder, filters);
    }

}
