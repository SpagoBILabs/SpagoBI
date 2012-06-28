/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.security;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import it.eng.spago.base.SourceBean;
import it.eng.spago.configuration.ConfigSingleton;

public class LdapConnectorFactory {

	/**
	 * Creates a new LdapConnector object.
	 * 
	 * @return the LDAP connector
	 */
	public static LDAPConnector createLDAPConnector(){
		
		SourceBean configSingleton = (SourceBean)ConfigSingleton.getInstance();
		SourceBean config = (SourceBean)configSingleton.getAttribute("LDAP_AUTHORIZATIONS.CONFIG");
		
		Map attr=new HashMap();

		
		String t=((SourceBean)config.getAttribute(LDAPConnector.ADMIN_USER)).getCharacters();
	
		
		attr.put(LDAPConnector.ADMIN_USER, ((SourceBean)config.getAttribute(LDAPConnector.ADMIN_USER)).getCharacters());
		attr.put(LDAPConnector.ADMIN_PSW, ((SourceBean)config.getAttribute(LDAPConnector.ADMIN_PSW)).getCharacters());
		attr.put(LDAPConnector.HOST, ((SourceBean)config.getAttribute(LDAPConnector.HOST)).getCharacters());
		attr.put(LDAPConnector.PORT, ((SourceBean)config.getAttribute(LDAPConnector.PORT)).getCharacters());
		attr.put(LDAPConnector.OBJECTCLASS, ((SourceBean)config.getAttribute(LDAPConnector.OBJECTCLASS)).getCharacters());
		attr.put(LDAPConnector.OU_ATTRIBUTE, ((SourceBean)config.getAttribute(LDAPConnector.OU_ATTRIBUTE)).getCharacters());
		attr.put(LDAPConnector.SEARCH_ROOT, ((SourceBean)config.getAttribute(LDAPConnector.SEARCH_ROOT)).getCharacters());
		attr.put(LDAPConnector.SEARCH_ROOT_GROUP, ((SourceBean)config.getAttribute(LDAPConnector.SEARCH_ROOT_GROUP)).getCharacters());
		attr.put(LDAPConnector.OBJECTCLASS_GROUP, ((SourceBean)config.getAttribute(LDAPConnector.OBJECTCLASS_GROUP)).getCharacters());		
		attr.put(LDAPConnector.USER_DN, ((SourceBean)config.getAttribute(LDAPConnector.USER_DN)).getCharacters());
		
		List attrList=config.getAttributeAsList(LDAPConnector.ATTRIBUTES_ID);
		Iterator iterAttr=attrList.iterator();
		String[] elencoAttributi=new String[attrList.size()];
		int i=0;
		while (iterAttr.hasNext()){
			SourceBean tmp=(SourceBean)iterAttr.next();
			elencoAttributi[i]=tmp.getCharacters();
			i++;
		}
		attr.put(LDAPConnector.ATTRIBUTES_ID, elencoAttributi);


		List attrListGroup=config.getAttributeAsList(LDAPConnector.ATTRIBUTES_ID_GROUP);
		Iterator iterAttrGroup=attrListGroup.iterator();
		String[] elencoAttributiGroup=new String[attrListGroup.size()];
		int j=0;
		while (iterAttrGroup.hasNext()){
			SourceBean tmp=(SourceBean)iterAttrGroup.next();
			elencoAttributiGroup[j]=tmp.getCharacters();
			j++;
		}
		attr.put(LDAPConnector.ATTRIBUTES_ID_GROUP, elencoAttributiGroup);


		return new LDAPConnector(attr);	
	}
}
