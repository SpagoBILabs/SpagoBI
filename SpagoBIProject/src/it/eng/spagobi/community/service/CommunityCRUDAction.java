/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.community.service;

import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.community.dao.ISbiCommunityDAO;
import it.eng.spagobi.community.mapping.SbiCommunity;
import it.eng.spagobi.utilities.exceptions.SpagoBIServiceException;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;


@Path("/communityCRUD")
public class CommunityCRUDAction {
	@Context
	private HttpServletResponse servletResponse;
	@Context
	HttpSession session;
	private static Logger logger = Logger.getLogger(CommunityCRUDAction.class);

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getAllCommunities(@Context HttpServletRequest req) {
		ISbiCommunityDAO commDao = null;

		List<SbiCommunity> communities;

		String communitiesJSONStr = "";
		try {
			commDao = DAOFactory.getCommunityDAO();
			
			communities = commDao.loadAllSbiCommunities();
			if(communities != null){
				ObjectMapper mapper = new ObjectMapper();    
				String innerList = mapper.writeValueAsString(communities);
				communitiesJSONStr ="{root:"+innerList+"}";
			}

		} catch (Throwable t) {
			throw new SpagoBIServiceException(
					"An unexpected error occured while instatiating the dao", t);
		}

		return communitiesJSONStr;

	}

}
