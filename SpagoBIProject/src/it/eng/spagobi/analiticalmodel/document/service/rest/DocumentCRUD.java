/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package it.eng.spagobi.analiticalmodel.document.service.rest;

import it.eng.spago.security.IEngUserProfile;
import it.eng.spagobi.analiticalmodel.document.AnalyticalModelDocumentManagementAPI;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;

/**
 * @authors Alberto Ghedin (alberto.ghedin@eng.it)
 * 
 */
@Path("/documents")
public class DocumentCRUD {

	public static final String OBJECT_ID = "docId";
	static private Logger logger = Logger.getLogger(DocumentCRUD.class);
	
	/**
	 * Service to clone a document
	 * @param req
	 * @return
	 */
	@POST
	@Path("/clone")
	@Produces(MediaType.APPLICATION_JSON)
	public String cloneDocument(@Context HttpServletRequest req){
		
		logger.debug("IN");
		String ids = req.getParameter(OBJECT_ID);
		Integer id = -1;
		try {
			id = new Integer(ids);
		} catch (Exception e) {
			logger.error("Error cloning the document.. Impossible to parse the id of the document "+ids,e);
			throw new SpagoBIRuntimeException("Error cloning the document.. Impossible to parse the id of the document "+ids,e);
		}
		IEngUserProfile profile = (IEngUserProfile) req.getSession().getAttribute(IEngUserProfile.ENG_USER_PROFILE);
		
		AnalyticalModelDocumentManagementAPI documentManagementAPI = new AnalyticalModelDocumentManagementAPI( profile );
		logger.debug("Execute clone");
		documentManagementAPI.cloneDocument(id);
		logger.debug("OUT");
		return "{}";
	}
	
}
