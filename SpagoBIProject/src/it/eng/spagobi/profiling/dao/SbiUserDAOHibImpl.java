/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005-2009 Engineering Ingegneria Informatica S.p.A.

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA

**/
package it.eng.spagobi.profiling.dao;

import it.eng.spago.error.EMFErrorSeverity;
import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.commons.dao.AbstractHibernateDAO;
import it.eng.spagobi.commons.metadata.SbiExtRoles;
import it.eng.spagobi.kpi.alarm.metadata.SbiAlarm;
import it.eng.spagobi.profiling.bean.SbiExtUserRoles;
import it.eng.spagobi.profiling.bean.SbiExtUserRolesId;
import it.eng.spagobi.profiling.bean.SbiUser;
import it.eng.spagobi.profiling.bean.SbiUserAttributes;
import it.eng.spagobi.profiling.bean.SbiUserAttributesId;
import it.eng.spagobi.profiling.bo.UserBO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class SbiUserDAOHibImpl extends AbstractHibernateDAO implements ISbiUserDAO{
	static private Logger logger = Logger.getLogger(SbiUserDAOHibImpl.class);
	/**
	 * Load SbiUser by userId.
	 * 
	 * @param userId the user id	/**
	 * Load SbiUser by id.
	 * 
	 * @param id the bi object id
	 * 
	 * @return the BI object
	 * 
	 * @throws EMFUserError the EMF user error
	 * 
	 */
	public Integer loadByUserId(String userId) throws EMFUserError {
		logger.debug("IN");
		Session aSession = null;
		Transaction tx = null;
		try {
			aSession = getSession();
			tx = aSession.beginTransaction();
			
			String q = "from SbiUser us where us.userId = :userId";
			Query query = aSession.createQuery(q);
			query.setString("userId", userId);
			
			SbiUser user = (SbiUser)query.uniqueResult();			
			
			if(user != null)
				return Integer.valueOf(user.getId());
		} catch (HibernateException he) {
			logger.error(he.getMessage(),he);
			if (tx != null)
				tx.rollback();
			throw new EMFUserError(EMFErrorSeverity.ERROR, 100);
		} finally {
			logger.debug("OUT");
			if (aSession!=null){
				if (aSession.isOpen()) aSession.close();
			}
		}
		return null;
	} 
	
	/**
	 * Load SbiUser by id.
	 * 
	 * @param id the identifier	/**
	 * Load SbiUser by id.
	 * 
	 * @param id the bi object id
	 * 
	 * @return the BI object
	 * 
	 * @throws EMFUserError the EMF user error
	 * 
	 */
	public SbiUser loadSbiUserById(Integer id) throws EMFUserError {
		logger.debug("IN");
		SbiUser toReturn = null;
		Session aSession = null;
		Transaction tx = null;
		try {
			aSession = getSession();
			tx = aSession.beginTransaction();
			String q = "from SbiUser us where us.id = :id";
			Query query = aSession.createQuery(q);
			query.setInteger("id", id);
			toReturn = (SbiUser)query.uniqueResult();
			Hibernate.initialize(toReturn);
			Hibernate.initialize(toReturn.getSbiExtUserRoleses());
			Hibernate.initialize(toReturn.getSbiUserAttributeses());
			for(SbiUserAttributes current : toReturn.getSbiUserAttributeses() ){
				Hibernate.initialize(current.getSbiAttribute());
			}
			
			tx.commit();
		} catch (HibernateException he) {
			logger.error(he.getMessage(), he);
			if (tx != null)
				tx.rollback();
			throw new EMFUserError(EMFErrorSeverity.ERROR, 100);
		} finally {
			if (aSession!=null){
				if (aSession.isOpen()) aSession.close();
			}
		}
		logger.debug("OUT");
		return toReturn;
	} 

	/**Insert SbiUser
	 * @param user
	 * @throws EMFUserError
	 */
	public Integer saveSbiUser(SbiUser user) throws EMFUserError {
		Session aSession = null;
		Transaction tx = null;
		try {
			aSession = getSession();
			tx = aSession.beginTransaction();
			//checks if user with same userId exists
			String q = "from SbiUser us where us.userId = :userId";
			Query query = aSession.createQuery(q);
			query.setString("userId", user.getUserId());
			
			SbiUser userExists = (SbiUser)query.uniqueResult();
			if(userExists == null){
				Integer id = (Integer)aSession.save(user);				
				tx.commit();
				return id;
			}else{
				throw new EMFUserError(EMFErrorSeverity.ERROR, 15001);
			}
			
		} catch (HibernateException he) {
			logger.error(he.getMessage(), he);
			if (tx != null)
				tx.rollback();
			throw new EMFUserError(EMFErrorSeverity.ERROR, 100);
		} finally {
			if (aSession!=null){
				if (aSession.isOpen()) aSession.close();
			}
		}
	}
	
	/**Update SbiUser
	 * @param user
	 * @throws EMFUserError
	 */
	public void updateSbiUser(SbiUser user, Integer userID) throws EMFUserError {
		Session aSession = null;
		Transaction tx = null;
		try {
			aSession = getSession();
			tx = aSession.beginTransaction();
			aSession.update(user, userID);
			aSession.flush();
			tx.commit();
		} catch (HibernateException he) {
			logger.error(he.getMessage(), he);
			if (tx != null)
				tx.rollback();
			throw new EMFUserError(EMFErrorSeverity.ERROR, 100);
		} finally {
			if (aSession!=null){
				if (aSession.isOpen()) aSession.close();
			}
		}
	}

	public void updateSbiUserAttributes(SbiUserAttributes attribute)
			throws EMFUserError {
		Session aSession = null;
		Transaction tx = null;
		try {
			aSession = getSession();
			tx = aSession.beginTransaction();
			aSession.saveOrUpdate(attribute);
			aSession.flush();
			tx.commit();
		} catch (HibernateException he) {
			logger.error(he.getMessage(), he);
			if (tx != null)
				tx.rollback();
			throw new EMFUserError(EMFErrorSeverity.ERROR, 100);
		} finally {
			if (aSession!=null){
				if (aSession.isOpen()) aSession.close();
			}
		}
	}

	public void updateSbiUserRoles(SbiExtUserRoles role) throws EMFUserError {
		Session aSession = null;
		Transaction tx = null;
		try {
			aSession = getSession();
			tx = aSession.beginTransaction();
			aSession.saveOrUpdate(role);
			aSession.flush();
			tx.commit();
		} catch (HibernateException he) {
			logger.error(he.getMessage(), he);
			if (tx != null)
				tx.rollback();
			throw new EMFUserError(EMFErrorSeverity.ERROR, 100);
		} finally {
			if (aSession!=null){
				if (aSession.isOpen()) aSession.close();
			}
		}
	}


	public SbiUser loadSbiUserByUserId(String userId) throws EMFUserError {
		logger.debug("IN");

		Session aSession = null;
		Transaction tx = null;
		try {
			aSession = getSession();
			tx = aSession.beginTransaction();
			String q = "from SbiUser us where us.userId = :userId";
			Query query = aSession.createQuery(q);
			query.setString("userId", userId);
			
			SbiUser user = (SbiUser)query.uniqueResult();
			tx.commit();
			return user;
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

	public ArrayList<SbiUserAttributes> loadSbiUserAttributesById(Integer id) throws EMFUserError {
		logger.debug("IN");

		Session aSession = null;
		Transaction tx = null;
		try {
			aSession = getSession();
			tx = aSession.beginTransaction();
			String q = "select us.sbiUserAttributeses from SbiUser us where us.id = :id";

			Query query = aSession.createQuery(q);
			query.setInteger("id", id);
			
			ArrayList<SbiUserAttributes> result = (ArrayList<SbiUserAttributes>)query.list();
			
			Hibernate.initialize(result);
			for(SbiUserAttributes current : result ){
				Hibernate.initialize(current.getSbiAttribute());
			}
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

	@SuppressWarnings("unchecked")
	public ArrayList<SbiExtRoles> loadSbiUserRolesById(Integer id) throws EMFUserError {
		logger.debug("IN");

		Session aSession = null;
		Transaction tx = null;
		try {
			aSession = getSession();
			tx = aSession.beginTransaction();
			String q = "select us.sbiExtUserRoleses from SbiUser us where us.id = :id";
			Query query = aSession.createQuery(q);
			query.setInteger("id", id);
			
			ArrayList<SbiExtRoles> result = (ArrayList<SbiExtRoles>)query.list();
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


	public ArrayList<SbiUser> loadSbiUsers() throws EMFUserError {
		logger.debug("IN");

		Session aSession = null;
		Transaction tx = null;
		try {
			aSession = getSession();
			tx = aSession.beginTransaction();
			String q = "from SbiUser ";
			Query query = aSession.createQuery(q);
			
			ArrayList<SbiUser> result = (ArrayList<SbiUser>)query.list();
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
	public void deleteSbiUserById(Integer id) throws EMFUserError {
        logger.debug("IN");

        Session aSession = null;
        Transaction tx = null;
        try {
            aSession = getSession();
            tx = aSession.beginTransaction();

            String q = " from SbiUserAttributes x where x.id.id = :id ";
            Query query = aSession.createQuery(q);
            query.setInteger("id", id);

            ArrayList<SbiUserAttributes> userAttributes = (ArrayList<SbiUserAttributes>)query.list();

            //deletes attributes associations
            if(userAttributes != null){
                Iterator attrsIt = userAttributes.iterator();
                while(attrsIt.hasNext()){
                    SbiUserAttributes temp = (SbiUserAttributes)attrsIt.next();
                    attrsIt.remove();

                    aSession.delete(temp);
                    aSession.flush();
                }
            }

            String qr = " from SbiExtUserRoles x where x.id.id = :id ";
            Query queryR = aSession.createQuery(qr);
            queryR.setInteger("id", id);

            ArrayList<SbiExtUserRoles> userRoles = (ArrayList<SbiExtUserRoles>)queryR.list();
            if(userRoles != null){
                Iterator rolesIt = userRoles.iterator();
                while(rolesIt.hasNext()){
                    SbiExtUserRoles temp = (SbiExtUserRoles)rolesIt.next();
                    rolesIt.remove();
                    aSession.delete(temp);
                    aSession.flush();
                }
            }
            SbiUser userToDelete =(SbiUser)aSession.load(SbiUser.class, id);

            aSession.delete(userToDelete);
            aSession.flush();
            tx.commit();
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


	public Integer fullSaveOrUpdateSbiUser(SbiUser user, List roles, HashMap<Integer, String> attributes)
			throws EMFUserError {
		logger.debug("IN");

		Session aSession = null;
		Transaction tx = null;
		Integer id = null;
		try {
			aSession = getSession();
			tx = aSession.beginTransaction();
			boolean save = true;
			
			SbiUser userToUpdate = user;
			id = userToUpdate.getId();
			Set<SbiExtUserRoles> extUserRoles = new HashSet<SbiExtUserRoles>();
			Set<SbiUserAttributes> userAttrList = new HashSet<SbiUserAttributes>();
			
			if(id!=0){
				save = false;
				userToUpdate =(SbiUser)aSession.load(SbiUser.class, id);
				if(user.getPassword() != null){
					userToUpdate.setPassword(user.getPassword());
				}
				userToUpdate.setFullName(user.getFullName());
				userToUpdate.setUserId(user.getUserId());
				userToUpdate.setId(id);

			}	
			
			if(save){
				//save
				id = (Integer)aSession.save(userToUpdate);	
				userToUpdate.setId(id);
			}
			
			//sets roles
			if(roles != null){
				//remove existing roles
	            String qr = " from SbiExtUserRoles x where x.id.id = :id ";
	            Query queryR = aSession.createQuery(qr);
	            queryR.setInteger("id", id);

	            ArrayList<SbiExtUserRoles> userRoles = (ArrayList<SbiExtUserRoles>)queryR.list();
	            if(userRoles != null){
	                Iterator rolesIt = userRoles.iterator();
	                while(rolesIt.hasNext()){
	                    SbiExtUserRoles temp = (SbiExtUserRoles)rolesIt.next();
	                    rolesIt.remove();
	                    aSession.delete(temp);
	                    aSession.flush();
	                }
	            }


	            Iterator rolesIt = roles.iterator();
				while(rolesIt.hasNext()){
					Integer extRoleId  = (Integer)rolesIt.next();

					SbiExtUserRoles sbiExtUserRole = new SbiExtUserRoles();
					SbiExtUserRolesId extUserRoleId = new SbiExtUserRolesId();

			    	extUserRoleId.setExtRoleId(extRoleId);//role Id
			    	extUserRoleId.setId(id.intValue());//user ID
			    	
			    	sbiExtUserRole.setId(extUserRoleId);
			    	sbiExtUserRole.setSbiUser(userToUpdate);  
			    	
			    	aSession.saveOrUpdate(sbiExtUserRole);
			    	aSession.flush();

				}
			}

			//sets attributes

			if(attributes != null){

	            String qr = " from SbiUserAttributes x where x.id.id = :id ";
	            Query queryR = aSession.createQuery(qr);
	            queryR.setInteger("id", id);
	            ArrayList<SbiUserAttributes> userAttributes = (ArrayList<SbiUserAttributes>)queryR.list();
	            
				boolean userAttrAlreadyExist = false;
				if(userAttrList.size()!=0){
					userAttrAlreadyExist=true;
				}
				//loop over db attributes for user
				Iterator attrsIt = userAttributes.iterator();
				while(attrsIt.hasNext()){				
				
					//if attribute is modified than update 		
					SbiUserAttributes attribute = (SbiUserAttributes)attrsIt.next();
					Integer attrID = attribute.getId().getAttributeId();
					if(attributes.containsKey(attrID)){
						String attrVal = attributes.get(attrID);
						attribute.setAttributeValue(attrVal);
						//checks if value ==""
						if(attrVal.equals("")){
							aSession.delete(attribute);
						}else{
							aSession.saveOrUpdate(attribute);
						}
						aSession.flush();
						attributes.remove(attrID);
					}

				
				}//else if attribute is not present than add it
				Iterator attrsItToAdd = attributes.keySet().iterator();
				while(attrsItToAdd.hasNext()){
					Integer attrID = (Integer)attrsItToAdd.next();
					SbiUserAttributes attributeToAdd =  new SbiUserAttributes();
					attributeToAdd.setAttributeValue(attributes.get(attrID));
					SbiUserAttributesId attributeToAddId = new SbiUserAttributesId();
					attributeToAddId.setId(id);
					attributeToAddId.setAttributeId(attrID);
					attributeToAdd.setId(attributeToAddId);
					aSession.saveOrUpdate(attributeToAdd);
					aSession.flush();
					
				}

			}

			//update
			aSession.saveOrUpdate(userToUpdate);
	
			tx.commit();
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
		return id ;
		
	}
	public UserBO loadUserById(Integer id) throws EMFUserError {
		// TODO Auto-generated method stub
		return null;
	}
	public ArrayList<UserBO> loadUsers() throws EMFUserError {
		logger.debug("IN");
		ArrayList<UserBO> users = null;
		Session aSession = null;
		Transaction tx = null;
		try {
			aSession = getSession();
			tx = aSession.beginTransaction();
			Criteria crit = aSession.createCriteria(SbiUser.class);

			ArrayList<SbiUser> result = (ArrayList<SbiUser>)crit.list();
			if(result != null && !result.isEmpty()){
				users = new ArrayList<UserBO> ();
				for(int i=0; i<result.size(); i++){
					users.add(toUserBO(result.get(i)));
				}
			}
			
			return users;
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
	/**
	 * From the Hibernate SbiUser at input, gives the corrispondent BI
	 * object (UserBO).
	 * 
	 * @param sbiUser The Hibernate SbiUser
	 * 
	 * @return the corrispondent output <code>UserBO</code>
	 * @throws EMFUserError 
	 */
	public UserBO toUserBO(SbiUser sbiUser) throws EMFUserError{
		logger.debug("IN");
		    // create empty UserBO
			UserBO userBO = new UserBO();
			userBO.setId(sbiUser.getId());
			userBO.setDtLastAccess(sbiUser.getDtLastAccess());
			userBO.setDtPwdBegin(sbiUser.getDtPwdBegin());
			userBO.setDtPwdEnd(sbiUser.getDtPwdEnd());
			userBO.setFlgPwdBlocked(sbiUser.getFlgPwdBlocked());
			userBO.setFullName(sbiUser.getFullName());
			userBO.setPassword(sbiUser.getPassword());
			userBO.setUserId(sbiUser.getUserId());
			
			List userRoles = new ArrayList();
			Set roles = sbiUser.getSbiExtUserRoleses();
			for (Iterator it = roles.iterator(); it.hasNext(); ) {
				SbiExtRoles role = (SbiExtRoles) it.next();
				Integer roleId = role.getExtRoleId();
				userRoles.add(roleId);
			}
			userBO.setSbiExtUserRoleses(userRoles);

			HashMap<Integer, HashMap<String, String>> userAttributes = new HashMap<Integer, HashMap<String, String>>(); 
			Set<SbiUserAttributes> attributes = sbiUser.getSbiUserAttributeses();

			for (Iterator<SbiUserAttributes> it = attributes.iterator(); it.hasNext(); ) {
				SbiUserAttributes attr = it.next();
				Integer attrId = attr.getSbiAttribute().getAttributeId();	
				HashMap<String, String> nameValueAttr = new HashMap<String, String>();

				nameValueAttr.put(attr.getSbiAttribute().getAttributeName(), attr.getAttributeValue());
				userAttributes.put(attrId, nameValueAttr);
			}
			userBO.setSbiUserAttributeses(userAttributes);
			
			logger.debug("OUT");
			return userBO;
	}

	public Integer countUsers() throws EMFUserError {
		logger.debug("IN");
		Session aSession = null;
		Transaction tx = null;
		Integer resultNumber;
		
		try {
			aSession = getSession();
			tx = aSession.beginTransaction();
		
			String hql = "select count(*) from SbiUser ";
			Query hqlQuery = aSession.createQuery(hql);
			resultNumber = (Integer)hqlQuery.uniqueResult();

		} catch (HibernateException he) {
			logger.error("Error while loading the list of SbiUser", he);	
			if (tx != null)
				tx.rollback();	
			throw new EMFUserError(EMFErrorSeverity.ERROR, 9104);
		
		} finally {
			if (aSession != null) {
				if (aSession.isOpen())
					aSession.close();
				logger.debug("OUT");
			}
		}
		return resultNumber;
	}

	public List<UserBO> loadPagedUsersList(Integer offset, Integer fetchSize)
			throws EMFUserError {
		logger.debug("IN");
		List<UserBO> toReturn = null;
		Session aSession = null;
		Transaction tx = null;
		Integer resultNumber;
		Query hibernateQuery;
		
		try {
			aSession = getSession();
			tx = aSession.beginTransaction();
		
			List toTransform = null;
			String hql = "select count(*) from SbiUser ";
			Query hqlQuery = aSession.createQuery(hql);
			resultNumber = (Integer)hqlQuery.uniqueResult();
			
			offset = offset < 0 ? 0 : offset;
			if(resultNumber > 0) {
				fetchSize = (fetchSize > 0)? Math.min(fetchSize, resultNumber): resultNumber;
			}
			
			hibernateQuery = aSession.createQuery("from SbiUser order by userId");
			hibernateQuery.setFirstResult(offset);
			if(fetchSize > 0) hibernateQuery.setMaxResults(fetchSize);			
	
			toTransform = hibernateQuery.list();	
			
			if(toTransform!=null && !toTransform.isEmpty()){
				toReturn = new ArrayList<UserBO>();
				Iterator it = toTransform.iterator();
				while(it.hasNext()){
					SbiUser sbiUser = (SbiUser)it.next();
					UserBO us = toUserBO(sbiUser);
					toReturn.add(us);
				}
			}
			
		} catch (HibernateException he) {
			logger.error("Error while loading the list of SbiAlarm", he);	
			if (tx != null)
				tx.rollback();	
			throw new EMFUserError(EMFErrorSeverity.ERROR, 9104);
		
		} finally {
			if (aSession != null) {
				if (aSession.isOpen())
					aSession.close();
				logger.debug("OUT");
			}
		}
		return toReturn;
	}


}
