/**

Copyright (c) 2005-2008, Engineering Ingegneria Informatica s.p.a.
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
