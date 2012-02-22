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
package it.eng.spagobi.engine.mobile.service;

import it.eng.spago.error.EMFUserError;
import it.eng.spago.security.IEngUserProfile;
import it.eng.spagobi.analiticalmodel.document.bo.BIObject;
import it.eng.spagobi.analiticalmodel.document.handlers.ExecutionInstance;
import it.eng.spagobi.commons.constants.ObjectsTreeConstants;
import it.eng.spagobi.commons.constants.SpagoBIConstants;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.commons.services.AbstractSpagoBIAction;
import it.eng.spagobi.commons.utilities.ObjectsAccessVerifier;
import it.eng.spagobi.commons.utilities.StringUtilities;
import it.eng.spagobi.engine.mobile.MobileConstants;
import it.eng.spagobi.utilities.assertion.Assert;
import it.eng.spagobi.utilities.exceptions.SpagoBIServiceException;

import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.safehaus.uuid.UUID;
import org.safehaus.uuid.UUIDGenerator;

/**
 * @authors Alberto Ghedin (alberto.ghedin@eng.it)
 *
 */
public abstract class AbstractExecuteMobileAction extends AbstractSpagoBIAction {
		
	private static final long serialVersionUID = 1273815056481796113L;

	public static final String SERVICE_NAME = "SPAGOBI_MOBILE_ACTION";
	
	//errors after parameters validation
	private List paramErrors;
	
	// request parameters
	public static String DOCUMENT_ID = ObjectsTreeConstants.OBJECT_ID;
	public static String DOCUMENT_LABEL = ObjectsTreeConstants.OBJECT_LABEL;
	public static String EXECUTION_ROLE = SpagoBIConstants.ROLE;
	
	// logger component
	private static Logger logger = Logger.getLogger(AbstractExecuteMobileAction.class);
	
	/**
	 * Load the document: check if the document stay in a composed document..
	 * If so it get from the request:
	 *  MobileConstants.IS_FROM_COMPOSED , DOCUMENT_ID , DOCUMENT_LABEL , EXECUTION_ROLE ,ObjectsTreeConstants.PARAMETERS
	 * @return
	 */
	public BIObject getAndValidateBIObject() {
		ExecutionInstance instance;
		
		Integer documentId;
		String documentLabel;
		String executionRole;
		String userProvidedParametersStr;
		JSONObject userProvidedParametersJSONObject;
				
		BIObject obj;
		IEngUserProfile profile;
		List roles;
		
		logger.debug("IN");
		
		try {
			paramErrors = null;
			
			boolean isFromComposed = this.getAttributeAsBoolean( MobileConstants.IS_FROM_COMPOSED );
			userProvidedParametersJSONObject = getAttributeAsJSONObject(ObjectsTreeConstants.PARAMETERS);
			userProvidedParametersStr = getParametersString(userProvidedParametersJSONObject);
			
			if(!isFromComposed){
				instance = getContext().getExecutionInstance( ExecutionInstance.class.getName() );
			}else{
				profile = getUserProfile();
				documentId = requestContainsAttribute( DOCUMENT_ID )? getAttributeAsInteger( DOCUMENT_ID ): null;
				documentLabel = getAttributeAsString( DOCUMENT_LABEL );
				executionRole = getAttributeAsString( EXECUTION_ROLE );
				
				
				logger.debug("Parameter [" + DOCUMENT_ID + "] is equals to [" + documentId + "]");
				logger.debug("Parameter [" + DOCUMENT_LABEL + "] is equals to [" + documentLabel + "]");
				logger.debug("Parameter [" + EXECUTION_ROLE + "] is equals to [" + executionRole + "]");
				
				Assert.assertTrue(!StringUtilities.isEmpty( documentLabel ) || documentId != null, 
						"At least one between [" + DOCUMENT_ID + "] and [" + DOCUMENT_LABEL + "] parameter must be specified on request");
				
				Assert.assertTrue(!StringUtilities.isEmpty( executionRole ), "Parameter [" + EXECUTION_ROLE + "] cannot be null");
				
				// load object to chek if it exists
				obj = null;
				if ( !StringUtilities.isEmpty( documentLabel ) ) {
					logger.debug("Loading document with label = [" + documentLabel + "] ...");
					try {
						obj = DAOFactory.getBIObjectDAO().loadBIObjectByLabel(documentLabel);
					} catch (EMFUserError error) {
						logger.error("Object with label equals to [" + documentLabel + "] not found");
						throw new SpagoBIServiceException(SERVICE_NAME, "Object with label equals to [" + documentId + "] not found", error);
					}		
				} else if ( documentId != null ) {
					logger.info("Loading biobject with id = [" + documentId + "] ...");
					try {
						obj = DAOFactory.getBIObjectDAO().loadBIObjectById(documentId);
					} catch (EMFUserError error) {
						logger.error("Object with id equals to [" + documentId + "] not found");
						throw new SpagoBIServiceException(SERVICE_NAME, "Object with id equals to [" + documentId + "] not found", error);
					}
				} else {
					Assert.assertUnreachable("At least one between [" + DOCUMENT_ID + "] and [" + DOCUMENT_LABEL + "] parameter must be specified on request");
				}
				Assert.assertNotNull(obj, "Impossible to load document");
				logger.debug("... docuemnt loaded succesfully");
				
				// retrive roles for execution
				try {
					roles = ObjectsAccessVerifier.getCorrectRolesForExecution(obj.getId(), profile);
				} catch (Throwable t) {
					throw new SpagoBIServiceException(SERVICE_NAME, t);			
				} 
				
				if (roles != null && !roles.contains(executionRole)) {
					logger.error("Document [id: " + obj.getId() +"; label: " + obj.getLabel() + " ] cannot be executed by any role of the user [" + profile.getUserUniqueIdentifier() + "]");
					throw new SpagoBIServiceException(SERVICE_NAME, "Document [id: " + obj.getId() +"; label: " + obj.getLabel() + " ] cannot be executed by any role of the user [" + profile.getUserUniqueIdentifier() + "]");
				}
						
				
				// so far so good: everything has been validated successfully. Let's create a new ExecutionInstance.
				//instance = createExecutionInstance(obj.getId(), executionRole);
				
				UUIDGenerator uuidGen  = UUIDGenerator.getInstance();
				UUID uuidObj = uuidGen.generateTimeBasedUUID();
				String executionContextId = uuidObj.toString();
				executionContextId = executionContextId.replaceAll("-", "");
				
				//CoreContextManager ccm = createContext( executionContextId );
				   // so far so good: everything has been validated successfully. Let's create a new ExecutionInstance.
				instance = createExecutionInstance(obj.getId(), executionRole, executionContextId, getLocale());
				   
			}
			
			try {
				//check the parameters
				instance.refreshParametersValues(userProvidedParametersJSONObject, true);	
				obj = instance.getBIObject();
				paramErrors = instance.getParametersErrors();
			} catch (Exception e) {
				logger.error("Error validating the parameters", e);
				throw new SpagoBIServiceException("Error validating the parameters", e);
			}
			
			// refresh obj variable because createExecutionInstance load the BIObject in a different way
			return obj;	
			
			
		} finally {
			logger.debug("OUT");
		}
	}
	
