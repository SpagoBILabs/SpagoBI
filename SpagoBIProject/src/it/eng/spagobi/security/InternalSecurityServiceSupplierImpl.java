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
package it.eng.spagobi.security;

import it.eng.spago.base.RequestContainer;
import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.commons.bo.Role;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.commons.dao.IRoleDAO;
import it.eng.spagobi.commons.metadata.SbiExtRoles;
import it.eng.spagobi.profiling.bean.SbiAttribute;
import it.eng.spagobi.profiling.bean.SbiExtUserRoles;
import it.eng.spagobi.profiling.bean.SbiUser;
import it.eng.spagobi.profiling.bean.SbiUserAttributes;
import it.eng.spagobi.services.security.bo.SpagoBIUserProfile;
import it.eng.spagobi.services.security.service.ISecurityServiceSupplier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

public class InternalSecurityServiceSupplierImpl implements
		ISecurityServiceSupplier {
	
	static private Logger logger = Logger.getLogger(InternalSecurityServiceSupplierImpl.class);

	public SpagoBIUserProfile checkAuthentication(String userId, String psw) {
		logger.debug("IN - userId: " + userId);

		// get request container
		RequestContainer reqCont = RequestContainer.getRequestContainer();
		// get user name
		String userName = userId;
		// get user from database
		
		try {
			SbiUser user = DAOFactory.getSbiUserDAO().loadSbiUserByUserId(userId);
			if(user == null){
				logger.error("UserName not found into database");
				return null;
			}
			String password = user.getPassword();
			if (password == null || password.length() == 0) {
			    logger.error("UserName/pws not defined into database");
			    return null;
			}else if(!password.equals(psw)){
			//else check password
				logger.error("UserName/pws not found into database");
				return null;
			}
			SpagoBIUserProfile obj=new SpagoBIUserProfile();
			obj.setUniqueIdentifier(userId);
			obj.setUserId(userId);
			obj.setUserName(userId);
			
			logger.debug("OUT");
			return obj;
		} catch (EMFUserError e) {
			logger.error(e.getMessage(), e);
		}
		return null;


	}

	public SpagoBIUserProfile checkAuthenticationWithToken(String userId,
			String token) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean checkAuthorization(String userId, String function) {
		// TODO Auto-generated method stub
		return false;
	}

	public SpagoBIUserProfile createUserProfile(String userId) {
		logger.debug("IN - userId: " + userId);
		SpagoBIUserProfile profile = null;
		try {
			SbiUser user = DAOFactory.getSbiUserDAO().loadSbiUserByUserId(userId);

			if (user == null) {
				logger.error("UserName [" + userId + "] not found!!");
			    return null;
			}
	
			profile = new SpagoBIUserProfile();
			profile.setUniqueIdentifier(userId);
			profile.setUserId(userId);
			profile.setUserName(user.getFullName());
	
			// get user name
			String userName = userId;
			// get roles of the user
			
			ArrayList<SbiExtRoles> rolesSB = DAOFactory.getSbiUserDAO().loadSbiUserRolesById(user.getId());
			List roles = new ArrayList();
			Iterator iterRolesSB = rolesSB.iterator();
			
			IRoleDAO roleDAO = DAOFactory.getRoleDAO();
			while (iterRolesSB.hasNext()) {
				SbiExtRoles roleSB = (SbiExtRoles) iterRolesSB.next();

			    roles.add(roleSB.getName());
			}
			HashMap attributes = new HashMap();
			ArrayList<SbiUserAttributes> attribs = DAOFactory.getSbiUserDAO().loadSbiUserAttributesById(user.getId());
			if(attribs != null){
				Iterator iterAttrs = attribs.iterator();
				while(iterAttrs.hasNext()){
				    // Attribute to lookup
					SbiUserAttributes attribute = (SbiUserAttributes) iterAttrs.next();
					
					String attributeName = attribute.getSbiAttribute().getAttributeName();

				    String attributeValue = attribute.getAttributeValue();
				    if (attributeValue != null) {
				    	logger.debug("Add attribute. " + attributeName + "=" + attributeName + " to the user"
				    			+ userName);
						attributes.put(attributeName, attributeValue);
				    }
				}
			}
	
			logger.debug("Attributes load into SpagoBI profile: " + attributes);
	
			// end load profile attributes
	
			String[] roleStr = new String[roles.size()];
			for (int i = 0; i < roles.size(); i++) {
			    roleStr[i] = (String) roles.get(i);
			}
	
			profile.setRoles(roleStr);
			profile.setAttributes(attributes);
		} catch (EMFUserError e) {
			logger.error(e.getMessage(), e);
			return null;
		}
		logger.debug("OUT");
		return profile;

	}

}
