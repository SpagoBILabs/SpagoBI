/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.engine.mobile.service;

import it.eng.spago.base.SessionContainer;
import it.eng.spago.base.SourceBean;
import it.eng.spago.error.EMFInternalError;
import it.eng.spago.error.EMFUserError;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Monica Franceschini
 * 
 */
public class DocumentBrowserAction extends AbstractBaseHttpAction {

	// REQUEST PARAMETERS
	public static final String FOLDER_ID = "node";

	public static final String ROOT_NODE_ID = "rootNode";

	// logger component
	private static Logger logger = Logger
			.getLogger(DocumentBrowserAction.class);

	public void service(SourceBean request, SourceBean response)
			throws Exception {

		List functionalities;

		List objects = new ArrayList<BIObject>();
		boolean isRoot = false;
		boolean isHome = false;

		logger.debug("IN");

		try {
			setSpagoBIRequestContainer(request);
			setSpagoBIResponseContainer(response);

			String functID = getAttributeAsString(FOLDER_ID);
			logger.debug("Parameter [" + FOLDER_ID + "] is equal to ["
					+ functID + "]");

			// getting default folder (root)
			LowFunctionality rootFunct = DAOFactory.getLowFunctionalityDAO()
					.loadRootLowFunctionality(false);
			if (functID == null || functID.equalsIgnoreCase(ROOT_NODE_ID)
					|| functID.equalsIgnoreCase("0")
					|| functID.equalsIgnoreCase("ext-data-treestore-1-root")) {
				isRoot = true;
				functID = String.valueOf(rootFunct.getId());
			} else if (functID.equalsIgnoreCase(rootFunct.getId().toString())) {
				isRoot = true;
			}

			SessionContainer sessCont = getSessionContainer();
			SessionContainer permCont = sessCont.getPermanentContainer();
			IEngUserProfile profile = (IEngUserProfile) permCont
					.getAttribute(IEngUserProfile.ENG_USER_PROFILE);

			// /case user not logged provides a fake store
			if (profile == null || functID.equals("ext-data-treestore-1-root")) {

				JSONObject foldersAndDocsResponseJSON = createJSONResponse(
						new JSONArray("[{name: Document Browser}]"),
						new JSONArray());

				try {
					writeBackToClient(new JSONSuccess(
							foldersAndDocsResponseJSON));
					return;
				} catch (IOException e) {
					SpagoBIEngineServiceException serviceError = new SpagoBIEngineServiceException(
							"Document Browser", "Document browser failure");
					writeBackToClient(new JSONFailure(serviceError));
					throw new SpagoBIException(
							"Impossible to write back the responce to the client",
							e);

				}
			}

			objects = fillInnerDocuments(functID, isHome, profile);

			HttpServletRequest httpRequest = getHttpRequest();
			MessageBuilder m = new MessageBuilder();
			Locale locale = m.getLocale(httpRequest);

			functionalities = DAOFactory.getLowFunctionalityDAO()
					.loadUserFunctionalities(Integer.valueOf(functID), false,
							profile);
			HashMap<Integer, String> functToRet = new HashMap<Integer, String>();

			List toRet = new ArrayList();
			
			List toExplore = new ArrayList();
			
			toRet.addAll(functionalities);
			for (int i = 0; i < functionalities.size(); i++) {
				LowFunctionality lf = (LowFunctionality)functionalities.get(i);
				getFoldersWithinMobileDocs(lf, profile,functToRet,lf.getId());
			}
			for (int i = 0; i < functionalities.size(); i++) {
				LowFunctionality functI = ((LowFunctionality) functionalities
						.get(i));

				if (!functToRet.keySet().contains(functI.getId())) {
					toRet.remove(functI);
				}
			}
			List toremove = new ArrayList<LowFunctionality>();
			List toreplace = new ArrayList<LowFunctionality>();
			for(int i =0; i< toRet.size(); i++){
				Integer folderId = ((LowFunctionality) toRet.get(i)).getId();
				ArrayList innerobjects = fillInnerDocuments(folderId.toString(), isHome,profile);
				if(innerobjects == null || innerobjects.isEmpty()){
					String innerFolderIds = functToRet.get(folderId);
					
					String [] ids = innerFolderIds.split(",");
					
					for (int j = 0; j < ids.length; j++) {
						if(ids[j] != null && !ids[j].equals("")){
							Integer innerFolderId = Integer.valueOf(ids[j]);
							LowFunctionality innerFolder = DAOFactory.getLowFunctionalityDAO().loadLowFunctionalityByID(innerFolderId, false);
							innerobjects = fillInnerDocuments(innerFolderId.toString(), isHome, profile);
							toremove.add((LowFunctionality) toRet.get(i));
							toRet.add(innerFolder);
						}
					}
				}
			}
			toRet.removeAll(toremove);

			
			

			JSONArray documentsJSON = (JSONArray) SerializerFactory
					.getSerializer("application/json").serialize(objects,
							locale);
			JSONArray foldersJSON = (JSONArray) SerializerFactory
					.getSerializer("application/json").serialize(toRet, locale);

			JSONObject foldersAndDocsResponseJSON = createJSONResponse(
					foldersJSON, documentsJSON);

			try {
				writeBackToClient(new JSONSuccess(foldersAndDocsResponseJSON));
			} catch (IOException e) {
				SpagoBIEngineServiceException serviceError = new SpagoBIEngineServiceException(
						"Document Browser", "Document browser failure");
				writeBackToClient(new JSONFailure(serviceError));
				throw new SpagoBIException(
						"Impossible to write back the responce to the client",
						e);
			}

		} catch (Throwable t) {

			throw new SpagoBIException(
					"An unexpected error occured while executing "
							+ getActionName(), t);
		} finally {
			logger.debug("OUT");
		}
	}

