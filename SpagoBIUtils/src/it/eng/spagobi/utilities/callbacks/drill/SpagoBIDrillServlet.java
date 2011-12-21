/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 *
 * This file is part of SpagoBI. SpagoBI is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 2.1 of the License, or any later version. 
 * SpagoBI is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received
 * a copy of the GNU Lesser General Public License along with SpagoBI. If not, see: http://www.gnu.org/licenses/.
 * The complete text of SpagoBI license is included in the COPYING.LESSER file. 
 */
package it.eng.spagobi.utilities.callbacks.drill;

import it.eng.spago.security.IEngUserProfile;
import it.eng.spagobi.commons.bo.UserProfile;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

public class SpagoBIDrillServlet extends HttpServlet {

    private static transient Logger logger = Logger
	    .getLogger(SpagoBIDrillServlet.class);

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServlet#service(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public void service(HttpServletRequest request, HttpServletResponse response) {
	HttpSession session = request.getSession();

	IEngUserProfile profile = (IEngUserProfile) session
		.getAttribute(IEngUserProfile.ENG_USER_PROFILE);
	if (profile == null) {
	    logger.error("IEngUserProfile is not in session!!!");
	} else {
	  //  String username = (String) profile.getUserUniqueIdentifier();
	    String username = (String) ((UserProfile)profile).getUserId();

	    String spagobiContextUrl = (String) session
		    .getAttribute("spagobicontext");
	    String url = spagobiContextUrl + "/servlet/AdapterHTTP?";
	    url += "USERNAME=" + username;
	    url += "&NEW_SESSION=TRUE";
	    url += "&PAGE=DirectExecutionPage";
	    url += "&DOCUMENT_LABEL=" + request.getParameter("DOCUMENT_LABEL");
	    String documentParameters = "";
	    Enumeration parameterNames = request.getParameterNames();
	    while (parameterNames.hasMoreElements()) {
		String parurlname = (String) parameterNames.nextElement();
		if (parurlname.equalsIgnoreCase("DOCUMENT_LABEL"))
		    continue;
		String parvalue = request.getParameter(parurlname);
		documentParameters += "%26" + parurlname + "%3D" + parvalue;
	    }
	    url += "&DOCUMENT_PARAMETERS=" + documentParameters;

	    try {
		response.sendRedirect(url);
	    } catch (IOException e1) {
		logger.error("IOException during sendRedirect",e1);
	    }
	}

    }

}
