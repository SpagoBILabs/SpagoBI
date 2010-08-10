/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005-2008 Engineering Ingegneria Informatica S.p.A.

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA

**/
package it.eng.spagobi.behaviouralmodel.lov.service;

import it.eng.spago.base.RequestContainer;
import it.eng.spago.base.SessionContainer;
import it.eng.spago.base.SourceBean;
import it.eng.spago.dispatching.action.AbstractHttpAction;
import it.eng.spago.security.IEngUserProfile;
import it.eng.spagobi.behaviouralmodel.lov.bo.ModalitiesValue;
import it.eng.spagobi.behaviouralmodel.lov.dao.IModalitiesValueDAO;
import it.eng.spagobi.commons.bo.UserProfile;
import it.eng.spagobi.commons.constants.SpagoBIConstants;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.commons.utilities.GeneralUtilities;
import it.eng.spagobi.commons.utilities.SpagoBITracer;
import it.eng.spagobi.commons.utilities.UserUtilities;
import it.eng.spagobi.monitoring.dao.AuditManager;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

public class GetLovResultAction extends AbstractHttpAction {

    private static transient Logger logger = Logger.getLogger(GetLovResultAction.class);

    /* (non-Javadoc)
     * @see it.eng.spago.dispatching.service.ServiceIFace#service(it.eng.spago.base.SourceBean, it.eng.spago.base.SourceBean)
     */
    public void service(SourceBean requestSB, SourceBean responseSB) throws Exception {
	logger.debug("IN");

	freezeHttpResponse();
	HttpServletRequest request = getHttpRequest();
	HttpServletResponse response = getHttpResponse();

	// AUDIT UPDATE
	Integer auditId = null;
	String auditIdStr = request.getParameter("SPAGOBI_AUDIT_ID");
	if (auditIdStr == null) {
	    logger.warn("Audit record id not specified! No operations will be performed");
	} else {
	    logger.debug("Audit id = [" + auditIdStr + "]");
	    auditId = new Integer(auditIdStr);
	}
	AuditManager auditManager = AuditManager.getInstance();
	if (auditId != null) {
	    auditManager.updateAudit(auditId, new Long(System.currentTimeMillis()), null, "EXECUTION_STARTED", null,
		    null);
	}
	IEngUserProfile profile = null;
	String userId = null;
	try {
	    RequestContainer reqCont = RequestContainer.getRequestContainer();
	    SessionContainer sessCont = reqCont.getSessionContainer();
	    SessionContainer permSess = sessCont.getPermanentContainer();
	    profile = (IEngUserProfile) permSess.getAttribute(IEngUserProfile.ENG_USER_PROFILE);
	    if (profile == null) {
	    	profile=UserUtilities.getUserProfile(request);
	    }
	    
	    
	    userId = (String) ((UserProfile)profile).getUserId();

	    String documentId = request.getParameter("documentId");
	    logger.debug("got parameter documentId=" + documentId);

	    // TODO control that template contains the reference to the lov to
	    // be executed
	    // ContentServiceImplSupplier c = new ContentServiceImplSupplier();
	    // Content template = c.readTemplate(userId, documentId);
	    // parse template ....

	    // get the mode (mode=single --> only one lov to execute, mode=list
	    // --> more than one lov to execute)
	    // if the parameter mode is not present the single mode is the
	    // default
	    String mode = request.getParameter("mode");

	    String result = "";

	    if (mode == null || !mode.equalsIgnoreCase("list")) {
		// ge the lov name
		String dataName = request.getParameter("dataname");
		// if lov name is not present send an error
		if ((dataName == null) || dataName.trim().equals("")) {
		    response.getOutputStream().write(createErrorMsg(10, "Param dataname not found"));
		    response.getOutputStream().flush();
		    return;
		}
		// check if the lov is supported
		if (!isLovSupported(dataName, response)) {
		    logger.debug("Lov is not supported.");
		    return;
		}
		// get the lov type
		String type = getLovType(dataName);
		// get the result
		if (profile == null) {
		    result = GeneralUtilities.getLovResult(dataName);
		} else {
		    result = GeneralUtilities.getLovResult(dataName, profile);
		}
		// if the lov is a query trasform the result to lower case (for
		// flash dashboard)
		if (type.equalsIgnoreCase("QUERY")) {
		    result = result.toLowerCase();
		}

	    } else {
		Map lovMap = new HashMap();
		Enumeration paramNames = request.getParameterNames();
		while (paramNames.hasMoreElements()) {
		    String paramKey = (String) paramNames.nextElement();
		    if (paramKey.startsWith("LovResLogName_")) {
			String logicalName = paramKey.substring(14);
			String paramValue = request.getParameter(paramKey);
			if (!(paramValue == null) && !(paramValue.trim().equals(""))) {
			    lovMap.put(logicalName, paramValue);
			}
		    }
		}
		result = GeneralUtilities.getLovMapResult(lovMap);
	    }

	    // replace special characters
	    result = result.replaceAll("&lt;", "<");
	    result = result.replaceAll("&gt;", ">");
	    // write the result into response
	    response.getOutputStream().write(result.getBytes());
	    response.getOutputStream().flush();
	    // AUDIT UPDATE
	    auditManager.updateAudit(auditId, null, new Long(System.currentTimeMillis()), "EXECUTION_PERFORMED", null,
		    null);

	} catch (Exception e) {
	    logger.error("Exception", e);
	    // AUDIT UPDATE
	    auditManager.updateAudit(auditId, null, new Long(System.currentTimeMillis()), "EXECUTION_FAILED", e
		    .getMessage(), null);
	} finally {
	    logger.debug("OUT");
	}
    }

    private String getLovType(String lovName) {
	String toReturn = "";
	try {
	    IModalitiesValueDAO lovDAO = DAOFactory.getModalitiesValueDAO();
	    ModalitiesValue lov = lovDAO.loadModalitiesValueByLabel(lovName);
	    String type = lov.getITypeCd();
	    toReturn = type;
	} catch (Exception e) {
	    logger.error("Error while recovering type of lov " + lovName);
	}
	return toReturn;
    }

    private boolean isLovSupported(String lovName, HttpServletResponse response) {
	boolean toReturn = true;
	try {
	    IModalitiesValueDAO lovDAO = DAOFactory.getModalitiesValueDAO();
	    ModalitiesValue lov = lovDAO.loadModalitiesValueByLabel(lovName);
	    String type = lov.getITypeCd();
	    if (!type.equalsIgnoreCase("QUERY") && !type.equalsIgnoreCase("SCRIPT")
		    && !type.equalsIgnoreCase("JAVA_CLASS")) {
		SpagoBITracer.major(SpagoBIConstants.NAME_MODULE, this.getClass().getName(), "isLovSupported",
			"Dashboard " + type + " lov Not yet Supported");
		response.getOutputStream().write(createErrorMsg(12, "Dashboard  " + type + " lov not yet supported"));
		response.getOutputStream().flush();
		toReturn = false;
	    }
	} catch (Exception e) {
	    toReturn = false;
	    logger.error("Error while checkin if lov " + lovName + " is supported");
	}
	return toReturn;
    }

    private byte[] createErrorMsg(int code, String message) {
	String response = "<response><error><code>" + code + "</code>" + "<message>" + message
		+ "</message></error></response>";
	return response.getBytes();
    }

}
