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
package it.eng.spagobi.profiling.services;

import it.eng.spago.base.RequestContainer;
import it.eng.spago.base.SessionContainer;
import it.eng.spago.error.EMFUserError;
import it.eng.spago.security.IEngUserProfile;
import it.eng.spagobi.commons.bo.Domain;
import it.eng.spagobi.commons.bo.Role;
import it.eng.spagobi.commons.constants.SpagoBIConstants;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.commons.dao.IRoleDAO;
import it.eng.spagobi.commons.serializer.SerializerFactory;
import it.eng.spagobi.commons.services.AbstractSpagoBIAction;
import it.eng.spagobi.security.RoleSynchronizer;
import it.eng.spagobi.utilities.exceptions.SpagoBIServiceException;
import it.eng.spagobi.utilities.service.JSONAcknowledge;
import it.eng.spagobi.utilities.service.JSONSuccess;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ManageRolesAction extends AbstractSpagoBIAction{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4873196748328877998L;

	// logger component
	private static Logger logger = Logger.getLogger(ManageRolesAction.class);
	
	private final String MESSAGE_DET = "MESSAGE_DET";
	// type of service
	private final String ROLES_LIST = "ROLES_LIST";
	private final String ROLE_INSERT = "ROLE_INSERT";
	private final String ROLE_DELETE = "ROLE_DELETE";
	private final String ROLES_SYNCHRONIZATION = "ROLES_SYNCHRONIZATION";
	
	private final String ID = "id";
	private final String NAME = "name";
	private final String DESCRIPTION = "description";
	private final String ROLE_TYPE_CD = "typeCd";
	private final String CODE = "code";
	private final String SAVE_SUBOBJECTS="saveSubobj";
	private final String SEE_SUBOBJECTS="seeSubobj";
	private final String SEE_VIEWPOINTS="seeViewpoints";
	private final String SEE_SNAPSHOTS="seeSnapshot";
	private final String SEE_NOTES="seeNotes";
	private final String SEND_MAIL="sendMail";
	private final String SAVE_INTO_PERSONAL_FOLDER="savePersonalFolder";
	private final String SAVE_REMEMBER_ME="saveRemember";
	private final String SEE_METADATA="seeMeta";
	private final String SAVE_METADATA="saveMeta";
	private final String BUILD_QBE_QUERY="buildQbe";
	
	public static String START = "start";
	public static String LIMIT = "limit";
	public static Integer START_DEFAULT = 0;
	public static Integer LIMIT_DEFAULT = 15;
	
	@Override
	public void doService() {
		logger.debug("IN");
		HttpServletResponse httpResponse = getHttpResponse();
		RequestContainer requestContainer = this.getRequestContainer();
		SessionContainer permanentSession = requestContainer.getSessionContainer().getPermanentContainer();
	    IEngUserProfile profile = (IEngUserProfile) permanentSession.getAttribute(IEngUserProfile.ENG_USER_PROFILE);
		IRoleDAO roleDao;
		try {
			roleDao = DAOFactory.getRoleDAO();
			roleDao.setUserProfile(profile);
		} catch (EMFUserError e1) {
			logger.error(e1.getMessage(), e1);
			throw new SpagoBIServiceException(SERVICE_NAME,	"Error occurred");
		}

		Locale locale = getLocale();

		String serviceType = this.getAttributeAsString(MESSAGE_DET);
		logger.debug("Service type "+serviceType);
		if (serviceType != null && serviceType.equalsIgnoreCase(ROLES_LIST)) {
			try {
				Integer start = getAttributeAsInteger( START );
				Integer limit = getAttributeAsInteger( LIMIT );
				
				if(start==null){
					start = START_DEFAULT;
				}
				if(limit==null){
					limit = LIMIT_DEFAULT;
				}

				Integer totalResNum = roleDao.countRoles();
				List<Role> roles = roleDao.loadPagedRolesList(start, limit);				
				
				//ArrayList<Role> roles = (ArrayList<Role>)roleDao.loadAllRoles();
				logger.debug("Loaded roles list");
				JSONArray rolesJSON = (JSONArray) SerializerFactory.getSerializer("application/json").serialize(roles,	locale);
				JSONObject rolesResponseJSON = createJSONResponseRoles(rolesJSON,totalResNum);

				writeBackToClient(new JSONSuccess(rolesResponseJSON));

			} catch (Throwable e) {
				logger.error(e.getMessage(), e);
				throw new SpagoBIServiceException(SERVICE_NAME,
						"Exception occurred while retrieving roles", e);
			}
		} else if (serviceType != null	&& serviceType.equalsIgnoreCase(ROLE_INSERT)) {
			String name = getAttributeAsString(NAME);
			String roleTypeCD = getAttributeAsString(ROLE_TYPE_CD);
			String code = getAttributeAsString(CODE);
			String description = getAttributeAsString(DESCRIPTION);
			
			Boolean saveSubobjects= getAttributeAsBoolean(SAVE_SUBOBJECTS);
			Boolean seeSubobjects= getAttributeAsBoolean(SEE_SUBOBJECTS);
			Boolean seeViewpoints= getAttributeAsBoolean(SEE_VIEWPOINTS);
			Boolean seeSnapshots= getAttributeAsBoolean(SEE_SNAPSHOTS);
			Boolean seeNotes= getAttributeAsBoolean(SEE_NOTES);
			Boolean sendMail= getAttributeAsBoolean(SEND_MAIL);
			Boolean saveIntoPersonalFolder= getAttributeAsBoolean(SAVE_INTO_PERSONAL_FOLDER);
			Boolean saveRememberMe= getAttributeAsBoolean(SAVE_REMEMBER_ME);
			Boolean seeMetadata= getAttributeAsBoolean(SEE_METADATA);
			Boolean saveMetadata= getAttributeAsBoolean(SAVE_METADATA);
			Boolean buildQbeQuery= getAttributeAsBoolean(BUILD_QBE_QUERY);

			if (name != null) {
				//checks for unique role name
				try {
					Role existentRole = DAOFactory.getRoleDAO().loadByName(name);
					if(existentRole != null){
						String id = getAttributeAsString(ID);
						if(id == null || id.equals("") || id.equals("0")){
							throw new SpagoBIServiceException(SERVICE_NAME,	"Role Name already present.");
						}
					}
				} catch (EMFUserError e1) {
					logger.error(e1.getMessage(), e1);
					throw new SpagoBIServiceException(SERVICE_NAME,
							"Exception occurred while retrieving role by name", e1);
				}

			    List<Domain> domains = (List<Domain>)getSessionContainer().getAttribute("roleTypes");

			    HashMap<String, Integer> domainIds = new HashMap<String, Integer> ();
			    for(int i=0; i< domains.size(); i++){
			    	domainIds.put(domains.get(i).getValueCd(), domains.get(i).getValueId());
			    }
			    
			    Integer roleTypeID = domainIds.get(roleTypeCD);
			    if(roleTypeID == null){
			    	logger.error("Role type CD not existing");
			    	throw new SpagoBIServiceException(SERVICE_NAME,	"Role Type ID is undefined");
			    }
			    
				Role role = new Role();
				role.setCode(code);
				role.setDescription(description);
				role.setName(name);
				role.setRoleTypeCD(roleTypeCD);
				role.setRoleTypeID(roleTypeID);
				role.setIsAbleToBuildQbeQuery(buildQbeQuery);
				role.setIsAbleToSaveIntoPersonalFolder(saveIntoPersonalFolder);
				role.setIsAbleToSaveMetadata(saveMetadata);
				role.setIsAbleToSaveRememberMe(saveRememberMe);
				role.setIsAbleToSaveSubobjects(saveSubobjects);
				role.setIsAbleToSeeMetadata(seeMetadata);
				role.setIsAbleToSeeNotes(seeNotes);
				role.setIsAbleToSeeSnapshots(seeSnapshots);
				role.setIsAbleToSeeSubobjects(seeSubobjects);
				role.setIsAbleToSeeViewpoints(seeViewpoints);
				role.setIsAbleToSendMail(sendMail);
				try {
					String id = getAttributeAsString(ID);
					if(id != null && !id.equals("") && !id.equals("0")){							
						role.setId(Integer.valueOf(id));
						roleDao.modifyRole(role);
						logger.debug("Role "+id+" updated");
						JSONObject attributesResponseSuccessJSON = new JSONObject();
						attributesResponseSuccessJSON.put("success", true);
						attributesResponseSuccessJSON.put("responseText", "Operation succeded");
						writeBackToClient( new JSONSuccess(attributesResponseSuccessJSON) );
					}else{
						Integer roleID = roleDao.insertRoleComplete(role);
						logger.debug("New Role inserted");
						JSONObject attributesResponseSuccessJSON = new JSONObject();
						attributesResponseSuccessJSON.put("success", true);
						attributesResponseSuccessJSON.put("responseText", "Operation succeded");
						attributesResponseSuccessJSON.put("id", roleID);
						writeBackToClient( new JSONSuccess(attributesResponseSuccessJSON) );
					}

				} catch (Throwable e) {
					logger.error(e.getMessage(), e);
					throw new SpagoBIServiceException(SERVICE_NAME,
							"Exception occurred while saving new role",
							e);
				}

			}else{
				logger.error("Missing role name");
				throw new SpagoBIServiceException(SERVICE_NAME,	"Please enter role name");
			}
		} else if (serviceType != null	&& serviceType.equalsIgnoreCase(ROLE_DELETE)) {
			Integer id = getAttributeAsInteger(ID);
			try {
				Role aRole = roleDao.loadByID(id);
				roleDao.eraseRole(aRole);
				logger.debug("Role deleted");
				writeBackToClient( new JSONAcknowledge("Operazion succeded") );

			} catch (Throwable e) {
				logger.error("Exception occurred while deleting role", e);
				throw new SpagoBIServiceException(SERVICE_NAME,
						"Exception occurred while deleting role",
						e);
			}
		} else if (serviceType != null	&& serviceType.equalsIgnoreCase(ROLES_SYNCHRONIZATION)) {
			try {
				RoleSynchronizer roleSynch = new RoleSynchronizer();
				roleSynch.synchronize();
				logger.debug("Roles synchronized");
				JSONObject attributesResponseSuccessJSON = new JSONObject();
				attributesResponseSuccessJSON.put("success", true);
				attributesResponseSuccessJSON.put("responseText", "Operation succeded");
				writeBackToClient( new JSONSuccess(attributesResponseSuccessJSON) );

			} catch (Throwable e) {
				logger.error("Exception occurred while syncronize roles", e);
				throw new SpagoBIServiceException(SERVICE_NAME,
						"Exception occurred while syncronize role",
						e);
			}
		}else if(serviceType == null){
			try {
				List<Domain> domains = DAOFactory.getDomainDAO().loadListDomainsByType("ROLE_TYPE");
				getSessionContainer().setAttribute("roleTypes", domains);
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
	 * Creates a json array with children roles informations
	 * 
	 * @param rows
	 * @return
	 * @throws JSONException
	 */
	private JSONObject createJSONResponseRoles(JSONArray rows, Integer totalResNumber)
			throws JSONException {
		JSONObject results;

		results = new JSONObject();
		results.put("total", totalResNumber);
		results.put("title", "Roles");
		results.put("rows", rows);
		return results;
	}
	
	/**Adds role flags to JSON response object
	 * @param object JSONObject representing role
	 * @param role the <code>Role</code> to publish
	 * @return JSONObject modified
	 * @throws JSONException
	 */
	private JSONObject addFlagsToJSONRoles(JSONObject object, Role role)
		throws JSONException {
		
		Boolean qbeQuery = role.isAbleToBuildQbeQuery();
		if(qbeQuery != null)
			object.put(BUILD_QBE_QUERY, qbeQuery.booleanValue());
		Boolean saveSub = role.isAbleToSaveSubobjects();
		if(saveSub != null)
			object.put(SAVE_SUBOBJECTS,saveSub.booleanValue());
		Boolean seeSub = role.isAbleToSeeSubobjects();
		if(seeSub != null)
			object.put(SEE_SUBOBJECTS,seeSub.booleanValue());
		Boolean seeView = role.isAbleToSeeViewpoints();
		if(seeView != null)
			object.put(SEE_VIEWPOINTS,seeView.booleanValue());
		Boolean seeSnap = role.isAbleToSeeSnapshots();
		if(seeSnap != null)
			object.put(SEE_SNAPSHOTS,seeSnap.booleanValue());
		Boolean seeNotes = role.isAbleToSeeNotes();
		if(seeNotes != null)
			object.put(SEE_NOTES,seeNotes.booleanValue());
		Boolean sendMail = role.isAbleToSendMail();
		if(sendMail != null)
			object.put(SEND_MAIL,sendMail.booleanValue());
		Boolean savePerFol = role.isAbleToSaveIntoPersonalFolder();
		if(savePerFol != null)
			object.put(SAVE_INTO_PERSONAL_FOLDER,savePerFol.booleanValue());
		Boolean saveRememb = role.isAbleToSaveRememberMe();
		if(saveRememb != null)
			object.put(SAVE_REMEMBER_ME,saveRememb.booleanValue());
		Boolean seeMeta = role.isAbleToSeeMetadata();
		if(seeMeta != null)
			object.put(SEE_METADATA,seeMeta.booleanValue());
		Boolean saveMeta = role.isAbleToSaveMetadata();
		if(saveMeta != null)
			object.put(SAVE_METADATA,saveMeta.booleanValue());
		
		return object;
	}
}
