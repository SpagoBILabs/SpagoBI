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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import commonj.work.Work;

import it.eng.spagobi.engines.talend.utils.FileUtils;
import it.eng.spagobi.services.proxy.EventServiceProxy;
import it.eng.spagobi.utilities.engines.EngineConstants;

public class TalendWork implements Work {

    private static transient Logger logger = Logger.getLogger(TalendWork.class);

    public static final String TALEND_ROLES_HANDLER_CLASS_NAME = "it.eng.spagobi.engines.drivers.talend.TalendRolesHandler";
    public static final String TALEND_PRESENTAION_HANDLER_CLASS_NAME = "it.eng.spagobi.engines.drivers.talend.TalendEventPresentationHandler";
    public static final String START_EVENT_ID = "startEventId";
    public static final String BIOBJECT_ID = "biobjectId";
    public static final String USER_NAME = "userName";
    public static final String EVENTS_MANAGER_URL = "events_manager_url";
    public static final String EVENT_TYPE = "event-type";
    public static final String DOCUMENT_EXECUTION_START = "biobj-start-execution";
    public static final String DOCUMENT_EXECUTION_END = "biobj-end-execution";

    private String _command = null;
    private File _executableJobDir = null;
    private String[] _envr = null;
    private List _filesToBeDeletedAfterJobExecution = null;
    private Map _parameters = null;
   
    private boolean completeWithoutError = false;

    /**
     * Checks if is complete without error.
     * 
     * @return true, if is complete without error
     */
    public boolean isCompleteWithoutError() {
	return completeWithoutError;
    }

    /**
     * Instantiates a new talend work.
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
    public TalendWork(String command, String[] envr, File executableJobDir, List filesToBeDeletedAfterJobExecution,
	    Map parameters) {
		this._command = command;
		this._executableJobDir = executableJobDir;
		this._envr = envr;
		this._filesToBeDeletedAfterJobExecution = filesToBeDeletedAfterJobExecution;
		this._parameters = parameters;
    }

    public void run() {
		logger.debug("IN");
	
		
		// registering the start execution event
		String startExecutionEventDescription = "${talend.execution.started}<br/>";
	
		String parametersList = "${talend.execution.parameters}<br/><ul>";
		Set paramKeys = _parameters.keySet();
		Iterator paramKeysIt = paramKeys.iterator();
		logger.debug("--- Request Parameters---");
		while (paramKeysIt.hasNext()) {
		    String key = (String) paramKeysIt.next();
		    
		    if (!key.equalsIgnoreCase("template") 
		    	&& !key.equalsIgnoreCase("biobjectId")
			    && !key.equalsIgnoreCase("cr_manager_url") && !key.equalsIgnoreCase("events_manager_url")
			    && !key.equalsIgnoreCase("user") && !key.equalsIgnoreCase("SPAGOBI_AUDIT_SERVLET")
			    && !key.equalsIgnoreCase("spagobicontext") && !key.equalsIgnoreCase("SPAGOBI_AUDIT_ID")
			    && !key.equalsIgnoreCase("username")) {
			Object valueObj = _parameters.get(key);
			logger.debug(key+"/"+valueObj);
			parametersList += "<li>" + key + " = " + (valueObj != null ? valueObj.toString() : "") + "</li>";
		    }
		}
		logger.debug("--- Request Parameters---");
		parametersList += "</ul>";
	
		Map startEventParams = new HashMap();
		startEventParams.put(EVENT_TYPE, DOCUMENT_EXECUTION_START);
		startEventParams.put(BIOBJECT_ID, _parameters.get(EngineConstants.ENV_DOCUMENT_ID));
	
		Integer startEventId = null;
		EventServiceProxy eventServiceProxy = (EventServiceProxy)_parameters.get( EngineConstants.ENV_EVENT_SERVICE_PROXY);
		
	
		try {
	
		    String startEventParamsStr = getParamsStr(startEventParams);
	
		    eventServiceProxy.fireEvent(startExecutionEventDescription + parametersList, startEventParamsStr,
			    TALEND_ROLES_HANDLER_CLASS_NAME, TALEND_PRESENTAION_HANDLER_CLASS_NAME);
		    logger.debug("Start Fire Event");
	
		} catch (Exception e) {
		    logger.error("problems while registering the start process event", e);
		}
	
		if (_command == null) {
		    logger.error("No command to be executed");
		    return;
		}
	
		Map endEventParams = new HashMap();
		endEventParams.put(EVENT_TYPE, DOCUMENT_EXECUTION_END);
		// endEventParams.put("biobj-path", params.get(TEMPLATE_PATH));
		endEventParams.put(BIOBJECT_ID, _parameters.get("document"));
		if (startEventId != null) {
		    endEventParams.put(START_EVENT_ID, startEventId.toString());
		}
	
		String endExecutionEventDescription = null;
		BufferedReader input = null;
		try {
		    logger.debug("Java Command:"+_command);
		    logger.debug("Executable Job Dir:"+_executableJobDir);
		    Process p = Runtime.getRuntime().exec(_command, _envr, _executableJobDir);
	
		    // any error message?
            StreamGobbler errorGobbler = new 
                StreamGobbler(p.getErrorStream(), "ERROR");            
            
            // any output?
            StreamGobbler outputGobbler = new 
                StreamGobbler(p.getInputStream(), "OUTPUT");
                
            // kick them off
            errorGobbler.start();
            outputGobbler.start();
                                  
		    
		    
		    int exitCode = p.waitFor();
		   
		    	    
		    //CODICE DA USARE EVENTUALMENTE IN FUTURO CON ALTRE MODIFICHE:
		    /*this.talendJobClass = Class.forName(talendJobClassName); 
		    this.runJob = this.talendJobClass.getMethod("runJob", new  Class[]{String[].class}); 
		    int status = ((Integer)runJob.invoke(null,  mainMethodParams)).intValue(); */
		    
