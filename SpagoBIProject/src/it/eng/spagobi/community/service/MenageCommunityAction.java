/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.community.service;

import it.eng.spago.error.EMFUserError;
import it.eng.spago.security.IEngUserProfile;
import it.eng.spagobi.commons.bo.Domain;
import it.eng.spagobi.commons.bo.UserProfile;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.commons.dao.IDomainDAO;
import it.eng.spagobi.commons.services.AbstractSpagoBIAction;
import it.eng.spagobi.community.dao.ISbiCommunityDAO;
import it.eng.spagobi.community.mapping.SbiCommunity;
import it.eng.spagobi.tools.dataset.measurecatalogue.MeasureCatalogue;
import it.eng.spagobi.tools.dataset.measurecatalogue.MeasureCatalogueSingleton;
import it.eng.spagobi.tools.datasource.bo.DataSource;
import it.eng.spagobi.tools.datasource.dao.IDataSourceDAO;
import it.eng.spagobi.utilities.exceptions.SpagoBIServiceException;

import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.json.JSONObject;

@Path("/community")
public class MenageCommunityAction {
	
	private static Logger logger = Logger.getLogger(MenageCommunityAction.class);

	
	@GET
	@Path("/accept")
	@Produces(MediaType.TEXT_HTML)
	public String accept(@Context HttpServletRequest req) {
		
		IEngUserProfile profile = (IEngUserProfile) req.getSession()
				.getAttribute(IEngUserProfile.ENG_USER_PROFILE);
		String userToAccept = (String)req.getParameter("userToAccept");
		String community = (String)req.getParameter("community");
		String owner = (String)req.getParameter("owner");
		String result="Operation succeded: "+userToAccept+" added to the community "+community;
		
		if(profile.getUserUniqueIdentifier().equals(owner)){

			
			SbiCommunity sbiComm;
			try {
				ISbiCommunityDAO communityDao;
				communityDao = DAOFactory.getCommunityDAO();
	
				sbiComm = communityDao.loadSbiCommunityByName(community);
				communityDao.addCommunityMember(sbiComm, userToAccept);
	
				
			} catch (EMFUserError e) {
				logger.error(e.getMessage());
				result= "Operation failed";
			}		
		}else{
			result= "User cannot perform action";
		}
		logger.debug("OUT");
		return result;
	}
}
