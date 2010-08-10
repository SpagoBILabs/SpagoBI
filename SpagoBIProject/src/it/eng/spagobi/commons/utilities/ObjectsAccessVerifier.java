/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005-2008 Engineering Ingegneria Informatica S.p.A.

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
package it.eng.spagobi.commons.utilities;

import it.eng.spago.error.EMFErrorSeverity;
import it.eng.spago.error.EMFInternalError;
import it.eng.spago.error.EMFUserError;
import it.eng.spago.security.IEngUserProfile;
import it.eng.spagobi.analiticalmodel.document.bo.BIObject;
import it.eng.spagobi.analiticalmodel.functionalitytree.bo.LowFunctionality;
import it.eng.spagobi.commons.bo.Role;
import it.eng.spagobi.commons.bo.UserProfile;
import it.eng.spagobi.commons.constants.SpagoBIConstants;
import it.eng.spagobi.commons.dao.DAOFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

/**
 * Contains some methods to control user exec/dev/test rights.
 * 
 * @author sulis
 */
public class ObjectsAccessVerifier {

	static private Logger logger = Logger.getLogger(ObjectsAccessVerifier.class);

	/**
	 * Controls if the current user can develop the object relative to the input
	 * folder id.
	 * 
	 * @param state
	 *                state of the object
	 * @param folderId
	 *                The id of the folder containing te object
	 * @param profile
	 *                user profile
	 * 
	 * @return A boolean control value
	 */
	public static boolean canDev(String state, Integer folderId, IEngUserProfile profile) {
		if (!state.equals("DEV")) {
			return false;
		}
		return canDevInternal(folderId, profile);
	}

	/**
	 * Controls if current user can exec the object relative to the input folder
	 * id.
	 * 
	 * @param state
	 *                state of the object
	 * @param folderId
	 *                The id of the folder containing te object
	 * @param profile
	 *                user profile
	 * 
	 * @return A boolean control value
	 */
	public static boolean canExec(String state, Integer folderId, IEngUserProfile profile) {
		logger.debug("IN.state=" + state);
		if(isAbleToExec(state, profile)) {
			/*if (!state.equals("REL")) {
			    return false;
			}*/
			LowFunctionality folder = null;
			try {
				folder = DAOFactory.getLowFunctionalityDAO().loadLowFunctionalityByID(folderId, false);
			} catch (Exception e) {
				logger.error("Exception in loadLowFunctionalityByID", e);
				return false;
			} finally {
				logger.debug("OUT");
			}
			return canExecInternal(folder, profile);
		} else{
			logger.debug("OUT.return false");
			return false;
		}
	}

	/**
	 * Metodo che verifica se nell'elenco delle funzionalità ne esiste almeno una con diritto di 
	 * esecuzione
	 * @param state
	 * @param profile
	 * @return
	 */
	public static boolean canExec(String state, List folders, IEngUserProfile profile) {

		logger.debug("IN.state=" + state);
		boolean canExec = false;
		if(isAbleToExec(state, profile)) {

			Iterator folderIt = folders.iterator();
			while(folderIt.hasNext()){
				LowFunctionality folder =(LowFunctionality) folderIt.next();
				canExec = canExecInternal(folder, profile);
				if (canExec){
					logger.debug("OUT.return true");
					return true;
				}
			}
			logger.debug("OUT.return false");
			return false;

		} else{
			logger.debug("OUT.return false");
			return false;
		}
	}

	/**
	 * Metodo che verifica se nell'elenco delle funzionalità ne esiste almeno una con diritto di 
	 * esecuzione
	 * @param state
	 * @param profile
	 * @return
	 */
	public static boolean canDev(String state, List folders, IEngUserProfile profile) {
		Monitor monitor =MonitorFactory.start("spagobi.core.ObjectAccessVerifier.canDev");
		logger.debug("IN.state=" + state);
		boolean canDev = false;
		if(isAbleToExec(state, profile)) {

			Iterator folderIt = folders.iterator();
			while(folderIt.hasNext()){
				LowFunctionality folder =(LowFunctionality) folderIt.next();
				canDev = canDevInternal(folder, profile);
				if (canDev){
					logger.debug("OUT.return true");
					monitor.stop();
					return true;
				}
			}
			logger.debug("OUT.return false");
			monitor.stop();
			return false;

		} else{
			logger.debug("OUT.return false");
			monitor.stop();
			return false;
		}
	}

