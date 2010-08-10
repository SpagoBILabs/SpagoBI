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
package it.eng.spagobi.engines.commonj.services;

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import commonj.work.Work;
import commonj.work.WorkEvent;
import commonj.work.WorkItem;

import de.myfoo.commonj.work.FooRemoteWorkItem;

import it.eng.spago.base.SourceBean;
import it.eng.spagobi.engines.commonj.runtime.CommonjWorkContainer;
import it.eng.spagobi.engines.commonj.runtime.CommonjWorkListener;
import it.eng.spagobi.engines.commonj.utils.GeneralUtils;
import it.eng.spagobi.engines.commonj.utils.ProcessesStatusContainer;
import it.eng.spagobi.utilities.engines.AbstractEngineAction;
import it.eng.spagobi.utilities.engines.SpagoBIEngineServiceException;
import it.eng.spagobi.utilities.engines.SpagoBIEngineServiceExceptionHandler;
import it.eng.spagobi.utilities.service.JSONAcknowledge;
import it.eng.spagobi.utilities.service.JSONFailure;
import it.eng.spagobi.utilities.service.JSONSuccess;
import it.eng.spagobi.utilities.threadmanager.WorkManager;


public class StatusWorkAction extends AbstractEngineAction {

	private static transient Logger logger = Logger.getLogger(StatusWorkAction.class);


	@Override
	public void init(SourceBean config) {
		// TODO Auto-generated method stub
		super.init(config);
	}

	@Override
	public void service(SourceBean request, SourceBean response) {
		logger.debug("IN");


		JSONObject info=null;
		Object pidO=request.getAttribute("PROCESS_ID");
		String pid="";
		if(pidO!=null){
			pid=pidO.toString();

		}
		else{   // if pidO not found just return an empty xml Object
			try {

				info=GeneralUtils.buildJSONObject(pid,0);
				writeBackToClient( new JSONSuccess(info));
			} catch (Exception e) {
				String message = "Impossible to write back the responce to the client";
				throw new SpagoBIEngineServiceException(getActionName(), message, e);
			}

			return;


		}

		super.service(request, response);
		HttpSession session=getHttpSession();

		// Get document id, must find
//		String document_id=null;
//		Object document_idO=null;
//		document_idO=request.getAttribute("DOCUMENT_ID");
//		if(document_idO!=null){
//			document_id=document_idO.toString();
//		}
//		else{
//			document_id="";
//			logger.error("could not retrieve document id");
//			throw new SpagoBIEngineServiceException(getActionName(), "could not find document id");
//		}

		CommonjWorkContainer container=null;
		ProcessesStatusContainer processesStatusContainer = ProcessesStatusContainer.getInstance();
		Object o=processesStatusContainer.getPidContainerMap().get(pid);
		//recover from session, if does not find means work is completed
		//Object o=session.getAttribute("SBI_PROCESS_"+document_id);
		try{
			int statusWI;

			if(o!=null){			// object found in session, work could be not started, running or completed

				container=(CommonjWorkContainer)o;
				FooRemoteWorkItem fooRwi=container.getFooRemoteWorkItem();
				WorkItem wi=container.getWorkItem();

				// if WorkItem is not set means work has never been started
				if(fooRwi!=null && wi!=null){
					statusWI=wi.getStatus();
					// if finds that work is finished delete the attribute from session
					if(statusWI==WorkEvent.WORK_COMPLETED){
						logger.debug("Work is finished - remove from session");
						//session.removeAttribute("SBI_PROCESS_"+document_id);
						processesStatusContainer.getPidContainerMap().remove(pid);
					}
				}
				else{
					// if not workitem is set means that is not started yet or has been cancelled by listener!?!
					statusWI=0;
				}
			}
			else{
				// No more present in session, so it has been deleted
				statusWI=WorkEvent.WORK_COMPLETED;
			}

			info=GeneralUtils.buildJSONObject(pid,statusWI);
			logger.debug(GeneralUtils.getEventMessage(statusWI));
			try {
				writeBackToClient( new JSONSuccess(info));
			} catch (IOException e) {
				String message = "Impossible to write back the responce to the client";
				throw new SpagoBIEngineServiceException(getActionName(), message, e);
			}

		}
		catch (Exception e) {
			logger.error("Error in reading work status");
			try {
				writeBackToClient( new JSONFailure( e) );
			} catch (IOException e1) {
				logger.error("Error in reading work status and in writing back to client",e);
				throw new SpagoBIEngineServiceException(getActionName(), "Error in reading work status and in writing back to client", e1);
			} catch (JSONException e1) {
				logger.error("Error in reading work status and in writing back to client",e);
				throw new SpagoBIEngineServiceException(getActionName(), "Error in reading work status and in writing back to client", e1);
			}
		}	
		logger.debug("OUT");

	}

}
