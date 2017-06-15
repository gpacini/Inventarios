package com.serinse.pers.dao.inventory;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;

import com.serinse.pers.dao.DAOJPABase;
import com.serinse.pers.entity.client.Client;
import com.serinse.pers.entity.inventory.customRow.CustomRowByClient;

@Stateless
@LocalBean
public class DAOJPACustomRowByClient extends DAOJPABase<CustomRowByClient, Long> {

	public DAOJPACustomRowByClient() {
		super(CustomRowByClient.class);
	}
	
	public CustomRowByClient getCustomRowByRowIdAndClient(int row_id, Client client){
		String sql = "select t1 from CustomRowByClient t1 where t1.client.id =:id AND t1.rowId = :rowId";
		Query query = this.em.createQuery(sql);
		query.setParameter("id", client.getId());
		query.setParameter("rowId", row_id);
		try{
			return (CustomRowByClient) query.getSingleResult();
		} catch( Exception e){
			return null;
		}
		
	}
	
	public List<CustomRowByClient> findByClient(Client client){
		String sql = "select t1 from CustomRowByClient t1 where t1.client.id = :id";
		Query query = this.em.createQuery(sql);
		query.setParameter("id", client.getId());
		return query.getResultList();
	}
}