	/**
	 * Metodo che verifica se nell'elenco delle funzionalità ne esiste almeno una con diritto di 
	 * esecuzione
	 * @param state
	 * @param profile
	 * @return
	 */
	public static boolean canTest(String state, List folders, IEngUserProfile profile) {
		Monitor monitor =MonitorFactory.start("spagobi.core.ObjectAccessVerifier.canTest");
		logger.debug("IN.state=" + state);
		boolean canTest = false;
		if(isAbleToExec(state, profile)) {

			Iterator folderIt = folders.iterator();
			while(folderIt.hasNext()){
				LowFunctionality folder =(LowFunctionality) folderIt.next();
				canTest = canTestInternal(folder, profile);
				if (canTest){
					logger.debug("OUT.return true");
					monitor.stop();
					return true;
				}
			}
			logger.debug("OUT.return false");
			monitor.stop();
			return false;

		} else{
			logger.debug("OUT.return false");
			monitor.stop();
			return false;
		}
	}

	/**
	 * Metodo che verifica il numero di istanze visibili del documento
	 * @param state
	 * @param userProfile
	 * @return
	 */
	public static int getVisibleInstances(String initialPath, List folders) {

		logger.debug("IN");

		int visibleInstances = 0 ;
		if (initialPath != null && !initialPath.trim().equals("")) {
			Iterator folderIt = folders.iterator();
			while(folderIt.hasNext()){
				LowFunctionality folder =(LowFunctionality) folderIt.next();
				String folderPath = folder.getPath();
				if (folderPath.equalsIgnoreCase(initialPath) || folderPath.startsWith(initialPath + "/")) {
					visibleInstances++;
				}		    
			}
		}else{
			visibleInstances = folders.size();
		}
		logger.debug("OUT");
		return visibleInstances ;

	}

	public static boolean isAbleToExec(String state, IEngUserProfile profile) {
		Monitor monitor =MonitorFactory.start("spagobi.core.ObjectAccessVerifier.isAbleToExec");
		logger.debug("IN.state=" + state);
		if (state.equals("REL")) {
			logger.debug("OUT.return true");
			monitor.stop();
			return true;
		}
		else if (state.equals("DEV")) {
			try {
				if(profile.isAbleToExecuteAction(SpagoBIConstants.DOCUMENT_MANAGEMENT_ADMIN)||profile.isAbleToExecuteAction(SpagoBIConstants.DOCUMENT_MANAGEMENT_DEV)){
					logger.debug("OUT.return true");
					return true;
				}else if (profile.isAbleToExecuteAction(SpagoBIConstants.DOCUMENT_MANAGEMENT_USER)||profile.isAbleToExecuteAction(SpagoBIConstants.DOCUMENT_MANAGEMENT_TEST)){
					logger.debug("OUT.return false");
					return false;
				}
			} catch (EMFInternalError e) {
				logger.error(e);
			}
		}
		else if (state.equals("TEST")) {
			try {
				if(profile.isAbleToExecuteAction(SpagoBIConstants.DOCUMENT_MANAGEMENT_ADMIN)||profile.isAbleToExecuteAction(SpagoBIConstants.DOCUMENT_MANAGEMENT_TEST)){
					logger.debug("OUT.return true");
					return true;
				}else if (profile.isAbleToExecuteAction(SpagoBIConstants.DOCUMENT_MANAGEMENT_USER)||profile.isAbleToExecuteAction(SpagoBIConstants.DOCUMENT_MANAGEMENT_DEV)){
					logger.debug("OUT.return false");
					return false;
				}
			} catch (EMFInternalError e) {
				logger.error(e);
			}
		}	
		logger.debug("OUT");
		monitor.stop();
		return false;
	}

	/**
	 * Control if current user can test the object relative to the folder id.
	 * 
	 * @param state
	 *                state of the object
	 * @param folderId
	 *                The id of the folder containing the object
	 * @param profile
	 *                user profile
	 * 
	 * @return A boolean control value
	 */
	public static boolean canTest(String state, Integer folderId, IEngUserProfile profile) {
		logger.debug("IN.state=" + state);
		if (!state.equals("TEST")) {
			return false;
		}
		return canTestInternal(folderId, profile);

	}

