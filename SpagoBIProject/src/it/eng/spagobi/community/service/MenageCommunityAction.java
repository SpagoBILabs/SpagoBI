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
import it.eng.spagobi.commons.constants.SpagoBIConstants;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.commons.utilities.messages.IMessageBuilder;
import it.eng.spagobi.commons.utilities.messages.MessageBuilderFactory;
import it.eng.spagobi.community.dao.ISbiCommunityDAO;
import it.eng.spagobi.community.mapping.SbiCommunity;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;

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
}
