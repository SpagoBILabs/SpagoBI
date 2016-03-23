/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.security;

import it.eng.spagobi.commons.utilities.StringUtilities;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.novell.ldap.LDAPAttribute;
import com.novell.ldap.LDAPConnection;
import com.novell.ldap.LDAPEntry;
import com.novell.ldap.LDAPException;
import com.novell.ldap.LDAPSearchResults;

public class LDAPConnector {
    
	static private Logger logger = Logger.getLogger(LDAPConnector.class);

	// server
    final public static String HOST = "HOST"; // host LDAP
    final public static String PORT = "PORT"; // connection port
    final public static String ADMIN_USER = "ADMIN_USER"; // administration user-id
    final public static String ADMIN_PSW = "ADMIN_PSW"; // password
    final public static String BASE_DN = "BASE_DN";
    
    // user
    final public static String USER_SEARCH_PATH = "USER_SEARCH_PATH"; // base search path
    final public static String USER_OBJECT_CLASS = "USER_OBJECT_CLASS";
    final public static String USER_ID_ATTRIBUTE_NAME = "USER_ID_ATTRIBUTE_NAME";
    final public static String USER_NAME_ATTRIBUTE_NAME = "USER_NAME_ATTRIBUTE_NAME";
    final public static String USER_MEMBEROF_ATTRIBUTE_NAME = "USER_MEMBEROF_ATTRIBUTE_NAME";
    final public static String SUPER_ADMIN_ATTRIBUTE_NAME = "SUPER_ADMIN_ATTRIBUTE_NAME";
    final public static String USER_ATTRIBUTE = "USER_ATTRIBUTE";// attributes list
    
    // group
    final public static String GROUP_SEARCH_PATH = "GROUP_SEARCH_PATH";
    final public static String GROUP_OBJECT_CLASS = "GROUP_OBJECT_CLASS";
    final public static String GROUP_ID_ATTRIBUTE_NAME = "GROUP_ID_ATTRIBUTE_NAME";
    final public static String GROUP_ATTRIBUTE = "GROUP_ATTRIBUTE";
    final public static String GROUP_MEMBERS_ATTRIBUTE_NAME = "GROUP_MEMBERS_ATTRIBUTE_NAME";
    final public static String ACCESS_GROUP_NAME = "ACCESS_GROUP_NAME";
   

    private String host;
    private int port;
    private String adminUsername;
    private String adminPassword;
    private String baseDN;
    
    private String userSearchPath;
    private String userObjectClass;
    private String userMemberOfAttributeName;
    private String userIdAttributeName;
    private String[] userAttributeNames;
    private boolean userAttributeForceCreation;
    private String userDefaultAttributeValue;
    
    private String groupSearchPath;
    private String groupObjectClass;
    private String groupIdAttributeName;
    private String[] groupAttributeNames;
    private String groupMembersAttributeName;
    private String accessGroupName;
    
    public LDAPConnector(Map<String, Object> configuration) {
		this.host = (String) configuration.get(HOST);
		this.port = Integer.parseInt((String) configuration.get(PORT));
		this.adminUsername = (String) configuration.get(ADMIN_USER);
		this.adminPassword = (String) configuration.get(ADMIN_PSW);
		this.baseDN = (String) configuration.get(BASE_DN);
		
		// user
		this.userSearchPath = (String) configuration.get(USER_SEARCH_PATH);
		this.userObjectClass = (String) configuration.get(USER_OBJECT_CLASS);
		this.userMemberOfAttributeName = (String) configuration.get(USER_MEMBEROF_ATTRIBUTE_NAME);
		this.userIdAttributeName = (String) configuration.get(USER_ID_ATTRIBUTE_NAME);
		this.userAttributeNames = (String[]) configuration.get(USER_ATTRIBUTE);
		this.userAttributeForceCreation = true;
		this.userDefaultAttributeValue = "nd";
		
		// groups
		this.groupSearchPath = (String) configuration.get(GROUP_SEARCH_PATH);
		this.groupObjectClass = (String) configuration.get(GROUP_OBJECT_CLASS);
		this.groupIdAttributeName = (String) configuration.get(GROUP_ID_ATTRIBUTE_NAME);
		this.groupAttributeNames = (String[]) configuration.get(GROUP_ATTRIBUTE);
		this.groupMembersAttributeName = (String) configuration.get(GROUP_MEMBERS_ATTRIBUTE_NAME);
		this.accessGroupName = (String) configuration.get(ACCESS_GROUP_NAME);
    }

