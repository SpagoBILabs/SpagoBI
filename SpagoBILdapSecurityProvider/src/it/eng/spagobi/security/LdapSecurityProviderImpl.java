/* SpagoBI, the Open Source Business Intelligence suite

* © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this file,
* You can obtain one at http://mozilla.org/MPL/2.0/. */
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
