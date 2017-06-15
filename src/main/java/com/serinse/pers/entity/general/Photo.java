package com.serinse.pers.entity.general;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.serinse.common.Constants;

@Entity
@Table( name = "SER_PHOTOS")

public class Photo implements Serializable {

	private static final long serialVersionUID = 7022884586774586698L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", insertable = true, updatable = true)
	private Long id;
	
	@Column( name = "directory", nullable = false)
	private String directory;
	
	@Column( name = "name", nullable = false)
	private String name;
	
	@Column( name = "title" )
	private String title;
	
	@Column( name = "description")
	private String description;
	
	@Column( name="fk_id" )
	private Long fkId; //Id de la tabla foranea
	
	@Column( name="foreign_table" )
	private String table;
	
	public String getCompleteDir(){
		return directory + Constants.FILES_SEPARATOR + name;
	}
	
	public String getWebDir(){
		return Constants.IMAGES_WEB_DIR + directory + "/" + name;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDirectory() {
		return directory;
	}

	public void setDirectory(String directory) {
		this.directory = directory;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getFkId() {
		return fkId;
	}

	public void setFkId(Long fkId) {
		this.fkId = fkId;
	}

	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result
				+ ((directory == null) ? 0 : directory.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		Photo other = (Photo) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (directory == null) {
			if (other.directory != null)
				return false;
		} else if (!directory.equals(other.directory))
			return false;
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
		return "Photo [id=" + id + ", directory=" + directory + ", name="
				+ name + ", description=" + description + "]";
	}
	
	
}