    protected LDAPConnection connectToLDAP() {
    	LDAPConnection connection;
    	
    	logger.debug("IN");
    	connection = null;
    	try {
    		logger.debug("Connecting to LDAP at url [" + host + ": " + port + "] ...");
    		connection = new LDAPConnection();
		    connection.connect(host, port);
		    if(connection.isConnected() == false) {
		    	throw new RuntimeException("Impossible to open the connection to LDAP at url [" + host + ": " + port + "]");
		    }
		    logger.info("Succesfully connectd to LDAP at url [" + host + ": " + port + "]");
		    return connection;
    	} catch(Throwable t) {
    		throw new RuntimeException("Impossible to connect to LDAP at url [" + host + ": " + port + "]", t);
    	} finally {
    		logger.debug("OUT");
    	}
    }
    
    protected LDAPConnection connectToLDAPAsAdmin()  {
		
    	logger.debug("IN");
		
		LDAPConnection connection = null;
	
		try {
			logger.debug("Binding user [" + adminUsername + "] to LDAP ...");
		    connection = connectToLDAP();
		    //connection.bind(LDAPConnection.LDAP_V3, adminUser, (defaultCipher.decrypt(adminPsw)).getBytes("UTF8"));
		    connection.bind(LDAPConnection.LDAP_V3, adminUsername, adminPassword.getBytes("UTF8"));
		   
		    if (connection.isBound() == false ) {
		    	throw new RuntimeException("Impossible to bind user [" + adminUsername +"] to LDAP");
		    }	
		    
		    logger.info("User [" + adminUsername + "] succesfully bounded to LDAP");
		} catch (Throwable t) {
			throw new RuntimeException("Impossible to connect to LDAP at url [" + host + ": " + port + "]", t);
		} finally {
			logger.debug("OUT");
		}
		
		 return connection;
    }
    
    protected void closeConnection(LDAPConnection connection) {
    	try {
			if(connection != null) {
				connection.disconnect();
			}
		} catch (Throwable t) {
			throw new RuntimeException("An unexpected error occured while closing connection", t);
		}
    }
    
    public boolean authenticateUser(String username, String password) throws LDAPException, UnsupportedEncodingException {
    	boolean authenticated = authenticateUser("UNDIRECT", username, password);
    	if (!authenticated) {
    		return false;
    	}
    	// we have to check that user belongs to access group
    	if (isAccessGroupDefined()) {
    		logger.debug("Access group is defined: checking if user belongs to access group");
    		return checkUserIsInAccessGroup(username);
    	} else {
    		logger.debug("Access group is not defined");
    		return true;
    	}
    }
    
    private boolean isAccessGroupDefined() {
    	return !StringUtilities.isEmpty(accessGroupName);
	}

	public boolean checkUserIsInAccessGroup(String username) {
    	logger.debug("IN: username " + username);
    	boolean toReturn = false;
    	List<String> usersInAccessGroup = getUsersInAccessGroup();
    	logger.debug("Users in Access group are [" + usersInAccessGroup + "]");
    	toReturn = usersInAccessGroup.contains(username);
    	logger.debug("OUT: " + toReturn);
    	return toReturn;
    	
	}

