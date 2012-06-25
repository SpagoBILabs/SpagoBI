/* SpagoBI, the Open Source Business Intelligence suite

* © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this file,
* You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.analiticalmodel.documentsbrowser.service;

import it.eng.spago.base.SessionContainer;
import it.eng.spago.base.SourceBean;
import it.eng.spago.error.EMFUserError;
import it.eng.spago.security.IEngUserProfile;
import it.eng.spagobi.analiticalmodel.document.bo.BIObject;
import it.eng.spagobi.analiticalmodel.functionalitytree.bo.LowFunctionality;
import it.eng.spagobi.commons.bo.Domain;
import it.eng.spagobi.commons.constants.SpagoBIConstants;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.commons.serializer.DocumentsJSONDecorator;
import it.eng.spagobi.commons.serializer.SerializerFactory;
import it.eng.spagobi.commons.utilities.ObjectsAccessVerifier;
import it.eng.spagobi.commons.utilities.messages.MessageBuilder;
import it.eng.spagobi.utilities.exceptions.SpagoBIException;
import it.eng.spagobi.utilities.service.AbstractBaseHttpAction;
import it.eng.spagobi.utilities.service.JSONSuccess;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 *
 */
public class GetFolderContentAction extends AbstractBaseHttpAction{

	// REQUEST PARAMETERS
	public static final String FOLDER_ID = "folderId";

	public static final String ROOT_NODE_ID = "rootNode";

	// logger component
	private static Logger logger = Logger.getLogger(GetFolderContentAction.class);

	public void service(SourceBean request, SourceBean response) throws Exception {


		List functionalities;
		List objects;
		boolean isRoot = false;
		boolean isHome = false;

		logger.debug("IN");

		try {
			setSpagoBIRequestContainer( request );
			setSpagoBIResponseContainer( response );

			String functID = getAttributeAsString(FOLDER_ID);		
			logger.debug("Parameter [" + FOLDER_ID + "] is equal to [" + functID + "]");

			//getting default folder (root)
			LowFunctionality rootFunct = DAOFactory.getLowFunctionalityDAO().loadRootLowFunctionality(false);
			if (functID == null || functID.equalsIgnoreCase(ROOT_NODE_ID)){
				isRoot = true;
				functID = String.valueOf(rootFunct.getId());
			}else if (functID.equalsIgnoreCase(rootFunct.getId().toString())) {
				isRoot = true;
			}


			SessionContainer sessCont = getSessionContainer();
			SessionContainer permCont = sessCont.getPermanentContainer();
			IEngUserProfile profile = (IEngUserProfile)permCont.getAttribute(IEngUserProfile.ENG_USER_PROFILE);

			LowFunctionality targetFunct = DAOFactory.getLowFunctionalityDAO().loadLowFunctionalityByID(new Integer(functID), false);
			isHome = "USER_FUNCT".equalsIgnoreCase( targetFunct.getCodType() );

			//getting children documents
			//LowFunctionality lowFunct = DAOFactory.getLowFunctionalityDAO().loadLowFunctionalityByID(functID, true);
			//objects = lowFunct.getBiObjects();
			List tmpObjects = DAOFactory.getBIObjectDAO().loadBIObjects(Integer.valueOf(functID), profile,isHome);
			objects = new ArrayList();
			if(tmpObjects != null) {
				for(Iterator it = tmpObjects.iterator(); it.hasNext();) {
					BIObject obj = (BIObject)it.next();
					if(ObjectsAccessVerifier.checkProfileVisibility(obj, profile))
						objects.add(obj);
				}
			}
			HttpServletRequest httpRequest = getHttpRequest();
			MessageBuilder m = new MessageBuilder();
			Locale locale = m.getLocale(httpRequest);
			JSONArray documentsJSON = (JSONArray)SerializerFactory.getSerializer("application/json").serialize( objects ,locale);
			
			for(int i=0; i<documentsJSON.length(); i++){
				DocumentsJSONDecorator.decoreDocument(documentsJSON.getJSONObject(i), profile);
			}
			
			Collection func = profile.getFunctionalities();

			if(func.contains("SeeMetadataFunctionality")){
				JSONObject showmetadataAction = new JSONObject();
				showmetadataAction.put("name", "showmetadata");
				showmetadataAction.put("description", "Show Metadata");
				for(int i = 0; i < documentsJSON.length(); i++) {
					JSONObject documentJSON = documentsJSON.getJSONObject(i);
					documentJSON.getJSONArray("actions").put(showmetadataAction);
				}
			}
			if(isHome) {
				JSONObject deleteAction = new JSONObject();
				deleteAction.put("name", "delete");
				deleteAction.put("description", "Delete this item");
				for(int i = 0; i < documentsJSON.length(); i++) {
					JSONObject documentJSON = documentsJSON.getJSONObject(i);
					documentJSON.getJSONArray("actions").put(deleteAction);
				}
			}
			JSONObject documentsResponseJSON =  createJSONResponseDocuments(documentsJSON);

			//getting children folders
			/*
			if (isRoot)
				functionalities = DAOFactory.getLowFunctionalityDAO().loadUserFunctionalities(true, false, profile);	
			else
				functionalities = DAOFactory.getLowFunctionalityDAO().loadChildFunctionalities(Integer.valueOf(functID), false);	
			 */
			boolean recoverBiObjects = false;
			// for massive export must also get the objects to check if there are worksheets
			if(func.contains("DoMassiveExportFunctionality")){
				recoverBiObjects = true;
			}
			
			functionalities = DAOFactory.getLowFunctionalityDAO().loadUserFunctionalities(Integer.valueOf(functID), recoverBiObjects, profile);

			JSONArray foldersJSON = (JSONArray)SerializerFactory.getSerializer("application/json").serialize( functionalities,locale );			

			JSONObject exportAction = new JSONObject();
			exportAction.put("name", "export");
			exportAction.put("description", "Export");

			JSONObject scheduleAction = new JSONObject();
			scheduleAction.put("name", "schedule");
			scheduleAction.put("description", "Schedule");

			// call check for worksheet presence only if user can eexecute massive export, otherwise jump over control
			if(func.contains("DoMassiveExportFunctionality")){
				Map<String, Boolean> folderToWorksheet = checkIfWorksheetContained(functionalities);

				for(int i = 0; i < foldersJSON.length(); i++) {
					JSONObject folderJSON = foldersJSON.getJSONObject(i);
					String code = folderJSON.getString("code");
					Boolean isWorksheet = folderToWorksheet.get(code);
					if(isWorksheet){
						folderJSON.getJSONArray("actions").put(exportAction);
						folderJSON.getJSONArray("actions").put(scheduleAction);
					}
				}
			}

			JSONObject foldersResponseJSON =  createJSONResponseFolders(foldersJSON);


			try {
				writeBackToClient( new JSONSuccess( createJSONResponse(foldersResponseJSON, documentsResponseJSON) ) );
			} catch (IOException e) {
				throw new SpagoBIException("Impossible to write back the responce to the client", e);
			}

		} catch (Throwable t) {
			throw new SpagoBIException("An unexpected error occured while executing " + getActionName(), t);
		} finally {
			logger.debug("OUT");
		}
	}

