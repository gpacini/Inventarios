package com.serinse.pers.dao;

import java.io.Serializable;
import java.util.List;

import com.serinse.pers.exception.NoDeletedEntityException;
import com.serinse.pers.exception.TheresNoEntityException;
import com.serinse.pers.exception.UnsavedEntityException;

public interface DAOGeneric<T, Identificador extends Serializable> {

	T merge(T o) throws UnsavedEntityException;
	T findById(Identificador codigo) throws TheresNoEntityException;
	void delete(T o) throws NoDeletedEntityException;
	List<T> findAll();
	void saveCalledNullValuesMethod(T o) throws UnsavedEntityException;
	public T findByName(String name);
	public List<T> findAllById();
	public void save(T o);
}
