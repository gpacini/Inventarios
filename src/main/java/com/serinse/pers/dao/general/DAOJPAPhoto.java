package com.serinse.pers.dao.general;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;

import com.serinse.pers.dao.DAOJPABase;
import com.serinse.pers.entity.general.Photo;

@Stateless
@LocalBean
public class DAOJPAPhoto extends DAOJPABase<Photo, Long>{
	
	public DAOJPAPhoto(){
		super(Photo.class);
	}
	
	public Photo findByDirectory(String directory){
		return this.findSingleByParameter("directory", directory);
	}
	
	public Photo findByTableAndId(String table, Long id){
		Query query = this.em.createQuery("SELECT p1 FROM Photo p1 where p1.table =:table AND p1.fkId =:id" );
		query.setParameter("table", table);
		query.setParameter("id", id);
		try{
			return (Photo) query.getSingleResult();
		} catch( Exception e){
			return null;
		}
	}
	
	public List<Photo> findByTable(String table){
		return this.findListByParameter("table", table);
	}
}
