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
package it.eng.spagobi.analiticalmodel.documentsbrowser.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import it.eng.spago.base.SessionContainer;
import it.eng.spago.base.SourceBean;
import it.eng.spago.security.IEngUserProfile;
import it.eng.spagobi.analiticalmodel.document.bo.BIObject;
import it.eng.spagobi.analiticalmodel.functionalitytree.bo.LowFunctionality;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.commons.serializer.SerializerFactory;
import it.eng.spagobi.commons.utilities.ObjectsAccessVerifier;
import it.eng.spagobi.commons.utilities.messages.MessageBuilder;
import it.eng.spagobi.utilities.exceptions.SpagoBIException;
import it.eng.spagobi.utilities.service.AbstractBaseHttpAction;
import it.eng.spagobi.utilities.service.JSONSuccess;


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
			functionalities = DAOFactory.getLowFunctionalityDAO().loadUserFunctionalities(Integer.valueOf(functID), false, profile);
			
			JSONArray foldersJSON = (JSONArray)SerializerFactory.getSerializer("application/json").serialize( functionalities,locale );			
			
			JSONObject exportAction = new JSONObject();
			exportAction.put("name", "export");
			exportAction.put("description", "Export");
			
			JSONObject scheduleAction = new JSONObject();
			scheduleAction.put("name", "schedule");
			scheduleAction.put("description", "Schedule");
							
			for(int i = 0; i < foldersJSON.length(); i++) {
				if(func.contains("DoMassiveExportFunctionality")){
					JSONObject folderJSON = foldersJSON.getJSONObject(i);
					folderJSON.getJSONArray("actions").put(exportAction);
				}
				if(func.contains("DoMassiveExportFunctionality")){
					JSONObject folderJSON = foldersJSON.getJSONObject(i);
					folderJSON.getJSONArray("actions").put(scheduleAction);
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
}
