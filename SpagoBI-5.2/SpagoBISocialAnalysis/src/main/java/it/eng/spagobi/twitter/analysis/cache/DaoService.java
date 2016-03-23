/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.twitter.analysis.cache;

import it.eng.spagobi.twitter.analysis.cache.exceptions.DaoServiceException;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 * @author Marco Cortella (marco.cortella@eng.it), Giorgio Federici (giorgio.federici@eng.it)
 *
 */
public class DaoService {

	Session session;

	public DaoService() {

	}

	/**
	 * Persist a new object
	 *
	 * @param entity
	 * @return
	 */
	public Object create(Object entity) throws DaoServiceException {

		this.session = TwitterHibernateUtil.getSessionFactory().openSession();

		if (this.session == null) {

			throw new DaoServiceException("Invalid session. Impossible to create a new entity without a valid session");
		}

		Transaction tx = null;

		try {

			tx = session.beginTransaction();
			session.persist(entity);
			tx.commit();

		} catch (Throwable t) {

			if (tx != null)
				tx.rollback();

			throw new DaoServiceException("DaoService create(): Impossible to create a new entity: " + entity, t);

		} finally {

			session.close();
		}

		return entity;

	}

	public void update(Object entity) throws DaoServiceException {

		this.session = TwitterHibernateUtil.getSessionFactory().openSession();

		if (this.session == null) {

			throw new DaoServiceException("Invalid session. Impossible to update an entity without a valid session");
		}

		Transaction tx = null;

		try {

			tx = session.beginTransaction();
			session.merge(entity);
			tx.commit();

		} catch (Throwable t) {

			if (tx != null)
				tx.rollback();

			throw new DaoServiceException("DaoService update(): Impossible to update entity: " + entity, t);

		} finally {
			session.close();
		}

	}

	public void delete(Object entity) throws DaoServiceException {

		this.session = TwitterHibernateUtil.getSessionFactory().openSession();

		if (this.session == null) {

			throw new DaoServiceException("Invalid session. Impossible to delete an entity without a valid session");
		}

		Transaction tx = null;

		try {

			tx = session.beginTransaction();
			session.delete(entity);
			tx.commit();

		} catch (Throwable t) {

			if (tx != null)
				tx.rollback();

			throw new DaoServiceException("DaoService delete(): Impossible to delete entity: " + entity, t);
		} finally {
			session.close();
		}

	}

	public <T> Object find(Class<T> entity, Serializable key) throws DaoServiceException {

		this.session = TwitterHibernateUtil.getSessionFactory().openSession();

		if (this.session == null) {

			throw new DaoServiceException("Invalid session. Impossible to find an entity without a valid session");
		}

		Object result = null;

		Transaction tx = null;

		try {

			tx = session.beginTransaction();
			result = session.get(entity, key);
			tx.commit();

		} catch (Throwable t) {

			if (tx != null)
				tx.rollback();

			throw new DaoServiceException("DaoService find(): Impossible to find entity: " + entity, t);
		} finally {
			session.close();
		}

		return result;
	}

	public Object refresh(Object entity) throws DaoServiceException {

		this.session = TwitterHibernateUtil.getSessionFactory().openSession();

		if (this.session == null) {

			throw new DaoServiceException("Invalid session. Impossible to refresh an entity without a valid session");
		}

		Transaction tx = null;

		try {

			tx = session.beginTransaction();
			session.refresh(entity);
			tx.commit();

		} catch (Throwable t) {

			if (tx != null)
				tx.rollback();

			throw new DaoServiceException("DaoService refresh(): Impossible to refresh entity: " + entity, t);

		} finally {
			session.close();
		}

		return entity;

	}

	public <T> List<T> listFromQuery(String query, Object... args) throws DaoServiceException {

		this.session = TwitterHibernateUtil.getSessionFactory().openSession();

		if (this.session == null) {

			throw new DaoServiceException("Invalid session. Impossible to execute a query without a valid session");
		}

		Transaction tx = null;

		List<T> result = null;

		try {

			tx = session.beginTransaction();
			Query q = session.createQuery(query);

			for (int i = 0; i < args.length; i++) {
				q.setParameter(i, args[i]);
			}

			result = q.list();
			tx.commit();

		} catch (Throwable t) {

			if (tx != null)
				tx.rollback();

			throw new DaoServiceException("DaoService listFromQuery(): Impossible to execute the query [ " + query + " ]", t);

		}

		finally {
			session.close();
		}

		return result;

	}