	/**
	 * Control if the user can develop the document specified by the input id
	 * 
	 * @param documentId The id of the document
	 * @param profile The user profile
	 * 
	 * @return A boolean control value
	 */
	public static boolean canDevBIObject(Integer biObjectID, IEngUserProfile profile) {
		Monitor monitor =MonitorFactory.start("spagobi.core.ObjectAccessVerifier.canDevBIObject(Integer biObjectID, IEngUserProfile profile)");
		boolean toReturn = false;
		try {
			logger.debug("IN: obj id = [" + biObjectID + "]; user id = [" + ((UserProfile) profile).getUserId() + "]");
			// if user is administrator, he can develop, no need to make any query to database
			if (profile.isAbleToExecuteAction(SpagoBIConstants.DOCUMENT_MANAGEMENT_ADMIN)) {
				logger.debug("User [" + ((UserProfile) profile).getUserId() + "] is administrator. He can develop every document");
				monitor.stop();
				return true;
			}
			BIObject obj = DAOFactory.getBIObjectDAO().loadBIObjectById(biObjectID);
			toReturn = canDevBIObject(obj, profile);
		} catch (Exception e) {
			logger.error(e);
			monitor.stop();
			return false;
		}
		logger.debug("OUT: returning " + toReturn);
		monitor.stop();
		return toReturn;
	}

	/**
	 * Control if the user can develop the input document
	 * 
	 * @param documentId The id of the document
	 * @param profile The user profile
	 * 
	 * @return A boolean control value
	 */
	public static boolean canDevBIObject(BIObject obj, IEngUserProfile profile) {
		Monitor monitor =MonitorFactory.start("spagobi.core.ObjectAccessVerifier.canDevBIObject(BIObject obj, IEngUserProfile profile)");
		boolean toReturn = false;
		try {
			logger.debug("IN: obj label = [" + obj.getLabel() + "]; user id = [" + ((UserProfile) profile).getUserId() + "]");
			// if user is administrator, he can develop, no need to make any query to database
			if (profile.isAbleToExecuteAction(SpagoBIConstants.DOCUMENT_MANAGEMENT_ADMIN)) {
				logger.debug("User [" + ((UserProfile) profile).getUserId() + "] is administrator. He can develop every document");
				monitor.stop();
				return true;
			}
			// if user is not an administrator and document is not in DEV state, document cannot be developed
			if (!"DEV".equals(obj.getStateCode())) {
				logger.debug("User [" + ((UserProfile) profile).getUserId() + "] is not an administrator and document is not in DEV state, so it cannot be developed");
				monitor.stop();
				return true;
			}
			// if user is not an administrator and document is in DEV state, we must see if he has development permission
			List folders = obj.getFunctionalities();
			Iterator it = folders.iterator();
			while (it.hasNext()) {
				Integer folderId = (Integer) it.next();
				boolean canDevInFolder = canDev(folderId, profile);
				if (canDevInFolder) {
					logger.debug("User can develop in functionality with id = " + folderId);
					toReturn = true;
					break;
				}
			}
		} catch (Exception e) {
			logger.error("Error while loading BIObject", e);
			monitor.stop();
			return false;
		}
		logger.debug("OUT: returning " + toReturn);
		monitor.stop();
		return toReturn;
	}

	/**
	 * Control if the current user can develop new object into the functionality
	 * identified by its id.
	 * 
	 * @param folderId
	 *                The id of the lowFunctionality
	 * @param profile
	 *                user profile
	 * 
	 * @return A boolean control value
	 */
	public static boolean canDev(Integer folderId, IEngUserProfile profile) {
		return canDevInternal(folderId, profile);
	}

	/**
	 * Control if the current user can test new object into the functionality
	 * identified by its id.
	 * 
	 * @param folderId
	 *                The id of the lowFunctionality
	 * @param profile
	 *                user profile
	 * 
	 * @return A boolean control value
	 */
	public static boolean canTest(Integer folderId, IEngUserProfile profile) {
		return canTestInternal(folderId, profile);

	}

	/**
	 * Control if the current user can execute objects into the input
	 * functionality.
	 * 
	 * @param folder
	 *                The lowFunctionality
	 * @param profile
	 *                user profile
	 * 
	 * @return A boolean control value
	 */
	public static boolean canExec(LowFunctionality folder, IEngUserProfile profile) {
		return canExecInternal(folder, profile);
	}

