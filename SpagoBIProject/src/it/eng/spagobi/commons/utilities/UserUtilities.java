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
package it.eng.spagobi.commons.utilities;

import it.eng.spago.base.RequestContainer;
import it.eng.spago.base.SessionContainer;
import it.eng.spago.error.EMFInternalError;
import it.eng.spago.security.IEngUserProfile;
import it.eng.spagobi.analiticalmodel.functionalitytree.bo.LowFunctionality;
import it.eng.spagobi.analiticalmodel.functionalitytree.bo.UserFunctionality;
import it.eng.spagobi.analiticalmodel.functionalitytree.dao.ILowFunctionalityDAO;
import it.eng.spagobi.commons.bo.Role;
import it.eng.spagobi.commons.bo.UserProfile;
import it.eng.spagobi.commons.constants.SpagoBIConstants;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.services.common.SsoServiceFactory;
import it.eng.spagobi.services.common.SsoServiceInterface;
import it.eng.spagobi.services.security.bo.SpagoBIUserProfile;
import it.eng.spagobi.services.security.exceptions.SecurityException;
import it.eng.spagobi.services.security.service.ISecurityServiceSupplier;
import it.eng.spagobi.services.security.service.SecurityServiceSupplierFactory;
import it.eng.spagobi.utilities.assertion.Assert;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

public class UserUtilities {

    static Logger logger = Logger.getLogger(UserUtilities.class);

    public static String getSchema(String ente,RequestContainer aRequestContainer){
    	
    	logger.debug("Ente: "+ente);
    	SessionContainer aSessionContainer = aRequestContainer.getSessionContainer();
    	SessionContainer permanentSession = aSessionContainer.getPermanentContainer();

    	IEngUserProfile userProfile = (IEngUserProfile) permanentSession.getAttribute(IEngUserProfile.ENG_USER_PROFILE);
    	
    	if (userProfile!=null){
    		try {
				return (String) userProfile.getUserAttribute(ente);
			} catch (EMFInternalError e) {
				logger.error("User profile is NULL!!!!");
			}
    	}else {
    		logger.warn("User profile is NULL!!!!");
    	}
    	return null;
    }
    
    public static String getSchema(String ente,IEngUserProfile userProfile){
    	logger.debug("Ente: "+ente);
    	if (userProfile!=null){
    		try {
				return (String) userProfile.getUserAttribute(ente);
			} catch (EMFInternalError e) {
				logger.error("User profile is NULL!!!!");
			}
    	}else {
    		logger.warn("User profile is NULL!!!!");
    	}
    	return null;
    }
    
    
    
    /**
     * Gets the user profile.
     * 
     * @return the user profile
     * 
     * @throws Exception the exception
     */
    public static IEngUserProfile getUserProfile() throws Exception {
	RequestContainer aRequestContainer = RequestContainer.getRequestContainer();
	SessionContainer aSessionContainer = aRequestContainer.getSessionContainer();
	SessionContainer permanentSession = aSessionContainer.getPermanentContainer();

	IEngUserProfile userProfile = (IEngUserProfile) permanentSession.getAttribute(IEngUserProfile.ENG_USER_PROFILE);

	if (userProfile == null) {

	    String userId = null;
	    PortletRequest portletRequest = PortletUtilities.getPortletRequest();
	    Principal principal = portletRequest.getUserPrincipal();
	    userId = principal.getName();
	    logger.debug("got userId from Principal=" + userId);

	    ISecurityServiceSupplier supplier = SecurityServiceSupplierFactory.createISecurityServiceSupplier();
	    SpagoBIUserProfile user = null;
	    try {
		user = supplier.createUserProfile(userId);
		
		user.setFunctions(readFunctionality(user.getRoles()));
		userProfile = new UserProfile(user);
	    } catch (Exception e) {
	    	logger.error("An error occured while retrieving user profile for user[" + userId +"]");
	    	throw new SecurityException("An error occured while retrieving user profile for user[" + userId +"]", e);
	    }

	    logger.debug("userProfile created.UserID= " + (String) userProfile.getUserUniqueIdentifier());
	    logger.debug("Attributes name of the user profile: " + userProfile.getUserAttributeNames());
	    logger.debug("Functionalities of the user profile: " + userProfile.getFunctionalities());
	    logger.debug("Roles of the user profile: " + userProfile.getRoles());

	    permanentSession.setAttribute(IEngUserProfile.ENG_USER_PROFILE, userProfile);

	   // String username = (String) userProfile.getUserUniqueIdentifier();
	    String username = (String) user.getUserId();
	    if (!UserUtilities.userFunctionalityRootExists(username)) {
		UserUtilities.createUserFunctionalityRoot(userProfile);
	    }

	}

	return userProfile;
    }