	private ArrayList fillInnerDocuments(String functID, boolean isHome,
			IEngUserProfile profile) throws NumberFormatException,
			EMFUserError, EMFInternalError {
		LowFunctionality targetFunct = DAOFactory.getLowFunctionalityDAO()
				.loadLowFunctionalityByID(new Integer(functID), false);
		isHome = "USER_FUNCT".equalsIgnoreCase(targetFunct.getCodType());
		List tmpObjects = DAOFactory.getBIObjectDAO().loadBIObjects(
				Integer.valueOf(functID), profile, isHome);
		ArrayList objects = new ArrayList();
		if (tmpObjects != null) {
			for (Iterator it = tmpObjects.iterator(); it.hasNext();) {
				BIObject obj = (BIObject) it.next();
				if (ObjectsAccessVerifier.checkProfileVisibility(obj, profile) && (obj.getEngine().getDriverName()
						.indexOf("Mobile") != -1))
					objects.add(obj);
			}
		}
		return objects;
	}

	private void getFoldersWithinMobileDocs(LowFunctionality lf, IEngUserProfile profile, HashMap toReturn, Integer origin)
			throws NumberFormatException, EMFUserError, EMFInternalError {

			List tmpBiObjs = DAOFactory.getBIObjectDAO().loadBIObjects(lf.getId(), profile, false);
			if (tmpBiObjs != null && !tmpBiObjs.isEmpty()) {

				for (Iterator iterator = tmpBiObjs.iterator(); iterator
						.hasNext();) {
					BIObject biobj = (BIObject) iterator.next();

					if (ObjectsAccessVerifier.checkProfileVisibility(biobj,
							profile)
							&& (biobj.getEngine().getDriverName()
									.indexOf("Mobile") != -1)) {
						
						String ids = (String)toReturn.get(origin);
						if(ids == null){
							ids =","+lf.getId()+",";
						}else{
							String [] idsArr = ids.split(",");
							
							if (!ArrayUtils.contains( idsArr, lf.getId()+"") ){
								ids+=lf.getId()+",";
							}else{
								ids+="";
							}
						}
						
						
						toReturn.put(origin, ids);
					}
				}
			}
			List tmpLowFuncts = DAOFactory.getLowFunctionalityDAO().loadUserFunctionalities(lf.getId(), true, profile);
			
			if (tmpLowFuncts != null && !tmpLowFuncts.isEmpty()) {
				for (int i = 0; i < tmpLowFuncts.size(); i++) {
					LowFunctionality lfi = (LowFunctionality) tmpLowFuncts.get(i);
					getFoldersWithinMobileDocs(lfi, profile, toReturn, origin);
				}
			}

	}

	/**
	 * Creates a json object with children document and folders
	 * 
	 * @param rows
	 * @return
	 * @throws JSONException
	 */
	private JSONObject createJSONResponse(JSONArray folders, JSONArray documents)
			throws JSONException {
		JSONObject results = new JSONObject();
		if (documents.length() != 0) {
			for (int i = 0; i < documents.length(); i++) {

				JSONObject doc = documents.getJSONObject(i);
				if (((String) doc.get("typeCode"))
						.equalsIgnoreCase(MobileConstants.DOCUMENT_TYPE_MOBILE_REPORT)
						|| ((String) doc.get("typeCode"))
								.equalsIgnoreCase(MobileConstants.DOCUMENT_TYPE_MOBILE_CHART)
						|| ((String) doc.get("typeCode"))
								.equalsIgnoreCase(MobileConstants.DOCUMENT_TYPE_MOBILE_COCKPIT)) {
					doc.put("leaf", "true");
					folders.put(doc);
				}
			}
		}
		results.put("name", "User Home");
		results.put("samples", folders);

		return results;
	}
}
