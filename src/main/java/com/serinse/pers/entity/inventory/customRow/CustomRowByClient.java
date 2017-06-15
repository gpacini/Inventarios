package com.serinse.pers.entity.inventory.customRow;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.serinse.pers.entity.client.Client;

@Entity
@Table(name = "SER_INVENTORY_CUSTOM_ROW_BY_CLIENT")
public class CustomRowByClient {

	public static final String TYPE_SUM = "SUM";
	public static final String TYPE_AVERAGE = "AVERAGE";
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", insertable = true, updatable = true)
	private Long id;
	
	@ManyToOne
	@JoinColumn( name = "id_client_fk")
	private Client client;
	
	@Column( name = "row_id")
	private int rowId;
	
	@Column( name = "name")
	private String name;
	
	@Column(name = "type")
	private String type;
	
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

	public int getRowId() {
		return rowId;
	}

	public void setRowId(int rowId) {
		this.rowId = rowId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
}
