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
