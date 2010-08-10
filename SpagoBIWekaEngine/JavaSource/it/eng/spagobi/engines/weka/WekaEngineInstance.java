/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005-2009 Engineering Ingegneria Informatica S.p.A.

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
package it.eng.spagobi.engines.weka;

import java.io.File;
import java.io.FileWriter;
import java.io.StringReader;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import it.eng.spagobi.services.proxy.EventServiceProxy;
import it.eng.spagobi.tools.datasource.bo.IDataSource;
import it.eng.spagobi.utilities.engines.AuditServiceProxy;
import it.eng.spagobi.utilities.engines.EngineConstants;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public class WekaEngineInstance  extends Thread {
	private Map env = null;
	private File file = null;	
	
	public static final String CLUSTERER = "clusterer";
	public static final String CLUSTERNUM = "clusterNum";
	public static final String CR_MANAGER_URL = "cr_manager_url"; 
	public static final String CONNECTION = "connectionName"; 
	public static final String INPUT_CONNECTION = "inputConnectionName"; 
	public static final String OUTPUT_CONNECTION = "outputConnectionName"; 
	public static final String WRITE_MODE = "writeMode"; 
	public static final String KEYS = "keys";
	public static final String VERSIONING = "versioning";
	public static final String VERSION_COLUMN_NAME = "versionColumnName";
	public static final String VERSION = "version";
	
	public static final String WEKA_ROLES_HANDLER_CLASS_NAME = "it.eng.spagobi.engines.drivers.weka.events.handlers.WekaRolesHandler";
	public static final String WEKA_PRESENTAION_HANDLER_CLASS_NAME = "it.eng.spagobi.engines.drivers.weka.events.handlers.WekaEventPresentationHandler";
	
	
	private static transient Logger logger = Logger.getLogger(WekaEngineInstance.class);
	
	public WekaEngineInstance(String template, Map env) {
		this.env = env;
		try {
			this.file = File.createTempFile("weka", null);
			ParametersFiller.fill(new StringReader(template), new FileWriter(file), env);
		} catch (Throwable t) {
			throw new SpagoBIRuntimeException("Impossible to replace parameters in template", t);
		}
		
		
	}
	
	public void run() {
		logger.debug("IN");
		logger.info(":service: Runner thread started succesfully");
			
		
		// registering the start execution event
		String startExecutionEventDescription = "${weka.execution.started}<br/>";
		
		String parametersList = "${weka.execution.parameters}<br/><ul>";
		Set paramKeys = env.keySet();
		Iterator paramKeysIt = paramKeys.iterator();
		while (paramKeysIt.hasNext()) {
			String key = (String) paramKeysIt.next();
			if (!key.equalsIgnoreCase("template") 
					&& !key.equalsIgnoreCase("document")
					&& !key.equalsIgnoreCase("processActivatedMsg")
					&& !key.equalsIgnoreCase("processNotActivatedMsg")
					&& !key.equalsIgnoreCase("userId")
					&& !key.equalsIgnoreCase("SPAGOBI_AUDIT_SERVLET")
					&& !key.equalsIgnoreCase("spagobicontext")
					&& !key.equalsIgnoreCase("SPAGOBI_AUDIT_ID")) {
				Object valueObj = env.get(key);
				parametersList += "<li>" + key + " = " + (valueObj != null ? valueObj.toString() : "") + "</li>";
			}
		}
		parametersList += "</ul>";

		
		Map startEventParams = new HashMap();				
		startEventParams.put(EventServiceProxy.EVENT_TYPE, EventServiceProxy.DOCUMENT_EXECUTION_START);
		startEventParams.put("document", env.get("DOCUMENT_ID"));
		
		String startEventId = null;
		try {
			startEventId = getEventServiceProxy().fireEvent(startExecutionEventDescription + parametersList, startEventParams, WEKA_ROLES_HANDLER_CLASS_NAME, WEKA_PRESENTAION_HANDLER_CLASS_NAME);
		} catch (Exception e) {
			logger.error(":run: problems while registering the start process event", e);
		}
		
		getAuditServiceProxy().notifyServiceStartEvent();
		
		
		Connection conn = null;
		try {
			conn = getDataSource().getConnection();
		} catch (Throwable t) {
			throw new SpagoBIRuntimeException("Impossible to get connection", t);
		} 
		Connection incon =  conn;  //(con!=null)?con: getConnection((String)params.get(INPUT_CONNECTION));
		Connection outcon = conn;    // (con!=null)?con: getConnection((String)params.get(OUTPUT_CONNECTION));
		
		if (incon == null || outcon == null) {
			logger.error(":service:Cannot obtain"
					+ " connection for engine ["
					+ this.getClass().getName() + "] control"
					+ " configuration in engine-config.xml config file");
			// AUDIT UPDATE
			getAuditServiceProxy().notifyServiceErrorEvent("No connection available");
			
			return;
		}
		
		
		
		Map endEventParams = new HashMap();				
		endEventParams.put(EventServiceProxy.EVENT_TYPE, EventServiceProxy.DOCUMENT_EXECUTION_END);
		endEventParams.put("document", env.get("DOCUMENT_ID"));
		endEventParams.put(EventServiceProxy.START_EVENT_ID, startEventId);
		
		String endExecutionEventDescription = "";
		
		WekaKFRunner kfRunner = new WekaKFRunner(incon, outcon);
		logger.debug("WekaKFRunner Instanciated");
		logger.debug(":service:Start parsing file: " + file);
		try {
			kfRunner.loadKFTemplate(file);
			kfRunner.setWriteMode((String)env.get(WRITE_MODE));
			kfRunner.setKeyColumnNames(parseKeysProp((String)env.get(KEYS)));
			String versioning = (String)env.get(VERSIONING);
			if(versioning != null && versioning.equalsIgnoreCase("true")){
				logger.debug(":service:versioning activated");
				kfRunner.setVersioning(true);
				String str;
				if( (str = (String)env.get(VERSION_COLUMN_NAME)) != null) 
					kfRunner.setVersionColumnName(str);
				logger.debug(":service:version column name is " + kfRunner.getVersionColumnName());
				if( (str = (String)env.get(VERSION)) != null) 
					kfRunner.setVersion(str);
				logger.debug(":service:version is " + kfRunner.getVersion());
				
			}
			kfRunner.setupSavers();
			kfRunner.setupLoaders();
			logger.debug(":service:Getting loaders & savers infos ...");
			logger.debug( "\n" + Utils.getLoderDesc(kfRunner.getLoaders()) );
			logger.debug( "\n" + Utils.getSaverDesc(kfRunner.getSavers()) );
			logger.debug(":service:Executing knowledge flow ...");
			kfRunner.run(false, true);
			
			endExecutionEventDescription = "${weka.execution.executionOk}<br/>";
			endEventParams.put("operation-result", "success");
			
			// AUDIT UPDATE
			getAuditServiceProxy().notifyServiceEndEvent();
			
			} catch (Exception e) {
			logger.error("Impossible to load/parse templete file",e);
			endExecutionEventDescription = "${weka.execution.executionKo}<br/>";
			endEventParams.put("operation-result", "failure");
			// AUDIT UPDATE
			getAuditServiceProxy().notifyServiceErrorEvent(e.getMessage());
		}
		
		try {	
			getEventServiceProxy().fireEvent(endExecutionEventDescription + parametersList, endEventParams, WEKA_ROLES_HANDLER_CLASS_NAME, WEKA_PRESENTAION_HANDLER_CLASS_NAME);
		} catch (Exception e) {
		
			logger.error(":run: problems while registering the end process event", e);
		}
		file.delete();		
    }			
	
	private String[] parseKeysProp(String keysStr) {
		if(keysStr == null) return null;
		return keysStr.split(",");
	}
	
	
	public Map getEnv() {
		return this.env;
	}
	
	public AuditServiceProxy getAuditServiceProxy() {
		return (AuditServiceProxy)this.getEnv().get(EngineConstants.ENV_AUDIT_SERVICE_PROXY);
	}
	
	public EventServiceProxy getEventServiceProxy() {
		return (EventServiceProxy)this.getEnv().get(EngineConstants.ENV_EVENT_SERVICE_PROXY);
	}
	
	public IDataSource getDataSource() {
		return (IDataSource)this.getEnv().get(EngineConstants.ENV_DATASOURCE);
	}
	
	
	
	
}
