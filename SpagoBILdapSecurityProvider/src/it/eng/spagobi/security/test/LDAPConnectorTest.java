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
package it.eng.spagobi.security.test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.eng.spagobi.security.LDAPConnector;
import junit.framework.TestCase;

public class LDAPConnectorTest extends TestCase {

	private Map attr=new HashMap();
	private LDAPConnector conn=null;

	protected void setUp() throws Exception {
		// TODO Auto-generated method stub
		super.setUp();
		
		
		attr.put(LDAPConnector.ADMIN_USER, "cn=root,dc=spagobi,dc=com");
		attr.put(LDAPConnector.ADMIN_PSW, "86b32dae6c8c1b693dbe310c12297748");
		String[] attrIDs = {"description","sn","cn","title","telephoneNumber"};
		attr.put(LDAPConnector.ATTRIBUTES_ID, attrIDs);
		String[] attrIDsG = {"description","ou"};
		attr.put(LDAPConnector.ATTRIBUTES_ID_GROUP, attrIDsG);
		attr.put(LDAPConnector.HOST, "localhost");
		attr.put(LDAPConnector.PORT, "389");
		attr.put(LDAPConnector.OBJECTCLASS, "person");
		attr.put(LDAPConnector.OU_ATTRIBUTE, "ou");
		attr.put(LDAPConnector.SEARCH_ROOT, "ou=People,dc=spagobi,dc=com");
		attr.put(LDAPConnector.SEARCH_ROOT_GROUP, "ou=Group,dc=spagobi,dc=com");
		attr.put(LDAPConnector.OBJECTCLASS_GROUP, "organizationalUnit");
		attr.put(LDAPConnector.USER_DN, "cn=*,ou=People,dc=spagobi,dc=com");
		conn=new LDAPConnector(attr);	
	}

	/**
	 * Test autenticate user.
	 * 
	 * @throws Exception the exception
	 */
	public void testAutenticateUser() throws Exception{
		assertTrue(conn.autenticateUser("biadmin", "biadmin"));
	}

	/**
	 * Test get user attributes.
	 * 
	 * @throws Exception the exception
	 */
	public void testGetUserAttributes() throws Exception{
		Map attr=conn.getUserAttributes("biadmin");
		assertEquals("descrizione", (String)attr.get("description"));
		assertEquals("biadmin", (String)attr.get("sn"));
		assertEquals("biadmin", (String)attr.get("cn"));
		assertEquals("Dot.", (String)attr.get("title"));
		assertEquals("051/6563707", (String)attr.get("telephoneNumber"));		
	}

	/**
	 * Test get user group.
	 * 
	 * @throws Exception the exception
	 */
	public void testGetUserGroup() throws Exception{
		List attr=conn.getUserGroup("biadmin");
		
		assertTrue(attr.contains("sbi_admin"));
		
	}

	/**
	 * Test get all groups.
	 * 
	 * @throws Exception the exception
	 */
	public void testGetAllGroups() throws Exception{
		List attr=conn.getAllGroups();
		

		assertTrue(attr.contains("sbi_admin"));
		assertTrue(attr.contains("sbi_dev"));		
		assertTrue(attr.contains("sbi_user"));
		assertTrue(attr.contains("sbi_test"));
		assertTrue(attr.contains("Group"));		
	}

}
