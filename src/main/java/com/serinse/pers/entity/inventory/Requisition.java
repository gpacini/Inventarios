package com.serinse.pers.entity.inventory;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "SER_INVENTORY_REQUISITION")
public class Requisition {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", insertable = true, updatable = true)
	private Long id;
	
	@Column(name = "consecutive")
	private String consecutive;
	
	@Column( name = "physical_requisition")
	private String physicalRequisition;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getConsecutive() {
		return consecutive;
	}

	public void setConsecutive(String consecutive) {
		this.consecutive = consecutive;
	}

	public String getPhysicalRequisition() {
		return physicalRequisition;
	}

	public void setPhysicalRequisition(String physicalRequisition) {
		this.physicalRequisition = physicalRequisition;
	}
}
