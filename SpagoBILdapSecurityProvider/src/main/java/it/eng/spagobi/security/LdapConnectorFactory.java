/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.security;

import it.eng.spago.base.SourceBean;
import it.eng.spago.configuration.ConfigSingleton;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LdapConnectorFactory {

	/**
	 * Creates a new LdapConnector object.
	 * 
	 * @return the LDAP connector
	 */
	public static LDAPConnector createLDAPConnector(){
		
		SourceBean configSingleton = (SourceBean)ConfigSingleton.getInstance();
		SourceBean config = (SourceBean)configSingleton.getAttribute("LDAP_AUTHORIZATIONS.CONFIG");
		
		Map<String, Object> attrbutes = new HashMap<String, Object>();

		attrbutes.put(LDAPConnector.ADMIN_USER, getAttribute(config, LDAPConnector.ADMIN_USER));
		attrbutes.put(LDAPConnector.ADMIN_PSW, getAttribute(config, LDAPConnector.ADMIN_PSW));
		attrbutes.put(LDAPConnector.HOST, getAttribute(config, LDAPConnector.HOST));
		attrbutes.put(LDAPConnector.PORT, getAttribute(config, LDAPConnector.PORT));
		attrbutes.put(LDAPConnector.BASE_DN, getAttribute(config, LDAPConnector.BASE_DN));
		
		attrbutes.put(LDAPConnector.USER_SEARCH_PATH, getAttribute(config, LDAPConnector.USER_SEARCH_PATH));
		attrbutes.put(LDAPConnector.USER_OBJECT_CLASS, getAttribute(config, LDAPConnector.USER_OBJECT_CLASS));
		attrbutes.put(LDAPConnector.USER_ID_ATTRIBUTE_NAME, getAttribute(config, LDAPConnector.USER_ID_ATTRIBUTE_NAME));
		attrbutes.put(LDAPConnector.USER_MEMBEROF_ATTRIBUTE_NAME, getAttribute(config, LDAPConnector.USER_MEMBEROF_ATTRIBUTE_NAME));
		
		List<SourceBean> userAttributesSB = config.getAttributeAsList(LDAPConnector.USER_ATTRIBUTE);
		String[] userAttributes = new String[userAttributesSB.size()];
		int i=0;
		for (SourceBean userAttributeSB : userAttributesSB){
			userAttributes[i++] = userAttributeSB.getCharacters();
		}
		attrbutes.put(LDAPConnector.USER_ATTRIBUTE, userAttributes);
		
		
		attrbutes.put(LDAPConnector.GROUP_SEARCH_PATH, getAttribute(config, LDAPConnector.GROUP_SEARCH_PATH));
		attrbutes.put(LDAPConnector.GROUP_OBJECT_CLASS, getAttribute(config, LDAPConnector.GROUP_OBJECT_CLASS));	
		attrbutes.put(LDAPConnector.GROUP_ID_ATTRIBUTE_NAME, getAttribute(config, LDAPConnector.GROUP_ID_ATTRIBUTE_NAME));	
		
		List<SourceBean> roleAttributesSB = config.getAttributeAsList(LDAPConnector.GROUP_ATTRIBUTE);
		String[] roleAttributes=new String[roleAttributesSB.size()];
		int j=0;
		for(SourceBean roleAttributeSB : roleAttributesSB){
			roleAttributes[j++]= roleAttributeSB.getCharacters();
		}
		attrbutes.put(LDAPConnector.GROUP_ATTRIBUTE, roleAttributes);

		attrbutes.put(LDAPConnector.GROUP_MEMBERS_ATTRIBUTE_NAME, getAttribute(config, LDAPConnector.GROUP_MEMBERS_ATTRIBUTE_NAME));
		attrbutes.put(LDAPConnector.ACCESS_GROUP_NAME, getAttribute(config, LDAPConnector.ACCESS_GROUP_NAME));
		
		return new LDAPConnector(attrbutes);	
	}
	
	private static String getAttribute(SourceBean config, String attributeName) {
		return ((SourceBean)config.getAttribute(attributeName)).getCharacters();
	}

	public static String getAttribute(String attributeName) {
		SourceBean configSingleton = (SourceBean) ConfigSingleton.getInstance();
		SourceBean config = (SourceBean) configSingleton.getAttribute("LDAP_AUTHORIZATIONS.CONFIG");
		return getAttribute(config, attributeName);
	}
}
