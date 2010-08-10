/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005-2009 Engineering Ingegneria Informatica S.p.A.

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
package it.eng.spagobi.analiticalmodel.documentsbrowser.service;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import it.eng.spago.error.EMFInternalError;
import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.analiticalmodel.document.bo.BIObject;
import it.eng.spagobi.analiticalmodel.document.dao.IBIObjectDAO;
import it.eng.spagobi.analiticalmodel.document.x.AbstractSpagoBIAction;
import it.eng.spagobi.analiticalmodel.functionalitytree.bo.LowFunctionality;
import it.eng.spagobi.analiticalmodel.functionalitytree.dao.ILowFunctionalityDAO;
import it.eng.spagobi.commons.bo.UserProfile;
import it.eng.spagobi.commons.constants.SpagoBIConstants;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.commons.utilities.ObjectsAccessVerifier;
import it.eng.spagobi.utilities.exceptions.SpagoBIServiceException;
import it.eng.spagobi.utilities.service.JSONSuccess;

/**
 * @author Bernabei Angelo (angelo.bernabei@eng.it)
 */
public class DeleteObjectAction extends AbstractSpagoBIAction {

	public static final String SERVICE_NAME = "DELETE_OBJECT_ACTION";
	public static final String OBJECT_ID = "docId";
	public static final String FUNCT_ID = "folderId";

	// logger component
	private static Logger logger = Logger.getLogger(DeleteObjectAction.class);

	public void doService() {
		logger.debug("IN");

		try {
			// BIObject obj = executionInstance.getBIObject();
			UserProfile userProfile = (UserProfile) this.getUserProfile();
			IBIObjectDAO dao = null;
			try {
				dao = DAOFactory.getBIObjectDAO();
			} catch (EMFUserError e) {
				logger.error("Error while istantiating DAO", e);
				throw new SpagoBIServiceException(SERVICE_NAME, "Cannot access database", e);
			}
			String ids = this.getAttributeAsString(OBJECT_ID);
			String func = this.getAttributeAsString(FUNCT_ID);
			Integer iFunc = new Integer(func);
			logger.debug("Input Folder:" + func);
			logger.debug("Input Object:" + ids);
			// ids contains the id of the object to be deleted separated by ,
			String[] idArray = ids.split(",");
			for (int i = 0; i < idArray.length; i++) {
				Integer id = new Integer(idArray[i]);
				BIObject biObject = null;
				try {
					biObject = dao.loadBIObjectById(id);
				} catch (EMFUserError e) {
					logger.error("BIObject with id = " + id + " not found", e);
					throw new SpagoBIServiceException(SERVICE_NAME, "Customized view not found", e);
				}

				if (userProfile.isAbleToExecuteAction(SpagoBIConstants.DOCUMENT_MANAGEMENT_ADMIN)) {
					// delete document
					try {
						dao.eraseBIObject(biObject, iFunc);
						logger.debug("Object deleted by administrator");
					} catch (EMFUserError e) {
						logger.error("Error to delete Document", e);
						throw new SpagoBIServiceException(SERVICE_NAME, "Error to delete Document", e);
					}
				} else {
					String userId = ((UserProfile)userProfile).getUserId().toString();
					ILowFunctionalityDAO functDAO = DAOFactory.getLowFunctionalityDAO();
					LowFunctionality lowFunc = functDAO.loadLowFunctionalityByID(iFunc, false);

					if(lowFunc==null){
						logger.error("Functionality does not exist");
						throw new Exception("Functionality does not exist");					
					}

					if(lowFunc.getPath().equals("/"+userId)){ // folder is current user one
						dao.eraseBIObject(biObject, iFunc);
						logger.debug("Object deleted");
					}
					else{
						logger.error("Functionality is not user's one");
						throw new Exception("Functionality  is not user's one");					
					}
				}

			}
			try {
				JSONObject results = new JSONObject();
				results.put("result", "OK");
				writeBackToClient(new JSONSuccess(results));
			} catch (IOException e) {
				throw new SpagoBIServiceException(SERVICE_NAME, "Impossible to write back the responce to the client", e);
			} catch (JSONException e) {
				throw new SpagoBIServiceException(SERVICE_NAME, "Cannot serialize objects into a JSON object", e);
			}
		} catch (EMFInternalError e) {
			throw new SpagoBIServiceException(SERVICE_NAME, "An internal error has occured", e);
		
		} catch (Exception e) {
			throw new SpagoBIServiceException(SERVICE_NAME, "An internal error has occured", e);
		} finally {
			logger.debug("OUT");
		}
	}

}