    public static IEngUserProfile getUserProfile(HttpServletRequest req) throws Exception {
    	logger.debug("IN");
    	SsoServiceInterface userProxy = SsoServiceFactory.createProxyService();
		String userId = userProxy.readUserIdentifier(req);
		
	    ISecurityServiceSupplier supplier = SecurityServiceSupplierFactory.createISecurityServiceSupplier();
	    try {
		SpagoBIUserProfile user = supplier.createUserProfile(userId);
		user.setFunctions(readFunctionality(user.getRoles()));
		return new UserProfile(user);
	    } catch (Exception e) {
	    	logger.error("Exception while creating user profile",e);
			throw new SecurityException("Exception while creating user profile", e);
	    }finally{
	    	logger.debug("OUT");
	    }
    }
    public static IEngUserProfile getUserProfile(String userId) throws Exception {
    	logger.debug("IN.userId="+userId);
    	if (userId==null) return null;
	    ISecurityServiceSupplier supplier = SecurityServiceSupplierFactory.createISecurityServiceSupplier();
	    try {
		SpagoBIUserProfile user = supplier.createUserProfile(userId);
		if (user==null) return null;
		user.setFunctions(readFunctionality(user.getRoles()));
		return new UserProfile(user);
	    } catch (Exception e) {
	    	logger.error("Exception while creating user profile",e);
			throw new SecurityException("Exception while creating user profile", e);
	    }finally{
	    	logger.debug("OUT");
	    }
    }    

    /**
     * User functionality root exists.
     * 
     * @param username the username
     * 
     * @return true, if successful
     * 
     * @throws Exception the exception
     */
    public static boolean userFunctionalityRootExists(String username) throws Exception {
	boolean exists = false;
	try {
		logger.debug("****  username checked: " + username);
	    ILowFunctionalityDAO functdao = DAOFactory.getLowFunctionalityDAO();
	    exists = functdao.checkUserRootExists(username);
	} catch (Exception e) {
		logger.error("Error while checking user functionality root existence", e);
	    throw new Exception("Unable to check user functionality existence", e);
	}
	return exists;
    }

    /**
     * User functionality root exists.
     * 
     * @param userProfile the user profile
     * 
     * @return true, if successful
     * 
     * @throws Exception the exception
     */
    public static boolean userFunctionalityRootExists(UserProfile userProfile) {
    	Assert.assertNotNull(userProfile, "User profile in input is null");
    	boolean toReturn = false;
    	String userName = (String) userProfile.getUserName();
    	try {
    		toReturn = userFunctionalityRootExists(userName);
    	} catch (Exception e) {
    		throw new SpagoBIRuntimeException("Cannot find if user functionality exists for user [" + userName + "]", e);
    	}
    	return toReturn;
    }
    
    /**
     * User functionality root exists.
     * 
     * @param userProfile the user profile
     * 
     * @return true, if successful
     * 
     * @throws Exception the exception
     */
    public static LowFunctionality loadUserFunctionalityRoot(UserProfile userProfile) {
    	Assert.assertNotNull(userProfile, "User profile in input is null");
    	String userId = (String) userProfile.getUserId();
    	LowFunctionality lf = null;
    	try {
    		lf = DAOFactory.getLowFunctionalityDAO().loadLowFunctionalityByPath("/" + userId, false);
    	} catch (Exception e) {
    		throw new SpagoBIRuntimeException("Cannot load user functionality for user with id [" + userId + "]", e);
    	}
    	return lf;
    }
    
