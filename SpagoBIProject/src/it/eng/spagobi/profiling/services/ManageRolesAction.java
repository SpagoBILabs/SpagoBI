/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.profiling.services;

import it.eng.spago.base.RequestContainer;
import it.eng.spago.base.SessionContainer;
import it.eng.spago.error.EMFUserError;
import it.eng.spago.security.IEngUserProfile;
import it.eng.spagobi.commons.bo.Domain;
import it.eng.spagobi.commons.bo.Role;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.commons.dao.IRoleDAO;
import it.eng.spagobi.commons.serializer.SerializerFactory;
import it.eng.spagobi.commons.services.AbstractSpagoBIAction;
import it.eng.spagobi.commons.utilities.AuditLogUtilities;
import it.eng.spagobi.security.RoleSynchronizer;
import it.eng.spagobi.utilities.exceptions.SpagoBIServiceException;
import it.eng.spagobi.utilities.service.JSONAcknowledge;
import it.eng.spagobi.utilities.service.JSONSuccess;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

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
	private final String DO_MASSIVE_EXPORT="doMassiveExport";
	private final String MANAGE_USERS="manageUsers";
	
	public static String START = "start";
	public static String LIMIT = "limit";
	public static Integer START_DEFAULT = 0;
	public static Integer LIMIT_DEFAULT = 15;
	
	@Override
	public void doService() {
		logger.debug("IN");
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
		String name = getAttributeAsString(NAME);
		String roleTypeCD = getAttributeAsString(ROLE_TYPE_CD);
		logger.debug("Service type "+serviceType);
		HashMap logParam = new HashMap();
		logParam.put("NAME", name);
		logParam.put("ROLE TYPE", roleTypeCD);
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
			Boolean doMassiveExport = getAttributeAsBoolean(DO_MASSIVE_EXPORT);
			Boolean manageUsers = getAttributeAsBoolean(MANAGE_USERS);
						
			if (name != null) {
				//checks for unique role name
				try {
					Role existentRole = DAOFactory.getRoleDAO().loadByName(name);
					if(existentRole != null){
						String id = getAttributeAsString(ID);
						if(id == null || id.equals("") || id.equals("0")){
							try {
								AuditLogUtilities.updateAudit(getHttpRequest(),  profile, "PROF_ROLES.ADD/MODIFY",logParam , "OK");
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							throw new SpagoBIServiceException(SERVICE_NAME,	"Role Name already present.");
						}
					}
				} catch (EMFUserError e1) {
					try {
						AuditLogUtilities.updateAudit(getHttpRequest(),  profile, "PROF_ROLES.ADD/MODIFY",logParam , "ERR");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
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
			    	try {
						AuditLogUtilities.updateAudit(getHttpRequest(),  profile, "PROF_ROLES.ADD/MODIFY",logParam , "ERR");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
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
				role.setIsAbleToDoMassiveExport(doMassiveExport);
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
				role.setIsAbleToManageUsers(manageUsers);
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
						AuditLogUtilities.updateAudit(getHttpRequest(),  profile, "PROF_ROLES.ADD/MODIFY",logParam , "OK");
					}else{
						Integer roleID = roleDao.insertRoleComplete(role);
						logger.debug("New Role inserted");
						JSONObject attributesResponseSuccessJSON = new JSONObject();
						attributesResponseSuccessJSON.put("success", true);
						attributesResponseSuccessJSON.put("responseText", "Operation succeded");
						attributesResponseSuccessJSON.put("id", roleID);
						writeBackToClient( new JSONSuccess(attributesResponseSuccessJSON) );
						AuditLogUtilities.updateAudit(getHttpRequest(),  profile, "PROF_ROLES.ADD",logParam , "OK");
					}

				} catch (Throwable e) {
					try {
						AuditLogUtilities.updateAudit(getHttpRequest(),  profile, "PROF_ROLES.ADD/MODIFY",logParam , "ERR");
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					logger.error(e.getMessage(), e);
					throw new SpagoBIServiceException(SERVICE_NAME,
							"Exception occurred while saving new role",
							e);
				}

			}else{
				try {
					AuditLogUtilities.updateAudit(getHttpRequest(),  profile, "PROF_ROLES.ADD/MODIFY",logParam , "KO");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
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
				AuditLogUtilities.updateAudit(getHttpRequest(),  profile, "PROF_ROLES.DELETE",logParam , "OK");
			} catch (Throwable e) {
				try {
					AuditLogUtilities.updateAudit(getHttpRequest(),  profile, "PROF_ROLES.DELETE",logParam , "KO");
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
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
				AuditLogUtilities.updateAudit(getHttpRequest(),  profile, "PROF_ROLES.SYNCHRONIZATION",logParam , "OK");
			} catch (Throwable e) {
				try {
					AuditLogUtilities.updateAudit(getHttpRequest(),  profile, "PROF_ROLES.SYNCHRONIZATION",logParam , "OK");
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
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
	
}