	private ExecutionInstance createExecutionInstance(Integer biobjectId, String aRoleName, String execId, Locale locale) {
		String executionFlowId = getAttributeAsString("EXECUTION_FLOW_ID");
		Boolean displayToolbar = getAttributeAsBoolean(SpagoBIConstants.TOOLBAR_VISIBLE, true);
		Boolean displaySlider = getAttributeAsBoolean(SpagoBIConstants.SLIDERS_VISIBLE, true);
		String modality = requestContainsAttribute(ObjectsTreeConstants.MODALITY)
							? getAttributeAsString(ObjectsTreeConstants.MODALITY)
							: SpagoBIConstants.NORMAL_EXECUTION_MODALITY;
		
		// create execution id
		UUIDGenerator uuidGen  = UUIDGenerator.getInstance();
		UUID uuidObj = uuidGen.generateTimeBasedUUID();
		String executionId = uuidObj.toString();
		executionId = executionId.replaceAll("-", "");
		
		if (executionFlowId == null) executionFlowId = executionId;
				
		// create new execution instance
		ExecutionInstance instance = null;
		try {
			instance = new ExecutionInstance(getUserProfile(), executionFlowId, execId, biobjectId, aRoleName, modality, 
					displayToolbar.booleanValue(), displaySlider.booleanValue(), locale);
		} catch (Exception e) {
			logger.error(e);
		}
		return instance;
	}

	public List getParamErrors() {
		return paramErrors;
	}
	
	private String getParametersString(JSONObject parametersObject){
		StringBuilder parametersString = new StringBuilder("");
		String[] fields = new String[0];
		
		try {
			if(parametersObject!=null){
				fields = JSONObject.getNames(parametersObject);
			}

			for (String field : fields) {
				parametersString.append(field);
				parametersString.append("=");
				parametersString.append(parametersObject.getString(field));
				parametersString.append("&");
			}
			if(parametersString.length()>0){
				parametersString.deleteCharAt(parametersString.length()-1);
			}
		} catch (Exception e) {
			logger.error("Error loading the parameters");
		}
		return parametersString.toString();
	}
}