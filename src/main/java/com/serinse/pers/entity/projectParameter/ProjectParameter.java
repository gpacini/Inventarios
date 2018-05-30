
package com.serinse.pers.entity.projectParameter;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Size;


@Entity
@Table(name = "GEN_PROJECT_PARAMETER")
public class ProjectParameter implements Serializable {
	private static final long serialVersionUID = -6739919052158515878L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", insertable = true, updatable = true)
	private Long id;

	@Size(max = 50)
	@Column(name = "name", length = 50, columnDefinition = "character varying (50)")
	private String name;

	@Size(max = 1024)
	@Column(name = "value", length = 1024, columnDefinition = "character varying (1024)")
	private String value;

	@Transient
	private boolean isNew;

	public ProjectParameter() {
		super();
		isNew = false;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public boolean isNew() {
		return isNew;
	}

	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}

	public Double getValueAsDouble(){
		try{
			return Double.parseDouble(value);
		}catch(Exception e){
			return 0.0;
		}
	}

	public Integer getValueAsInt(){
		try{
			return Integer.parseInt(value);
		}catch(Exception e){
			return 0;
		}
	}

}
