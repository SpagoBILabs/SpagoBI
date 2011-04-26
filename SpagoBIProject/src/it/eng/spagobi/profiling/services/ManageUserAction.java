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
package it.eng.spagobi.profiling.services;

import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.analiticalmodel.document.x.AbstractSpagoBIAction;
import it.eng.spagobi.analiticalmodel.document.x.SaveMetadataAction;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.commons.metadata.SbiExtRoles;
import it.eng.spagobi.commons.serializer.SerializerFactory;
import it.eng.spagobi.profiling.bean.SbiAttribute;
import it.eng.spagobi.profiling.bean.SbiUser;
import it.eng.spagobi.profiling.bo.UserBO;
import it.eng.spagobi.profiling.dao.ISbiUserDAO;
import it.eng.spagobi.utilities.exceptions.SpagoBIServiceException;
import it.eng.spagobi.utilities.service.JSONAcknowledge;
import it.eng.spagobi.utilities.service.JSONSuccess;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ManageUserAction extends AbstractSpagoBIAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8920524215721282986L;
	// logger component
	private static Logger logger = Logger.getLogger(SaveMetadataAction.class);
	private final String MESSAGE_DET = "MESSAGE_DET";
	// type of service
	private final String USERS_LIST = "USERS_LIST";
	private final String USER_INSERT = "USER_INSERT";
	private final String USER_DELETE = "USER_DELETE";

	// USER detail
	private final String ID = "id";
	private final String USER_ID = "userId";
	private final String FULL_NAME = "fullName";
	private final String PASSWORD = "pwd";
	
	private final String ROLES = "userRoles";
	private final String ATTRIBUTES = "userAttributes";

	public static String START = "start";
	public static String LIMIT = "limit";
	public static Integer START_DEFAULT = 0;
	public static Integer LIMIT_DEFAULT = 16;

	@Override
	public void doService() {
		logger.debug("IN");
		ISbiUserDAO userDao;
		try {
			userDao = DAOFactory.getSbiUserDAO();
		} catch (EMFUserError e1) {
			logger.error(e1.getMessage(), e1);
			throw new SpagoBIServiceException(SERVICE_NAME,	"Error occurred");
		}
		Locale locale = getLocale();

		String serviceType = this.getAttributeAsString(MESSAGE_DET);
		logger.debug("Service type "+serviceType);
		if (serviceType != null && serviceType.equalsIgnoreCase(USERS_LIST)) {
			
			try {				
				Integer start = getAttributeAsInteger( START );
				Integer limit = getAttributeAsInteger( LIMIT );
				
				if(start==null){
					start = START_DEFAULT;
				}
				if(limit==null){
					limit = LIMIT_DEFAULT;
				}

				Integer totalResNum = userDao.countUsers();
				List<UserBO> users = userDao.loadPagedUsersList(start, limit);
				logger.debug("Loaded users list");
				JSONArray usersJSON = (JSONArray) SerializerFactory.getSerializer("application/json").serialize(users,	locale);
				JSONObject usersResponseJSON = createJSONResponseUsers(usersJSON, totalResNum);

				writeBackToClient(new JSONSuccess(usersResponseJSON));

			} catch (Throwable e) {
				logger.error("Exception occurred while retrieving users", e);
				throw new SpagoBIServiceException(SERVICE_NAME,
						"Exception occurred while retrieving users", e);
			}
		} else if (serviceType != null	&& serviceType.equalsIgnoreCase(USER_INSERT)) {
			Integer id = getAttributeAsInteger(ID);
			String userId = getAttributeAsString(USER_ID);
			String fullName = getAttributeAsString(FULL_NAME);
			String password = getAttributeAsString(PASSWORD);
			JSONArray rolesJSON = getAttributeAsJSONArray(ROLES);
			JSONArray attributesJSON = getAttributeAsJSONArray(ATTRIBUTES);
			if (userId != null) {
				SbiUser user = new SbiUser();
				user.setUserId(userId);
				user.setFullName(fullName);
				if(password != null){
					user.setPassword(password);
				}				
				
				if(id!=null){
					user.setId(id);
				}
				try {
					HashMap<Integer, String> attrList = null;
					if(attributesJSON != null){
						attrList = deserializeAttributesJSONArray(attributesJSON);
					}
					
					List rolesList = null;
					if(rolesJSON != null){
						rolesList = deserializeRolesJSONArray(rolesJSON);
					}
					
					id = userDao.fullSaveOrUpdateSbiUser(user, rolesList, attrList);
					logger.debug("User updated or Inserted");
					JSONObject attributesResponseSuccessJSON = new JSONObject();
					attributesResponseSuccessJSON.put("success", true);
					attributesResponseSuccessJSON.put("responseText", "Operation succeded");
					attributesResponseSuccessJSON.put("id", id);
					writeBackToClient( new JSONSuccess(attributesResponseSuccessJSON) );

				} catch (EMFUserError e) {
					logger.error("Exception occurred while saving new user", e);
					writeErrorsBackToClient();
					throw new SpagoBIServiceException(SERVICE_NAME,	"Exception occurred while saving new user",	e);
				} catch (IOException e) {
					logger.error("Exception occurred while writing response to client", e);
					throw new SpagoBIServiceException(SERVICE_NAME,
							"Exception occurred while writing response to client",
							e);
				} catch (JSONException e) {
					logger.error("JSON Exception", e);
					e.printStackTrace();
				}
			}else{
				logger.error("User name missing");
				throw new SpagoBIServiceException(SERVICE_NAME,	"Please enter user name");
			}
		} else if (serviceType != null	&& serviceType.equalsIgnoreCase(USER_DELETE)) {
			Integer id = getAttributeAsInteger(ID);
			try {
				userDao.deleteSbiUserById(id);
				logger.debug("User deleted");
				writeBackToClient( new JSONAcknowledge("Operation succeded") );

			} catch (Throwable e) {
				logger.error("Exception occurred while retrieving user to delete", e);
				throw new SpagoBIServiceException(SERVICE_NAME,
						"Exception occurred while retrieving user to delete",
						e);
			}
		}else if(serviceType == null){
			try {
				List<SbiAttribute> attributes = DAOFactory.getSbiAttributeDAO().loadSbiAttributes();
				List<SbiExtRoles> roles = DAOFactory.getRoleDAO().loadAllRoles();
				getSessionContainer().setAttribute("attributesList", attributes);
				getSessionContainer().setAttribute("rolesList", roles);
				
			} catch (EMFUserError e) {
				logger.error(e.getMessage(), e);
				throw new SpagoBIServiceException(SERVICE_NAME,
						"Exception retrieving role types",
						e);
			}
		}
		logger.debug("OUT");

	}

	/**
	 * Creates a json array with children users informations
	 * 
	 * @param rows
	 * @return
	 * @throws JSONException
	 */
	private JSONObject createJSONResponseUsers(JSONArray rows, Integer totalResNumber)
			throws JSONException {
		JSONObject results;

		results = new JSONObject();
		results.put("total", totalResNumber);
		results.put("title", "Users");
		results.put("rows", rows);
		return results;
	}
	
	private List deserializeRolesJSONArray(JSONArray rows) throws JSONException{
		List toReturn = new ArrayList();
		for(int i=0; i< rows.length(); i++){
			JSONObject obj = (JSONObject)rows.get(i);
			Integer id = obj.getInt("id");
			toReturn.add(id);
		}	
		return toReturn;
	}
	
	private HashMap<Integer, String> deserializeAttributesJSONArray(JSONArray rows) throws JSONException{
		HashMap<Integer, String> toReturn = new HashMap<Integer, String>();
		for(int i=0; i< rows.length(); i++){
			JSONObject obj = (JSONObject)rows.get(i);
			Integer key = obj.getInt("id");
			String value = obj.getString("value");
			toReturn.put(key, value);
		}	
		return toReturn;
	}

}
