package com.serinse.pers.entity.inventory;


import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table( name = "SER_INVENTORY_DELIVERY")
public class Delivery {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", insertable = true, updatable = true)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name = "id_product_fk", referencedColumnName = "ID")
	private Product product;
	
	@Column( name = "who_asked")
	private String whoAsked;
	
	@Column( name = "who_received")
	private String whoReceived;
	
	@Column( name = "ask_date")
	@Temporal(TemporalType.DATE)
	private Date askDate;
	
	@Column( name = "lead_time")
	private String leadTime;

	@Column( name = "delivery_date")
	@Temporal(TemporalType.DATE)
	private Date deliveryDate;
	
	@Column( name = "delivery_point")
	private String deliveryPoint;
	
	@Column( name = "cities")
	private String cities;
	
	@Column( name = "status")
	private String status;
	
	@Column( name = "deliveryType")
	private DeliveryType deliveryType;
	
	@Column( name = "quantity")
	private Double quantity;
	
	@ManyToOne
	@JoinColumn(name = "id_product_by_storehouse_fk", referencedColumnName = "ID")
	private ProductByStorehouse productByStorehouse;
	
	@ManyToOne  //TODO Eliminar
	@JoinColumn(name = "id_storehouse_fk", referencedColumnName = "ID")
	private Storehouse storehouse;
	
	@ManyToOne
	@JoinColumn(name = "id_requisition_fk")
	private Requisition requisition;
	
	public Requisition getRequisition() {
		return requisition;
	}

	public void setRequisition(Requisition requisition) {
		this.requisition = requisition;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public String getWhoAsked() {
		return whoAsked;
	}

	public void setWhoAsked(String whoAsked) {
		this.whoAsked = whoAsked;
	}

	public Date getAskDate() {
		return askDate;
	}

	public void setAskDate(Date askDate) {
		this.askDate = askDate;
	}

	public String getLeadTime() {
		return leadTime;
	}

	public void setLeadTime(String leadTime) {
		this.leadTime = leadTime;
	}

	public Date getDeliveryDate() {
		return deliveryDate;
	}

	public void setDeliveryDate(Date deliveryDate) {
		this.deliveryDate = deliveryDate;
	}

	public String getDeliveryPoint() {
		return deliveryPoint;
	}

	public void setDeliveryPoint(String deliveryPoint) {
		this.deliveryPoint = deliveryPoint;
	}

	public String getCities() {
		return cities;
	}

	public void setCities(String cities) {
		this.cities = cities;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public DeliveryType getDeliveryType() {
		return deliveryType;
	}

	public void setDeliveryType(DeliveryType typeOfDelivery) {
		this.deliveryType = typeOfDelivery;
	}

	public Double getQuantity() {
		return quantity;
	}

	public void setQuantity(Double quantity) {
		this.quantity = quantity;
	}

	public String getWhoReceived() {
		return whoReceived;
	}

	public void setWhoReceived(String whoReceived) {
		this.whoReceived = whoReceived;
	}

	public Storehouse getStorehouse() {
		return storehouse;
	}

	public void setStorehouse(Storehouse storehouse) {
		this.storehouse = storehouse;
	}

	public ProductByStorehouse getProductByStorehouse() {
		return productByStorehouse;
	}

	public void setProductByStorehouse(ProductByStorehouse productByStorehouse) {
		this.productByStorehouse = productByStorehouse;
	}
	
}
