/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.security.test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.eng.spago.base.SourceBean;
import it.eng.spago.configuration.ConfigSingleton;
import it.eng.spago.configuration.FileCreatorConfiguration;
import it.eng.spagobi.security.LDAPConnector;
import junit.framework.TestCase;

public class ConfigTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	
	/**
	 * Test config.
	 * 
	 * @throws Exception the exception
	 */
	public void testConfig() throws Exception{
		ConfigSingleton.setRootPath("C:\\progetti\\spagobi\\workspace\\SpagoBIProject\\web-content");
		ConfigSingleton.setConfigFileName("/WEB-INF/conf/master.xml");
		FileCreatorConfiguration fc=new FileCreatorConfiguration("C:\\progetti\\spagobi\\workspace\\SpagoBIProject\\web-content");
		ConfigSingleton.setConfigurationCreation(fc);
		SourceBean configSingleton = (SourceBean)ConfigSingleton.getInstance();
		

		SourceBean config = (SourceBean)configSingleton.getAttribute("LDAP_AUTHORIZATIONS.CONFIG");
		SourceBean t=(SourceBean)config.getAttribute(LDAPConnector.ADMIN_USER);
		String valore=t.getCharacters();
		
		assertEquals("cn=root,dc=spagobi,dc=com", valore);
		
		
		List t2=(List)config.getAttributeAsList(LDAPConnector.ATTRIBUTES_ID);
		String v=(String)((SourceBean)t2.get(0)).getAttribute("name");
		assertEquals("nome", v);
	}
}
