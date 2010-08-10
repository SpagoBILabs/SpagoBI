/**
Copyright (c) 2005-2010, Engineering Ingegneria Informatica s.p.a.
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
package it.eng.spagobi.engines.commonj.runtime;

import it.eng.spagobi.engines.commonj.utils.ProcessesStatusContainer;
import it.eng.spagobi.services.proxy.EventServiceProxy;
import it.eng.spagobi.utilities.engines.AuditServiceProxy;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

import commonj.work.WorkEvent;
import commonj.work.WorkException;
import commonj.work.WorkItem;
import commonj.work.WorkListener;


public class CommonjWorkListener implements WorkListener {


	public static final String COMMONJ_ROLES_HANDLER_CLASS_NAME = "it.eng.spagobi.engines.drivers.commonj.CommonjRolesHandler";
	public static final String COMMONJ_PRESENTAION_HANDLER_CLASS_NAME = "it.eng.spagobi.engines.drivers.commonj.CommonjEventPresentationHandler";
	public static final String BIOBJECT_ID = "biobjectId";
	public static final String BIOBJECT_LABEL = "biobjectLabel";
	public static final String USER_NAME = "userName";

	AuditServiceProxy auditServiceProxy;
	EventServiceProxy eventServiceProxy;
	String workName;
	String workClass;
	String executionRole;
	String biObjectID;
	String biObjectLabel;
	String pid;

	private static transient Logger logger = Logger.getLogger(CommonjWorkListener.class);

	public CommonjWorkListener(AuditServiceProxy auditServiceProxy, EventServiceProxy eventServiceProxy) {
		this.auditServiceProxy = auditServiceProxy;
		this.eventServiceProxy = eventServiceProxy;
	}




	public String getWorkClass() {
		return workClass;
	}

	public void setWorkClass(String workClass) {
		this.workClass = workClass;
	}

	public String getWorkName() {
		return workName;
	}

	public void setWorkName(String workName) {
		this.workName = workName;
	}

	public void workAccepted(WorkEvent event) {
		logger.info("IN.Work "+workName+" accepted");
		logger.debug("Work "+workName+" accepted");
		logger.info("OUT");
	}


	public void workRejected(WorkEvent event) {
		logger.info("IN.Work "+workName+" rejected");
		if(auditServiceProxy != null) {
			auditServiceProxy.notifyServiceErrorEvent("An error occurred while work execution");
		} else {
			logger.warn("Impossible to log START-EVENT because the audit proxy has not been instatiated properly");
		}
		if(eventServiceProxy != null) {
			String pars=builParametersString();
			eventServiceProxy.fireEvent("Event of work "+workName+" launching class "+workClass+": Error",pars,COMMONJ_ROLES_HANDLER_CLASS_NAME, COMMONJ_PRESENTAION_HANDLER_CLASS_NAME);	
		} else {
			logger.warn("Impossible to log ERROR-EVENT because the event proxy has not been instatiated properly");
		}				
		logger.debug("Work "+workName+" rejected");
		logger.info("OUT");
	}


	public void workCompleted(WorkEvent event) {
		logger.info("IN.Entering work "+workName+" completed");

		WorkException workException;
		//Work commonjWork;

		logger.info("IN");

		try {
			workException = event.getException();
			if (workException != null) {
				logger.error(workException); 
			}

			//commonjWork = (Work) event.getWorkItem().getResult();
			if (workException != null) {
				if(auditServiceProxy != null) {
					auditServiceProxy.notifyServiceErrorEvent("An error occurred while work execution");
				} else {
					logger.warn("Impossible to log START-EVENT because the audit proxy has not been instatiated properly");
				}
				if(eventServiceProxy != null) {
					String pars=builParametersString();					
					eventServiceProxy.fireEvent("Event of work "+workName+" launching class "+workClass+": Error",pars,COMMONJ_ROLES_HANDLER_CLASS_NAME, COMMONJ_PRESENTAION_HANDLER_CLASS_NAME);	
				} else {
					logger.warn("Impossible to log ERROR-EVENT because the event proxy has not been instatiated properly");
				}				
			} else {
				if(auditServiceProxy != null) {
					auditServiceProxy.notifyServiceEndEvent();
				} else {
					logger.warn("Impossible to log START-EVENT because the audit proxy has not been instatiated properly");
				}
				if(eventServiceProxy != null) {
					String pars=builParametersString();					
					eventServiceProxy.fireEvent("Event of work "+workName+" launching class "+workClass+": End",pars,COMMONJ_ROLES_HANDLER_CLASS_NAME, COMMONJ_PRESENTAION_HANDLER_CLASS_NAME);	
				} else {
					logger.warn("Impossible to log END-EVENT because the event proxy has not been instatiated properly");
				}
			}

		} catch (Throwable t) {
			throw new RuntimeException("An error occurred while handling process completed event");
		} finally {
			logger.debug("OUT");
		}

		// clean the singleton!!! 
		if(pid != null){
			ProcessesStatusContainer processesStatusContainer = ProcessesStatusContainer.getInstance();
			processesStatusContainer.getPidContainerMap().remove(pid);
			logger.debug("removed from singleton process item with pid "+pid);
		}
		logger.info("OUT");
	}

	public void workStarted(WorkEvent event) {
		logger.info("IN");
		logger.debug("Work "+workName+" started");

		if(auditServiceProxy != null) {
			auditServiceProxy.notifyServiceStartEvent();
		} else {
			logger.warn("Impossible to log START-EVENT because the audit proxy has not been instatiated properly");
		}

		WorkItem wi=event.getWorkItem();

		if(eventServiceProxy != null) {
			String pars=builParametersString();			
			eventServiceProxy.fireEvent("Event of work "+workName+" launching class "+workClass+": Started",pars,COMMONJ_ROLES_HANDLER_CLASS_NAME, COMMONJ_PRESENTAION_HANDLER_CLASS_NAME);	
		} else {
			logger.warn("Impossible to log START-EVENT because the event proxy has not been instatiated properly");
		}

		logger.info("OUT");


	}


	private String builParametersString(){
		logger.debug("IN");
		Map startEventParams = new HashMap();
		//startEventParams.put(EVENT_TYPE, DOCUMENT_EXECUTION_START);
		if(biObjectID!=null){
			startEventParams.put(BIOBJECT_ID, biObjectID);
		}
		if(biObjectLabel != null){
			startEventParams.put(BIOBJECT_LABEL, biObjectLabel);			
		}

		String startEventParamsStr = getParamsStr(startEventParams);
		logger.debug("OUT");
		return  startEventParamsStr;
	}

	String getExecutionRole() {
		return executionRole;
	}


	public void setExecutionRole(String executionRole) {
		this.executionRole = executionRole;
	}




	public String getBiObjectID() {
		return biObjectID;
	}




	public void setBiObjectID(String biObjectID) {
		this.biObjectID = biObjectID;
	}




	public String getBiObjectLabel() {
		return biObjectLabel;
	}




	public void setBiObjectLabel(String biObjectLabel) {
		this.biObjectLabel = biObjectLabel;
	}




	private String getParamsStr(Map params) {
		logger.debug("IN");
		StringBuffer buffer = new StringBuffer();
		Iterator it = params.keySet().iterator();
		boolean isFirstParameter = true;
		while (it.hasNext()) {
			String pname = (String) it.next();
			String pvalue = (String) params.get(pname);
			if (!isFirstParameter)
				buffer.append("&");
			else
				isFirstParameter = false;
			buffer.append(pname + "=" + pvalue);
		}
		logger.debug("parameters: " + buffer.toString());
		logger.debug("OUT");
		return buffer.toString();
	}

}
