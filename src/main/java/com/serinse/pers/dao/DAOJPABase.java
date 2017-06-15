package com.serinse.pers.dao;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TransactionRequiredException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import com.serinse.pers.exception.NoDeletedEntityException;
import com.serinse.pers.exception.TheresNoEntityException;
import com.serinse.pers.exception.UnsavedEntityException;

public abstract class DAOJPABase<T, I extends Serializable> implements
		DAOGeneric<T, I> {

	@PersistenceContext
	protected EntityManager em;
	private final Class<T> type;

	/**
	 * Constructor
	 * 
	 * @param type
	 *            la clase de la entidad a utilizar
	 */
	public DAOJPABase(Class<T> type) {
		this.type = type;
	}
	
	@Override
	public T merge(T o) throws UnsavedEntityException {
		try {
			T r = em.merge(o);
			em.flush();
			return r;
		} catch (IllegalStateException e) {
			throw new UnsavedEntityException(e);
		} catch (IllegalArgumentException e) {
			throw new UnsavedEntityException(e);
		} catch (TransactionRequiredException e) {
			throw new UnsavedEntityException(e);
		}
	}

	@Override
	public void save(T o) throws UnsavedEntityException {
		try {
			em.persist(o);
			em.flush();
		} catch (EntityExistsException e) {
			throw new UnsavedEntityException(e);
		} catch (IllegalStateException e) {
			throw new UnsavedEntityException(e);
		} catch (IllegalArgumentException e) {
			throw new UnsavedEntityException(e);
		} catch (TransactionRequiredException e) {
			throw new UnsavedEntityException(e);
		} catch (Exception e) {
			throw new UnsavedEntityException(e);
		}
	}

	@Override
	public T findById(I id) throws TheresNoEntityException {
		try {
			T o = em.find(type, id);
			if (o == null) {
				throw new TheresNoEntityException("Not found ".concat(
						type.getSimpleName()).concat(" with id: " + id));
			}
			return o;
		} catch (IllegalStateException e) {
			throw new TheresNoEntityException(e);
		} catch (IllegalArgumentException e) {
			throw new TheresNoEntityException(e);
		}
	}

	@Override
	public void delete(T entidad) throws NoDeletedEntityException {
		try {
			T entidadEliminar = em.merge(entidad);
			em.remove(entidadEliminar);
		} catch (IllegalStateException e) {
			throw new NoDeletedEntityException(e);
		} catch (IllegalArgumentException e) {
			throw new NoDeletedEntityException(e);
		} catch (TransactionRequiredException e) {
			throw new NoDeletedEntityException(e);
		}
	}

	@Override
	public List<T> findAll() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<T> cq = cb.createQuery(type);
		Root<T> t = cq.from(type);
		cq.select(t);
		TypedQuery<T> tq = em.createQuery(cq);
		return tq.getResultList();
	}
	
	public T findByName(String name){
		return findSingleByParameter("name", name);
		
	}
	
	@SuppressWarnings("unchecked")
	protected T findSingleByParameter(String param, String value){
		Query query = this.em.createQuery("SELECT t1 FROM " + type.getSimpleName() + " t1 where t1." + param + " = :"+param.replace(".", "") );
		query.setParameter(param.replace(".", ""), value);
		
		try {
			return (T) query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	protected List<T> findListByParameter(String param, String value){
		Query query = this.em.createQuery("SELECT t1 FROM " + type.getSimpleName() + " t1 where t1." + param + " = :"+param );
		query.setParameter(param, value);
		
		return query.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<T> findAllById(){
		Query query = this.em.createQuery("SELECT t1 FROM " + type.getSimpleName() + " t1 order by id");
		return query.getResultList();
	}

	public void setEm(EntityManager em) {
		this.em = em;
	}

	@Override
	public void saveCalledNullValuesMethod(T o) throws UnsavedEntityException {
		try {
			em.persist(o);
		} catch (EntityExistsException e) {
			throw new UnsavedEntityException(e);
		} catch (IllegalStateException e) {
			throw new UnsavedEntityException(e);
		} catch (IllegalArgumentException e) {
			throw new UnsavedEntityException(e);
		} catch (TransactionRequiredException e) {
			throw new UnsavedEntityException(e);
		} catch (Exception e) {
			throw new UnsavedEntityException(e);
		}
	}

}