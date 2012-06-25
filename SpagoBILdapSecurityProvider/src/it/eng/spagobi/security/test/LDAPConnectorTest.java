/* SpagoBI, the Open Source Business Intelligence suite

* © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this file,
* You can obtain one at http://mozilla.org/MPL/2.0/. */
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
