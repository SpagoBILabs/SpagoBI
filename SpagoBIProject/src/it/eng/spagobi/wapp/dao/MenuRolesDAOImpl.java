/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005-2008 Engineering Ingegneria Informatica S.p.A.

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
package it.eng.spagobi.wapp.dao;

import it.eng.spago.error.EMFErrorSeverity;
import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.commons.dao.AbstractHibernateDAO;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.wapp.bo.Menu;
import it.eng.spagobi.wapp.bo.MenuRoles;
import it.eng.spagobi.wapp.metadata.SbiMenuRole;
import it.eng.spagobi.wapp.metadata.SbiMenuRoleId;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 * @author Antonella Giachino (antonella.giachino@eng.it)
 *
 */
public class MenuRolesDAOImpl extends AbstractHibernateDAO implements IMenuRolesDAO {
    private static transient Logger logger = Logger.getLogger(MenuRolesDAOImpl.class);
	/**
	 * Load menu by role id.
	 * 
	 * @param roleId the role id
	 * 
	 * @return the list
	 * 
	 * @throws EMFUserError the EMF user error
	 * 
	 * @see it.eng.spagobi.wapp.dao.IMenuRolesDAO#loadMenuByRoleId(java.lang.Integer)
	 */
	public List loadMenuByRoleId(Integer roleId) throws EMFUserError {
	    logger.debug("IN");
	    if (roleId!=null) logger.debug("roleId="+roleId.toString());
		Session aSession = null;
		Transaction tx = null;
		List realResult = new ArrayList();
		String hql = null;
		Query hqlQuery = null;
		
		try {
			aSession = getSession();
			tx = aSession.beginTransaction();

				hql = " select mf.id.menuId, mf.id.extRoleId from SbiMenuRole as mf, SbiMenu m " +
				  " where mf.id.menuId = m.menuId " + 
				  " and mf.id.extRoleId = ? " +
				  " order by m.parentId, m.prog";
			
			hqlQuery = aSession.createQuery(hql);
			hqlQuery.setInteger(0, roleId.intValue());
			List hibList = hqlQuery.list();
			
			Iterator it = hibList.iterator();
			IMenuDAO menuDAO = DAOFactory.getMenuDAO();
			SbiMenuRole tmpMenuRole = null;
			Menu tmpMenu = null;
			while (it.hasNext()) {	
				Object[] tmpLst = (Object[])it.next();
				Integer menuId = (Integer)tmpLst[0];
				tmpMenu = menuDAO.loadMenuByID(menuId, roleId);
				if (tmpMenu != null){
				    logger.debug("Add Menu:"+tmpMenu.getName());
					realResult.add(tmpMenu);
				}
			}
			tx.commit();
		} catch (HibernateException he) {
		    logger.error("HibernateException",he);

			if (tx != null)
				tx.rollback();

			throw new EMFUserError(EMFErrorSeverity.ERROR, 100);

		} finally {			
			if (aSession!=null){
				if (aSession.isOpen()) aSession.close();
			}			
		}
		logger.debug("OUT");
		return realResult;
	}