	/**
	 * Creates a json array with children document informations
	 * @param rows
	 * @return
	 * @throws JSONException
	 */
	private JSONObject createJSONResponseDocuments(JSONArray rows) throws JSONException {
		JSONObject results;

		results = new JSONObject();
		results.put("title", "Documents");
		results.put("icon", "document.png");
		results.put("samples", rows);
		return results;
	}

	/**
	 * Creates a json array with children folders informations
	 * @param rows
	 * @return
	 * @throws JSONException
	 */
	private JSONObject createJSONResponseFolders(JSONArray rows) throws JSONException {
		JSONObject results;

		results = new JSONObject();
		results.put("title", "Folders");
		results.put("icon", "folder.png");
		results.put("samples", rows);
		return results;
	}

	/**
	 * Creates a json array with children document informations
	 * @param rows
	 * @return
	 * @throws JSONException
	 */
	private JSONObject createJSONResponse(JSONObject folders, JSONObject documents) throws JSONException {
		JSONObject results = new JSONObject();
		JSONArray folderContent = new JSONArray();

		folderContent.put(folders);
		folderContent.put(documents);
		results.put("folderContent", folderContent);

		return results;
	}

	private Map checkIfWorksheetContained(List functionalities) throws SpagoBIException{
		logger.debug("IN");
		// link each functionality to bo0olean indicating if containing worksheets
		Domain worksheetDomain;
		try {
			worksheetDomain = DAOFactory.getDomainDAO().loadDomainByCodeAndValue(SpagoBIConstants.BIOBJ_TYPE, SpagoBIConstants.WORKSHEET_TYPE_CODE);
		} catch (EMFUserError e) {
			logger.error("Could not recover Worksheet domain type", e);
			throw new SpagoBIException("Could not recover Worksheet domain type", e );
		}

		Map<String, Boolean> functWorksheet = new HashMap<String, Boolean>();
		for (Iterator iterator = functionalities.iterator(); iterator.hasNext();) {
			LowFunctionality lowFunc = (LowFunctionality) iterator.next();
			boolean isThereWorksheet = false;
			if(lowFunc.getBiObjects() != null){
				for (Iterator iterator2 = lowFunc.getBiObjects().iterator(); iterator2.hasNext() && !isThereWorksheet;) {
					BIObject biObj = (BIObject) iterator2.next();
					Integer typeId = biObj.getBiObjectTypeID();
					if(typeId.equals(worksheetDomain.getValueId())){
						isThereWorksheet = true;
					}
				}
			}
			logger.debug("functionality "+lowFunc.getCode()+" has worksheets inside? "+isThereWorksheet);
			functWorksheet.put(lowFunc.getCode(), isThereWorksheet);

		}
		logger.debug("OUT");
		return functWorksheet;
	}

}
