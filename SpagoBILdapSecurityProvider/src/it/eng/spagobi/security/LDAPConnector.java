/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.security;

import it.eng.spago.security.DefaultCipher;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.novell.ldap.LDAPAttributeSet;
import com.novell.ldap.LDAPConnection;
import com.novell.ldap.LDAPDN;
import com.novell.ldap.LDAPEntry;
import com.novell.ldap.LDAPException;
import com.novell.ldap.LDAPSearchResults;

public class LDAPConnector {
    static private Logger logger = Logger.getLogger(LDAPConnector.class);

    final public static String HOST = "HOST"; // host LDAP

    final public static String PORT = "PORT"; // connection port

    final public static String ADMIN_USER = "ADMIN_USER"; // administration
							    // user-id

    final public static String ADMIN_PSW = "ADMIN_PSW"; // password

    final public static String SEARCH_ROOT = "SEARCH_ROOT"; // base search path

    final public static String ATTRIBUTES_ID = "ATTRIBUTES_ID";// attributes
								// list

    final public static String OU_ATTRIBUTE = "OU_ATTRIBUTE"; // groups
								// attribute
								// name

    final public static String OBJECTCLASS = "OBJECTCLASS"; // object class

    final public static String ATTRIBUTES_ID_GROUP = "ATTRIBUTES_ID_GROUP";
    final public static String SEARCH_ROOT_GROUP = "SEARCH_ROOT_GROUP";
    final public static String OBJECTCLASS_GROUP = "OBJECTCLASS_GROUP";
    final public static String USER_DN = "USER_DN";

    private String host = null;

    private int port = 0;

    private String adminUser = null;

    private String adminPsw = null;

    private String searchRoot = null;
    private String searchRootGroup = null;

    private String[] attrIDs = null;

    private String ouAttributeName = null;
    private String[] attrIDsGroup = null;

    private String objectClass = null;
    private String objectClassGroup = null;
    private String userDn = null;

    /**
     * Instantiates a new lDAP connector.
     * 
     * @param prop
     *                the prop
     */
    public LDAPConnector(Map prop) {
	this.host = (String) prop.get(HOST);
	this.port = Integer.parseInt((String) prop.get(PORT));
	this.adminUser = (String) prop.get(ADMIN_USER);
	this.adminPsw = (String) prop.get(ADMIN_PSW);
	this.searchRoot = (String) prop.get(SEARCH_ROOT);
	this.attrIDs = (String[]) prop.get(ATTRIBUTES_ID);
	this.ouAttributeName = (String) prop.get(OU_ATTRIBUTE);
	this.objectClass = (String) prop.get(OBJECTCLASS);
	this.attrIDsGroup = (String[]) prop.get(ATTRIBUTES_ID_GROUP);
	this.searchRootGroup = (String) prop.get(SEARCH_ROOT_GROUP);
	this.objectClassGroup = (String) prop.get(OBJECTCLASS_GROUP);
	this.userDn = (String) prop.get(USER_DN);
    }

    /**
     * 
     * @return
     * @throws LDAPException
     * @throws UnsupportedEncodingException
     */
    private LDAPConnection createConnection() throws LDAPException, UnsupportedEncodingException {
	logger.debug("IN");
	DefaultCipher defaultCipher = new DefaultCipher();
	LDAPConnection connection = null;

	try {
	    connection = new LDAPConnection();
	    connection.connect(host, port);
	    if (connection.isConnected()) {
		connection.bind(LDAPConnection.LDAP_V3, adminUser, (defaultCipher.decrypt(adminPsw)).getBytes("UTF8"));
	    }
	    if (connection.isBound()) {
		logger.debug("OUT");
		return connection;
	    }
	    if (!connection.isConnected() || !connection.isBound()) {
		logger.debug("OUT");
		logger.warn("!connection.isConnected() || !connection.isBound()");
		throw new LDAPException();
	    }

	} catch (LDAPException e) {
	    logger.error("LDAPException", e);
	    throw e;
	} catch (UnsupportedEncodingException e) {
	    logger.error("UnsupportedEncodingException", e);
	    throw e;
	}
	logger.debug("OUT");
	return null;

    }

    /**
     * Autenticate user.
     * 
     * @param userId
     *                the user id
     * @param psw
     *                the psw
     * 
     * @return true, if autenticate user
     * 
     * @throws LDAPException
     *                 the LDAP exception
     * @throws UnsupportedEncodingException
     *                 the unsupported encoding exception
     */
    public boolean autenticateUser(String userId, String psw) throws LDAPException, UnsupportedEncodingException {
	logger.debug("IN");
	LDAPConnection connection = null;
	String compsedUserId = userDn.replace("*", userId);
	try {
	    connection = new LDAPConnection();
	    connection.connect(host, port);
	    if (connection.isConnected()) {
		connection.bind(LDAPConnection.LDAP_V3, compsedUserId, psw.getBytes("UTF8"));
	    }
	    if (connection.isBound()) {
		logger.debug("OUT");
		return true;
	    }
	    if (!connection.isConnected() || !connection.isBound()) {
		logger.debug("OUT");
		logger.warn("!connection.isConnected() || !connection.isBound()");
		return false;
	    }

	} catch (LDAPException e) {
	    logger.error("LDAPException",e);
	    throw e;
	} catch (UnsupportedEncodingException e) {
	    logger.error("UnsupportedEncodingException",e);
	    throw e;
	}
	logger.debug("OUT");
	return false;

    }

