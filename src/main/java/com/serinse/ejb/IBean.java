package com.serinse.ejb;

import java.util.List;

public interface IBean<T> {

	public T findByName(String name);
	public T findById(Long id);
	public List<T> findAllById();
	public void save(T o);
	public T update(T o);
	
}
