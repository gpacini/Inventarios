package com.serinse.pers.entity.adm;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table( name = "SER_ADM_ROLE")
public class Role implements Serializable, Comparable<Role>{

	private static final long serialVersionUID = -8603658794758755273L;
	
	public static String ADMIN_ROLE = "Administrador";
	public static String PROGRAMMING_ROLE = "Programador";
	public static String CLIENT_ROLE = "Cliente";
	public static String DIGITATOR_ROLE = "Digitador";
	public static String CHIEF_ROLE = "Jefe";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", insertable = true, updatable = true)
	private Long id;
	
	@Column(name = "role_name", length = 50, columnDefinition = "character varying (50)", nullable = false, unique = true)
	private String name;
	
	@Column(name = "description", length = 500, columnDefinition = "character varying (500)")
	private String description;
	
	@Column(name = "importance")
	private int importance;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getRoleName() {
		return name;
	}

	public void setRoleName(String roleName) {
		this.name = roleName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getImportance() {
		return importance;
	}

	public void setImportance(int importance) {
		this.importance = importance;
	}
	
	public int compareTo(Role r){
		return Integer.compare(importance, r.getImportance());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((name == null) ? 0 : name.hashCode());
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
		Role other = (Role) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Role [id=" + id + ", roleName=" + name + "]";
	}
	
	
}
