/**
Copyright (c) 2005-2008, Engineering Ingegneria Informatica s.p.a.
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
package it.eng.spagobi.engines.talend.runtime;

import org.apache.log4j.Logger;

import commonj.work.WorkEvent;
import commonj.work.WorkException;
import commonj.work.WorkListener;

import it.eng.spagobi.services.proxy.EventServiceProxy;
import it.eng.spagobi.utilities.engines.AuditServiceProxy;

/**
 * @author Angelo Bernabei
 *         angelo.bernabei@eng.it
 */
public class TalendWorkListener implements WorkListener {

	AuditServiceProxy auditServiceProxy;
	EventServiceProxy eventServiceProxy;
	
	public static final String TALEND_ROLES_HANDLER_CLASS_NAME = "it.eng.spagobi.engines.drivers.talend.TalendRolesHandler";
    public static final String TALEND_PRESENTAION_HANDLER_CLASS_NAME = "it.eng.spagobi.engines.drivers.talend.TalendEventPresentationHandler";
    
    
    private static transient Logger logger = Logger.getLogger(TalendWorkListener.class);
	
    public TalendWorkListener(AuditServiceProxy auditServiceProxy, EventServiceProxy eventServiceProxy) {
    	this.auditServiceProxy = auditServiceProxy;
    	this.eventServiceProxy = eventServiceProxy;
    }
    
    
    public void workAccepted(WorkEvent event) {
    	logger.info("IN");
    }

    public void workRejected(WorkEvent event) {
    	logger.info("IN");
    }
    
    public void workCompleted(WorkEvent event) {
		
    	WorkException workException;
    	TalendWork talendWork;
    	
    	logger.info("IN");
    	
    	try {
			workException = event.getException();
			if (workException != null) {
				logger.error(workException); 
			}
			
			talendWork = (TalendWork) event.getWorkItem().getResult();
			if (workException != null || !talendWork.isCompleteWithoutError()) {
				if(auditServiceProxy != null) {
		    		auditServiceProxy.notifyServiceErrorEvent("An error occurred while job execution");
		    	} else {
		    		logger.warn("Impossible to log START-EVENT because the audit proxy has not been instatiated properly");
		    	}
			} else {
				if(auditServiceProxy != null) {
		    		auditServiceProxy.notifyServiceEndEvent();
		    	} else {
		    		logger.warn("Impossible to log START-EVENT because the audit proxy has not been instatiated properly");
		    	}
			}
	
    	} catch (Throwable t) {
			throw new RuntimeException("An error occurred while handling process completed event");
		} finally {
    		logger.debug("OUT");
    	}

    }

    public void workStarted(WorkEvent event) {
    	
    	if(auditServiceProxy != null) {
    		auditServiceProxy.notifyServiceStartEvent();
    	} else {
    		logger.warn("Impossible to log START-EVENT because the audit proxy has not been instatiated properly");
    	}
    	
    	/*
    	if(eventServiceProxy != null) {
    		
    		
    	} else {
    		logger.error("Impossible to log START-EVENT because the audit proxy has not been instatiated properly");
    	}
    	*/
    	
    	
    	 
    }



}
