/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 *
 * This file is part of SpagoBI. SpagoBI is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 2.1 of the License, or any later version. 
 * SpagoBI is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received
 * a copy of the GNU Lesser General Public License along with SpagoBI. If not, see: http://www.gnu.org/licenses/.
 * The complete text of SpagoBI license is included in the COPYING.LESSER file. 
 */
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