    /**
     * Gets the user attributes.
     * 
     * @param userId
     *                the user id
     * 
     * @return the user attributes
     * 
     * @throws LDAPException
     *                 the LDAP exception
     * @throws UnsupportedEncodingException
     *                 the unsupported encoding exception
     */
    public HashMap getUserAttributes(String userId) throws LDAPException, UnsupportedEncodingException {
	logger.debug("IN");
	HashMap userAttributes = new HashMap();

	LDAPConnection connection = createConnection();
	if (connection != null) {
	    try {
		LDAPSearchResults searchResults = connection.search(searchRoot, LDAPConnection.SCOPE_SUB,
			"(&(objectclass=" + objectClass + ")(cn=" + userId + "))", attrIDs, false);

		LDAPEntry entry = null;
		if (searchResults.hasMore()) {
		    try {
			entry = searchResults.next();
		    } catch (LDAPException e) {
			logger.error("LDAPException",e);
		    }

		    if (entry != null) {
			userAttributes.put("dn", entry.getDN());
			for (int i = 0; i < attrIDs.length; i++) {
			    String attr = attrIDs[i];
			    userAttributes.put(attr, entry.getAttribute(attr).getStringValue());
			}

		    }
		}
	    } catch (LDAPException e) {
		logger.error("LDAPException",e);
		throw e;
	    } finally {
		if (connection != null)
		    connection.disconnect();
	    }

	}
	logger.debug("OUT");
	return userAttributes;

    }

    /**
     * Gets the user group.
     * 
     * @param userId
     *                the user id
     * 
     * @return the user group
     * 
     * @throws LDAPException
     *                 the LDAP exception
     * @throws UnsupportedEncodingException
     *                 the unsupported encoding exception
     */
    public List getUserGroup(String userId) throws LDAPException, UnsupportedEncodingException {
	logger.debug("IN");
	List userAttributes = new ArrayList();

	LDAPConnection connection = createConnection();
	if (connection != null) {
	    try {
		String[] attributes = new String[1];
		attributes[0] = ouAttributeName;
		LDAPSearchResults searchResults = connection.search(searchRoot, LDAPConnection.SCOPE_SUB,
			"(&(objectclass=" + objectClass + ")(cn=" + userId + "))", attributes, false);

		// popolamento userAttributes con attributeName e attributeValue
		LDAPEntry entry = null;
		if (searchResults.hasMore()) {
		    try {
			entry = searchResults.next();
		    } catch (LDAPException e) {
			logger.error("LDAPException",e);
			throw e;
		    }
		    if (entry != null) {
			String[] ou = entry.getAttribute(ouAttributeName).getStringValueArray();
			for (int i = 0; i < ou.length; i++) {
			    if (ou[i].indexOf("=") > 0) {
				String[] appUserOUs = LDAPDN.explodeDN(ou[i], false);
				for (int j = appUserOUs.length - 1; j >= 0; j--) {
				    String s = appUserOUs[j];
				    if (s.toUpperCase().startsWith("OU") && !userAttributes.contains(s.substring(3))) {
					userAttributes.add(s.substring(3));
				    }
				}

			    } else {
				userAttributes.add(ou[i]);
			    }
			}
		    }
		}
	    } catch (LDAPException e) {
		logger.error("LDAPException",e);
		throw e;
	    } finally {
		if (connection != null)
		    connection.disconnect();
	    }

	}
	logger.debug("OUT");
	return userAttributes;

    }

    /**
     * Gets the all groups.
     * 
     * @return the all groups
     * 
     * @throws LDAPException
     *                 the LDAP exception
     * @throws UnsupportedEncodingException
     *                 the unsupported encoding exception
     */
    public List getAllGroups() throws LDAPException, UnsupportedEncodingException {
	logger.debug("IN");
	List groups = new ArrayList();

	LDAPConnection connection = createConnection();
	if (connection != null) {
	    try {

		LDAPSearchResults searchResults = connection.search(searchRootGroup, LDAPConnection.SCOPE_SUB,
			"(objectclass=" + objectClassGroup + ")", attrIDsGroup, false);

		LDAPEntry entry = null;
		LDAPAttributeSet attributeSet = null;
		while (searchResults.hasMore()) {

		    try {
			entry = searchResults.next();
			if (entry != null) {
			    attributeSet = entry.getAttributeSet();
			    groups.add(attributeSet.getAttribute(ouAttributeName).getStringValue());

			}
		    } catch (LDAPException e) {
			logger.error("LDAPException",e);
			throw e;
		    }
		}

	    } catch (LDAPException e) {
		logger.error("LDAPException",e);
		throw e;
	    } finally {
		if (connection != null)
		    connection.disconnect();
	    }

	}
	logger.debug("OUT");
	return groups;

    }

}
