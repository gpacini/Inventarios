package com.serinse.pers.dao.inventory;

import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.primefaces.model.SortOrder;

import com.serinse.pers.dao.DAOJPABase;
import com.serinse.pers.entity.inventory.Inventory;
import com.serinse.pers.entity.inventory.Product;

@Stateless
@LocalBean
public class DAOJPAProduct extends DAOJPABase<Product, Long> {

	@EJB
	DAOJPAProduct daojpaProduct;

	public DAOJPAProduct() {
		super(Product.class);
	}

	public Product findByCode(String code) {
		return findSingleByParameter("code", code);
	}

	@SuppressWarnings("unchecked")
	public List<Product> findByInventoryIdAndProduct(Long invId, String product) {
		Query query = this.em.createQuery("SELECT t1 FROM Product t1 where t1.inventory.id = :id "
				+ "AND (t1.brand LIKE :product OR t1.code LIKE :product OR t1.material LIKE :product)");
		query.setParameter("id", invId);
		query.setParameter("product", "%" + product + "%");
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<Product> findByInventoryId(Long invId) {
		Query query = this.em.createQuery("SELECT t1 FROM Product t1 where t1.inventory.id = :id ");
		query.setParameter("id", invId);
		return query.getResultList();
	}

	private Predicate getFilterCondition(CriteriaBuilder cb, Root<Product> myObj, Map<String, String> filters,
			Inventory inventory) {
		Predicate filterCondition = cb.conjunction();
		String wildCard = "%";
		for (Map.Entry<String, String> filter : filters.entrySet()) {
			String value = wildCard + filter.getValue() + wildCard;
			if (filter.getKey().equalsIgnoreCase("not_available")) {
				if (filter.getValue().equalsIgnoreCase("true")) {
					filterCondition = cb.and(filterCondition, cb.notEqual(myObj.get("brand"), "N/A"));
				} else if (filter.getValue().equalsIgnoreCase("false")) {
					filterCondition = cb.and(filterCondition, cb.equal(myObj.get("brand"), "N/A"));
				}
			} else if (filter.getKey().equals("active_item")) {
				if (filter.getValue().equals("true")) {
					filterCondition = cb.and(filterCondition, cb.equal(myObj.get("active"), true));
				}
			} else if (!filter.getValue().equals("")) {
				javax.persistence.criteria.Path<String> path = myObj.get(filter.getKey());
				filterCondition = cb.and(filterCondition, cb.like(path, value));
			}
		}
		filterCondition = cb.and(filterCondition, cb.equal(myObj.get("inventory"), inventory));
		return filterCondition;
	}

	public int count(Map<String, String> filters, Inventory inventory) {
		CriteriaBuilder cb = this.em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Product> myObj = cq.from(Product.class);
		cq.where(daojpaProduct.getFilterCondition(cb, myObj, filters, inventory));
		cq.select(cb.count(myObj));
		return em.createQuery(cq).getSingleResult().intValue();
	}

	public List<Product> getResultList(int first, int pageSize, String sortField, SortOrder sortOrder,
			Map<String, String> filters, Inventory inventory) {
		CriteriaBuilder cb = this.em.getCriteriaBuilder();
		CriteriaQuery<Product> cq = cb.createQuery(Product.class);
		Root<Product> myObj = cq.from(Product.class);
		cq.where(daojpaProduct.getFilterCondition(cb, myObj, filters, inventory));
		if (sortField != null) {
			if (sortOrder == SortOrder.ASCENDING) {
				cq.orderBy(cb.asc(myObj.get(sortField)));
			} else if (sortOrder == SortOrder.DESCENDING) {
				cq.orderBy(cb.desc(myObj.get(sortField)));
			}
		}
		return em.createQuery(cq).setFirstResult(first).setMaxResults(pageSize).getResultList();
	}

	public String getFilterCondition(Map<String, Object> filters, String sortField, SortOrder sortOrder) {
		String sql = "SELECT t1 FROM Product t1";
		System.out.println(filters.size());
		if (filters.size() > 0) {
			sql += " WHERE";
			for (Map.Entry<String, Object> entry : filters.entrySet()) {
				if (entry.getKey().equals("not_available")) {
					sql += " AND t1.brand != 'N/A'";
				} else if (entry.getKey().equals("multiple_inventory")) {
					String subSql = "AND (";
					List<Long> ids = (List<Long>) entry.getValue();
					for (Long id : ids) {
						subSql += " OR t1.inventory.id = " + id;
					}
					subSql = subSql.replaceFirst("OR", "") + " )";
					sql += subSql;
				} else {
					if (entry.getValue() instanceof String) {
						sql += " AND t1." + entry.getKey() + " LIKE :" + entry.getKey().replace(".", "");
					} else {
						sql += " AND t1." + entry.getKey() + " = :" + entry.getKey().replace(".", "");
					}
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
			if (!entry.getKey().equals("multiple_inventory") && !entry.getKey().equals("not_available")) {
				if (entry.getValue() instanceof String) {
					query.setParameter(entry.getKey().replace(".", ""), "%" + entry.getValue() + "%");
				} else {
					query.setParameter(entry.getKey().replace(".", ""), entry.getValue());
				}
			}
		}
		return query.getResultList().size();
	}

	public List<Product> getResultList(int first, int pageSize, String sortField, SortOrder sortOrder,
			Map<String, Object> filters) {
		TypedQuery<Product> query = this.em.createQuery(getFilterCondition(filters, sortField, sortOrder),
				Product.class);
		for (Map.Entry<String, Object> entry : filters.entrySet()) {
			if (!entry.getKey().equals("multiple_inventory") && !entry.getKey().equals("not_available")) {
				if (entry.getValue() instanceof String) {
					query.setParameter(entry.getKey().replace(".", ""), "%" + entry.getValue() + "%");
				} else {
					query.setParameter(entry.getKey().replace(".", ""), entry.getValue());
				}
			}
		}
		query.setFirstResult(first);
		query.setMaxResults(pageSize);
		return query.getResultList();
	}
}
