/**

Copyright (C) 2004 - 2011, Engineering Ingegneria Informatica s.p.a.
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, this list of 
      conditions and the following disclaimer.
      
 * Redistributions in binary form must reproduce the above copyright notice, this list of 
      conditions and the following disclaimer in the documentation and/or other materials 
      provided with the distribution.
      
 * Neither the name of the Engineering Ingegneria Informatica s.p.a. nor the names of its contributors may
      be used to endorse or promote products derived from this software without specific
      prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND 
CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, 
INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, 
EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE

 **/
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
