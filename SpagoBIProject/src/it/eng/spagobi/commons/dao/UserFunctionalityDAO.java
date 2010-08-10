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
package it.eng.spagobi.commons.dao;

import it.eng.spago.error.EMFErrorSeverity;
import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.commons.metadata.SbiExtRoles;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class UserFunctionalityDAO extends AbstractHibernateDAO implements IUserFunctionalityDAO {

    static private Logger logger = Logger.getLogger(UserFunctionalityDAO.class);
    
    /* (non-Javadoc)
     * @see it.eng.spagobi.commons.dao.IUserFunctionalityDAO#readUserFunctionality(java.lang.String[])
     */
    public String[] readUserFunctionality(String[] roles) throws Exception{
	logger.debug("IN");
	if (roles==null || roles.length==0) {
	    logger.warn("The array of roles is empty...");
	    return new String[0];
	}
	ArrayList toReturn = new ArrayList();
	Session aSession = null;
	Transaction tx = null;
	try{
		aSession = getSession();
		tx = aSession.beginTransaction();
		
		List roleTypes = new ArrayList();
		
		for (int i = 0; i < roles.length; i++) {
				String hql = "from SbiExtRoles ser where ser.name=?";
				Query query = aSession.createQuery(hql);
				query.setParameter(0, roles[i]);
				logger.debug("Read role of=" + roles[i]);
				SbiExtRoles spaobiRole = (SbiExtRoles) query.uniqueResult();
				if (spaobiRole != null) {
					String roleTypeCode = spaobiRole.getRoleType().getValueCd();
					if (!roleTypes.contains(roleTypeCode))
						roleTypes.add(roleTypeCode);
				}else{
					logger.warn("The role " + roles[i]+ "doesn't exist in SBI_EXT_ROLES");
				}
			}
		logger.debug("Role type="+roleTypes);
		if (roleTypes.size()==0) logger.warn("No role types found for the user...!!!!!");
		
		//String hql = "from SbiRolesUserFunctionality suf where suf.userFunctionality.domainCd = 'USER_FUNCTIONALITY'" + 
		// " and suf.roleType.valueCd in ("+strRoles+")";
		//String hql = "Select distinct suf.name from SbiUserFunctionality suf where suf.roleType.valueCd in ("+strRoles+") and suf.roleType.domainCd='ROLE_TYPE'";
		String hql = "Select distinct suf.name from SbiUserFunctionality suf where suf.roleType.valueCd in (:ROLE_TYPES) and suf.roleType.domainCd='ROLE_TYPE'";
		Query query = aSession.createQuery(hql);
		query.setParameterList("ROLE_TYPES", roleTypes);
		List userFuncList = query.list();
		Iterator iter=userFuncList.iterator();
		while (iter.hasNext()){
		    String tmp=(String)iter.next();
		    toReturn.add(tmp);
		    logger.debug("Add Functionality="+tmp);
		}
		tx.commit();
	}catch(HibernateException he){
		logger.error("HibernateException during query",he);
		
		if (tx != null) tx.rollback();	

		throw new EMFUserError(EMFErrorSeverity.ERROR, 100);  
	
	}finally{
		if (aSession!=null){
			if (aSession.isOpen()) aSession.close();
		}
		logger.debug("OUT");
	}
	String[] ris=new String[toReturn.size()];
	toReturn.toArray(ris);
	return ris;
	
    }

}
