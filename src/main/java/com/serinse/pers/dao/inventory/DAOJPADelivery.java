package com.serinse.pers.dao.inventory;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;

import org.primefaces.model.SortOrder;

import com.serinse.pers.dao.DAOJPABase;
import com.serinse.pers.entity.inventory.Delivery;
import com.serinse.pers.entity.inventory.DeliveryType;

@Stateless
@LocalBean
public class DAOJPADelivery extends DAOJPABase<Delivery, Long> {

	@EJB
	DAOJPADelivery daojpaDelivery;

	public DAOJPADelivery() {
		super(Delivery.class);
	}

	@SuppressWarnings("unchecked")
	public List<Delivery> findByInventoryIdAndDate(Long id, Date date) {

		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.DATE, 5);
		Date dateMax = c.getTime();
		c.add(Calendar.DATE, -10);
		Date dateMin = c.getTime();

		System.out.println("Vamos a buscar por inventario entre fechas: " + dateMin + " & " + dateMax);

		Query query = this.em.createQuery("SELECT t1 FROM Delivery t1 where t1.product.inventory.id =:id "
				+ "AND ((t1.askDate BETWEEN :dateMin AND :dateMax) OR (t1.deliveryDate BETWEEN :dateMin AND :dateMax))");
		query.setParameter("id", id);
		query.setParameter("dateMin", dateMin);
		query.setParameter("dateMax", dateMax);

		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<Delivery> findByProductIdAndDate(Long id, Date date) {
		Query query = this.em.createQuery("SELECT t1 FROM Delivery t1 where t1.product.id = :id "
				+ "AND ((t1.askDate BETWEEN :dateMin AND :dateMax) OR (t1.deliveryDate BETWEEN :dateMin AND :dateMax))");
		query.setParameter("id", id);

		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.DATE, 5);
		Date dateMax = c.getTime();
		c.add(Calendar.DATE, -10);
		Date dateMin = c.getTime();

		System.out.println("Vamos a buscar el producto " + id + " entre fechas: " + dateMin + " & " + dateMax);

