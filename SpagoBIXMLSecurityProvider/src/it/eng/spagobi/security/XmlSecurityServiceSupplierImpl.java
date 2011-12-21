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
package it.eng.spagobi.security;

import it.eng.spago.base.RequestContainer;
import it.eng.spago.base.SourceBean;
import it.eng.spago.configuration.ConfigSingleton;
import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.commons.bo.Role;
import it.eng.spagobi.commons.constants.SpagoBIConstants;
import it.eng.spagobi.services.security.bo.SpagoBIUserProfile;
import it.eng.spagobi.services.security.service.ISecurityServiceSupplier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

public class XmlSecurityServiceSupplierImpl implements ISecurityServiceSupplier {

    static private Logger logger = Logger.getLogger(XmlSecurityServiceSupplierImpl.class);

    /**
     * Return an SpagoBIUserProfile implementation starting from the id of the
     * user.
     * 
     * @param userId
     *                the user id
     * 
     * @return The User Profile Interface implementation object
     */

    public SpagoBIUserProfile createUserProfile(String userId) {
	logger.debug("IN - userId: " + userId);

	// get config
	SourceBean configSingleton = (SourceBean) ConfigSingleton.getInstance();
	// check if user is defined
	List userPwdsSB = configSingleton.getFilteredSourceBeanAttributeAsList("AUTHORIZATIONS.ENTITIES.USERS.USER",
			"userID", userId);
	if (userPwdsSB == null || userPwdsSB.size() == 0) {
	    logger.error("UserName [" + userId + "] not found!!");
	    return null;
	}
	
	SpagoBIUserProfile profile = new SpagoBIUserProfile();
	profile.setUniqueIdentifier(userId);
	profile.setUserId(userId);

	// get user name
	String userName = userId;
	// get roles of the user
	List rolesSB = configSingleton.getFilteredSourceBeanAttributeAsList(
		"AUTHORIZATIONS.RELATIONS.BEHAVIOURS.BEHAVIOUR", "userID", userName);
	List roles = new ArrayList();
	Iterator iterRolesSB = rolesSB.iterator();
	while (iterRolesSB.hasNext()) {
	    SourceBean roleSB = (SourceBean) iterRolesSB.next();
	    String rolename = (String) roleSB.getAttribute("roleName");
	    roles.add(rolename);
	}

	// start load profile attributes
	HashMap userAttributes = new HashMap();
	List userSB = configSingleton.getFilteredSourceBeanAttributeAsList("AUTHORIZATIONS.ENTITIES.USERS.USER",
		"userID", userName);
	if (userSB.size() == 0) {
	    logger.warn("User " + userName + " not found on configuration!!!");
	} else if (userSB.size() > 1)
	    logger.warn("There are more user with userID=" + userName);
	else {
	    SourceBean userTmp = (SourceBean) userSB.get(0);
	    XmlSecurityInfoProviderImpl xmlSecInfo = new XmlSecurityInfoProviderImpl();
	    List attributesList = xmlSecInfo.getAllProfileAttributesNames();
	    if (attributesList != null) {
		Iterator iterAttributesList = attributesList.iterator();
		while (iterAttributesList.hasNext()) {
		    // Attribute to lookup
		    String attributeName = (String) iterAttributesList.next();
		    String attributeValue = (String) userTmp.getAttribute(attributeName);
		    if (attributeValue != null) {
			logger.debug("Add attribute. " + attributeName + "=" + attributeName + " to the user"
				+ userName);
			userAttributes.put(attributeName, attributeValue);
		    }
		}
	    }
	}

	logger.debug("Attributes load into SpagoBI profile: " + userAttributes);

	// end load profile attributes

	String[] roleStr = new String[roles.size()];
	for (int i = 0; i < roles.size(); i++) {
	    roleStr[i] = (String) roles.get(i);
	}

	profile.setRoles(roleStr);
	profile.setAttributes(userAttributes);
	//profile.setFunctions(readFunctionality(profile.getRoles()));

	logger.debug("OUT");
	return profile;
    }

    public SpagoBIUserProfile checkAuthentication(String userId, String psw) {
	logger.debug("IN - userId: " + userId);

	// get request container
	RequestContainer reqCont = RequestContainer.getRequestContainer();
	// get user name
	String userName = userId;
	// get config
	SourceBean configSingleton = (SourceBean) ConfigSingleton.getInstance();
	List userPwdsSB = configSingleton.getFilteredSourceBeanAttributeAsList("AUTHORIZATIONS.ENTITIES.USERS.USER",
		"userID", userName);
	if (userPwdsSB == null || userPwdsSB.size() == 0) {
	    logger.error("UserName/pws not defined into xml file");
	    return null;
	}
	Iterator iterPwdSB = userPwdsSB.iterator();
	while (iterPwdSB.hasNext()) {
	    SourceBean pwdSB = (SourceBean) iterPwdSB.next();
	    String tmpPwd = (String) pwdSB.getAttribute("password");
	    if (!tmpPwd.equals(psw)) {
		logger.error("UserName/pws not found into xml file");
		return null;
	    }

	}
	SpagoBIUserProfile obj=new SpagoBIUserProfile();
	obj.setUniqueIdentifier(userId);
	obj.setUserId(userId);
	obj.setUserName(userId);

	logger.debug("OUT");
	return obj;
    }

    /**
     * Return a boolean : true if the user is authorized to continue, false
     * otherwise.
     * 
     * @param String
     *                the current user id
     * @param String
     *                the current pwd
     * @return The User Profile Interface implementation object
     */

    public boolean checkAuthorization(String userId, String pwd) {
	logger.warn("checkAuthorization NOT implemented");
	return false;
    }
    
	public SpagoBIUserProfile checkAuthenticationWithToken(String userId,
			String token) {
		logger.error("checkAuthenticationWithToken NOT implemented");
		return null;
	}
}
