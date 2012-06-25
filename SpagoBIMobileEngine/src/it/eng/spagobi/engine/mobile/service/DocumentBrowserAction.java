/* SpagoBI, the Open Source Business Intelligence suite

* © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this file,
* You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.engine.mobile.service;

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
import it.eng.spagobi.engine.mobile.MobileConstants;
import it.eng.spagobi.utilities.engines.SpagoBIEngineServiceException;
import it.eng.spagobi.utilities.exceptions.SpagoBIException;
import it.eng.spagobi.utilities.service.AbstractBaseHttpAction;
import it.eng.spagobi.utilities.service.JSONFailure;
import it.eng.spagobi.utilities.service.JSONSuccess;


/**
 * @author Monica Franceschini
 *
 */
public class DocumentBrowserAction extends AbstractBaseHttpAction{
	
	// REQUEST PARAMETERS
	public static final String FOLDER_ID = "node";
	
	public static final String ROOT_NODE_ID = "rootNode";
	
	// logger component
	private static Logger logger = Logger.getLogger(DocumentBrowserAction.class);
	
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
			if (functID == null || functID.equalsIgnoreCase(ROOT_NODE_ID) ||functID.equalsIgnoreCase("0")||functID.equalsIgnoreCase("root")){
				isRoot = true;
				functID = String.valueOf(rootFunct.getId());
			}else if (functID.equalsIgnoreCase(rootFunct.getId().toString())) {
				isRoot = true;
			}
			
			
			SessionContainer sessCont = getSessionContainer();
			SessionContainer permCont = sessCont.getPermanentContainer();
			IEngUserProfile profile = (IEngUserProfile)permCont.getAttribute(IEngUserProfile.ENG_USER_PROFILE);
			
			///case user not logged provides a fake store
			if(profile == null || functID.equals("root")){

				JSONObject foldersAndDocsResponseJSON =  createJSONResponse(new JSONArray("[{name: Document Browser}]"), new JSONArray());
				
				try {
					writeBackToClient( new JSONSuccess( foldersAndDocsResponseJSON ) );
					return;
				} catch (IOException e) {
					SpagoBIEngineServiceException serviceError = new SpagoBIEngineServiceException("Document Browser", "Document browser failure");
					writeBackToClient(new JSONFailure(serviceError));
					throw new SpagoBIException("Impossible to write back the responce to the client", e);
					
				}
			}
			
			LowFunctionality targetFunct = DAOFactory.getLowFunctionalityDAO().loadLowFunctionalityByID(new Integer(functID), false);
			isHome = "USER_FUNCT".equalsIgnoreCase( targetFunct.getCodType() );

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


			functionalities = DAOFactory.getLowFunctionalityDAO().loadUserFunctionalities(Integer.valueOf(functID), false, profile);
			
			JSONArray foldersJSON = (JSONArray)SerializerFactory.getSerializer("application/json").serialize( functionalities,locale );			
			
			JSONObject foldersAndDocsResponseJSON =  createJSONResponse(foldersJSON, documentsJSON);
			
			
			try {
				writeBackToClient( new JSONSuccess( foldersAndDocsResponseJSON ) );
			} catch (IOException e) {
				SpagoBIEngineServiceException serviceError = new SpagoBIEngineServiceException("Document Browser", "Document browser failure");
				writeBackToClient(new JSONFailure(serviceError));
				throw new SpagoBIException("Impossible to write back the responce to the client", e);
			}
			
		} catch (Throwable t) {

			throw new SpagoBIException("An unexpected error occured while executing " + getActionName(), t);
		} finally {
			logger.debug("OUT");
		}
	}


	/**
	 * Creates a json object with children document and folders
	 * @param rows
	 * @return
	 * @throws JSONException
	 */
	private JSONObject createJSONResponse(JSONArray folders, JSONArray documents) throws JSONException {
		JSONObject results = new JSONObject();
		if(documents.length() != 0){
			for(int i=0; i< documents.length(); i++){
				
				JSONObject doc = documents.getJSONObject(i);
				if(((String)doc.get("typeCode")).equalsIgnoreCase(MobileConstants.DOCUMENT_TYPE_MOBILE_REPORT) || 
						((String)doc.get("typeCode")).equalsIgnoreCase(MobileConstants.DOCUMENT_TYPE_MOBILE_CHART)||
						((String)doc.get("typeCode")).equalsIgnoreCase(MobileConstants.DOCUMENT_TYPE_MOBILE_COCKPIT)){
					doc.put("leaf", true);
					folders.put(doc);
				}
			}
		}
		results.put("name", "User Home");
		results.put("samples", folders);

		return results;
	}
}
