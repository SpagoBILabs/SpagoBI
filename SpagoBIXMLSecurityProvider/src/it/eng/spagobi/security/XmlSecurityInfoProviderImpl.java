/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.security;

import it.eng.spago.base.SourceBean;
import it.eng.spago.configuration.ConfigSingleton;
import it.eng.spagobi.commons.bo.Role;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;


public class XmlSecurityInfoProviderImpl implements ISecurityInfoProvider {
	
    static private Logger logger = Logger.getLogger(XmlSecurityInfoProviderImpl.class);
	/**
	 * Get all the roles.
	 * 
	 * @return List of the roles (list of it it.eng.spagobi.bo.Role)
	 */
	public List getRoles() {
	    	logger.debug("IN");
		List roles = new ArrayList();
		ConfigSingleton config = ConfigSingleton.getInstance();
		List sb_roles = config.getAttributeAsList("AUTHORIZATIONS.ENTITIES.ROLES.ROLE");
		Iterator iter_sb_roles = sb_roles.iterator();
		while(iter_sb_roles.hasNext()) {
			SourceBean roleSB = (SourceBean)iter_sb_roles.next();
			String roleName = (String)roleSB.getAttribute("roleName");
			String roleDescription = (String)roleSB.getAttribute("description");
			Role role = new Role(roleName, roleDescription);
			logger.debug("ADD: roleName="+roleName);
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
		List attributes = new ArrayList();
		ConfigSingleton config = ConfigSingleton.getInstance();
		List sb_attributes = config.getAttributeAsList("AUTHORIZATIONS.ENTITIES.ATTRIBUTES.ATTRIBUTE");
		Iterator iter_sb_attributes = sb_attributes.iterator();
		while(iter_sb_attributes.hasNext()) {
			SourceBean attributeSB = (SourceBean)iter_sb_attributes.next();
			String attribute = (String)attributeSB.getAttribute("name");
			logger.debug("ADD: attribute="+attribute);
			attributes.add(attribute);
		}
		logger.debug("OUT");
		return attributes;
	}


	
}
