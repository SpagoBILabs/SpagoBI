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

import it.eng.spagobi.engines.talend.utils.FileUtils;
import it.eng.spagobi.services.proxy.EventServiceProxy;
import it.eng.spagobi.utilities.callbacks.audit.AuditAccessUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

public class JobRunnerFacilities {
	
	private static transient Logger logger = Logger.getLogger(JobRunnerFacilities.class);

	public static final String TALEND_ROLES_HANDLER_CLASS_NAME = "it.eng.spagobi.engines.drivers.talend.TalendRolesHandler";
	public static final String TALEND_PRESENTAION_HANDLER_CLASS_NAME = "it.eng.spagobi.engines.drivers.talend.TalendEventPresentationHandler";

	private String _command = null;
	private File _executableJobDir = null;
	private String[] _envr = null;
	private List _filesToBeDeletedAfterJobExecution = null;
	private Map _parameters = null;
	private HttpSession _session=null;



	/**
	 * Instantiates a new job runner facilities.
	 * 
	 * @param command the command
	 * @param envr the envr
	 * @param executableJobDir the executable job dir
	 * @param filesToBeDeletedAfterJobExecution the files to be deleted after job execution
	 * @param auditAccessUtils the audit access utils
	 * @param auditId the audit id
	 * @param parameters the parameters
	 * @param session the session
	 */
	public JobRunnerFacilities(String command, String[] envr, File executableJobDir, 
			List filesToBeDeletedAfterJobExecution, AuditAccessUtils auditAccessUtils, String auditId,
			Map parameters,HttpSession session) {
		this._command = command;
		this._executableJobDir = executableJobDir;
		this._envr = envr;
		this._filesToBeDeletedAfterJobExecution = filesToBeDeletedAfterJobExecution;
		this._parameters = parameters;
		this._session = session;
	}

	/**
	 * Execute job.
	 */
	public void executeJob() {

		logger.debug("IN");

		String userId=(String) _parameters.get("userId");

		// registering the start execution event
		String startExecutionEventDescription = "${talend.execution.started}<br/>";

		String parametersList = "${talend.execution.parameters}<br/><ul>";
		Set paramKeys = _parameters.keySet();
		Iterator paramKeysIt = paramKeys.iterator();
		while (paramKeysIt.hasNext()) {
			String key = (String) paramKeysIt.next();
			if (!key.equalsIgnoreCase("template") 
					&& !key.equalsIgnoreCase("biobjectId")
					&& !key.equalsIgnoreCase("cr_manager_url")
					&& !key.equalsIgnoreCase("events_manager_url")
					&& !key.equalsIgnoreCase("user")
					&& !key.equalsIgnoreCase("SPAGOBI_AUDIT_SERVLET")
					&& !key.equalsIgnoreCase("spagobicontext")
					&& !key.equalsIgnoreCase("SPAGOBI_AUDIT_ID")
					&& !key.equalsIgnoreCase("username")) {
				Object valueObj = _parameters.get(key);
				parametersList += "<li>" + key + " = " + (valueObj != null ? valueObj.toString() : "") + "</li>";
			}
		}
		parametersList += "</ul>";

		Map startEventParams = new HashMap();				
		startEventParams.put(EventServiceProxy.EVENT_TYPE, EventServiceProxy.DOCUMENT_EXECUTION_START);
		startEventParams.put(EventServiceProxy.BIOBJECT_ID, _parameters.get("document"));

		Integer startEventId = null;
		EventServiceProxy eventServiceProxy=new EventServiceProxy(userId,_session);

		try {
			eventServiceProxy.fireEvent(startExecutionEventDescription + parametersList, startEventParams, TALEND_ROLES_HANDLER_CLASS_NAME, TALEND_PRESENTAION_HANDLER_CLASS_NAME);

		} catch (Exception e) {
			logger.error(this.getClass().getName() + ":run: problems while registering the start process event", e);
		}

		if (_command == null) {
			logger.error("No command to be executed");
			return;
		}

		Map endEventParams = new HashMap();				
		endEventParams.put(EventServiceProxy.EVENT_TYPE, EventServiceProxy.DOCUMENT_EXECUTION_END);
		endEventParams.put(EventServiceProxy.BIOBJECT_ID, _parameters.get("document"));
		if (startEventId != null) {
			endEventParams.put(EventServiceProxy.START_EVENT_ID, startEventId.toString());
		}

		String endExecutionEventDescription = null;
		BufferedReader input = null;
		try { 

			Process p = Runtime.getRuntime().exec(_command, _envr, _executableJobDir);

			/*
			input = new BufferedReader (new InputStreamReader(p.getInputStream()));
			while ((line = input.readLine()) != null) {
				logger.debug(line);
				//System.out.println(line);
			}*/

			endExecutionEventDescription = "${talend.execution.executionOk}<br/>";
			endEventParams.put("operation-result", "success");

		} catch (Exception e){
			logger.error("Error while executing command " + _command, e);
			endExecutionEventDescription = "${talend.execution.executionKo}<br/>";
			endEventParams.put("operation-result", "failure");
		} finally {
			//clean temporary files
			if (_filesToBeDeletedAfterJobExecution != null && _filesToBeDeletedAfterJobExecution.size() > 0) {
				Iterator it = _filesToBeDeletedAfterJobExecution.iterator();
				while (it.hasNext()) {
					File aFile = (File) it.next();
					if (aFile != null && aFile.exists()) FileUtils.deleteDirectory(aFile);
				}
			}
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
					logger.error("IO error");
				}
			}
		}
		try {	
			eventServiceProxy.fireEvent(endExecutionEventDescription + parametersList, endEventParams, TALEND_ROLES_HANDLER_CLASS_NAME, TALEND_PRESENTAION_HANDLER_CLASS_NAME);
		} catch (Exception e) {
			logger.error(":run: problems while registering the end process event: "+e);
		}


	}


}
