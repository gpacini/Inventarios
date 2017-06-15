package com.serinse.ejb.impl.general;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import com.serinse.ejb.AbstractBean;
import com.serinse.pers.dao.general.DAOJPAPhoto;
import com.serinse.pers.entity.general.Photo;
import com.serinse.pers.exception.NoDeletedEntityException;

@Stateless
@LocalBean
public class PhotoBean extends AbstractBean<Photo> {

	@EJB
	private DAOJPAPhoto daojpaPhoto;
	
	@PostConstruct
	public void init(){
		super.init(daojpaPhoto);
	}
	
	public Photo findByDirectory(String directory){
		return daojpaPhoto.findByDirectory(directory);
	}
	
	public Photo findByTableAndId(String table, Long id){
		return daojpaPhoto.findByTableAndId(table, id);
	}
	
	public List<Photo> findByTable(String table){
		return daojpaPhoto.findByTable(table);
	}
	
	public void delete(Photo p){
		try {
			daojpaPhoto.delete(p);
		} catch (NoDeletedEntityException e) {
			e.printStackTrace();
		}
	}
	
}