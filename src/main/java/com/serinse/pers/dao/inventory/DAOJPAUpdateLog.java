package com.serinse.pers.dao.inventory;

import java.util.List;
import java.util.Map;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.primefaces.model.SortOrder;

import com.serinse.pers.dao.DAOJPABase;
import com.serinse.pers.entity.inventory.UpdateLog;

@Stateless
@LocalBean
public class DAOJPAUpdateLog extends DAOJPABase<UpdateLog, Long> {

	public DAOJPAUpdateLog(){
		super(UpdateLog.class);
	}
	
	public String getFilterCondition(Map<String, Object> filters, String sortField, SortOrder sortOrder) {
		String sql = "SELECT t1 FROM UpdateLog t1";
		if (filters.size() > 0) {
			sql += " WHERE";
			for (Map.Entry<String, Object> entry : filters.entrySet()) {
				if( entry.getValue() instanceof String){
					sql += " AND t1."+entry.getKey() + " LIKE :"+entry.getKey().replace(".", "");
				} else {
					sql += " AND t1."+entry.getKey() + " = :"+entry.getKey().replace(".", "");
				}
			}
			sql = sql.replaceFirst("AND ", "");
		}
		if (sortField != null) {
			sql += " order by t1." + sortField;
			if (sortOrder == SortOrder.ASCENDING) {
				sql += " ASC";
			} else {
				sql += " DESC";
			}
		}
		return sql;
	}

	public int count(Map<String, Object> filters) {
		Query query = this.em.createQuery(getFilterCondition(filters, null, null));
		for (Map.Entry<String, Object> entry : filters.entrySet()) {
			if( entry.getValue() instanceof String){
				query.setParameter(entry.getKey().replace(".", ""), "%" + entry.getValue() + "%");
			} else {
				query.setParameter(entry.getKey().replace(".", ""), entry.getValue());
			}
		}
		return query.getResultList().size();
	}

	public List<UpdateLog> getResultList(int first, int pageSize, String sortField, SortOrder sortOrder,
			Map<String, Object> filters) {
		TypedQuery<UpdateLog> query = this.em.createQuery(getFilterCondition(filters, sortField, sortOrder), UpdateLog.class);
		for (Map.Entry<String, Object> entry : filters.entrySet()) {
			if( entry.getValue() instanceof String){
				query.setParameter(entry.getKey().replace(".", ""), "%" + entry.getValue() + "%");
			} else {
				query.setParameter(entry.getKey().replace(".", ""), entry.getValue());
			}
		}
		query.setFirstResult(first);
		query.setMaxResults(pageSize);
		return query.getResultList();
	}
}
