package com.serinse.pers.entity.inventory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.serinse.pers.entity.client.Client;

@Entity
@Table( name = "SER_INVENTORY")
public class Inventory implements Serializable{

	private static final long serialVersionUID = 2085862909879018001L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", insertable = true, updatable = true)
	private Long id;
	
	@OneToOne
	@JoinColumn(name = "id_client_fk", nullable = false, referencedColumnName = "ID")
	private Client client;
	
	@Column( name = "unit_cost_activated")
	private Boolean unitCost;

	@Column( name = "rack_positions_activated")
	private Boolean rackPositions;
	
	@OneToMany( mappedBy = "inventory", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private List<Product> products = new ArrayList<Product>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public List<Product> getProducts() {
		return products;
	}

	public void setProducts(List<Product> products) {
		this.products = products;
	}
	
	public void addProduct(Product p){
		products.add(p);
	}
	
	public void removeProduct(Product p){
		products.remove(p);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((client == null) ? 0 : client.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((products == null) ? 0 : products.hashCode());
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
		Inventory other = (Inventory) obj;
		if (client == null) {
			if (other.client != null)
				return false;
		} else if (!client.equals(other.client))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (products == null) {
			if (other.products != null)
				return false;
		} else if (!products.equals(other.products))
			return false;
		return true;
	}

	public Boolean getUnitCost() {
		if( unitCost == null ) return false;
		return unitCost;
	}

	public void setUnitCost(Boolean unitCost) {
		this.unitCost = unitCost;
	}

	public Boolean getRackPositions() {
		return rackPositions;
	}

	public void setRackPositions(Boolean rackPositions) {
		this.rackPositions = rackPositions;
	}
	
	
}