	private List<String> getUsersInAccessGroup() {
    	LDAPConnection connection = null;
    	List<String> users = new ArrayList<String>();
    	
    	logger.debug("IN");
    	
    	try {
    		connection = this.connectToLDAPAsAdmin();
    		
			String searchPath = groupSearchPath;
			
			if (StringUtilities.isEmpty(searchPath)) {
				searchPath = baseDN;
			} else {
				if(StringUtilities.isNotEmpty(baseDN)) {
					searchPath +=  "," + baseDN;
				}
			}
			
			String searchQuery = "(&(objectclass=" + groupObjectClass + ")(" + groupIdAttributeName + "=" + accessGroupName + "))";
			LDAPSearchResults searchResults = connection.search(searchPath, LDAPConnection.SCOPE_SUB,
					searchQuery, groupAttributeNames, false);
	
			if (searchResults.hasMore()) {
				LDAPEntry entry = searchResults.next();
				if (entry != null) {
				    LDAPAttribute groupIdAttribute = entry.getAttribute(groupMembersAttributeName);
					if (groupIdAttribute == null) {
						throw new RuntimeException("Attribute [" + groupMembersAttributeName + "] is not defined for group [" + entry.getDN() + "]");
					}
				    String[] usersNames = groupIdAttribute.getStringValueArray();
				    for (int i = 0; i < usersNames.length; i++) {
				    	String completeUserName = usersNames[i];
				    	logger.debug("Found user with complete name : [" + completeUserName + "]");
				    	String userName = completeUserName.substring(completeUserName.indexOf("=") + 1, completeUserName.indexOf(","));
				    	logger.debug("User name is [" + userName + "]");
				    	users.add(userName);
				    }
				} else {
					throw new RuntimeException("Cannot find access group!!!");
				}
			}
			
			return users;
    	} catch(Throwable t) {
    		throw new RuntimeException("An unexpected error occured while serching users of Access group", t);
    	} finally {
    		closeConnection(connection);
    		logger.debug("OUT");
    	}
	}

    public boolean authenticateUser(String authenticationStrategy, String username, String password) throws LDAPException, UnsupportedEncodingException {
    	if("DIRECT".equalsIgnoreCase(authenticationStrategy)) {
    		return doDirectAuthentication(username, password);
    	} if("UNDIRECT".equalsIgnoreCase(authenticationStrategy)) {
    		return doUndirectAuthentication(username, password);
    	} else {
    		throw new RuntimeException("Unsupported authentication strategy [" + authenticationStrategy + "]");
    	}
    }
    
    protected boolean doDirectAuthentication(String username, String password) throws LDAPException, UnsupportedEncodingException {
    	
    	LDAPConnection ldapConnection = null;
    	
    	logger.debug("IN");
    	
		try {
			logger.debug("Authenticating user [" + username +"] ...");
			
			ldapConnection = connectToLDAP();
			try {
				ldapConnection.bind(LDAPConnection.LDAP_V3, username, password.getBytes("UTF8"));
			} catch(LDAPException t) {
				// @see http://tools.ietf.org/html/rfc4511
				if(t.getResultCode() == 49 // Invalid Credentials (= password is wrong)
						|| t.getResultCode() == 34) {  // // Invalid DN Syntax (= username is wrong)
					return false;
				} else {
					throw t;
				}
			}
			
		    if (ldapConnection.isBound()) {
		    	logger.debug("User [" + username +"] succesfully authenticated");
		    	return true;
		    } else {
		    	logger.debug("User [" + username +"] not authenticated");
		    	return false;
		    }
		    
		} catch (Throwable t) {
		    throw new RuntimeException("An unexpected error occured while authenticating user [" + username +"]", t);
		} finally {
			closeConnection(ldapConnection);
			logger.debug("OUT");
		}
    }
    
    protected boolean doUndirectAuthentication(String username, String password) {
		
    	logger.debug("IN");
    	
    	try {
			LDAPEntry userEntry = getUserById(username);
			if(userEntry != null) {
				String userDistinguishName = userEntry.getDN();
				return doDirectAuthentication(userDistinguishName, password);
			}
			return false;
		} catch(Throwable t) {
			throw new RuntimeException("An unexpected error occured while authenticating user [" + username +"]", t);
		} finally {
			logger.debug("OUT");
		}
    }
    
