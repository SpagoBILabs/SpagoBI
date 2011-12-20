/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2004 - 2011 Engineering Ingegneria Informatica S.p.A.

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA

 **/
package it.eng.spagobi.commons.bo;

import it.eng.spago.error.EMFInternalError;
import it.eng.spago.security.IEngUserProfile;
import it.eng.spagobi.security.AuthorizationsBusinessMapper;
import it.eng.spagobi.services.security.bo.SpagoBIUserProfile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

/**
 * This class contain the information about the user
 */
public class UserProfile implements IEngUserProfile {

	private static transient Logger logger = Logger.getLogger(UserProfile.class);

	private static String WORKFLOW_USER_NAME = "[SYSTEM - WORKFLOW]";
	private static String SCHEDULER_USER_NAME = "scheduler";

	private String userUniqueIdentifier = null;
	private String userId = null;
	private String userName = null;
	private Map userAttributes = null;
	private Collection roles = null;
	private Collection functionalities = null;
	private String defaultRole = null;

	/**
	 * The Constructor.
	 * 
	 * @param profile SpagoBIUserProfile
	 */
	public UserProfile(SpagoBIUserProfile profile) {
		logger.debug("IN");
		this.userUniqueIdentifier = profile.getUniqueIdentifier();
		this.userName = profile.getUserName();
		this.userId = profile.getUserId();
		roles = new ArrayList();
		if (profile.getRoles() != null) {
			int l = profile.getRoles().length;
			for (int i = 0; i < l; i++) {
				logger.debug("ROLE:" + profile.getRoles()[i]);
				roles.add(profile.getRoles()[i]);
			}

		}
		functionalities = new ArrayList();
		if (profile.getFunctions() != null) {
			int l = profile.getFunctions().length;
			for (int i = 0; i < l; i++) {
				logger.debug("USER FUNCTIONALITY:" + profile.getFunctions()[i]);
				functionalities.add(profile.getFunctions()[i]);
			}
		}

		userAttributes = profile.getAttributes();
		if (userAttributes != null) {
			logger.debug("USER ATTRIBUTES----");
			Set keis = userAttributes.keySet();
			Iterator iter = keis.iterator();
			while (iter.hasNext()) {
				String key = (String) iter.next();
				logger.debug(key + "=" + userAttributes.get(key));
			}
			logger.debug("USER ATTRIBUTES----");
		}

		logger.debug("OUT");
	}

	/**
	 * The Constructor.
	 * 
	 * @param user String
	 */
	public UserProfile(String user) {
		this.userUniqueIdentifier = user;
		this.userId=user;
		this.userName=user;
	}

	/**
	 * Usato solo nel workflow.
	 * 
	 * @return the user profile
	 */
	public static final UserProfile createWorkFlowUserProfile() {
		UserProfile profile = new UserProfile(WORKFLOW_USER_NAME);
		profile.roles = new ArrayList();
		profile.userAttributes = new HashMap();
//		profile.userAttributes.put("password", WORKFLOW_USER_NAME);
		return profile;
	}

	/**
	 * Usato solo per lanciare i job.
	 * 
	 * @return the user profile
	 */
	public static final UserProfile createSchedulerUserProfile() {
		UserProfile profile = new UserProfile(SCHEDULER_USER_NAME);
		profile.roles = new ArrayList();
		profile.userAttributes = new HashMap();
		return profile;
	}

	/**
	 * Checks if is scheduler user.
	 * 
	 * @param userid String
	 * 
	 * @return true, if checks if is scheduler user
	 */
	public static boolean isWorkflowUser(String userid) {
		return WORKFLOW_USER_NAME.equals(userid);
	}

	/**
	 * Checks if is scheduler user.
	 * 
	 * @param userid String
	 * 
	 * @return true, if checks if is scheduler user
	 */
	public static boolean isSchedulerUser(String userid) {
		return SCHEDULER_USER_NAME.equals(userid);
	}

	/* (non-Javadoc)
	 * @see it.eng.spago.security.IEngUserProfile#getFunctionalities()
	 */
	public Collection getFunctionalities() throws EMFInternalError {
		return functionalities;
	}


	/* (non-Javadoc)
	 * @see it.eng.spago.security.IEngUserProfile#getFunctionalitiesByRole(java.lang.String)
	 */
	public Collection getFunctionalitiesByRole(String arg0) throws EMFInternalError {
		return new ArrayList();

	}


	/* (non-Javadoc)
	 * @see it.eng.spago.security.IEngUserProfile#getRoles()
	 */
	public Collection getRoles() throws EMFInternalError {
		return this.roles;
	}

