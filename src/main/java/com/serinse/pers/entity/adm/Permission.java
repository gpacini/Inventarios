package com.serinse.pers.entity.adm;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table( name = "SER_ADM_PERMISSION")
public class Permission implements Serializable{

	private static final long serialVersionUID = 12387123L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", insertable = true, updatable = true)
	private Long id;
	
	@Column(name = "path" )
	private String path;
	
	@Column(name = "type" )
	private PermissionEnum type;
	
	@ManyToOne
	@JoinColumn(name="id_role_fk")
	private Role role;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public PermissionEnum getType() {
		return type;
	}

	public void setType(PermissionEnum type) {
		this.type = type;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}
}
