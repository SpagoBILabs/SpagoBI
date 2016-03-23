/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.security;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.novell.ldap.LDAPConnection;

import it.eng.spagobi.security.LDAPConnector;
import junit.framework.TestCase;

public class LDAPConnectorTest extends TestCase {

	private Map<String, Object> configurationProperties;
	private LDAPConnector ldapConnector;

	protected void setUp() throws Exception {
		super.setUp();
		
		configurationProperties = new HashMap<String,Object>();
		
		// Connection properties
		configurationProperties.put(LDAPConnector.HOST, "localhost");
		configurationProperties.put(LDAPConnector.PORT, "10389");
		configurationProperties.put(LDAPConnector.ADMIN_USER, "uid=admin,ou=system");
		configurationProperties.put(LDAPConnector.ADMIN_PSW, "secret");
		configurationProperties.put(LDAPConnector.BASE_DN, "");
		
		
		// User properties
		configurationProperties.put(LDAPConnector.USER_SEARCH_PATH, "ou=users,ou=system");
		configurationProperties.put(LDAPConnector.USER_OBJECT_CLASS, "person");
		//configurationProperties.put(LDAPConnector.USER_ID_ATTRIBUTE_NAME, "sAMAccountName");
		configurationProperties.put(LDAPConnector.USER_ID_ATTRIBUTE_NAME, "sn");
		configurationProperties.put(LDAPConnector.USER_MEMBEROF_ATTRIBUTE_NAME, "memberOf");
		
		String[] attrIDs = {"cn","sn","uid","memberOf"};
		configurationProperties.put(LDAPConnector.USER_ATTRIBUTE, attrIDs);
		
		// Group properties		
		configurationProperties.put(LDAPConnector.GROUP_SEARCH_PATH, "OU=BI,OU=apps-role");
		configurationProperties.put(LDAPConnector.GROUP_OBJECT_CLASS, "group");
		configurationProperties.put(LDAPConnector.GROUP_ID_ATTRIBUTE_NAME, "cn");
		
		String[] attrIDsG = {"cn"};
		configurationProperties.put(LDAPConnector.GROUP_ATTRIBUTE, attrIDsG);
		configurationProperties.put(LDAPConnector.GROUP_MEMBERS_ATTRIBUTE_NAME, "");
		configurationProperties.put(LDAPConnector.ACCESS_GROUP_NAME, "");
		
		ldapConnector=new LDAPConnector(configurationProperties);	
	}

	
	public void testConnectToLDAP() {
		try {
			LDAPConnection ldapConnection = ldapConnector.connectToLDAP();
			assertNotNull("Connection cannot be null", ldapConnection);
			assertTrue("Connection mus be opened", ldapConnection.isConnected());
			assertTrue("Connection must be alive", ldapConnection.isConnectionAlive());
			assertFalse("Connection must be unbound", ldapConnection.isBound());
		} catch (Throwable t) {
			fail("An unexpected exception occured: " + t.getMessage());
		}
	}
	
	public void testConnectToLDAPAsAdmin() {
		try {
			LDAPConnection ldapConnection = ldapConnector.connectToLDAPAsAdmin();
			assertNotNull("Connection cannot be null", ldapConnection);
			assertTrue("Connection mus be opened", ldapConnection.isConnected());
			assertTrue("Connection must be alive", ldapConnection.isConnectionAlive());
			assertTrue("Connection must be bound", ldapConnection.isBound());
		} catch (Throwable t) {
			fail("An unexpected exception occured: " + t.getMessage());
		}
	}
	
	public void testDoUndirectAuthentication() {
		try {
			assertTrue("MUST be possible to log in with credentials [yfloren/yfloren]"
					, ldapConnector.doUndirectAuthentication("yfloren", "yfloren"));
			assertFalse("MUST NOT be possible to log in with credentials [Gioia/wrongpwd]"
					, ldapConnector.doUndirectAuthentication("Gioia", "wrongpwd"));
		} catch (Throwable t) {
			fail("An unexpected exception occured: " + t.getMessage());
		}
	}
	
	public void testDoDirectAuthentication() {
		try {
			assertTrue("MUST be possible to log in with credentials [" + configurationProperties.get(LDAPConnector.ADMIN_USER) + "/" + configurationProperties.get(LDAPConnector.ADMIN_PSW) + "]"
					, ldapConnector.doDirectAuthentication( (String) configurationProperties.get(LDAPConnector.ADMIN_USER), (String) configurationProperties.get(LDAPConnector.ADMIN_PSW)));
//			assertTrue("MUST be possible to log in with credentials [uid=angioia,ou=users,ou=system/angioia]"
//					, ldapConnector.doDirectAuthentication("uid=angioia,ou=users,ou=system", "angioia"));
			assertFalse("MUST NOT be possible to log in with credentials [" + configurationProperties.get(LDAPConnector.ADMIN_USER) + "/wrongpassword]"
					, ldapConnector.doDirectAuthentication( (String) configurationProperties.get(LDAPConnector.ADMIN_USER), "wrongpassword"));
//			assertFalse("MUST NOT be possible to log in with credentials [uid=angioia,ou=users,ou=system/wrongpassword]"
//					, ldapConnector.doDirectAuthentication("uid=angioia,ou=users,ou=system", "wrongpassword"));
//			assertFalse("MUST NOT be possible to log in with credentials [uid=wrongusername,ou=users,ou=system/wrongpassword]"
//					, ldapConnector.doDirectAuthentication("uid=wrongusername,ou=users,ou=system", "wrongpassword"));
		} catch (Throwable t) {
			fail("An unexpected exception occured: " + t.getMessage());
		}
	}
	
//
//	public void testGetUserGroup() throws Exception{
//		List<String> groups = ldapConnector.getUserGroup("Gioia");
//		assertTrue(groups.contains("bi_cti_admin"));	
//	}
	
	
	public void testGetAllGroups() throws Exception{
		List groups = ldapConnector.getAllGroups();
		System.out.println(groups);
		//assertFalse(groups.contains("Access"));
		assertTrue(groups.size() > 0);
	}
	
	
	public void testAuthenticateUser() throws Exception{
		try {
			assertTrue("MUST be possible to log in with credentials [biadmin/biadmin]"
					, ldapConnector.authenticateUser("biadmin", "biadmin"));
			assertFalse("MUST NOT be possible to log in with credentials [wrong/wrong]"
					, ldapConnector.authenticateUser("wrong", "wrong"));
		} catch (Throwable t) {
			fail("An unexpected exception occured: " + t.getMessage());
		}
	}
	
	
	public void testGetUserGroup() throws Exception{
		try {
			List<String> groups = ldapConnector.getUserGroup("biadmin");
			System.out.println(groups);
			assertTrue(groups.size() > 0);
//			assertFalse(groups.contains("Access"));
		} catch (Throwable t) {
			fail("An unexpected exception occured: " + t.getMessage());
		}
	}
	
	public void testGetUserAttributes() throws Exception{
		try {
			Map<String, String> attributes = ldapConnector.getUserAttributes("biadmin");
			System.out.println(attributes);
			assertTrue(attributes.size() > 0);
			assertTrue(attributes.containsKey("name"));
		} catch (Throwable t) {
			fail("An unexpected exception occured: " + t.getMessage());
		}
	}
	
}