	/* 
	 *  if a role default is assigned return it, else returns all roles
	 */
	public Collection getRolesForUse() throws EMFInternalError {
		logger.debug("IN");
		Collection toReturn = null;
		logger.debug("look if default role is selected");		
		if (defaultRole != null){
			logger.debug("default role selected is "+defaultRole);				
			toReturn=new ArrayList<String>();
			toReturn.add(defaultRole); 
		}
		else{
			logger.debug("default role not selected");

			toReturn = this.roles;
		}
		
		
		logger.debug("OUT");
		return toReturn;
	}



	/* (non-Javadoc)
	 * @see it.eng.spago.security.IEngUserProfile#getUserAttribute(java.lang.String)
	 */
	public Object getUserAttribute(String attributeName) throws EMFInternalError {
		return userAttributes.get(attributeName);
	}


	/* (non-Javadoc)
	 * @see it.eng.spago.security.IEngUserProfile#getUserAttributeNames()
	 */
	public Collection getUserAttributeNames() {
		return userAttributes.keySet();
	}


	/* (non-Javadoc)
	 * @see it.eng.spago.security.IEngUserProfile#getUserUniqueIdentifier()
	 */
	public Object getUserUniqueIdentifier() {
		return userUniqueIdentifier;
	}

	/* (non-Javadoc)
	 * @see it.eng.spago.security.IEngUserProfile#getUserName()
	 */
	public Object getUserName() {
		String retVal = userName;
		if (retVal == null) retVal = userUniqueIdentifier;
		return retVal;
	}

	/* (non-Javadoc)
	 * @see it.eng.spago.security.IEngUserProfile#getUserId()
	 */
	public Object getUserId() {
		String retVal = userId;
		if (retVal == null) retVal = userUniqueIdentifier;
		return retVal;
	}

	/* (non-Javadoc)
	 * @see it.eng.spago.security.IEngUserProfile#hasRole(java.lang.String)
	 */
	public boolean hasRole(String roleName) throws EMFInternalError {
		return this.roles.contains(roleName);
	}


	/* (non-Javadoc)
	 * @see it.eng.spago.security.IEngUserProfile#isAbleToExecuteAction(java.lang.String)
	 */
	public boolean isAbleToExecuteAction(String actionName) throws EMFInternalError {
		// first check if the actionName is a functionality...
		if ( this.functionalities.contains(actionName) ){
			return true;
		}
		String functionality = AuthorizationsBusinessMapper.getInstance().mapActionToBusinessProcess(actionName);
		if (functionality != null){
			return this.functionalities.contains(functionality);
		}else return false;    
	}


	/* (non-Javadoc)
	 * @see it.eng.spago.security.IEngUserProfile#isAbleToExecuteModuleInPage(java.lang.String, java.lang.String)
	 */
	public boolean isAbleToExecuteModuleInPage(String pageName, String moduleName) throws EMFInternalError {
		String functionality = AuthorizationsBusinessMapper.getInstance().mapPageModuleToBusinessProcess(pageName, moduleName);
		if (functionality != null){
			return this.functionalities.contains(functionality);
		}else return false;  
	}


	/* (non-Javadoc)
	 * @see it.eng.spago.security.IEngUserProfile#setApplication(java.lang.String)
	 */
	public void setApplication(String arg0) throws EMFInternalError {
	}

	/**
	 * Sets the functionalities.
	 * 
	 * @param functs the new functionalities
	 */
	public void setFunctionalities(Collection functs) {
		this.functionalities = functs;
	}

	/**
	 * Sets the attributes.
	 * 
	 * @param attrs the new attributes
	 */
	public void setAttributes(Map attrs) {
		this.userAttributes = attrs;
	}

	/**
	 * Adds an attribute.
	 * 
	 * @param attrs the new attributes
	 */
	public void addAttributes(String key, Object value) {
		this.userAttributes.put(key, value);
	}

	/**
	 * Modify an attribute value
	 * 
	 * @param attrs the new attributes
	 */
	public void setAttributeValue(String key, Object value) {
		this.userAttributes.remove(key);
		this.userAttributes.put(key, value);
	}


	/**
	 * Sets the roles.
	 * 
	 * @param rols the new roles
	 */
	public void setRoles(Collection rols) {
		this.roles = rols;
	}

	public String getDefaultRole() {
		return defaultRole;
	}


	public void setDefaultRole(String defaultRole) {
		logger.debug("IN "+defaultRole);
		this.defaultRole = defaultRole;
		logger.debug("OUT");
	}

	public Map getUserAttributes() {
		return userAttributes;
	}


}
