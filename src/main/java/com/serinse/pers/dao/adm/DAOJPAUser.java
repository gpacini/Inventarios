package com.serinse.pers.dao.adm;

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
public class DAOJPAUser extends DAOJPABase<User, Long> {
	
	@EJB DAOJPAUser daojpaUser;

	public DAOJPAUser() {
		super(User.class);
	}
	
	public User findUserByUsername(String username){
		return this.findSingleByParameter("username", username);
	}
	
	public User findUserByEmail(String email){
		return this.findSingleByParameter("email", email);
	}
	
	@SuppressWarnings("unchecked")
	public List<User> findAllByRole(String role){
		String sql = "SELECT t1 FROM User t1 where t1.role.name = :role";
		Query query = this.em.createQuery(sql);
		query.setParameter("role", role);
		
		return query.getResultList();
	}
	

	private Predicate getFilterCondition(CriteriaBuilder cb, Root<User> myObj, Map<String, String> filters) {
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
        Root<User> myObj = cq.from(User.class);
        cq.where(daojpaUser.getFilterCondition(cb, myObj, filters));
        cq.select(cb.count(myObj));
        return em.createQuery(cq).getSingleResult().intValue();
    }

    public List<User> getResultList(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
        CriteriaBuilder cb = this.em.getCriteriaBuilder();
        CriteriaQuery<User> cq = cb.createQuery(User.class);
        Root<User> myObj = cq.from(User.class);
        cq.where(daojpaUser.getFilterCondition(cb, myObj, filters));
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
	public List<User> getUsersByClient(String clientName){
    	String sql = "SELECT t1.user from ClientByUser t1 where t1.client.name = :name";
    	Query query = this.em.createQuery(sql);
    	query.setParameter("name", clientName);
    	return query.getResultList();
    }
    
	public void deleteClientsUsers(String clientName){
		Client client = (Client) this.em.createQuery("SELECT t1 FROM Client t1 where t1.name = :name")
				.setParameter("name", clientName).getSingleResult();
    	String sql = "DELETE FROM ClientByUser t1 where t1.client = :client";
    	Query query = this.em.createQuery(sql);
    	query.setParameter("client", client);
    	query.executeUpdate();
    }
    
    public void saveClientByUser(Client client, User user){
    	ClientByUser cbu = new ClientByUser();
    	cbu.setClient(client);
    	cbu.setUser(user);
    	this.em.persist(cbu);
    	this.em.flush();
    }

}