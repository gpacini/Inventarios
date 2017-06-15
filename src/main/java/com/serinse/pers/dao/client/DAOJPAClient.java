package com.serinse.pers.dao.client;

import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.primefaces.model.SortOrder;

import com.serinse.pers.dao.DAOJPABase;
import com.serinse.pers.entity.adm.User;
import com.serinse.pers.entity.client.Client;
import com.serinse.pers.entity.client.ClientByUser;

@Stateless
@LocalBean
public class DAOJPAClient extends DAOJPABase<Client, Long>{

	@EJB DAOJPAClient daojpaClient;
	
	public DAOJPAClient() {
		super(Client.class);
	}
	
	private Predicate getFilterCondition(CriteriaBuilder cb, Root<Client> myObj, Map<String, String> filters) {
        Predicate filterCondition = cb.conjunction();
        String wildCard = "%";
        for (Map.Entry<String, String> filter : filters.entrySet()) {
            String value = wildCard + filter.getValue() + wildCard;
            if (!filter.getValue().equals("")) {
                javax.persistence.criteria.Path<String> path = myObj.get(filter.getKey());
                filterCondition = cb.and(filterCondition, cb.like(path, value));
            }
        }
        return filterCondition;
    }

    public int count(Map<String, String> filters) {
        CriteriaBuilder cb = this.em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Client> myObj = cq.from(Client.class);
        cq.where(daojpaClient.getFilterCondition(cb, myObj, filters));
        cq.select(cb.count(myObj));
        return em.createQuery(cq).getSingleResult().intValue();
    }

    public List<Client> getResultList(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
        CriteriaBuilder cb = this.em.getCriteriaBuilder();
        CriteriaQuery<Client> cq = cb.createQuery(Client.class);
        Root<Client> myObj = cq.from(Client.class);
        cq.where(daojpaClient.getFilterCondition(cb, myObj, filters));
        if (sortField != null) {
            if (sortOrder == SortOrder.ASCENDING) {
                cq.orderBy(cb.asc(myObj.get(sortField)));
            } else if (sortOrder == SortOrder.DESCENDING) {
                cq.orderBy(cb.desc(myObj.get(sortField)));
            }
        }
        return em.createQuery(cq).setFirstResult(first).setMaxResults(pageSize).getResultList();
    }
    
    @SuppressWarnings("unchecked")
	public List<Client> getClientsByUsername(String username){
    	String sql = "SELECT t1.client from ClientByUser t1 where t1.user.username = :username";
    	Query query = this.em.createQuery(sql);
    	query.setParameter("username", username);
    	return query.getResultList();
    }

    
    public void saveClientByUser(Client client, User user){
    	ClientByUser cbu = new ClientByUser();
    	cbu.setClient(client);
    	cbu.setUser(user);
    	this.em.persist(cbu);
    	this.em.flush();
    }
	
}