		    endExecutionEventDescription = "${talend.execution.executionOk}<br/>";
		    endEventParams.put("operation-result", "success");
	
		} catch (Throwable e) {
		    logger.error("Error while executing command " + _command, e);
		    endExecutionEventDescription = "${talend.execution.executionKo}<br/>";
		    endEventParams.put("operation-result", "failure");
		} finally {
		    if (_filesToBeDeletedAfterJobExecution != null && _filesToBeDeletedAfterJobExecution.size() > 0) {
			Iterator it = _filesToBeDeletedAfterJobExecution.iterator();
			while (it.hasNext()) {
			    File aFile = (File) it.next();
			    if (aFile != null && aFile.exists())
				FileUtils.deleteDirectory(aFile);
			}
		    }
		    if (input != null) {
			try {
			    input.close();
			} catch (IOException e) {
			    e.printStackTrace();
			}
		    }
		}
	
		try {
	
		    String endEventParamsStr = getParamsStr(endEventParams);
	
		    eventServiceProxy.fireEvent(endExecutionEventDescription + parametersList, endEventParamsStr,
			    TALEND_ROLES_HANDLER_CLASS_NAME, TALEND_PRESENTAION_HANDLER_CLASS_NAME);
		    logger.debug("End fire event");
	
		} catch (Exception e) {
		    logger.error("problems while registering the end process event", e);
		}
		completeWithoutError = true;

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

    public boolean isDaemon() {
    	return false;
    }

    public void release() {
    	logger.debug("IN");
    }
    
    class StreamGobbler extends Thread {
        InputStream is;
        String type;
        
        StreamGobbler(InputStream is, String type) {
            this.is = is;
            this.type = type;
        }
        
        public void run() {
        	try {
        		InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                String line=null;
                while ( (line = br.readLine()) != null) {
                    logger.debug("[" + type + "]: " + line);
                }
                
        	} catch (IOException ioe) {
                 ioe.printStackTrace();  
            }
        }
    }

}
