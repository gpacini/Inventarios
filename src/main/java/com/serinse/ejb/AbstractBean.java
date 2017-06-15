package com.serinse.ejb;

import java.util.List;

import com.serinse.pers.dao.DAOGeneric;

public class AbstractBean<T> implements IBean<T>{

	private DAOGeneric<T, Long> dao;
	private boolean initialized = false;
	
	public void init(DAOGeneric<T, Long> dao){
		this.dao = dao;
		initialized = true;
	}
	
	@Override
	public T findByName(String name) {
		return dao.findByName(name);
	}

	@Override
	public T findById(Long id) {
		return dao.findById(id);
	}

	@Override
	public List<T> findAllById() {
		return dao.findAllById();
	}

	@Override
	public void save(T o) {
		dao.save(o);
	}

	@Override
	public T update(T o) {
		return dao.merge(o);
	}
	
	public boolean isInitialized(){
		return initialized;
	}

	
	
}