	/**
	 * Control if the current user can execute new object into the functionality
	 * identified by its id.
	 * 
	 * @param folderId
	 *                The id of the lowFunctionality
	 * @param profile
	 *                user profile
	 * 
	 * @return A boolean control value
	 */
	public static boolean canExec(Integer folderId, IEngUserProfile profile) {
		Monitor monitor =MonitorFactory.start("spagobi.core.ObjectAccessVerifier.canExec");
		logger.debug("IN");
		LowFunctionality folder = null;
		try {
			folder = DAOFactory.getLowFunctionalityDAO().loadLowFunctionalityByID(folderId, false);
		} catch (Exception e) {
			logger.error("Exception in loadLowFunctionalityByID", e);

			return false;
		} finally {
			monitor.stop();
			logger.debug("OUT");
		}
		return canExecInternal(folder, profile);
	}

	/**
	 * Private method called by the corrispondent public method canExec.
	 * Executes roles functionalities control .
	 * 
	 * @param folder
	 *                The lowFunctionality
	 * @param profile
	 *                user profile
	 * @return A boolean control value
	 */
	private static boolean canExecInternal(LowFunctionality folder, IEngUserProfile profile) {
		logger.debug("IN");
		Monitor monitor =MonitorFactory.start("spagobi.core.ObjectAccessVerifier.canExecInternal");


		Collection roles = null;

		try {
			roles = ((UserProfile)profile).getRolesForUse();
		} catch (EMFInternalError emfie) {
			logger.error("EMFInternalError in profile.getRoles");
			logger.debug("OUT.return false");
			monitor.stop();
			return false;
		}



		Role[] execRoles = folder.getExecRoles();
		List execRoleNames = new ArrayList();
		for (int i = 0; i < execRoles.length; i++) {
			Role role = execRoles[i];
			execRoleNames.add(role.getName());
		}

		Iterator iterRoles = roles.iterator();
		String roleName = "";
		while (iterRoles.hasNext()) {
			roleName = (String) iterRoles.next();
			if (execRoleNames.contains(roleName)) {
				logger.debug("OUT.return true");
				monitor.stop();
				return true;
			}
		}
		logger.debug("OUT.return false");
		monitor.stop();
		return false;

	}

	/**
	 * Private method called by the corrispondent public method canTest.
	 * Executes roles functionalities control .
	 * 
	 * @param folderId
	 *                The id of the lowFunctionality
	 * @param profile
	 *                user profile
	 * @return A boolean control value
	 */
	private static boolean canTestInternal(LowFunctionality folder, IEngUserProfile profile) {
		Monitor monitor =MonitorFactory.start("spagobi.core.ObjectAccessVerifier.canTestInternal");
		logger.debug("IN");
		Collection roles = null;

		try {
			roles = ((UserProfile)profile).getRolesForUse();
		} catch (EMFInternalError emfie) {
			logger.error("EMFInternalError in profile.getRoles", emfie);
			monitor.stop();
			return false;
		}

		Role[] testRoles = folder.getTestRoles();
		List testRoleNames = new ArrayList();
		for (int i = 0; i < testRoles.length; i++) {
			Role role = testRoles[i];
			testRoleNames.add(role.getName());
		}

		Iterator iterRoles = roles.iterator();
		String roleName = "";
		while (iterRoles.hasNext()) {
			roleName = (String) iterRoles.next();
			if (testRoleNames.contains(roleName)) {
				logger.debug("OUT. return true");
				monitor.stop();
				return true;
			}
		}
		logger.debug("OUT. return false");
		monitor.stop();
		return false;

	}

	/**
	 * Private method called by the corrispondent public method canDev. Executes
	 * roles functionalities control .
	 * 
	 * @param folderId
	 *                The id of the lowFunctionality
	 * @param profile
	 *                user profile
	 * @return A boolean control value
	 */
	private static boolean canDevInternal(LowFunctionality folder, IEngUserProfile profile) {
		logger.debug("IN");
		Collection roles = null;
		try {
				roles = ((UserProfile)profile).getRolesForUse();
			
		} catch (EMFInternalError emfie) {
			logger.error("EMFInternalError in profile.getRoles", emfie);
			logger.debug("OUT. return false");
			return false;
		}

		Role[] devRoles = folder.getDevRoles();
		List devRoleNames = new ArrayList();
		for (int i = 0; i < devRoles.length; i++) {
			Role role = devRoles[i];
			devRoleNames.add(role.getName());
		}

		Iterator iterRoles = roles.iterator();
		String roleName = "";
		while (iterRoles.hasNext()) {
			roleName = (String) iterRoles.next();
			if (devRoleNames.contains(roleName)) {

				logger.debug("OUT. return true");
				return true;
			}
		}
		logger.debug("OUT. return false");
		return false;

	}