    protected LDAPEntry getUserById(String username) throws LDAPException {
    	LDAPEntry user = null;
    	LDAPConnection ldapAdminConnection = null;
    	
    	logger.debug("IN");
    	
    	try {
    		ldapAdminConnection = this.connectToLDAPAsAdmin();
    		logger.debug("Searching for user [" + username + "] ...");
			String searchQuery = "(&(objectclass=" + userObjectClass + ")(" + userIdAttributeName + "=" + username + "))";
			String searchPath = userSearchPath;
			
			if(StringUtilities.isEmpty(searchPath)) {
				searchPath = baseDN;
			} else {
				if(StringUtilities.isNotEmpty(baseDN)) {
					searchPath +=  "," + baseDN;
				}
			}
			
			LDAPSearchResults searchResults = ldapAdminConnection.search(
					searchPath, LDAPConnection.SCOPE_SUB,
					searchQuery, userAttributeNames, false);
			if (searchResults.hasMore()) {
				user = searchResults.next();
				if(user == null) {
					throw new RuntimeException("Impossible to read user's information from query result object");
				} 
				
				if (searchResults.hasMore()) {
					throw new RuntimeException("There are more users with the name equals to [" + username + "]");
				}
				logger.info("User [" + username + "] succesfully found");
			} else {
				logger.warn("User [" + username + "] not found");
			}
			
			return user;
    	} catch(Throwable t) {
    		throw new RuntimeException("An unexpected error occured while serching for user [" + username + "]", t);
    	} finally {
    		closeConnection(ldapAdminConnection);
    		logger.debug("OUT");
    	}
    }

    private boolean isValidGroup(String groupDistingushedName) {
    	
    	String searchPath = groupSearchPath;
    	
		if (StringUtilities.isEmpty(searchPath)) {
			searchPath = baseDN;
		} else {
			if(StringUtilities.isNotEmpty(baseDN)) {
				searchPath +=  "," + baseDN;
			}
		}
    	
    	return groupDistingushedName.endsWith(searchPath);
    }
    
    private String getGroupName(String groupDistingushedName) {
    	try {
    		logger.debug("IN: groupDistingushedName = [" + groupDistingushedName + "]");
        	String groupName = null;
        	int beginIndex = groupDistingushedName.indexOf("=");
        	int endIndex = groupDistingushedName.indexOf(",");
        	groupName = groupDistingushedName.substring(beginIndex + 1, endIndex);
        	logger.debug("OUT: returning [" + groupName + "]");
    		return groupName;
    	} catch (Exception e) {
    		throw new RuntimeException("Error while getting the name of the rome for group [" + groupDistingushedName + "]", e);
    	}

    }
    
    public List<String> getUserGroup(String username) throws LDAPException  {
    	
    	logger.debug("IN");
		
    	List<String> userGroups = new ArrayList<String>();

		try {
			LDAPEntry entry = getUserById(username);
			
			LDAPAttribute groupAttribute =  entry.getAttribute(userMemberOfAttributeName);
			if(groupAttribute == null) {
				throw new RuntimeException("Attribute [" + userMemberOfAttributeName + "] is not defined for user [" + username+ "]");
			}
			String[] groupDistingushedNames = groupAttribute.getStringValueArray();
			for(int i = 0; i < groupDistingushedNames.length; i++) {
				String groupDistingushedName = groupDistingushedNames[i];
				if (isValidGroup(groupDistingushedName)) {
					String groupName = getGroupName(groupDistingushedName);
					if (!isAccessGroupDefined() || !isAccessGroup(groupName)) {
						userGroups.add(groupName);
					}
				}
			}
		} catch (Throwable t) {
		    logger.error("An unexpected error occured while loading roles of user [" + username+ "]", t);
		} finally {
		    logger.debug("OUT");
		}
		
		return userGroups;
    }

