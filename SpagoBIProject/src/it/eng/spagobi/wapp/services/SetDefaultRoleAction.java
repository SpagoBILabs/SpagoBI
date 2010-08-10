package it.eng.spagobi.wapp.services;

import it.eng.spago.base.RequestContainer;
import it.eng.spago.base.SessionContainer;
import it.eng.spago.error.EMFInternalError;
import it.eng.spago.security.IEngUserProfile;
import it.eng.spagobi.analiticalmodel.document.x.AbstractSpagoBIAction;
import it.eng.spagobi.commons.bo.UserProfile;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.commons.utilities.StringUtilities;
import it.eng.spagobi.commons.utilities.UserUtilities;
import it.eng.spagobi.utilities.engines.SpagoBIEngineServiceException;
import it.eng.spagobi.utilities.exceptions.SpagoBIServiceException;
import it.eng.spagobi.utilities.service.JSONAcknowledge;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.apache.log4j.Logger;

public class SetDefaultRoleAction extends AbstractSpagoBIAction{

	static private Logger logger = Logger.getLogger(SetDefaultRoleAction.class);

	UserProfile userProfile = null;

	public static final String SERVICE_NAME = "SET_DEFAULT_ROLE_ACTION";
	// REQUEST PARAMETERS
	public static final String MESSAGE = "MESSAGE";
	public static final String SELECTED_ROLE = "SELECTED_ROLE";

	//	static public String[] funcsToRemove = {
	//		SpagoBIConstants.DOCUMENT_MANAGEMENT_ADMIN
	//		, SpagoBIConstants.DOCUMENT_MANAGEMENT_DEV
	//		, SpagoBIConstants.DOCUMENT_MANAGEMENT_TEST
	//		, SpagoBIConstants.DOCUMENT_MANAGEMENT_USER
	//		, SpagoBIConstants.DISTRIBUTIONLIST_MANAGEMENT
	//		//, SpagoBIConstants.FUNCTIONALITIES_MANAGEMENT
	//	};

	/**
	 *  Returns Default role if present
	 */

	public void doService() {
		logger.debug("IN on service");
		try {

			IEngUserProfile	profile = this.getUserProfile();

			String selRole = this.getAttributeAsString(SELECTED_ROLE);
			logger.debug("Selected role "+selRole);

			// check if selected role is part of the user ones
			ArrayList<String> roles = (ArrayList<String>)profile.getRoles();

			for (int i = 0; i < roles.size(); i++) {
				logger.debug("user roles "+roles.get(i));
			}			

			if(selRole.equals("")){
				selRole = null;
			}

			if(selRole!=null && !roles.contains(selRole)){
				logger.error("Security alert. Role not among the user ones");
				throw new SpagoBIServiceException(SERVICE_NAME, "Role selected is not permitted for user "+userProfile.getUserId());	
			}

			// set this role as default one, or clear default role if not present
			String previousDefault = ((UserProfile)profile).getDefaultRole();
			logger.debug("previous default role "+previousDefault);
			logger.debug("new default role "+selRole);
			((UserProfile)profile).setDefaultRole(selRole);
			logger.debug("default role set! ");

			// now I must refresh userProfile functions

			//String[] newFunctions = null;
			Collection coll = null;

			it.eng.spagobi.commons.dao.IUserFunctionalityDAO dao = DAOFactory.getUserFunctionalityDAO();

			// if new selROle is null refresh all the functionalities!
			if(selRole == null){
				logger.debug("Selected role is null, refresh all functionalities ");				
				Collection allRoles = profile.getRoles();
				String[] array = StringUtilities.convertCollectionInArray(allRoles);
				//String[] arrayFuncs = dao.readUserFunctionality(array);				
				String[] arrayFuncs = UserUtilities.readFunctionality(array);

				coll = StringUtilities.convertArrayInCollection(arrayFuncs);
				((UserProfile)profile).setFunctionalities(coll);
				//				ISecurityServiceSupplier supplier = SecurityServiceSupplierFactory.createISecurityServiceSupplier();		
				//				SpagoBIUserProfile user = supplier.createUserProfile(profile.getUserUniqueIdentifier().toString());
				//				newFunctions = user.getRoles();

				if( coll == null ) StringUtilities.convertArrayInCollection(arrayFuncs);
				((UserProfile)profile).setFunctionalities(coll);
			}
			else {
				// there is a default role selected so filter only its functionalities
				logger.debug("Selected role is not null, put right functionality");				
				String[] selRoleArray = new String[1];
				selRoleArray[0] = selRole;
				String[] arrayFuncs = UserUtilities.readFunctionality(selRoleArray);

				//String[] arrayFuncs = dao.readUserFunctionality(selRoleArray);
				//String[] arrayFuncs = UserUtilities.readFunctionality(selRoleArray);

				coll = StringUtilities.convertArrayInCollection(arrayFuncs);

				for (Iterator iterator = coll.iterator(); iterator.hasNext();) {
					Object object = (Object) iterator.next();
					logger.debug("functionality to add "+object.toString());
				}

				((UserProfile)profile).setFunctionalities(coll);

				logger.debug("set functionalities for default role");
				// check if single functionality is included in role, else remove it
				//			    for (int i = 0; i < funcsToRemove.length; i++) {
				//			    	String funcToRem = funcsToRemove[i];
				//			    	if(!coll.contains(funcToRem)){
				//			    		((UserProfile)profile).getFunctionalities().remove(funcToRem);
				//			    	}
				//			    }

			}

			//			if( coll == null ) StringUtilities.convertArrayInCollection(newFunctions);
			//			((UserProfile)profile).setFunctionalities(coll);
			// end refresh of the functionalities
			logger.debug("FIltered functionalities for selected role "+selRole);

			try {
				writeBackToClient( new JSONAcknowledge() );
			} catch (IOException e) {
				String message = "Impossible to write back the responce to the client";
				throw new SpagoBIEngineServiceException(getActionName(), message, e);
			}

		} catch (Exception e) {
			throw new SpagoBIServiceException(SERVICE_NAME, "Exception occurred while retrieving metadata", e);
		} finally {
			logger.debug("OUT");
		}	


	}






}
