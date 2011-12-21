/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 *
 * This file is part of SpagoBI. SpagoBI is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 2.1 of the License, or any later version. 
 * SpagoBI is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received
 * a copy of the GNU Lesser General Public License along with SpagoBI. If not, see: http://www.gnu.org/licenses/.
 * The complete text of SpagoBI license is included in the COPYING.LESSER file. 
 */
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

}
