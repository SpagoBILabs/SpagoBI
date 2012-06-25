/* SpagoBI, the Open Source Business Intelligence suite

* © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this file,
* You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.security;

import it.eng.spago.base.RequestContainer;
import it.eng.spago.base.SourceBean;
import it.eng.spago.configuration.ConfigSingleton;
import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.commons.bo.Role;
import it.eng.spagobi.commons.constants.SpagoBIConstants;
import it.eng.spagobi.services.security.bo.SpagoBIUserProfile;
import it.eng.spagobi.services.security.service.ISecurityServiceSupplier;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.novell.ldap.LDAPException;

/**
 * Implementation of the IEngUserProfile interface Factory. Defines methods to
 * get a IEngUserProfile starting from the exo user information
 */
public class LdapUserProfileFactoryImpl implements ISecurityServiceSupplier {

    static private Logger logger = Logger.getLogger(LdapUserProfileFactoryImpl.class);

    /*
     * (non-Javadoc)
     * 
     * @see it.eng.spagobi.services.security.service.ISecurityServiceSupplier#checkAuthorization(java.lang.String,
     *      java.lang.String)
     */
    public boolean checkAuthorization(String userId, String pwd) {
	logger.warn("NOT IMPLEMENTED!!!!!!!!");
	return false;
    }

    public SpagoBIUserProfile checkAuthentication(String userId, String psw) {
	logger.debug("IN");
	LDAPConnector conn = LdapConnectorFactory.createLDAPConnector();
	try {
	    if ( conn.autenticateUser(userId, psw)){
	    	SpagoBIUserProfile obj=new SpagoBIUserProfile();
	    	obj.setUniqueIdentifier(userId);
	    	obj.setUserId(userId);
	    	obj.setUserName(userId);
	    	return obj;
	    }else{
	    	return null;
	    }
	} catch (UnsupportedEncodingException e) {
	    logger.error("UnsupportedEncodingException", e);
	} catch (LDAPException e) {
	    logger.error("LDAPException", e);
	}
	logger.debug("OUT.False");
	return null;
    }



    /**
     * Return an IEngUserProfile implementation starting from the Principal of
     * the user.
     * 
     * @param userId
     *                the user id
     * 
     * @return The User Profile Interface implementation object
     */
    public SpagoBIUserProfile createUserProfile(String userId) {
	logger.debug("IN.userId="+userId);
	SpagoBIUserProfile profile = new SpagoBIUserProfile();
	profile.setUniqueIdentifier(userId);
	profile.setUserId(userId);

	LDAPConnector conn = LdapConnectorFactory.createLDAPConnector();
	List ldapRoles = null;
	HashMap attributes = null;
	try {
	    ldapRoles = conn.getUserGroup(userId);
	    attributes = conn.getUserAttributes(userId);
	} catch (UnsupportedEncodingException e) {
	    logger.error("UnsupportedEncodingException", e);
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
	profile.setRoles(roleStr);
	profile.setAttributes(createMapAttributes(attributes));
	

	logger.debug("OUT");

	return profile;
    }

 


    private HashMap createMapAttributes(Map attr) {
	HashMap result = new HashMap();

	SourceBean configSingleton = (SourceBean) ConfigSingleton.getInstance();
	SourceBean config = (SourceBean) configSingleton.getAttribute("LDAP_AUTHORIZATIONS.CONFIG");
	List attrList = config.getAttributeAsList(LDAPConnector.ATTRIBUTES_ID);
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

}