	public <T> List<T> listFromLimitedQuery(String query, int max, Object... args) throws DaoServiceException {

		this.session = TwitterHibernateUtil.getSessionFactory().openSession();

		if (this.session == null) {

			throw new DaoServiceException("Invalid session. Impossible to execute a query without a valid session");
		}

		Transaction tx = null;

		List<T> result = null;

		try {

			tx = session.beginTransaction();
			Query q = session.createQuery(query);

			for (int i = 0; i < args.length; i++) {
				q.setParameter(i, args[i]);
			}

			q.setMaxResults(max);

			result = q.list();
			tx.commit();

		} catch (Throwable t) {

			if (tx != null)
				tx.rollback();

			throw new DaoServiceException("DaoService listFromLimitedQuery(): Impossible to execute the query [ " + query + " ] ", t);

		}

		finally {
			session.close();
		}

		return result;

	}

	public <T> List<T> listFromBetweenLimitedQuery(String query, int min, int max, Object... args) throws DaoServiceException {

		this.session = TwitterHibernateUtil.getSessionFactory().openSession();

		if (this.session == null) {

			throw new DaoServiceException("Invalid session. Impossible to execute a query without a valid session");
		}

		Transaction tx = null;

		List<T> result = null;

		try {

			tx = session.beginTransaction();
			Query q = session.createQuery(query);

			for (int i = 0; i < args.length; i++) {
				q.setParameter(i, args[i]);
			}

			q.setFirstResult(min);
			q.setMaxResults(max);

			result = q.list();
			tx.commit();

		} catch (Throwable t) {

			if (tx != null)
				tx.rollback();

			throw new DaoServiceException("DaoService listFromLimitedQuery(): Impossible to execute the query [ " + query + " ] ", t);

		}

		finally {
			session.close();
		}

		return result;

	}

	public int countQuery(String queryHQL, Object... args) throws DaoServiceException {
		this.session = TwitterHibernateUtil.getSessionFactory().openSession();

		if (this.session == null) {

			throw new DaoServiceException("Invalid session. Impossible to execute a query without a valid session");
		}

		Transaction tx = null;

		int countResult = 0;

		try {

			tx = session.beginTransaction();
			Query query = session.createQuery(queryHQL);

			for (int i = 0; i < args.length; i++) {
				query.setParameter(i, args[i]);
			}

			countResult = query.list().size();

			tx.commit();

		} catch (Throwable t) {

			if (tx != null)
				tx.rollback();

			throw new DaoServiceException("DaoService countQuery(): Impossible to execute the query [ " + queryHQL + " ]", t);

		}

		finally {
			session.close();
		}

		return countResult;
	}

	public <T> T singleResultQuery(String query, Object... args) throws DaoServiceException {

		this.session = TwitterHibernateUtil.getSessionFactory().openSession();

		if (this.session == null) {

			throw new DaoServiceException("Invalid session. Impossible to execute a query without a valid session");
		}

		T result;

		Transaction tx = null;

		try {

			tx = session.beginTransaction();
			Query q = session.createQuery(query);

			for (int i = 0; i < args.length; i++) {
				q.setParameter(i, args[i]);
			}

			result = (T) q.uniqueResult();

			tx.commit();

		} catch (Throwable t) {

			if (tx != null)
				tx.rollback();

			throw new DaoServiceException("DaoService listFromQuery(): Impossible to execute the query [ " + query + " ]", t);

		}

		finally {
			session.close();
		}

		return result;
	}

	public void updateFromQuery(String query, Object... args) throws DaoServiceException {

		this.session = TwitterHibernateUtil.getSessionFactory().openSession();

		if (this.session == null) {

			throw new DaoServiceException("Invalid session. Impossible to execute a query without a valid session");
		}

		Transaction tx = null;

		try {

			tx = session.beginTransaction();
			Query q = session.createQuery(query);

			for (int i = 0; i < args.length; i++) {
				q.setParameter(i, args[i]);
			}

			q.executeUpdate();

			tx.commit();

		} catch (Throwable t) {

			if (tx != null)
				tx.rollback();

			throw new DaoServiceException("DaoService updateFromQuery(): Impossible to execute the query [ " + query + " ]", t);

		}

		finally {
			session.close();
		}

	}

}
