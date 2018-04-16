package com.serinse.pers.entity.inventory;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

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

import com.serinse.pers.entity.general.Photo;

@Entity
@Table( name = "SER_INVENTORY_PRODUCT")
public class Product implements Serializable{

	private static final long serialVersionUID = 8683482859046331412L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", insertable = true, updatable = true)
	private Long id;
	
	@Column(name = "category", nullable = false)
	private String category;
	
	@Column(name = "brand", nullable = false)
	private String brand;
	
	@Column( name = "code", nullable = false, unique = true)
	private String code;
	
	@Column( name = "material_service", nullable = false)
	private String material;
	
	@ManyToOne
	@JoinColumn( name = "id_inventory_fk", referencedColumnName = "ID")
	private Inventory inventory;
	
	@Column( name = "unit_cost")
	private Double unitCost;
	
	@Column( name = "active")
	private Boolean active;
	
	@Column( name = "rack_positions")
	private String positions;
	
	@Column( name = "responsible")
	private String responsible;
	
	@Column( name = "sales_channel")
	private String salesChannel;
	
	@OneToMany(mappedBy="product", cascade=CascadeType.ALL, fetch=FetchType.LAZY)
	private Set<ProductByStorehouse> quantities;
	
	@Transient
	private Photo photo;
	
	@Transient
	private Double totalCost;
	
	public Product(){
		quantities = new HashSet<>();
		unitCost = 0.0;
	}
	
	public Set<Lot> getStorehouseLots(String sh){
		for( ProductByStorehouse pbs : quantities ){
			if( pbs.getStorehouse().getName().equals(sh) ){
				return pbs.getLots();
			}
		}
		return null;
	}
	
	public int getStorehouseQuantity(String sh){
		for( ProductByStorehouse pbs : quantities ){
			if( pbs.getStorehouse().getName().equals(sh) ){
				return pbs.getQuantity().intValue();
			}
		}
		return 0;
	}
	
	public ProductByStorehouse getStorehouseProduct(String sh){
		for( ProductByStorehouse pbs : quantities ){
			if( pbs.getStorehouse().getName().equals(sh)){
				return pbs;
			}
		}
		return null;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMaterial() {
		return material;
	}

	public void setMaterial(String material) {
		this.material = material;
	}

	public Inventory getInventory() {
		return inventory;
	}

	public void setInventory(Inventory inventory) {
		this.inventory = inventory;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((brand == null) ? 0 : brand.hashCode());
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((material == null) ? 0 : material.hashCode());
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
		Product other = (Product) obj;
		if (brand == null) {
			if (other.brand != null)
				return false;
		} else if (!brand.equals(other.brand))
			return false;
		
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (material == null) {
			if (other.material != null)
				return false;
		} else if (!material.equals(other.material))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Product [id=" + id + ", brand=" + brand + ", code=" + code
				+ ", material=" + material + "]";
	}

	public Photo getPhoto() {
		return photo;
	}

	public void setPhoto(Photo photo) {
		this.photo = photo;
	}

	public Set<ProductByStorehouse> getQuantities() {
		return quantities;
	}

	public void setQuantities(Set<ProductByStorehouse> quantities) {
		this.quantities = quantities;
	}

	public Double getUnitCost() {
		return unitCost;
	}

	public void setUnitCost(Double unitCost) {
		this.unitCost = unitCost;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public String getPositions() {
		return positions;
	}

	public void setPositions(String positions) {
		this.positions = positions;
	}

	public Double getTotalCost() {
		return totalCost;
	}

	public void setTotalCost(Double totalCost) {
		this.totalCost = totalCost;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getResponsible() {
		return responsible;
	}

	public void setResponsible(String responsible) {
		this.responsible = responsible;
	}

	public String getSalesChannel() {
		return salesChannel;
	}

	public void setSalesChannel(String salesChannel) {
		this.salesChannel = salesChannel;
	}

}