    private boolean isAccessGroup(String groupName) {
	    return accessGroupName.equals(groupName);
	}

	public List<String> getAllGroups() throws LDAPException {
		
    	logger.debug("IN");
		
    	List<String> groups = new ArrayList<String>();
	
		LDAPConnection connection = connectToLDAPAsAdmin();
		
		try {
	
			logger.debug("Loading roles from LDAP ...");
			
			String searchPath = groupSearchPath;
			
			if(StringUtilities.isEmpty(searchPath)) {
				searchPath = baseDN;
			} else {
				if(StringUtilities.isNotEmpty(baseDN)) {
					searchPath +=  "," + baseDN;
				}
			}
			
			LDAPSearchResults searchResults = connection.search(searchPath, LDAPConnection.SCOPE_SUB,
				"(objectclass=" + groupObjectClass + ")", groupAttributeNames, false);
	
			LDAPEntry entry = null;
			while (searchResults.hasMore()) {
				entry = searchResults.next();
				if (entry != null) {
				    LDAPAttribute groupIdAttribute =  entry.getAttribute(groupIdAttributeName);
					if(groupIdAttribute == null) {
						throw new RuntimeException("Attribute [" + groupIdAttributeName + "] is not defined for group [" + entry.getDN() + "]");
					}
				    String groupName = groupIdAttribute.getStringValue();
				    // Access group is not considered
				    if (!isAccessGroupDefined() || !isAccessGroup(groupName)) {
					    groups.add(groupName);	
					    logger.debug("Role [" +  groupName + "] succesfully loaded");
				    }

				}
			}
			
			logger.info("Roles loaded succesfully from ldap");
	
		} catch (Throwable t) {
			throw new RuntimeException("An unexpected error occured while loading roles from the LDAP", t);   
		} finally {
			if (connection != null)
			    connection.disconnect();
			logger.debug("OUT");
		}
	
		
		
		return groups;
    }
    
    public Map<String, String> getUserAttributes(String username) throws LDAPException {
		
		Map<String, String> userAttributes;
		
		logger.debug("IN");
				
		userAttributes = new HashMap<String, String>();
		
		try {
			logger.debug("Loading profile attributes of user [" + username + "] ...");
			
			
			//LDAPEntry userEntry = getUserByName(username, connection);
			LDAPEntry userEntry = getUserById(username);
			if (userEntry != null) {
				userAttributes.put("dn", userEntry.getDN());
				for (int i = 0; i < userAttributeNames.length; i++) {
				    String attributeName = userAttributeNames[i];
				    if(attributeName.equalsIgnoreCase(userMemberOfAttributeName)) continue;
				    logger.debug("Loading attribute [" + attributeName + "] ...");
				    LDAPAttribute attribute = userEntry.getAttribute(attributeName);
				    if(attribute != null) {
				    	String attributeValue = attribute.getStringValue();
				    	userAttributes.put(attributeName, attributeValue);
				    	logger.debug("Attribute [" + attributeName + "] succesfully loaded. Its value is equal to [" + attributeValue + "]");
				    } else {
				    	logger.warn("Impossible to load attribute [" + attributeName + "] for user [" + username + "]");
				    	if(userAttributeForceCreation == true) {
				    		userAttributes.put(attributeName, userDefaultAttributeValue);
				    	}				    	
				    }
				}
				logger.debug("Profile attributes of user [" + username + "] has been loaded succesfully");
			} else {
				logger.warn("Impossible to find an user whose name is equal to [" + username + "]");
			}
		} catch (Throwable t) {
			throw new RuntimeException("An unexpected error occured while loading profile attributes of the user [" + username + "]", t);
		} finally {
			logger.debug("OUT");
		}
	
		
		
		return userAttributes;
    } 

}
