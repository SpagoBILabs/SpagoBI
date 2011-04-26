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
package it.eng.spagobi.analiticalmodel.document.x;

import it.eng.spago.error.EMFInternalError;
import it.eng.spago.error.EMFUserError;
import it.eng.spago.security.IEngUserProfile;
import it.eng.spagobi.analiticalmodel.document.handlers.ExecutionInstance;
import it.eng.spagobi.commons.constants.SpagoBIConstants;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.commons.serializer.SerializationException;
import it.eng.spagobi.commons.serializer.SerializerFactory;
import it.eng.spagobi.utilities.exceptions.SpagoBIServiceException;
import it.eng.spagobi.utilities.service.JSONSuccess;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Zerbetto Davide
 */
public class GetSubObjectsAction extends AbstractSpagoBIAction {
	
	public static final String SERVICE_NAME = "GET_SUBOBJECTS_ACTION";
	
	// logger component
	private static Logger logger = Logger.getLogger(GetSubObjectsAction.class);
	
	public void doService() {
		logger.debug("IN");
		ExecutionInstance executionInstance;
		
		try {
			// retrieving execution instance from session, no need to check if user is able to execute the required document
			executionInstance = getContext().getExecutionInstance( ExecutionInstance.class.getName() );
			Integer biobjectId = executionInstance.getBIObject().getId();
			List subObjectsList = null;
			IEngUserProfile userProfile = this.getUserProfile();
			try {
				if (userProfile.isAbleToExecuteAction(SpagoBIConstants.DOCUMENT_MANAGEMENT_ADMIN)) {
					subObjectsList = DAOFactory.getSubObjectDAO().getSubObjects(biobjectId);
				} else {
					subObjectsList = DAOFactory.getSubObjectDAO().getAccessibleSubObjects(biobjectId, userProfile);
				}
			} catch (EMFUserError e) {
				logger.error("Error while recovering subobjects list for document with id = " + biobjectId, e);
				throw new SpagoBIServiceException(SERVICE_NAME, "Cannot load customized views", e);
			} catch (EMFInternalError e) {
				logger.error("Error while recovering information about user", e);
				throw new SpagoBIServiceException(SERVICE_NAME, "Error while recovering information about user", e);
			}
			
			try {
				JSONArray subObjectsListJSON = (JSONArray) SerializerFactory.getSerializer("application/json").serialize( subObjectsList,null );
				JSONObject results = new JSONObject();
				results.put("results", subObjectsListJSON);
				writeBackToClient( new JSONSuccess( results ) );
			} catch (IOException e) {
				throw new SpagoBIServiceException(SERVICE_NAME, "Impossible to write back the responce to the client", e);
			} catch (SerializationException e) {
				throw new SpagoBIServiceException(SERVICE_NAME, "Cannot serialize objects", e);
			} catch (JSONException e) {
				throw new SpagoBIServiceException(SERVICE_NAME, "Cannot serialize objects into a JSON object", e);
			}

		} finally {
			logger.debug("OUT");
		}
	}

}
