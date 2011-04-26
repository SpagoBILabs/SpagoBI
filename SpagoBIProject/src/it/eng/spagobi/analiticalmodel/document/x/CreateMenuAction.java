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

import it.eng.spagobi.analiticalmodel.document.bo.BIObject;
import it.eng.spagobi.analiticalmodel.document.bo.SubObject;
import it.eng.spagobi.analiticalmodel.document.handlers.ExecutionInstance;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.bo.BIObjectParameter;
import it.eng.spagobi.commons.bo.UserProfile;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.commons.utilities.ParameterValuesEncoder;
import it.eng.spagobi.hotlink.rememberme.dao.IRememberMeDAO;
import it.eng.spagobi.utilities.exceptions.SpagoBIServiceException;
import it.eng.spagobi.utilities.service.JSONSuccess;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 
 * @author Zerbetto Davide
 *
 */
public class CreateMenuAction extends AbstractSpagoBIAction {
	
	public static final String CREATE_MENU_ACTION = "CREATE_MENU_ACTION";
	
	// logger component
	private static Logger logger = Logger.getLogger(CreateMenuAction.class);
	
	public void doService() {
		logger.debug("IN");
		try {
			// retrieving execution instance from session, no need to check if user is able to execute the current document
			ExecutionInstance executionInstance = getContext().getExecutionInstance( ExecutionInstance.class.getName() );
			BIObject obj = executionInstance.getBIObject();
			String name = getAttributeAsString("name");
			String description = getAttributeAsString("description");
			UserProfile profile = (UserProfile) this.getUserProfile();
			SubObject subobject = executionInstance.getSubObject();
			Integer subobjectId = null;
			if (subobject != null) {
				subobjectId = subobject.getId();
			}
			String parameters = getParametersQueryString(executionInstance);
			
			String message = null;
			boolean inserted;
			try {
				IRememberMeDAO dao=DAOFactory.getRememberMeDAO();
				dao.setUserProfile(profile);
				inserted = dao.saveRememberMe(name, description, obj.getId(), subobjectId, profile.getUserId().toString(), parameters);
			} catch (Exception e) {
				throw new SpagoBIServiceException(SERVICE_NAME, "Cannot save remember me", e);
			}
			if (inserted) {
				message = "ok";
			} else {
				message = "alreadyExisting";
			}
			
			try {
				JSONObject result = new JSONObject();
				result.put("result", message);
				writeBackToClient( new JSONSuccess( result ) );
			} catch (IOException e) {
				throw new SpagoBIServiceException(SERVICE_NAME, "Impossible to write back the responce to the client", e);
			} catch (JSONException e) {
				throw new SpagoBIServiceException(SERVICE_NAME, "Cannot serialize objects into a JSON object", e);
			}
		} finally {
			logger.debug("OUT");
		}
	}

	private String getParametersQueryString(ExecutionInstance executionInstance) {
		logger.debug("IN");
		try {
			StringBuffer documentParametersStr = new StringBuffer();
			BIObject obj = executionInstance.getBIObject();
			List parametersList = obj.getBiObjectParameters();
			ParameterValuesEncoder parValuesEncoder = new ParameterValuesEncoder();
			if (parametersList != null && parametersList.size() > 0) {
				for (int i = 0; i < parametersList.size(); i++) {
					BIObjectParameter parameter = (BIObjectParameter) parametersList.get(i);
					if (parameter.getParameterValues() != null) {
						String value = parValuesEncoder.encode(parameter);
						documentParametersStr.append(parameter.getParameterUrlName() + "=" + value);
						if (i < parametersList.size() - 1) documentParametersStr.append("&");
					}
				}
			}
			if (documentParametersStr.length() > 1 && documentParametersStr.charAt(documentParametersStr.length() - 1) == '&') {
				documentParametersStr.deleteCharAt(documentParametersStr.length() - 1);
			}
			return documentParametersStr.toString();
		} finally {
			logger.debug("OUT");
		}
	}

}
