/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.community.bo;

import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.analiticalmodel.functionalitytree.bo.LowFunctionality;
import it.eng.spagobi.analiticalmodel.functionalitytree.dao.ILowFunctionalityDAO;
import it.eng.spagobi.commons.bo.Role;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.commons.dao.IRoleDAO;
import it.eng.spagobi.commons.metadata.SbiExtRoles;
import it.eng.spagobi.community.dao.ISbiCommunityDAO;
import it.eng.spagobi.community.mapping.SbiCommunity;
import it.eng.spagobi.community.util.CommunityUtilities;
import it.eng.spagobi.profiling.bean.SbiAttribute;
import it.eng.spagobi.profiling.bean.SbiUser;
import it.eng.spagobi.profiling.bean.SbiUserAttributes;
import it.eng.spagobi.profiling.dao.ISbiAttributeDAO;
import it.eng.spagobi.profiling.dao.ISbiUserDAO;

import java.util.ArrayList;
import java.util.Random;

import org.apache.log4j.Logger;

public class CommunityManager {
	
	static private Logger logger = Logger.getLogger(CommunityManager.class);
	
	public Integer saveCommunity(SbiCommunity community, String communityName, String userId){
		Integer communityId = null;
		//if user is registering to SpagoBI and inserts a community,
		//the systems checks for community existence by its name.
		ISbiCommunityDAO commDAO = DAOFactory.getCommunityDAO();

		try {
			ISbiAttributeDAO attrsDAO = DAOFactory.getSbiAttributeDAO();
			ISbiUserDAO userDao = DAOFactory.getSbiUserDAO();
			
			//loads the user:
			SbiUser user = userDao.loadSbiUserByUserId(userId);

			if(community != null  && community.getCommunityId() != null){
				//if exists a mail is sent to the owner of the community that accepts him as 
				//member or refuse him
				
				//1. recovers the e-mail address of the community owner from the user attributes
				SbiUser owner = userDao.loadSbiUserByUserId(community.getOwner());
				
				SbiAttribute attrMail = attrsDAO.loadSbiAttributeByName("email");
				if(attrMail != null){
					Integer attrId = attrMail.getAttributeId();
					SbiUserAttributes userAttr= attrsDAO.loadSbiAttributesByUserAndId(owner.getId(), attrId);
					String emailValue = userAttr.getAttributeValue();
					
					//2. sends the email
					CommunityUtilities communityUtil = new CommunityUtilities();
					boolean result = communityUtil.dispatchMail(communityName, user, owner, emailValue);
				}else{
					logger.info("Owner doesn't have an email address");
				}
				
			}else{
				//if doesn't exist then the community is created, together with a new folder with 
				//the name of the community (functionality code)	
			
				Random generator = new Random();
				int randomInt = generator.nextInt();
				//1.creates a folder:
				LowFunctionality aLowFunctionality = new LowFunctionality();
				
				ILowFunctionalityDAO lowFunct = DAOFactory.getLowFunctionalityDAO();
				LowFunctionality root = lowFunct.loadRootLowFunctionality(false);
				
				aLowFunctionality.setCodType("COMMUNITY_FUNCT");
				String code = "community"+Integer.valueOf(randomInt).toString();
				aLowFunctionality.setCode(code);
				aLowFunctionality.setName(communityName);
				aLowFunctionality.setPath("/"+communityName);	
				aLowFunctionality.setParentId(root.getId());

				//2.populates community bean
				if(community == null){
					community = populateCommunity(userId, communityName, code);				
				}
				//4. saves community and user-community relashionship
				communityId = commDAO.saveSbiComunityUsers(community, userId);
				
				Integer functId = lowFunct.insertCommunityFunctionality(aLowFunctionality);
				
				//add roles for the user				
				addRolesToFunctionality(userId, code);
			}
		} catch (EMFUserError e) {
			logger.error(e.getMessage());
		}
		
		return communityId;
		
	}
	private SbiCommunity populateCommunity(String userId, 
			String communityName,
			String functCode){
		SbiCommunity community = new SbiCommunity();
		community.setName(communityName);
		community.setDescription(communityName);
		community.setFunctCode(functCode);
		community.setOwner(userId);
		
		return community;
		
	}
	public void addRolesToFunctionality(String userId, String functCode) throws EMFUserError{
		ISbiUserDAO userDao = DAOFactory.getSbiUserDAO();
		IRoleDAO roledao= DAOFactory.getRoleDAO();
		ILowFunctionalityDAO lowFunctDao = DAOFactory.getLowFunctionalityDAO();
		SbiUser user = userDao.loadSbiUserByUserId(userId);
		
		ArrayList<SbiExtRoles> userRoles = userDao.loadSbiUserRolesById(user.getId());
		LowFunctionality funct = lowFunctDao.loadLowFunctionalityByCode(functCode, false);
		Role [] execRole4Funct = funct.getExecRoles();
		ArrayList<Role> roles = new ArrayList<Role>();
		for(int j=0; j<execRole4Funct.length; j++){
			Role alreadySetRole = execRole4Funct[j];
			roles.add(alreadySetRole);

		}
		for(int i =0; i<userRoles.size();i++){
			SbiExtRoles extr = userRoles.get(i);
			Integer extRID= extr.getExtRoleId();
			Role r = roledao.loadByID(extRID);
			if(!roles.contains(r)){
				roles.add(r);
			}					
		}
		Role [] rolesArr = roles.toArray(new Role[roles.size()]);
		
		
		funct.setDevRoles(rolesArr);
		funct.setExecRoles(rolesArr);
		funct.setTestRoles(rolesArr);
		funct.setCreateRoles(rolesArr);
		
		lowFunctDao.modifyLowFunctionality(funct);
	}
}
