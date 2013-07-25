/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.community.service;

import it.eng.spago.base.RequestContainer;
import it.eng.spago.base.RequestContainerAccess;
import it.eng.spago.base.SessionContainer;
import it.eng.spago.error.EMFUserError;
import it.eng.spago.security.IEngUserProfile;
import it.eng.spagobi.analiticalmodel.functionalitytree.bo.LowFunctionality;
import it.eng.spagobi.analiticalmodel.functionalitytree.dao.ILowFunctionalityDAO;
import it.eng.spagobi.commons.bo.Role;
import it.eng.spagobi.commons.constants.SpagoBIConstants;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.commons.dao.IRoleDAO;
import it.eng.spagobi.commons.metadata.SbiExtRoles;
import it.eng.spagobi.commons.utilities.messages.IMessageBuilder;
import it.eng.spagobi.commons.utilities.messages.MessageBuilderFactory;
import it.eng.spagobi.community.bo.CommunityManager;
import it.eng.spagobi.community.dao.ISbiCommunityDAO;
import it.eng.spagobi.community.mapping.SbiCommunity;
import it.eng.spagobi.profiling.bean.SbiUser;
import it.eng.spagobi.profiling.dao.ISbiUserDAO;
import it.eng.spagobi.utilities.exceptions.SpagoBIServiceException;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@Path("/community")
public class MenageCommunityAction {
	protected IMessageBuilder msgBuilder = MessageBuilderFactory.getMessageBuilder();;
	private static Logger logger = Logger.getLogger(MenageCommunityAction.class);

	
	@GET
	@Path("/accept")
	@Produces(MediaType.TEXT_HTML)
	public String accept(@Context HttpServletRequest req) {
		
		RequestContainer reqCont = RequestContainerAccess.getRequestContainer(req);
		Locale locale = null;	
		if(reqCont != null){
			SessionContainer aSessionContainer = reqCont.getSessionContainer();
	
			SessionContainer permanentSession = aSessionContainer.getPermanentContainer();
			String curr_language=(String)permanentSession.getAttribute(SpagoBIConstants.AF_LANGUAGE);
			String curr_country=(String)permanentSession.getAttribute(SpagoBIConstants.AF_COUNTRY);
					

			if(curr_language!=null && curr_country!=null && !curr_language.equals("") && !curr_country.equals("")){
				locale=new Locale(curr_language, curr_country, "");
			}
		}
		
		IEngUserProfile profile = (IEngUserProfile) req.getSession()
				.getAttribute(IEngUserProfile.ENG_USER_PROFILE);
		String userToAccept = (String)req.getParameter("userToAccept");
		String community = (String)req.getParameter("community");
		String owner = (String)req.getParameter("owner");
		String result=msgBuilder.getMessage("community.save.membership.ok.1", "messages", locale)+userToAccept+msgBuilder.getMessage("community.save.membership.ok.2", "messages", locale)+community;
		
		if(profile.getUserUniqueIdentifier().equals(owner)){

			
			SbiCommunity sbiComm;
			try {
				ISbiCommunityDAO communityDao;
				communityDao = DAOFactory.getCommunityDAO();
	
				sbiComm = communityDao.loadSbiCommunityByName(community);
				communityDao.addCommunityMember(sbiComm, userToAccept);
				
				
				//add missing roles to the folder, in order that docs are executable by the accepted user
				CommunityManager cm = new CommunityManager();
				cm.addRolesToFunctionality(userToAccept, sbiComm.getFunctCode());
//				
//				ISbiUserDAO userDao = DAOFactory.getSbiUserDAO();
//				IRoleDAO roledao= DAOFactory.getRoleDAO();
//				ILowFunctionalityDAO lowFunctDao = DAOFactory.getLowFunctionalityDAO();
//				
//				
//				SbiUser user = userDao.loadSbiUserByUserId(userToAccept);
//				
//				ArrayList<SbiExtRoles> userRoles = userDao.loadSbiUserRolesById(user.getId());
//				LowFunctionality funct = lowFunctDao.loadLowFunctionalityByCode(sbiComm.getFunctCode(), false);
//				Role [] execRole4Funct = funct.getExecRoles();
//				ArrayList<Role> roles = new ArrayList<Role>();
//				for(int j=0; j<execRole4Funct.length; j++){
//					Role alreadySetRole = execRole4Funct[j];
//					roles.add(alreadySetRole);
//
//				}
//				for(int i =0; i<userRoles.size();i++){
//					SbiExtRoles extr = userRoles.get(i);
//					Integer extRID= extr.getExtRoleId();
//					Role r = roledao.loadByID(extRID);
//					if(!roles.contains(r)){
//						roles.add(r);
//					}					
//				}
//				Role [] rolesArr = roles.toArray(new Role[roles.size()]);
//				
//				
//				funct.setDevRoles(rolesArr);
//				funct.setExecRoles(rolesArr);
//				funct.setTestRoles(rolesArr);
//				funct.setCreateRoles(rolesArr);
//				
//				lowFunctDao.modifyLowFunctionality(funct);
	
				
			} catch (EMFUserError e) {
				logger.error(e.getMessage());
				result= msgBuilder.getMessage("community.save.membership.ko", "messages", locale);
			}		
		}else{
			result= msgBuilder.getMessage("community.save.membership.cannot", "messages", locale);
		}
		logger.debug("OUT");
		return result;
	}
	
	@GET
	@Path("/user")
	@Produces(MediaType.APPLICATION_JSON)
	public String getCommunity(@Context HttpServletRequest req) {
		ISbiCommunityDAO commDao = null;
		List<SbiCommunity> communities;
		IEngUserProfile profile = (IEngUserProfile) req.getSession().getAttribute(IEngUserProfile.ENG_USER_PROFILE);
		
		String communitiesJSONStr = "";
		try {
			commDao = DAOFactory.getCommunityDAO();
			
			communities = commDao.loadSbiCommunityByUser((String)profile.getUserUniqueIdentifier());
			if(communities != null){
				String innerList = communityDeser(communities);
				communitiesJSONStr ="{root:"+innerList+"}";
			}

		} catch (Throwable t) {
			throw new SpagoBIServiceException(
					"An unexpected error occured while instatiating the dao", t);
		}

		return communitiesJSONStr;
		
	}
	private String communityDeser(List<SbiCommunity> communities) throws JSONException, EMFUserError{

		JSONArray jsonComm= new JSONArray();
		for(int i=0; i<communities.size(); i++){
			SbiCommunity com = communities.get(i);
			Integer id = com.getCommunityId();
			String name = com.getName();
			String descr = com.getDescription();
			String owner = com.getOwner();
			String functCode = com.getFunctCode();
			
			JSONObject obj = new JSONObject();
			obj.put("communityId", id);
			obj.put("name", name);
			obj.put("description", descr);
			obj.put("owner", owner);
			obj.put("functCode", functCode);
			LowFunctionality folder = DAOFactory.getLowFunctionalityDAO().loadLowFunctionalityByCode(functCode, false);
			if(folder!= null){
				obj.put("functId", folder.getId());
			}
			
			jsonComm.put(obj);
		}
		
		return jsonComm.toString();		
	}
}
