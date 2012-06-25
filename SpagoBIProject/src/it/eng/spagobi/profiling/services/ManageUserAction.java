/* SpagoBI, the Open Source Business Intelligence suite

* © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this file,
* You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.profiling.services;


import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.commons.constants.SpagoBIConstants;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.commons.metadata.SbiExtRoles;
import it.eng.spagobi.commons.serializer.SerializerFactory;
import it.eng.spagobi.commons.services.AbstractSpagoBIAction;
import it.eng.spagobi.profiling.bean.SbiAttribute;
import it.eng.spagobi.profiling.bean.SbiUser;
import it.eng.spagobi.profiling.bo.UserBO;
import it.eng.spagobi.profiling.dao.ISbiUserDAO;
import it.eng.spagobi.security.Password;
import it.eng.spagobi.utilities.exceptions.SpagoBIServiceException;
import it.eng.spagobi.utilities.service.JSONAcknowledge;
import it.eng.spagobi.utilities.service.JSONSuccess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * @author Monica Franceschini (monica.franceschini@eng.it)
 * @author Alessandro Pegoraro (alessandro.pegoraro@eng.it)
 */
public class ManageUserAction extends AbstractSpagoBIAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8920524215721282986L;
	// logger component
	private static Logger logger = Logger.getLogger(ManageUserAction.class);
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
	public static String FILTERS = "FILTERS";
	
	@Override
	public void doService() {
		logger.debug("IN");
		ISbiUserDAO userDao;
		try {
			userDao = DAOFactory.getSbiUserDAO();
			userDao.setUserProfile(getUserProfile());
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

				JSONObject filtersJSON = null;
				List<UserBO> users = null;
				if(this.requestContainsAttribute( FILTERS ) ) {
					filtersJSON = getAttributeAsJSONObject( FILTERS );
					String hsql = filterList(filtersJSON);
					users = userDao.loadSbiUserListFiltered(hsql, start, limit);
				}else{//not filtered
					users = userDao.loadPagedUsersList(start, limit);
				}
				
				
				
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
			saveUser(userDao);
		} else if (serviceType != null	&& serviceType.equalsIgnoreCase(USER_DELETE)) {
			deleteUser(userDao);
		}else if(serviceType == null){
			setAttributesAndRolesInResponse();
		}
		logger.debug("OUT");

	}


	protected void saveUser(ISbiUserDAO userDao) {
		Integer id = getAttributeAsInteger(ID);
		String userId = getAttributeAsString(USER_ID);
		String fullName = getAttributeAsString(FULL_NAME);
		String password = getAttributeAsString(PASSWORD);
		JSONArray rolesJSON = getAttributeAsJSONArray(ROLES);
		JSONArray attributesJSON = getAttributeAsJSONArray(ATTRIBUTES);
		
		if (userId == null) {
			logger.error("User name missing");
			throw new SpagoBIServiceException(SERVICE_NAME,
					"User name missing");
		}
		
		SbiUser user = new SbiUser();
		user.setUserId(userId);
		user.setFullName(fullName);
		if (password != null && password.length() > 0) {
			try {
				user.setPassword(Password.encriptPassword(password));
			} catch (Exception e) {
				logger.error("Impossible to encrypt Password", e);
				throw new SpagoBIServiceException(SERVICE_NAME,
						"Impossible to encrypt Password", e);
			}
		}

		if (id != null) {
			user.setId(id);
		}
		
		HashMap<Integer, String> attrList = null;
		List rolesList = null;
		try {
			if (attributesJSON != null) {
				attrList = deserializeAttributesJSONArray(attributesJSON);
			}
			if (rolesJSON != null) {
				rolesList = deserializeRolesJSONArray(rolesJSON);
			}
		} catch (JSONException e) {
			throw new SpagoBIServiceException(SERVICE_NAME, "Exception occurred while deserializing attributes and roles", e);
		}
		
		// check if user id is valid: in case it is not, an exception will be thrown
		checkUserId(userId, id);
		
		try {
			id = userDao.fullSaveOrUpdateSbiUser(user, rolesList, attrList);
			logger.debug("User updated or Inserted");
		} catch (Throwable t) {
			logger.error("Exception occurred while saving user", t);
			throw new SpagoBIServiceException(SERVICE_NAME, "Exception occurred while saving user", t);
		}
		
		try {
			JSONObject attributesResponseSuccessJSON = new JSONObject();
			attributesResponseSuccessJSON.put("success", true);
			attributesResponseSuccessJSON.put("responseText",
					"Operation succeded");
			attributesResponseSuccessJSON.put("id", id);
			writeBackToClient(new JSONSuccess(attributesResponseSuccessJSON));
		} catch (Exception e) {
			throw new SpagoBIServiceException(SERVICE_NAME, "Impossible to write back the responce to the client", e);
		}
	}


	protected void deleteUser(ISbiUserDAO userDao) {
		Integer id = getAttributeAsInteger(ID);
		try {
			userDao.deleteSbiUserById(id);
			logger.debug("User deleted");
			writeBackToClient(new JSONAcknowledge("Operation succeded"));
		} catch (Throwable e) {
			logger.error("Exception occurred while deleting user",
					e);
			throw new SpagoBIServiceException(SERVICE_NAME,
					"Exception occurred while deleting user", e);
		}
	}


	protected void setAttributesAndRolesInResponse() {
		try {
			List<SbiAttribute> attributes = DAOFactory.getSbiAttributeDAO()
					.loadSbiAttributes();
			List<SbiExtRoles> roles = DAOFactory.getRoleDAO().loadAllRoles();
			getSessionContainer().setAttribute("attributesList", attributes);
			getSessionContainer().setAttribute("rolesList", roles);
		} catch (EMFUserError e) {
			logger.error(e.getMessage(), e);
			throw new SpagoBIServiceException(SERVICE_NAME,
					"Exception retrieving role types", e);
		}
	}


	protected void checkUserId(String userId, Integer id) {
		logger.debug("Validating user id " + userId + " ...");
		try {
			DAOFactory.getSbiUserDAO().checkUserId(userId, id);
		} catch (EMFUserError e) {
			if (e.getErrorCode().equals("400")) {
				throw new SpagoBIServiceException(SERVICE_NAME, "User id " + userId + " already in use");
			} else {
				throw new SpagoBIServiceException(SERVICE_NAME, "Error checking if user identifier is valid", e);
			}
		}
		logger.debug("User id " + userId + " is valid");
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

	private String filterList(JSONObject filtersJSON) throws JSONException {
		logger.debug("IN");				
		String hsql= " from SbiUser h where";
		if (filtersJSON != null) {
			String valuefilter = (String) filtersJSON.get(SpagoBIConstants.VALUE_FILTER);
			String typeFilter = (String) filtersJSON.get(SpagoBIConstants.TYPE_FILTER);
			String columnFilter = (String) filtersJSON.get(SpagoBIConstants.COLUMN_FILTER);
			valuefilter = valuefilter != null? valuefilter.toUpperCase() : "";
			if(typeFilter.equals("=")){
				hsql += " upper(h."+columnFilter+") = '" + valuefilter +"'";
			}else if(typeFilter.equals("like")){
				hsql += " upper(h."+columnFilter+") like '%"+ valuefilter + "%'";			
			}
			logger.debug("Apply filter on user "+hsql);
		}
		logger.debug("OUT");
		return hsql;
	}
	
	

	
}
