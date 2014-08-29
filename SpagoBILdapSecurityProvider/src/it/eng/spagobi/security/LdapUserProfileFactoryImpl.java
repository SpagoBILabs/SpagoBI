/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.security;

import it.eng.spago.base.SourceBean;
import it.eng.spago.configuration.ConfigSingleton;
import it.eng.spagobi.commons.bo.Role;
import it.eng.spagobi.commons.utilities.StringUtilities;
import it.eng.spagobi.services.security.bo.SpagoBIUserProfile;
import it.eng.spagobi.services.security.service.ISecurityServiceSupplier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.novell.ldap.LDAPException;

public class LdapUserProfileFactoryImpl implements ISecurityServiceSupplier {

    static private Logger logger = Logger.getLogger(LdapUserProfileFactoryImpl.class);
    
    /**
     * @return The profile of the user if the provided credentials are valid. null otherwise. Note: the profile if returned contains
     * only the user id and user name. All other properties are not initialized yet.
     */
    public SpagoBIUserProfile checkAuthentication(String username, String password) {
		
    	SpagoBIUserProfile userProfile;
    	
    	logger.debug("IN");
			
    	userProfile = null;
    	try {
    		logger.debug("Authenticating user [" + username + "] ...");
    		LDAPConnector ldapConnector = LdapConnectorFactory.createLDAPConnector();
		    if ( ldapConnector.authenticateUser(username, password)){
		    	userProfile = new SpagoBIUserProfile();
		    	userProfile.setUniqueIdentifier(username);
		    	userProfile.setUserId(username);
		    	userProfile.setUserName(username);
		    	logger.info("User [" + username + "] succesfully authenticated");
		    } else {
		    	logger.warn("Impossible to authenticate user [" + username + "]");
		    }
		} catch (Exception e) {
		    logger.error("An unexpected error occure while loading profile of user [" + username + "]", e);
		    return null;
		} finally {
			logger.debug("OUT");
		}
		
		return userProfile;
    }



    public SpagoBIUserProfile createUserProfile(String username) {
		
    	logger.debug("IN");
  
    	logger.debug("Creating user profile for user [" + username + "] ...");
		
    	SpagoBIUserProfile userProfile = new SpagoBIUserProfile();
		userProfile.setUniqueIdentifier(username);
		userProfile.setUserId(username);
		userProfile.setUserName(username);
		userProfile.setIsSuperadmin(false);
	
		LDAPConnector ldapConnector = LdapConnectorFactory.createLDAPConnector();
		List ldapRoles = null;
		Map attributes = null;
		try {
		    ldapRoles = ldapConnector.getUserGroup(username);
		    attributes = ldapConnector.getUserAttributes(username);
		} catch (LDAPException e) {
		    logger.error("LDAPException", e);
		}
		Iterator iterRoles = ldapRoles.iterator();
		List roles = new ArrayList();
		
		while (iterRoles.hasNext()) {
		    String roleName = (String) iterRoles.next();
		    logger.debug("RoleName from LDAP:"+roleName);
		    if (roleName!=null && !roleName.equals("Group")){
			Role role = new Role(roleName, roleName);
			roles.add(role);
		    }
		    
		}
	
		String[] roleStr = new String[roles.size()];
		for (int i = 0; i < roles.size(); i++) {
		    roleStr[i] = (String) ((Role)roles.get(i)).getName();
		}
		userProfile.setRoles(roleStr);
		
		HashMap userAttributes = createMapAttributes(attributes);
		userProfile.setAttributes(userAttributes);
		
		String userNameAttributeKey = LdapConnectorFactory.getAttribute(LDAPConnector.USER_NAME_ATTRIBUTE_NAME);
		if (!StringUtilities.isEmpty(userNameAttributeKey)) {
			String userName = (String) userAttributes.get(userNameAttributeKey);
			logger.debug("User name is [" + userName + "]");
			if (!StringUtilities.isEmpty(userName)) {
				userProfile.setUserName(userName);
			}
		}
		
		String superAdminAttributeKey = LdapConnectorFactory
				.getAttribute(LDAPConnector.SUPER_ADMIN_ATTRIBUTE_NAME);
		if (!StringUtilities.isEmpty(superAdminAttributeKey)) {
			String superAdmin = (String) userAttributes
					.get(superAdminAttributeKey);
			logger.debug("Super admin is [" + superAdmin + "]");
			if (!StringUtilities.isEmpty(superAdmin)
					&& superAdmin.equalsIgnoreCase("true")) {
				userProfile.setIsSuperadmin(true);
			}
		}
		
		logger.debug("OUT");
	
		return userProfile;
    }

 


    private HashMap createMapAttributes(Map attr) {
	HashMap result = new HashMap();

	SourceBean configSingleton = (SourceBean) ConfigSingleton.getInstance();
	SourceBean config = (SourceBean) configSingleton.getAttribute("LDAP_AUTHORIZATIONS.CONFIG");
	List attrList = config.getAttributeAsList(LDAPConnector.USER_ATTRIBUTE);
	Iterator iterAttr = attrList.iterator();
	while (iterAttr.hasNext()) {
	    SourceBean tmp = (SourceBean) iterAttr.next();
	    String key = (String) tmp.getAttribute("name");
	    String keyLdap = (String) tmp.getCharacters();
	    String value = (String) attr.get(keyLdap);
	    if (value != null && key != null) {
		result.put(key, value);
	    }
	}
	return result;
    }

	public SpagoBIUserProfile checkAuthenticationWithToken(String userId,
			String token) {
		logger.error("checkAuthenticationWithToken NOT implemented");
		return null;
	}
	
	
	// ================================================================================
	// DEPRECATION TAIL
	// ================================================================================
	
	/**
     * @deprecated
     */
    public boolean checkAuthorization(String userId, String pwd) {
    	throw new UnsupportedOperationException("Method not implemented because it is deprecated in the parent interface");
    }

}
