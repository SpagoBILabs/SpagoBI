/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.community.service;

import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.commons.bo.UserProfile;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.commons.services.AbstractSpagoBIAction;
import it.eng.spagobi.community.dao.ISbiCommunityDAO;
import it.eng.spagobi.utilities.exceptions.SpagoBIServiceException;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

public class MenageCommunityAction extends AbstractSpagoBIAction {
	
	private static Logger logger = Logger.getLogger(MenageCommunityAction.class);
	
	private final String MESSAGE_DET = "MESSAGE_DET";
	private final String SAVE_COMMUNITY = "SAVE_COMMUNITY";
	private final String LIST_COMMUNITIES = "LIST_COMMUNITIES";
	private final String PUBLISH_TO_COMMUNITY = "PUBLISH_TO_COMMUNITY";
	
	@Override
	public void doService() {

		logger.debug("IN");
		ISbiCommunityDAO communityDao;
		UserProfile profile = (UserProfile) this.getUserProfile();
		communityDao = DAOFactory.getCommunityDAO();
		communityDao.setUserProfile(getUserProfile());
		HttpServletRequest httpRequest = getHttpRequest();

		Locale locale = getLocale();

		String serviceType = this.getAttributeAsString(MESSAGE_DET);
		logger.debug("Service type "+serviceType);
		
		
		if (serviceType != null && serviceType.contains(SAVE_COMMUNITY)) {
			//if user is registering to SpagoBI and inserts a community,
			//the systems checks for community existence.
			
			//if exists a mail is sent to the owner of the community that accepts him as 
			//member or refuse him
			
			//if doesn't exist then the community is created, together with a new folder with 
			//the name of the community (label?)
			
			
		}else if (serviceType != null && serviceType.contains(LIST_COMMUNITIES)) {
			//gets the list of the communities for a specific user
			
		}else if (serviceType != null && serviceType.contains(PUBLISH_TO_COMMUNITY)) {
			//when user saves a document for a community
			//the document is saved inside the community folder
			
		}
		logger.debug("OUT");
	}

}
