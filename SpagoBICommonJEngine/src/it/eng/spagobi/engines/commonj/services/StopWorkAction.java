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

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import commonj.work.WorkEvent;

import de.myfoo.commonj.work.FooRemoteWorkItem;

import it.eng.spago.base.SourceBean;
import it.eng.spagobi.commons.utilities.StringUtilities;
import it.eng.spagobi.engines.commonj.runtime.CommonjWorkContainer;
import it.eng.spagobi.engines.commonj.utils.GeneralUtils;
import it.eng.spagobi.engines.commonj.utils.ProcessesStatusContainer;
import it.eng.spagobi.utilities.assertion.Assert;
import it.eng.spagobi.utilities.engines.AbstractEngineAction;
import it.eng.spagobi.utilities.engines.SpagoBIEngineServiceException;
import it.eng.spagobi.utilities.service.JSONFailure;
import it.eng.spagobi.utilities.service.JSONSuccess;


public class StopWorkAction extends AbstractEngineAction {

	
	private static final String PID = "PROCESS_ID";
	
	private static transient Logger logger = Logger.getLogger(StopWorkAction.class);


	@Override
	public void init(SourceBean config) {
		// TODO Auto-generated method stub
		super.init(config);
	}

	@Override
	public void service(SourceBean request, SourceBean response) {
		String pid;
		JSONObject info;
		int statusWI;
		
		logger.debug("IN");
		
		
		
		
		try{
			super.service(request, response);
			
			pid = getAttributeAsString( PID );
			logger.debug("Parameter [" + PID + "] is equals to [" + pid + "]");			
			Assert.assertTrue(!StringUtilities.isEmpty( pid ), "Parameter [" + PID + "] cannot be null or empty");
			
			
			ProcessesStatusContainer processesStatusContainer = ProcessesStatusContainer.getInstance();
			Object o=processesStatusContainer.getPidContainerMap().get(pid);

			// if process still in session stop the process and delete the attribute, otherwise it could have been finished
			if(o!=null){
				CommonjWorkContainer container=(CommonjWorkContainer)o;
				FooRemoteWorkItem fooRwi=container.getFooRemoteWorkItem();
				// release the resource
				fooRwi.release();
				// Use it to give time to set the status
				Thread.sleep(1000);
				statusWI=container.getWorkItem().getStatus();
				//session.removeAttribute("SBI_PROCESS_"+document_id);
				processesStatusContainer.getPidContainerMap().remove(pid);


			} // if no more in session is completed
			else statusWI=WorkEvent.WORK_COMPLETED;

			info=GeneralUtils.buildJSONObject(pid,statusWI);
			try {
				writeBackToClient( new JSONSuccess(info));
			} catch (IOException e) {
				String message = "Impossible to write back the responce to the client";
				throw new SpagoBIEngineServiceException(getActionName(), message, e);
			}
		}
		catch (Exception e) {
			logger.error("Error in stopping the work");
			try {
				writeBackToClient( new JSONFailure( e) );
			} catch (IOException e1) {
				logger.error("Error in stopping the work and in writing back to client",e);
				throw new SpagoBIEngineServiceException(getActionName(), "Error in stopping the work and in writing back to client", e1);
			} catch (JSONException e1) {
				logger.error("Error in stopping the work and in writing back to client",e);
				throw new SpagoBIEngineServiceException(getActionName(), "Error in stopping the work and in writing back to client", e1);
			}
		
		
		} finally {
			logger.debug("OUT");
		}
		

	}

}