	/**
	 * Private method called by the corrispondent public method canTest.
	 * Executes roles functionalities control .
	 * 
	 * @param folderId
	 *                The id of the lowFunctionality
	 * @param profile
	 *                user profile
	 * @return A boolean control value
	 */
	private static boolean canTestInternal(Integer folderId, IEngUserProfile profile) {
		logger.debug("IN");
		Collection roles = null;


		try {
				roles = ((UserProfile)profile).getRolesForUse();
			
		} catch (EMFInternalError emfie) {
			logger.error("EMFInternalError in profile.getRoles", emfie);
			return false;
		}

		LowFunctionality funct = null;
		try {
			funct = DAOFactory.getLowFunctionalityDAO().loadLowFunctionalityByID(folderId, false);
		} catch (Exception e) {
			logger.error("Exception in loadLowFunctionalityByID", e);
			logger.debug("OUT. return false");
			return false;
		}
		Role[] testRoles = funct.getTestRoles();
		List testRoleNames = new ArrayList();
		for (int i = 0; i < testRoles.length; i++) {
			Role role = testRoles[i];
			testRoleNames.add(role.getName());
		}

		Iterator iterRoles = roles.iterator();
		String roleName = "";
		while (iterRoles.hasNext()) {
			roleName = (String) iterRoles.next();
			if (testRoleNames.contains(roleName)) {
				logger.debug("OUT. return true");
				return true;
			}
		}
		logger.debug("OUT. return false");
		return false;

	}

	/**
	 * Private method called by the corrispondent public method canDev. Executes
	 * roles functionalities control .
	 * 
	 * @param folderId
	 *                The id of the lowFunctionality
	 * @param profile
	 *                user profile
	 * @return A boolean control value
	 */
	private static boolean canDevInternal(Integer folderId, IEngUserProfile profile) {
		logger.debug("IN");
		Monitor monitor =MonitorFactory.start("spagobi.core.ObjectAccessVerifier.canDevInternal");
		Collection roles = null;
		try {
				roles = ((UserProfile)profile).getRolesForUse();
			
		} catch (EMFInternalError emfie) {
			logger.error("EMFInternalError in profile.getRoles", emfie);
			logger.debug("OUT. return false");
			monitor.stop();
			return false;
		}

		LowFunctionality funct = null;
		try {
			funct = DAOFactory.getLowFunctionalityDAO().loadLowFunctionalityByID(folderId, false);
		} catch (Exception e) {
			logger.error("EMFInternalError in loadLowFunctionalityByID", e);
			logger.debug("OUT. return false");
			monitor.stop();
			return false;
		}
		Role[] devRoles = funct.getDevRoles();
		List devRoleNames = new ArrayList();
		for (int i = 0; i < devRoles.length; i++) {
			Role role = devRoles[i];
			devRoleNames.add(role.getName());
		}

		Iterator iterRoles = roles.iterator();
		String roleName = "";
		while (iterRoles.hasNext()) {
			roleName = (String) iterRoles.next();
			if (devRoleNames.contains(roleName)) {

				logger.debug("OUT. return true");
				monitor.stop();
				return true;
			}
		}
		logger.debug("OUT. return false");
		monitor.stop();
		return false;

	}

