/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.twitter.analysis.api;

import it.eng.spagobi.commons.constants.SpagoBIConstants;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;

import org.apache.log4j.Logger;

/**
 * @author Giorgio Federici (giorgio.federici@eng.it)
 */

@Path("/start")
public class SpagoBISocialAnalysisStartGuiAPI {

	static final Logger logger = Logger.getLogger(SpagoBISocialAnalysisStartGuiAPI.class);

	@GET
	public void startGUI(@Context HttpServletRequest request, @Context HttpServletResponse response) {

		logger.debug("REST Service: /SpagoBISocialAnalysis/restful-services/start");

		try {

			request.getSession().setAttribute(SpagoBIConstants.SBI_LANGUAGE, request.getParameter(SpagoBIConstants.SBI_LANGUAGE));
			request.getSession().setAttribute(SpagoBIConstants.SBI_COUNTRY, request.getParameter(SpagoBIConstants.SBI_COUNTRY));
			response.sendRedirect(request.getContextPath() + "/index.jsp");

		} catch (Throwable t) {
			throw new SpagoBIRuntimeException("Error in REST Service: /SpagoBISocialAnalysis/restful-services/start", t);
		}

	}
}
