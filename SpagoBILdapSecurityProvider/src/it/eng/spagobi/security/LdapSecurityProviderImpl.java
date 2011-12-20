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

import it.eng.spago.base.SourceBean;
import it.eng.spago.configuration.ConfigSingleton;
import it.eng.spago.error.EMFInternalError;
import it.eng.spagobi.commons.bo.Role;
import it.eng.spagobi.commons.constants.SpagoBIConstants;
import it.eng.spagobi.commons.utilities.SpagoBITracer;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.novell.ldap.LDAPException;

/**
 * Implements the IPortalSecurityProvider interface defining method to get the
 * system and user roles.
 */
public class LdapSecurityProviderImpl implements ISecurityInfoProvider {
    static private Logger logger = Logger.getLogger(LdapSecurityProviderImpl.class);

    /**
     * Get all the roles.
     * 
     * @return List of the roles (list of it it.eng.spagobi.bo.Role)
     */
    public List getRoles() {
	logger.debug("IN");
	List roles = new ArrayList();
	LDAPConnector conn = LdapConnectorFactory.createLDAPConnector();
	List ldapRoles = null;
	try {
	    ldapRoles = conn.getAllGroups();

	} catch (UnsupportedEncodingException e) {
	    logger.error("UnsupportedEncodingException", e);
	} catch (LDAPException e) {
	    logger.error("LDAPException", e);
	}

	Iterator iter_sb_roles = ldapRoles.iterator();
	while (iter_sb_roles.hasNext()) {
	    String roleStr = (String) iter_sb_roles.next();
	    Role role = new Role(roleStr, roleStr);
	    logger.debug("ADD ROLE:roleStr=" + roleStr);
	    roles.add(role);
	}
	logger.debug("OUT");
	return roles;
    }

 
    /**
     * Gets the list of names of all attributes of all profiles .
     * 
     * @return the list of names of all attributes of all profiles defined
     */
    public List getAllProfileAttributesNames() {
	logger.debug("IN");
	List toReturn = new ArrayList();

	SourceBean configSingleton = (SourceBean) ConfigSingleton.getInstance();
	SourceBean config = (SourceBean) configSingleton.getAttribute("LDAP_AUTHORIZATIONS.CONFIG");

	List attrList = config.getAttributeAsList(LDAPConnector.ATTRIBUTES_ID);
	Iterator iterAttr = attrList.iterator();
	while (iterAttr.hasNext()) {
	    SourceBean tmp = (SourceBean) iterAttr.next();
	    toReturn.add((String) tmp.getAttribute("name"));
	}
	logger.debug("OUT");
	return toReturn;
    }



}
