package com.serinse.pers.entity.inventory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;


@Entity
@Table( name = "SER_INVENTORY_PRODUCT_STOREHOUSE")
public class ProductByStorehouse {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", insertable = true, updatable = true)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name = "id_product_fk", referencedColumnName = "ID")
	private Product product;

	@ManyToOne
	@JoinColumn(name = "id_storehouse_fk", referencedColumnName = "ID")
	private Storehouse storehouse;
	
	@Column( name = "quantity" )
	private Double quantity;
	
	@Column(name = "last_day")
	private Date lastDate;
	
	@OneToMany( mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.EAGER )
	private List<Lot> lots = new ArrayList<>();

	@OneToMany( mappedBy = "productByStorehouse", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private List<Delivery> deliveries = new ArrayList<>();
	
	@Transient
	private Boolean isNew = false;
	
	public Long getDaysInStorehouse(){
		if( quantity > 0 ){
			long thisDiff = (new Date()).getTime() - lastDate.getTime();
			return TimeUnit.DAYS.convert(thisDiff, TimeUnit.MILLISECONDS);
		} else {
			return 0L;
		}
	}
	
	public Double getTotalCost(){
		if( product.getUnitCost() == null ) return 0.0;
		return product.getUnitCost() * quantity;
	}

	public Boolean getIsNew() {
		return isNew;
	}

	public void setIsNew(Boolean isNew) {
		this.isNew = isNew;
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

	public Storehouse getStorehouse() {
		return storehouse;
	}

	public void setStorehouse(Storehouse storehouse) {
		this.storehouse = storehouse;
	}

	public Double getQuantity() {
		return quantity;
	}

	public void setQuantity(Double quantity) {
		this.quantity = quantity;
	}

	public List<Lot> getLots() {
		return lots;
	}

	public void setLots(List<Lot> lots) {
		this.lots = lots;
	}

	public List<Delivery> getDeliveries() {
		return deliveries;
	}

	public void setDeliveries(List<Delivery> deliveries) {
		this.deliveries = deliveries;
	}

	public Date getLastDate() {
		return lastDate;
	}

	public void setLastDate(Date lastDate) {
		this.lastDate = lastDate;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((product == null) ? 0 : product.hashCode());
		result = prime * result + ((storehouse == null) ? 0 : storehouse.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ProductByStorehouse other = (ProductByStorehouse) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (product == null) {
			if (other.product != null)
				return false;
		} else if (!product.equals(other.product))
			return false;
		if (storehouse == null) {
			if (other.storehouse != null)
				return false;
		} else if (!storehouse.equals(other.storehouse))
			return false;
		return true;
	}
	
	
	
}