	/**
	 * Controls if the current user can see the document: - if the document is
	 * in DEV state the user must have the development permission in a folder
	 * containing it; - if the document is in TEST state the user must have the
	 * test permission in a folder containing it; - if the document is in REL
	 * state the user must have the execution permission in a folder containing
	 * it.
	 * 
	 * @param obj
	 *                The BIObject
	 * @param profile
	 *                user profile
	 * 
	 * @return A boolean control value
	 * 
	 * @throws EMFInternalError
	 *                 the EMF internal error
	 */
	public static boolean canSee(BIObject obj, IEngUserProfile profile) throws EMFInternalError {
		logger.debug("IN");
		Monitor monitor =MonitorFactory.start("spagobi.core.ObjectAccessVerifier.canSee(BIObject obj, IEngUserProfile profile)");
		boolean canSee = false;
		if (obj == null){
			logger.warn("BIObject in input is null!!");
			monitor.stop();
			throw new EMFInternalError(EMFErrorSeverity.ERROR, "BIObject in input is null!!");
		}
		if (profile == null){
			logger.warn("User profile in input is null!!");
			monitor.stop();
			throw new EMFInternalError(EMFErrorSeverity.ERROR, "User profile in input is null!!");
		}
		String state = obj.getStateCode();
		if ("SUSP".equalsIgnoreCase(state)) {
			monitor.stop();
			return false;
		}


		List foldersId = obj.getFunctionalities();
		if (foldersId == null || foldersId.size() == 0){
			logger.warn("BIObject does not belong to any functionality!!");
			monitor.stop();
			throw new EMFInternalError(EMFErrorSeverity.ERROR, "BIObject does not belong to any functionality!!");
		}
		Iterator foldersIdIt = foldersId.iterator();
		while (foldersIdIt.hasNext()) {
			Integer folderId = (Integer) foldersIdIt.next();
			boolean canDev = canDev(state, folderId, profile);
			if (canDev) {
				canSee = true;
				break;
			}
			boolean canTest = canTest(state, folderId, profile);
			if (canTest) {
				canSee = true;
				break;
			}
			boolean canExec = canExec(state, folderId, profile);
			if (canExec) {
				// administrators, developers, testers, behavioural model administrators can see that document
				if (profile.isAbleToExecuteAction(SpagoBIConstants.DOCUMENT_MANAGEMENT_ADMIN)  // for administrators
						|| profile.isAbleToExecuteAction(SpagoBIConstants.DOCUMENT_MANAGEMENT_DEV)  // for developers
						|| profile.isAbleToExecuteAction(SpagoBIConstants.DOCUMENT_MANAGEMENT_TEST)  // for testers
						|| profile.isAbleToExecuteAction(SpagoBIConstants.PARAMETER_MANAGEMENT)) {  // for behavioral model administrators
					canSee = true;
				} else {
					canSee = checkProfileVisibility(obj, profile);
				}
				break;
			}
		}
		monitor.stop();
		logger.debug("OUT.canSee=" + canSee);
		return canSee;
	}


	/**
	 * Controls if the user can see the LowFunctionality.
	 * The root LowFunctionality is visible by everybody.
	 * The administrator can see all LowFunctionalities.
	 * Other users can see the LowFunctionality only if they have 
	 * at least one of the following permission:
	 * - they can develop on that folder;
	 * - they can test on that folder;
	 * - they can execute on that folder.
	 * 
	 * @param lowFunctionality
	 *                The LowFunctionality
	 * @param profile
	 *                user profile
	 * 
	 * @return true if the user can see the specified lowFunctionality, false otherwise
	 * 
	 * @throws EMFInternalError
	 *                 the EMF internal error
	 */
	public static boolean canSee(LowFunctionality lowFunctionality, IEngUserProfile profile) throws EMFInternalError {
		boolean canSee = false;
		Monitor monitor =MonitorFactory.start("spagobi.core.ObjectAccessVerifier.canSee(LowFunctionality lowFunctionality, IEngUserProfile profile)");
		logger.debug("IN: lowFunctionality path = [" + lowFunctionality.getPath() + "]; userId = [" + ((UserProfile) profile).getUserId() + "]");
		// if it is root folder, anybody can see it
		if (lowFunctionality.getParentId() == null) {
			canSee = true;
		} else {
			// if user is administrator, he can see all functionalities
			if (profile.isAbleToExecuteAction(SpagoBIConstants.DOCUMENT_MANAGEMENT_ADMIN)) {
				canSee = true;
			} else {
				// if user can exec or dev or test on functionality, he can see it, otherwise he cannot see it
				if (ObjectsAccessVerifier.canExec(lowFunctionality.getId(), profile) ||
						ObjectsAccessVerifier.canTest(lowFunctionality.getId(), profile) ||
						ObjectsAccessVerifier.canDev(lowFunctionality.getId(), profile)) {
					canSee = true;
				} else {
					canSee = false;
				}
			}
		}
		logger.debug("OUT.canSee=" + canSee);
		monitor.stop();
		return canSee;
	}

