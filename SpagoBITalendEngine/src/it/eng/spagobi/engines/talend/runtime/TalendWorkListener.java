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
