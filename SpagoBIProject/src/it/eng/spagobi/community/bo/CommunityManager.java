/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.community.bo;

import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.analiticalmodel.functionalitytree.bo.LowFunctionality;
import it.eng.spagobi.commons.bo.UserProfile;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.commons.metadata.SbiDomains;
import it.eng.spagobi.community.dao.ISbiCommunityDAO;
import it.eng.spagobi.community.mapping.SbiCommunity;

import java.util.Random;

import org.apache.log4j.Logger;

public class CommunityManager {
	
	static private Logger logger = Logger.getLogger(CommunityManager.class);
	
	public Integer saveCommunity(String communityName, UserProfile profile){
		Integer communityId = null;
		//if user is registering to SpagoBI and inserts a community,
		//the systems checks for community existence by its name.
		ISbiCommunityDAO commDAO = DAOFactory.getCommunityDAO();
		
		try {
			SbiCommunity community = commDAO.loadSbiCommunityByName(communityName);
			if(community != null){
				//if exists a mail is sent to the owner of the community that accepts him as 
				//member or refuse him
			}else{
				//if doesn't exist then the community is created, together with a new folder with 
				//the name of the community (functionality code)	
				SbiDomains domain = DAOFactory.getDomainDAO().loadSbiDomainByCodeAndValue("FUNCT_TYPE", "COMMUNITY_FUNCT");
				
				Random generator = new Random();
				int randomInt = generator.nextInt();
				//1.creates a folder:
				LowFunctionality aLowFunctionality = new LowFunctionality();
				
				aLowFunctionality.setCodType("COMMUNITY_FUNCT");
				String code = "community-"+Integer.valueOf(randomInt).toString();
				aLowFunctionality.setCode(code);
				aLowFunctionality.setName(communityName);
				aLowFunctionality.setPath("/"+communityName);
				
				Integer functId = DAOFactory.getLowFunctionalityDAO().insertCommunityFunctionality(aLowFunctionality, profile);
				//2.populates community bean
				if(functId != null){
					community = populateCommunity((Integer)profile.getUserId(), communityName, code);
					
					//3.saves it
					communityId = commDAO.saveSbiComunity(community);
				}
			}
		} catch (EMFUserError e) {
			logger.error(e.getMessage());
		}
		
		return communityId;
		
	}
	private SbiCommunity populateCommunity(Integer userId, 
			String communityName,
			String functCode){
		SbiCommunity community = new SbiCommunity();
		community.setName(communityName);
		community.setFunctCode(functCode);
		community.setOwner(userId);
		
		return community;
		
	}
}
