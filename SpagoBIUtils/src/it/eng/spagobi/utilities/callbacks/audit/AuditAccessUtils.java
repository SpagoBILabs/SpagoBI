/**
Copyright (C) 2004 - 2011, Engineering Ingegneria Informatica s.p.a.
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice, this list of 
      conditions and the following disclaimer.
      
    * Redistributions in binary form must reproduce the above copyright notice, this list of 
      conditions and the following disclaimer in the documentation and/or other materials 
      provided with the distribution.
      
    * Neither the name of the Engineering Ingegneria Informatica s.p.a. nor the names of its contributors may
      be used to endorse or promote products derived from this software without specific
      prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND 
CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, 
INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, 
EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE

**/
package it.eng.spagobi.utilities.callbacks.audit;

import it.eng.spagobi.services.proxy.AuditServiceProxy;

import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

public class AuditAccessUtils implements Serializable{

    static private Logger logger = Logger.getLogger(AuditAccessUtils.class);
    private List _auditIds;

    // private boolean _isNewExecution = true;

    /**
     * Instantiates a new audit access utils.
     * 
     * @param auditId the audit id
     */
    public AuditAccessUtils(String auditId) {
	_auditIds = new ArrayList();
	_auditIds.add(auditId);
    }

    /**
     * Gets the audit ids.
     * 
     * @return the audit ids
     */
    public List getAuditIds() {
	return _auditIds;
    }

    /**
     * Adds the audit id.
     * 
     * @param auditId the audit id
     */
    public void addAuditId(String auditId) {
	_auditIds.add(auditId);
    }

    /**
     * Updates the audit record with the id specified using the constructor or
     * by the setAuditId method. It makes an http call to the servlet specified
     * using the constructor or by the setAuditServlet method. If the current
     * execution is not a new one (examples: page refresh, portlet rendering)
     * nothing is updated.
     * 
     * @param auditId The id of the audit record to be modified
     * @param startTime The start time
     * @param endTime The end time
     * @param executionState The execution state
     * @param errorMessage The error message
     * @param errorCode The error code
     * @param session the session
     * @param userId the user id
     */
    public void updateAudit(HttpSession session, String userId,String auditId, Long startTime, Long endTime,
	    String executionState, String errorMessage, String errorCode) {
	logger.debug("IN");
	logger.debug("*** updateAudit userId: " + userId);
	try {
	    if (auditId == null || !_auditIds.contains(auditId))
		return;
	    // limits errorMessage length
	    if (errorMessage != null && errorMessage.length() > 390) {
		errorMessage = errorMessage.substring(0, 390);
	    }

	    AuditServiceProxy proxy = new AuditServiceProxy(userId,session); 
	    String ris = proxy.log( auditId, startTime != null ? startTime
		    .toString() : "",
		    endTime != null ? endTime.toString() : "",
		    executionState != null ? executionState : "",
		    errorMessage != null ? errorMessage : "",
		    errorCode != null ? errorCode : "");

	    if (ris != null && ris.equals("KO")) {
		logger.warn("Audit service doesn't work correctly!!!");
	    }
	} catch (Exception e) {
	    logger.error("Audit service don't work!!!",e);
	} finally {
	    logger.debug("OUT");
	}
    }

}
