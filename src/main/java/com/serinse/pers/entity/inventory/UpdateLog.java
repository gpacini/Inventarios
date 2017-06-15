package com.serinse.pers.entity.inventory;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table( name = "SER_INVENTORY_UPDATE_LOG")
public class UpdateLog {
	
	public static final String DELIVERY = "Movimiento";
	public static final String PRODUCT = "Producto";
	
	public static final String UPDATE = "Actualizacion";
	public static final String DELETE = "Eliminacion";
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", insertable = true, updatable = true)
	private Long id;
	
	@Column( name = "date" )
	private Date date;
	
	@Column( name = "currentValue")
	private Double currentValue;
	
	@Column( name = "previousValue" )
	private Double previousValue;
	
	@Column( name = "user" )
	private String user;
	
	@Column( name = "reason" )
	private String reason;
	
	@Column( name = "type" )
	private String type;
	
	@Column( name = "code_fk" )
	private String code;
	
	@Column( name = "action" )
	private String action;

	public UpdateLog(){
		
	}
	
	public UpdateLog(String action, String user, String type){
		this.user = user;
		this.type = type;
		this.action = action;
		date = new Date();
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Double getCurrentValue() {
		return currentValue;
	}

	public void setCurrentValue(Double currentValue) {
		this.currentValue = currentValue;
	}

	public Double getPreviousValue() {
		return previousValue;
	}

	public void setPreviousValue(Double previousValue) {
		this.previousValue = previousValue;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}
	
	
	
}