    /**
     * Creates the user functionality root.
     * 
     * @param userProfile the user profile
     * 
     * @throws Exception the exception
     */
    public static void createUserFunctionalityRoot(IEngUserProfile userProfile) throws Exception {
    	logger.debug("IN");
	try {
	    String userId = (String) ((UserProfile)userProfile).getUserId();
	    logger.debug("userId: " + userId);
	    Collection roleStrs = ((UserProfile)userProfile).getRolesForUse();
	    Iterator roleIter = roleStrs.iterator();
	    List roles = new ArrayList();
	    logger.debug("Roles's number: " + roleStrs.size());
	    while (roleIter.hasNext()) {
	    	String rolename = (String) roleIter.next();
	    	logger.debug("Rolename: " + rolename);
	    	Role role = DAOFactory.getRoleDAO().loadByName(rolename);
	    	if (role!=null)  {
	    		roles.add(role);
	    		logger.debug("Add Rolename ( " + rolename +") ");
	    	}
	    	else logger.debug("Rolename ( " + rolename +") doesn't exist in EXT_ROLES");
	    }
	    Role[] rolesArr = new Role[roles.size()];
	    rolesArr = (Role[]) roles.toArray(rolesArr);

	    UserFunctionality userFunct = new UserFunctionality();
	    userFunct.setCode("ufr_" + userId);
	    userFunct.setDescription("User Functionality Root");
	    userFunct.setName(userId);
	    userFunct.setPath("/" + userId);
	    //userFunct.setExecRoles(rolesArr);
	    ILowFunctionalityDAO functdao = DAOFactory.getLowFunctionalityDAO();
	    functdao.insertUserFunctionality(userFunct);
	} catch (Exception e) {
	   logger.error("Error while creating user functionality root", e);
	    throw new Exception("Unable to create user functionality root", e);
	}finally{
		logger.debug("OUT");
	}
    }


    public static String[] readFunctionality(String[] roles) {
		logger.debug("IN");
		try {
		    it.eng.spagobi.commons.dao.IUserFunctionalityDAO dao = DAOFactory.getUserFunctionalityDAO();
		    String[] functionalities = dao.readUserFunctionality(roles);
		    logger.debug("Functionalities retrieved: " + functionalities == null ? "" : functionalities.toString());
		    
		    List<String> roleFunctionalities = new ArrayList<String>();
		    Role virtualRole = getVirtualRole(roles);
		    
			if (virtualRole.isAbleToSaveSubobjects()) {
				roleFunctionalities.add(SpagoBIConstants.SAVE_SUBOBJECT_FUNCTIONALITY);
			}
			if (virtualRole.isAbleToSeeSubobjects()) {
				roleFunctionalities.add(SpagoBIConstants.SEE_SUBOBJECTS_FUNCTIONALITY);
			}
			if (virtualRole.isAbleToSeeSnapshots()) {
				roleFunctionalities.add(SpagoBIConstants.SEE_SNAPSHOTS_FUNCTIONALITY);
			}
			if (virtualRole.isAbleToSeeViewpoints()) {
				roleFunctionalities.add(SpagoBIConstants.SEE_VIEWPOINTS_FUNCTIONALITY);
			}
			if (virtualRole.isAbleToSeeNotes()) {
				roleFunctionalities.add(SpagoBIConstants.SEE_NOTES_FUNCTIONALITY);
			}
			if (virtualRole.isAbleToSendMail()) {
				roleFunctionalities.add(SpagoBIConstants.SEND_MAIL_FUNCTIONALITY);
			}
			if (virtualRole.isAbleToSaveIntoPersonalFolder()) {
				roleFunctionalities.add(SpagoBIConstants.SAVE_INTO_FOLDER_FUNCTIONALITY);
			}
			if (virtualRole.isAbleToSaveRememberMe()) {
				roleFunctionalities.add(SpagoBIConstants.SAVE_REMEMBER_ME_FUNCTIONALITY);
			}
			if (virtualRole.isAbleToSeeMetadata()) {
				roleFunctionalities.add(SpagoBIConstants.SEE_METADATA_FUNCTIONALITY);
			}
			if (virtualRole.isAbleToSaveMetadata()) {
				roleFunctionalities.add(SpagoBIConstants.SAVE_METADATA_FUNCTIONALITY);
			}
			if (virtualRole.isAbleToBuildQbeQuery()) {
				roleFunctionalities.add(SpagoBIConstants.BUILD_QBE_QUERIES_FUNCTIONALITY);
			}
			
			if (!roleFunctionalities.isEmpty()) {
				List<String> roleTypeFunctionalities = Arrays.asList(functionalities);
				roleFunctionalities.addAll(roleTypeFunctionalities);
				String[] a = new String[]{""};
				functionalities = roleFunctionalities.toArray(a);
			}
		    
		    return functionalities;
		} catch (Exception e) {
		    logger.error("Exception", e);
		    throw new RuntimeException("Error while loading functionalities", e);
		} finally {
		    logger.debug("OUT");
		}
		
    }
    