		query.setParameter("dateMin", dateMin);
		query.setParameter("dateMax", dateMax);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<Delivery> findByInventoryIdAndProduct(Long id, String product) {
		Query query = this.em.createQuery("SELECT t1 FROM Delivery t1 where t1.product.inventory.id = :id "
				+ "AND (t1.product.brand LIKE :product OR t1.product.code LIKE :product OR t1.product.material LIKE :product)");
		query.setParameter("id", id);
		query.setParameter("product", "%" + product + "%");
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<Delivery> findByProductId(Long id) {
		Query query = this.em.createQuery("SELECT t1 FROM Delivery t1 where t1.product.id = :id");
		query.setParameter("id", id);

		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<Delivery> findByInventoryId(Long id) {
		Query query = this.em.createQuery("SELECT t1 FROM Delivery t1 where t1.product.inventory.id = :id");
		query.setParameter("id", id);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<Delivery> findByProductCode(String code) {
		Query query = this.em.createQuery("SELECT t1 FROM Delivery t1 where t1.product.code = :code");
		query.setParameter("code", code);

		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<Delivery> findByInventoryIdAndDeliveryType(Long invId, DeliveryType dt, int from, int to) {
		Query query = this.em.createQuery("Select t1 FROM Delivery t1 where t1.product.inventory.id = :invid AND "
				+ "t1.deliveryType = :dt ORDER BY by t1.id LIMIT :to OFFSET :from");
		query.setParameter("invid", invId);
		query.setParameter("dt", dt);
		query.setParameter("to", from + to);
		query.setParameter("from", from);

		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<Delivery> findByInventoryIdLimit(Long invId, int limit) {
		Query query = this.em.createQuery(
				"Select t1 FROM Delivery t1 where t1.product.inventory.id = :invId " + "ORDER BY t1.id DESC");
		query.setParameter("invId", invId);

		return query.setMaxResults(limit).getResultList();
	}

	public List<Delivery> findLastByProductAndTypeAndStorehouse(Long id, DeliveryType entrega, Long storehouseId) {
		Query query = this.em
				.createQuery("SELECT t1 FROM Delivery t1 where t1.product.id = :id AND t1.deliveryType = :delivery "
						+ "AND t1.storehouse.id = :storehouseId order by t1.askDate desc");
		query.setParameter("id", id);
		query.setParameter("delivery", entrega);
		query.setParameter("storehouseId", storehouseId);
		return query.setMaxResults(3).getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<Delivery> findByRequisitionId(Long id) {
		Query query = this.em.createQuery("SELECT t1 FROM Delivery t1 where t1.requisition.id = :id");
		query.setParameter("id", id);
		return query.getResultList();
	}

	private String getFilterCondition(Map<String, String> filters, String sortField, SortOrder sortOrder) {
		String sql = "SELECT t1 FROM Delivery t1";
		if (filters.size() > 0) {
			sql += " WHERE";
			for (Map.Entry<String, String> entry : filters.entrySet()) {
				if (entry.getKey().equals("date")) {
					sql += "AND ((t1.askDate BETWEEN :dateMin AND :dateMax) OR (t1.deliveryDate BETWEEN :dateMin AND :dateMax))";
				} else if (entry.getKey().equals("inventory")) {
					sql += " AND t1.product.inventory.id = :inventory";
				} else if (entry.getKey().equals("product")) {
					sql += " AND t1.product.id = :product";
				} else if(entry.getKey().equals("deliveryType")){
					sql += " AND t1.deliveryType = :deliveryType";
				} else {
					sql += " AND t1." + entry.getKey() + " LIKE :" + entry.getKey().replace(".", "");
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

	public int count(Map<String, String> filters) {
		String sql = getFilterCondition(filters, null, null);
		Query query = this.em.createQuery(getFilterCondition(filters, null, null));
		for (Map.Entry<String, String> entry : filters.entrySet()) {
			if (entry.getKey().equals("date")) {
				Date date = new Date(Date.parse(entry.getValue()));
				Calendar c = Calendar.getInstance();
				c.setTime(date);
				c.add(Calendar.DATE, 5);
				Date dateMax = c.getTime();
				c.add(Calendar.DATE, -10);
				Date dateMin = c.getTime();
				query.setParameter("dateMin", dateMin);
				query.setParameter("dateMax", dateMax);
			} else if (entry.getKey().equals("inventory")) {
				query.setParameter("inventory", Long.parseLong(entry.getValue()));
			} else if (entry.getKey().equals("product")) {
				query.setParameter("product", Long.parseLong(entry.getValue()));
			} else if(entry.getKey().equals("deliveryType")){
				try {
					query.setParameter("deliveryType", DeliveryType.getByName(entry.getValue()));
				} catch (Exception e) {
				}
			} else {
				query.setParameter(entry.getKey().replace(".", ""), "%" + entry.getValue() + "%");
			}
		}
		return query.getResultList().size();
	}

	public List<Delivery> getResultList(int first, int pageSize, String sortField, SortOrder sortOrder,
			Map<String, String> filters) {
		Query query = this.em.createQuery(getFilterCondition(filters, sortField, sortOrder));
		for (Map.Entry<String, String> entry : filters.entrySet()) {
			if (entry.getKey().equals("date")) {
				Date date = new Date(Date.parse(entry.getValue()));
				Calendar c = Calendar.getInstance();
				c.setTime(date);
				c.add(Calendar.DATE, 5);
				Date dateMax = c.getTime();
				c.add(Calendar.DATE, -10);
				Date dateMin = c.getTime();
				query.setParameter("dateMin", dateMin);
				query.setParameter("dateMax", dateMax);
			} else if (entry.getKey().equals("inventory")) {
				query.setParameter("inventory", Long.parseLong(entry.getValue()));
			} else if (entry.getKey().equals("product")) {
				query.setParameter("product", Long.parseLong(entry.getValue()));
			} else if(entry.getKey().equals("deliveryType")){
				try {
					query.setParameter("deliveryType", DeliveryType.getByName(entry.getValue()));
				} catch (Exception e) {
				}
			} else {
				query.setParameter(entry.getKey().replace(".", ""), "%" + entry.getValue() + "%");
			}
		}
		query.setFirstResult(first);
		query.setMaxResults(pageSize);
		return query.getResultList();
	}

}