	/**
	 * Load menu roles.
	 * 
	 * @param menuId the menu id
	 * @param roleId the role id
	 * 
	 * @return the menu roles
	 * 
	 * @throws EMFUserError the EMF user error
	 * 
	 * @see it.eng.spagobi.wapp.dao.IMenuRolesDAO#loadMenuRoles(java.lang.Integer, java.lang.Integer)
	 */
	public MenuRoles loadMenuRoles(Integer menuId, Integer roleId) throws EMFUserError{
	    logger.debug("IN");
		MenuRoles toReturn = null;
		Session aSession = null;
		Transaction tx = null;
		try {
			aSession = getSession();
			tx = aSession.beginTransaction();
			
			//String hql = "from SbiMenuRole s where s.id.menuId=" + menuId.toString() + 
			//             " and s.id.roleId=" +  roleId.toString();
			
			String hql = "from SbiMenuRole s where s.id.menuId= ? " + 
            " and s.id.roleId= ? ";
			Query query = aSession.createQuery(hql);
			query.setInteger(0, menuId.intValue());
			query.setInteger(1, roleId.intValue());
			//toReturn =(MenuRoles) query.uniqueResult();			
			SbiMenuRole hibMenuRole = (SbiMenuRole)query.uniqueResult();
			if (hibMenuRole == null) return null;
			toReturn = toMenuRoles(hibMenuRole);
			
			tx.commit();
		} catch (HibernateException he) {
		    logger.error("HibernateException",he);
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

	/**
	 * Modify menu role.
	 * 
	 * @param aMenuRole the a menu role
	 * 
	 * @throws EMFUserError the EMF user error
	 * 
	 * @see it.eng.spagobi.wapp.dao.IMenuRolesDAOO#modifyMenuRole(it.eng.spagobi.wapp.bo.MenuRoles)
	 */
	public void modifyMenuRole(MenuRoles aMenuRole) throws EMFUserError {
	    logger.debug("IN");
		Session aSession = null;
		Transaction tx = null;
		try {
			aSession = getSession();
			tx = aSession.beginTransaction();
			SbiMenuRoleId hibMenuRoleId = new SbiMenuRoleId();
			hibMenuRoleId.setMenuId(aMenuRole.getMenuId());
			hibMenuRoleId.setExtRoleId(aMenuRole.getExtRoleId());

			SbiMenuRole hibFeature = (SbiMenuRole) aSession.load(SbiMenuRole.class, hibMenuRoleId);
			tx.commit();
		} catch (HibernateException he) {
		    logger.error("HibernateException",he);

			if (tx != null)
				tx.rollback();

			throw new EMFUserError(EMFErrorSeverity.ERROR, 100);

		} finally {
			if (aSession!=null){
				if (aSession.isOpen()) aSession.close();
			}
			
		}
		logger.debug("OUT");
	}

	/**
	 * Insert menu role.
	 * 
	 * @param aMenuRole the a menu role
	 * 
	 * @throws EMFUserError the EMF user error
	 * 
	 * @see it.eng.spagobi.wapp.dao.IMenuRolesDAO#insertMenuRole(it.eng.spagobi.wapp.bo.MenuRoles)
	 */
	public void insertMenuRole(MenuRoles aMenuRole) throws EMFUserError {
	    logger.debug("IN");
		Session aSession = null;
		Transaction tx = null;
		try {
			aSession = getSession();
			tx = aSession.beginTransaction();
			SbiMenuRole hibMenuRole = new SbiMenuRole();	
			
			SbiMenuRoleId hibMenuRoleId = new SbiMenuRoleId();			
			hibMenuRoleId.setMenuId(aMenuRole.getMenuId());
			hibMenuRoleId.setExtRoleId(aMenuRole.getExtRoleId());
			hibMenuRole.setId(hibMenuRoleId);
			aSession.save(hibMenuRole);
			tx.commit();
		} catch (HibernateException he) {
			logException(he);

			if (tx != null)
				tx.rollback();

			throw new EMFUserError(EMFErrorSeverity.ERROR, 100);

		} finally {			
			if (aSession!=null){
				if (aSession.isOpen()) aSession.close();
			}			
		}
		logger.debug("OUT");
	}


	/**
	 * Erase menu role.
	 * 
	 * @param aMenuRole the a menu role
	 * 
	 * @throws EMFUserError the EMF user error
	 * 
	 * @see it.eng.spagobi.wapp.dao.IMenuRolesDAO#eraseMenuRole(it.eng.spagobi.wapp.bo.MenuRoles)
	 */
	public void eraseMenuRole(MenuRoles aMenuRole) throws EMFUserError{
	    logger.debug("IN");
		Session aSession = null;
		Transaction tx = null;
		try {
			aSession = getSession();
			tx = aSession.beginTransaction();
			
			SbiMenuRoleId hibMenuRoleId = new SbiMenuRoleId();			
			hibMenuRoleId.setMenuId(aMenuRole.getMenuId());
			hibMenuRoleId.setExtRoleId(aMenuRole.getExtRoleId());
			
			SbiMenuRole hibMenuRole = (SbiMenuRole) aSession.load(SbiMenuRole.class, hibMenuRoleId);

			aSession.delete(hibMenuRole);
			tx.commit();
		} catch (HibernateException he) {
			logException(he);

			if (tx != null)
				tx.rollback();

			throw new EMFUserError(EMFErrorSeverity.ERROR, 100);

		} finally {			
			if (aSession!=null){
				if (aSession.isOpen()) aSession.close();
			}			
		}
		logger.debug("OUT");
	}
	
	/**
	 * From the Hibernate menuRoles relation at input, gives the corrispondent
	 * <code>MenuRoles</code> object.
	 * 
	 * @param hibMenuRole The Hibernate MenuRole object
	 * 
	 * @return the corrispondent output <code>MenuRoles</code>
	 */
	public MenuRoles toMenuRoles(SbiMenuRole hibMenuRole){
		
		MenuRoles menuRoles = new MenuRoles();					
		
		menuRoles.setMenuId(hibMenuRole.getId().getMenuId());
		menuRoles.setExtRoleId(hibMenuRole.getId().getExtRoleId());
		
		return menuRoles;
	}

}