    public static String getUserId(HttpServletRequest req){
        logger.debug("IN");
        SsoServiceInterface userProxy = SsoServiceFactory.createProxyService();
        String userId = userProxy.readUserIdentifier(req);
        logger.debug("OUT,userId:"+userId);
        return userId;
    }
    
	private static Role getVirtualRole(String[] roles) throws Exception {
		logger.debug("IN");
		Role virtualRole = new Role("", "");
		virtualRole.setIsAbleToSaveSubobjects(false);
		virtualRole.setIsAbleToSeeSubobjects(false);
		virtualRole.setIsAbleToSeeSnapshots(false);
		virtualRole.setIsAbleToSeeViewpoints(false);
		virtualRole.setIsAbleToSeeMetadata(false);
		virtualRole.setIsAbleToSaveMetadata(false);
		virtualRole.setIsAbleToSendMail(false);
		virtualRole.setIsAbleToSeeNotes(false);
		virtualRole.setIsAbleToSaveRememberMe(false);
		virtualRole.setIsAbleToSaveIntoPersonalFolder(false);
		virtualRole.setIsAbleToBuildQbeQuery(false);
		if (roles != null) {
			for (int i = 0; i < roles.length; i++) {
				String roleName = roles[i];
				logger.debug("RoleName="+roleName);
				Role anotherRole = DAOFactory.getRoleDAO().loadByName(roleName);
				if (anotherRole!=null) { 
					if (anotherRole.isAbleToSaveSubobjects()) {
						logger.debug("User has role " + roleName + " that is able to save subobjects.");
						virtualRole.setIsAbleToSaveSubobjects(true);
					}
					if (anotherRole.isAbleToSeeSubobjects()) {
						logger.debug("User has role " + roleName + " that is able to see subobjects.");
						virtualRole.setIsAbleToSeeSubobjects(true);
					}
					if (anotherRole.isAbleToSeeViewpoints()) {
						logger.debug("User has role " + roleName + " that is able to see viewpoints.");
						virtualRole.setIsAbleToSeeViewpoints(true);
					}
					if (anotherRole.isAbleToSeeSnapshots()) {
						logger.debug("User has role " + roleName + " that is able to see snapshots.");
						virtualRole.setIsAbleToSeeSnapshots(true);
					}
					if (anotherRole.isAbleToSeeMetadata()) {
						logger.debug("User has role " + roleName + " that is able to see metadata.");
						virtualRole.setIsAbleToSeeMetadata(true);
					}
					if (anotherRole.isAbleToSaveMetadata()) {
						logger.debug("User has role " + roleName + " that is able to save metadata.");
						virtualRole.setIsAbleToSaveMetadata(true);
					}
					if (anotherRole.isAbleToSendMail()) {
						logger.debug("User has role " + roleName + " that is able to send mail.");
						virtualRole.setIsAbleToSendMail(true);
					}
					if (anotherRole.isAbleToSeeNotes()) {
						logger.debug("User has role " + roleName + " that is able to see notes.");
						virtualRole.setIsAbleToSeeNotes(true);
					}
					if (anotherRole.isAbleToSaveRememberMe()) {
						logger.debug("User has role " + roleName + " that is able to save remember me.");
						virtualRole.setIsAbleToSaveRememberMe(true);
					}
					if (anotherRole.isAbleToSaveIntoPersonalFolder()) {
						logger.debug("User has role " + roleName + " that is able to save into personal folder.");
						virtualRole.setIsAbleToSaveIntoPersonalFolder(true);
					}
					if (anotherRole.isAbleToBuildQbeQuery()) {
						logger.debug("User has role " + roleName + " that is able to build QBE queries.");
						virtualRole.setIsAbleToBuildQbeQuery(true);
					}
				}
			}
		}
		logger.debug("OUT");
		return virtualRole;
	}

}
