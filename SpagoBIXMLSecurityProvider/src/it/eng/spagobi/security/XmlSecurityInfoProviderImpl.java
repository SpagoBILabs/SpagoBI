/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2004 - 2011 Engineering Ingegneria Informatica S.p.A.

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
