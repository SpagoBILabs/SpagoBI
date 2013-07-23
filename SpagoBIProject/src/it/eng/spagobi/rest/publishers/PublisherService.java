/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.rest.publishers;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;


@Path("/publish")
public class PublisherService {
	@Context
	private HttpServletResponse servletResponse;
	@Context
	HttpSession session;
	private static Logger logger = Logger.getLogger(PublisherService.class);

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public void publish(@Context HttpServletRequest req) {

		try {
			String publisher = req.getParameter("PUBLISHER");
			req.getRequestDispatcher(publisher).forward(req, servletResponse);

		} catch (ServletException e) {
			logger.error("Error dispatching request");
		} catch (IOException e) {
			logger.error("Error writing content");
		}
	}

}
