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

@Entity
@Table( name = "SER_INVENTORY_LOT")
public class Lot {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", insertable = true, updatable = true)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name = "id_product_storehouse_fk", referencedColumnName = "ID")
	private ProductByStorehouse product;
	
	@Column
	private String lotNumber;
	
	@Column
	private double quantity;
	
	@Column
	private Date expirationDate;
	
	@Column
	private Date elaborationDate;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public ProductByStorehouse getProduct() {
		return product;
	}

	public void setProduct(ProductByStorehouse product) {
		this.product = product;
	}

	public String getLotNumber() {
		return lotNumber;
	}

	public void setLotNumber(String lotNumber) {
		this.lotNumber = lotNumber;
	}

	public double getQuantity() {
		return quantity;
	}

	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}

	public Date getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}

	public Date getElaborationDate() {
		return elaborationDate;
	}

	public void setElaborationDate(Date elaborationDate) {
		this.elaborationDate = elaborationDate;
	}
	
}
