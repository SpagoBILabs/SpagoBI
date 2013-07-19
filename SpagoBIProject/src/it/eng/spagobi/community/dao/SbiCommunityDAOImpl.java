/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.community.dao;

import it.eng.spago.error.EMFErrorSeverity;
import it.eng.spago.error.EMFUserError;
import it.eng.spago.security.IEngUserProfile;
import it.eng.spagobi.commons.dao.AbstractHibernateDAO;
import it.eng.spagobi.commons.metadata.SbiExtRoles;
import it.eng.spagobi.community.mapping.SbiCommunity;
import it.eng.spagobi.community.mapping.SbiCommunityUsers;
import it.eng.spagobi.community.mapping.SbiCommunityUsersId;
import it.eng.spagobi.profiling.bean.SbiUser;
import it.eng.spagobi.profiling.dao.SbiAttributeDAOHibImpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class SbiCommunityDAOImpl extends AbstractHibernateDAO implements ISbiCommunityDAO {
	
	static private Logger logger = Logger.getLogger(SbiCommunityDAOImpl.class);
	
	public void setUserProfile(IEngUserProfile profile) {
		// TODO Auto-generated method stub

	}

	public void setUserID(String user) {
		// TODO Auto-generated method stub

	}

	public IEngUserProfile getUserProfile() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setTenant(String tenant) {
		// TODO Auto-generated method stub

	}

	private Integer saveSbiComunity(SbiCommunity community) throws EMFUserError {
		logger.debug("IN");
		Session aSession = null;
		Transaction tx = null;
		Integer id = null;
		try {

			aSession = getSession();
			tx = aSession.beginTransaction();
			community.setCreationDate(new Date());
			community.setLastChangeDate(new Date());
			updateSbiCommonInfo4Insert(community);
			id = (Integer)aSession.save(community);

			tx.commit();

			logger.debug("OUT");
			return id;
		} catch (HibernateException he) {
			logger.error(he.getMessage(),he);
			if (tx != null)
				tx.rollback();
			throw new EMFUserError(EMFErrorSeverity.ERROR, 100);
		} finally {
			if (aSession != null) {
				if (aSession.isOpen())
					aSession.close();
			}
		}
	}

	public List<SbiCommunity> loadSbiCommunityByUser(String userId)
			throws EMFUserError {
		logger.debug("IN");

		Session aSession = null;
		Transaction tx = null;
		try {
			aSession = getSession();
			tx = aSession.beginTransaction();
			String q = "select cu.sbiCommunity from SbiCommunityUsers cu where cu.id.userId = :userId";
			Query query = aSession.createQuery(q);
			query.setString("userId", userId);

			ArrayList<SbiCommunity> result = (ArrayList<SbiCommunity>)query.list();
			return result;
		} catch (HibernateException he) {
			logger.error(he.getMessage(), he);
			if (tx != null)
				tx.rollback();
			throw new EMFUserError(EMFErrorSeverity.ERROR, 100);
		} finally {
			logger.debug("OUT");
			if (aSession!=null){
				if (aSession.isOpen()) aSession.close();
			}
		}
	}

	public List<SbiCommunity> loadSbiCommunityByOwner(String owner)
			throws EMFUserError {
		logger.debug("IN");

		Session aSession = null;
		Transaction tx = null;
		try {

			aSession = getSession();
			tx = aSession.beginTransaction();
			String q = "from SbiCommunity c where c.owner = :userId";
			Query query = aSession.createQuery(q);
			query.setString("userId", owner);

			ArrayList<SbiCommunity> result = (ArrayList<SbiCommunity>)query.list();
			return result;
		} catch (HibernateException he) {
			logger.error(he.getMessage(), he);
			if (tx != null)
				tx.rollback();
			throw new EMFUserError(EMFErrorSeverity.ERROR, 100);
		} finally {
			logger.debug("OUT");
			if (aSession!=null){
				if (aSession.isOpen()) aSession.close();
			}
		}
	}

	public SbiCommunity loadSbiCommunityByName(String name) throws EMFUserError {
		logger.debug("IN");

		Session aSession = null;
		Transaction tx = null;
		try {
			aSession = getSession();
			tx = aSession.beginTransaction();
			String q = "from SbiCommunity c where c.name = :name";
			Query query = aSession.createQuery(q);
			query.setString("name", name);

			SbiCommunity result = (SbiCommunity)query.uniqueResult();
			return result;
		} catch (HibernateException he) {
			logger.error(he.getMessage(), he);
			if (tx != null)
				tx.rollback();
			throw new EMFUserError(EMFErrorSeverity.ERROR, 100);
		} finally {
			logger.debug("OUT");
			if (aSession!=null){
				if (aSession.isOpen()) aSession.close();
			}
		}
	}

	public void saveSbiComunityUsers(SbiCommunity community, String userID)
			throws EMFUserError {
		logger.debug("IN");
		Session aSession = null;
		Transaction tx = null;
		SbiCommunityUsers commUsers = new SbiCommunityUsers();
		try {
			
			Integer res = saveSbiComunity(community);
			if(res != null){
			
				aSession = getSession();
				tx = aSession.beginTransaction();
				
				commUsers.setCreationDate(new Date());
				commUsers.setLastChangeDate(new Date());
				SbiCommunityUsersId id = new SbiCommunityUsersId();
				id.setCommunityId(community.getCommunityId());
				id.setUserId(userID);
				
				commUsers.setId(id);
				
				updateSbiCommonInfo4Insert(commUsers);
				aSession.save(commUsers);
	
				tx.commit();
			}
			logger.debug("OUT");

		} catch (HibernateException he) {
			logger.error(he.getMessage(),he);
			if (tx != null)
				tx.rollback();
			throw new EMFUserError(EMFErrorSeverity.ERROR, 100);
		} finally {
			if (aSession != null) {
				if (aSession.isOpen())
					aSession.close();
			}
		}
	}

	public void addCommunityMember(SbiCommunity community, String userID)
			throws EMFUserError {
		logger.debug("IN");
		Session aSession = null;
		Transaction tx = null;
		SbiCommunityUsers commUsers = new SbiCommunityUsers();
		try {
			
			aSession = getSession();
			tx = aSession.beginTransaction();
			
			commUsers.setCreationDate(new Date());
			commUsers.setLastChangeDate(new Date());
			SbiCommunityUsersId id = new SbiCommunityUsersId();
			id.setCommunityId(community.getCommunityId());
			id.setUserId(userID);
			
			commUsers.setId(id);
			
			updateSbiCommonInfo4Insert(commUsers);
			aSession.save(commUsers);

			tx.commit();
			logger.debug("OUT");

		} catch (HibernateException he) {
			logger.error(he.getMessage(),he);
			if (tx != null)
				tx.rollback();
			throw new EMFUserError(EMFErrorSeverity.ERROR, 100);
		} finally {
			if (aSession != null) {
				if (aSession.isOpen())
					aSession.close();
			}
		}
	}

}