	/**
	 * Checks if the document in input has profiled visibility constraints. If it is the case, checks if the user in input has 
	 * suitable profile attributes.
	 * @param obj
	 * @param profile
	 * @return true if document profiled visibility constraints are satisfied by the user
	 * @throws EMFInternalError 
	 */
	public static boolean checkProfileVisibility(BIObject obj, IEngUserProfile profile) throws EMFInternalError {
		Monitor monitor =MonitorFactory.start("spagobi.core.ObjectAccessVerifier.checkProfileVisibility");

		logger.debug("IN: obj label is [" + obj.getLabel() + "]; user is [" + ((UserProfile)profile).getUserId().toString() + "]");
		boolean toReturn = true;
		String profVisibility = obj.getProfiledVisibility();
		if (profVisibility == null || profVisibility.trim().equals("")) {
			logger.debug("Biobject with label [" + obj.getLabel() + "] has no profile visibility contraints.");
			monitor.stop();
			return true;
		}
		logger.debug("Biobject with label [" + obj.getLabel() + "] has profile visibility contraints = [" + profVisibility + "]");
		String[] constraints = profVisibility.split(" AND ");
		for (int i = 0; i < constraints.length; i++) {
			String constraint = constraints[i];
			logger.debug("Examining constraint [" + constraint + "] ...");
			int index = constraint.indexOf("=");
			if (index == -1) {
				logger.error("Constraint [" + constraint + "] is not correct!! It should have the syntax PROFILE_ATTRIBUTE_NAME=VALUE. It will be ignored.");
				continue;
			}
			String profileAttrName = constraint.substring(0, index).trim();
			String value = constraint.substring(index + 1).trim();
			if (!profile.getUserAttributeNames().contains(profileAttrName)) {
				logger.debug("User profile hasn't the required profile attribute [" + profileAttrName + "], it does not satisfy constraint");
				toReturn = false;
				break;
			}
			Object profileAttr = profile.getUserAttribute(profileAttrName);
			if (profileAttr == null) {
				logger.debug("User profile attribute [" + profileAttrName + "] is null, it does not satisfy constraint");
				toReturn = false;
				break;
			}
			String profileAttrStr = profileAttr.toString();
			if (profileAttrStr.startsWith("{")) {
				// the profile attribute is multi-value
				String[] values = null;
				try {
					values = GeneralUtilities.findAttributeValues(profileAttrStr);
				} catch (Exception e) {
					logger.error("Error while reading profile attribute", e);
					logger.debug("User profile attribute [" + profileAttrName + "] does not satisfy constraint");
					toReturn = false;
					break;
				}
				if (!Arrays.asList(values).contains(value)) {
					logger.debug("User profile attribute [" + profileAttrName + "] does not contain [" + value + "] value, it does not satisfy constraint");
					toReturn = false;
					break;
				}
			} else {
				// the profile attribute is single-value
				if (!profileAttrStr.equals(value)) {
					logger.debug("User profile attribute [" + profileAttrName + "] is not equal to [" + value + "], it does not satisfy constraint");
					toReturn = false;
					break;
				}
			}
		}
		logger.debug("OUT.canSee=" + toReturn);
		monitor.stop();
		return toReturn;
	}

	/**
	 * returns the list of correct roles of the input profile for the execution of the document with the specified input
	 * @param objectId the document id
	 * @param profile the user profile
	 * @return the list of correct roles of the input profile for the execution of the document with the specified input
	 * @throws EMFUserError 
	 * @throws EMFInternalError 
	 */
	public static List getCorrectRolesForExecution(Integer objectId , IEngUserProfile profile) throws EMFInternalError, EMFUserError {
		Monitor monitor =MonitorFactory.start("spagobi.core.ObjectAccessVerifier.getCorrectRolesForExecution");
		logger.debug("IN");
		List correctRoles = null;
		if (profile.isAbleToExecuteAction(SpagoBIConstants.DOCUMENT_MANAGEMENT_DEV)
				|| profile.isAbleToExecuteAction(SpagoBIConstants.DOCUMENT_MANAGEMENT_USER)
				|| profile.isAbleToExecuteAction(SpagoBIConstants.DOCUMENT_MANAGEMENT_ADMIN))
			correctRoles = DAOFactory.getBIObjectDAO()
			.getCorrectRolesForExecution(objectId, profile);
		else
			correctRoles = DAOFactory.getBIObjectDAO()
			.getCorrectRolesForExecution(objectId);
		logger.debug("OUT");
		monitor.stop();
		return correctRoles;
	}
}
