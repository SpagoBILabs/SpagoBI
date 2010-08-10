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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import it.eng.spago.base.SourceBean;
import it.eng.spagobi.engines.commonj.CommonjEngine;
import it.eng.spagobi.engines.commonj.exception.WorkExecutionException;
import it.eng.spagobi.engines.commonj.exception.WorkNotFoundException;
import it.eng.spagobi.engines.commonj.runtime.CommonjWork;
import it.eng.spagobi.engines.commonj.runtime.WorkConfiguration;
import it.eng.spagobi.engines.commonj.runtime.WorksRepository;
import it.eng.spagobi.engines.commonj.utils.ProcessesStatusContainer;
import it.eng.spagobi.services.proxy.EventServiceProxy;
import it.eng.spagobi.utilities.engines.AbstractEngineStartAction;
import it.eng.spagobi.utilities.engines.AuditServiceProxy;
import it.eng.spagobi.utilities.engines.EngineConstants;
import it.eng.spagobi.utilities.engines.SpagoBIEngineException;
import it.eng.spagobi.utilities.engines.SpagoBIEngineServiceException;
import it.eng.spagobi.utilities.service.JSONFailure;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.json.JSONException;

public class CommonjEngineStartAction extends AbstractEngineStartAction {


	private static final long serialVersionUID = 1L;


	// request
	/** The Constant EXECUTION_CONTEXT. */
	public static final String EXECUTION_CONTEXT = "EXECUTION_CONTEXT";
	public static final String EXECUTION_ID = "EXECUTION_ID";
	public static final String DOCUMENT_LABEL = "DOCUMENT_LABEL";
	public static final String OUTPUT_TYPE = "outputType";

	public static transient Logger logger = Logger.getLogger(CommonjEngineStartAction.class);
	public static final String ENGINE_NAME = "SpagoBICommonjEngine";

	EventServiceProxy eventServiceProxy = null;


	@Override
	public void init(SourceBean config) {
		// TODO Auto-generated method stub
		super.init(config);
	}


	public Map getEnv() {
		Map env = new HashMap();

		copyRequestParametersIntoEnv(env, getSpagoBIRequestContainer());
		//env.put(EngineConstants.ENV_DATASOURCE, getDataSource());
		env.put(EngineConstants.ENV_DOCUMENT_ID, getDocumentId());
		env.put(EngineConstants.ENV_USER_PROFILE, getUserProfile());
		//env.put(EngineConstants.ENV_CONTENT_SERVICE_PROXY, getContentServiceProxy());
		// in scxheduling AUDIT not working 
		try{
			env.put(EngineConstants.ENV_AUDIT_SERVICE_PROXY, getAuditServiceProxy() );
		}
		catch(Exception e){
			logger.warn("No audit service proxy: probably in shedulign mode");
		}

		try{
			env.put(EngineConstants.ENV_EVENT_SERVICE_PROXY, getEventServiceProxy() );
		}
		catch(Exception e){
			logger.warn("No event service proxy: probably in shedulign mode");
		}

		env.put(EngineConstants.ENV_LOCALE, getLocale()); 

		return env;
	}


	   public EventServiceProxy getEventServiceProxy() {
		   if(eventServiceProxy == null ) {
			   eventServiceProxy = new EventServiceProxy(getUserIdentifier(), getHttpSession());
		   }	   
		   return eventServiceProxy;
	   }


	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws SpagoBIEngineException {


		logger.debug("IN");

		try {		

			//servletIOManager.auditServiceStartEvent();

			super.service(serviceRequest, serviceResponse);

			// get parameters in request
			Map parameters=getEnv();

			// get documentId
			Object documentIdO=parameters.get("DOCUMENT_ID");
			String documentId=documentIdO!=null ? documentIdO.toString() : null; 
			if(documentId==null){
				logger.error("Could not retrieve document ID");
				throw new Exception("Could not retrieve document ID");

			}

			//getAnalytical driver of document with id documentId

			// instantiate a work from template definition
			//work = new CommonjWork( getTemplateAsSourceBean()); 

			String parametersString="";
			for (Iterator iterator = parameters.keySet().iterator(); iterator.hasNext();) {
				String url= (String) iterator.next();
				Object val=parameters.get(url);
				if(val!=null){
					parametersString+="&"+url+"="+val.toString();
				}
			}


			// calculate process Id
			/*
			work.calculatePId(documentId, parameters);

			work.setSbiParametersMap(parameters);

			CommonjEngine cm=new CommonjEngine();
			worksRepository = CommonjEngine.getWorksRepository();

			try {
				// call Work configurqations's configure method
				WorkConfiguration workConfiguration=new WorkConfiguration(worksRepository);
				if(workConfiguration != null) {
					workConfiguration.configure(session,work,parameters,documentId);
				}		



			} catch (Exception e) {
				if(e instanceof WorkNotFoundException){
					try {
						logger.error("work not found!",e);
						writeBackToClient( new JSONFailure( e) );
					} catch (IOException ioe) {
						String message = "Impossible to write back the responce to the client";
						throw new SpagoBIEngineServiceException(getActionName(), message, e);
					} catch (JSONException je) {
						String message = "Error while serializing error into JSON object";
						throw new SpagoBIEngineServiceException(getActionName(), message, je);
					}
				}
				else if(e instanceof WorkExecutionException){
					try {
						logger.error("work execution exception!",e);
						writeBackToClient( new JSONFailure( e) );
					} catch (IOException ioe) {
						String message = "Impossible to write back the responce to the client";
						throw new SpagoBIEngineServiceException(getActionName(), message, e);
					} catch (JSONException je) {
						String message = "Error while serializing error into JSON object";
						throw new SpagoBIEngineServiceException(getActionName(), message, je);
					}
				}
				else{
					logger.error("work execution exception!",e);
					writeBackToClient( new JSONFailure( e) );

				}
			}
			 */
			//servletIOManager.tryToWriteBackToClient(work.getWorkName()+": class "+work.getClassName());

		}
		catch (Exception e) {
			logger.error("Error in servlet",e);
		}
		finally {
			logger.debug("OUT");
		}
	}
}
