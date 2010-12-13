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
import java.util.Arrays;
import java.util.Map;

import org.apache.log4j.Logger;

import it.eng.spagobi.tools.datasource.bo.IDataSource;
import it.eng.spagobi.utilities.engines.EngineConstants;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public class WekaEngineInstance extends RunnbleEngineInstance {
	
	private File file = null;	
	


	public static final String WRITE_MODE = "writeMode"; 
	public static final String KEYS = "keys";
	public static final String VERSIONING = "versioning";
	public static final String VERSION_COLUMN_NAME = "versionColumnName";
	public static final String VERSION = "version";
	
	public static final String WEKA_ROLES_HANDLER_CLASS_NAME = "it.eng.spagobi.engines.drivers.weka.events.handlers.WekaRolesHandler";
	public static final String WEKA_PRESENTAION_HANDLER_CLASS_NAME = "it.eng.spagobi.engines.drivers.weka.events.handlers.WekaEventPresentationHandler";
	
	
	private static transient Logger logger = Logger.getLogger(WekaEngineInstance.class);
	
	 WekaEngineInstanceMonitor knowledgeFlowStartupMonitor; 
	
	public WekaEngineInstance(String template, Map env) {
		this.env = env;
		this.knowledgeFlowStartupMonitor = new WekaEngineInstanceMonitor(env);
		try {
			this.file = File.createTempFile("weka", null);
			ParametersFiller.fill(new StringReader(template), new FileWriter(file), env);
		} catch (Throwable t) {
			throw new WekaEngineRuntimeException("Impossible to replace parameters in template", t);
		}		
	}
	
	
	
	public void run() {
		WekaKnowledgeFlowRunner knowledgeFlowRunner;
		
		logger.debug("IN");
				
		try {
			knowledgeFlowStartupMonitor.start();
			
			
			knowledgeFlowRunner = buildKnowledgeFlowRunner();
			
			
			try {
				knowledgeFlowRunner.run(false, true);
			} catch (Throwable t2) {
				throw new WekaEngineRuntimeException("Impossible to run the Knowledge-Flow", t2);
			}
			
		} catch(Throwable error) {
			knowledgeFlowStartupMonitor.setError(error);
		} finally {
			file.delete();
			knowledgeFlowStartupMonitor.stop();
			logger.debug("OUT");
		}
    }			
	
	private WekaKnowledgeFlowRunner buildKnowledgeFlowRunner() {
		Connection conn;
		WekaKnowledgeFlowRunner knowledgeFlowRunner;
		
		logger.debug("IN");
		
		knowledgeFlowRunner = null;
		
		try {
			conn = getConnection();
			knowledgeFlowRunner = new WekaKnowledgeFlowRunner(getConnection(), getConnection());
			logger.debug("Knowledge-Flow Runner successfully created");
			
			knowledgeFlowRunner.loadKnowledgeFlowTemplate(file);
			knowledgeFlowRunner.setWriteMode((String)env.get(WRITE_MODE));
			logger.debug("Write mode is equal to [" + knowledgeFlowRunner.getWriteMode() + "]");
			
			String keys = (String)env.get(KEYS);
			String[] keyColumnNames = keys == null? null: keys.split(",");
			knowledgeFlowRunner.setKeyColumnNames(keyColumnNames);
			logger.debug("Key column names are " + Arrays.toString( knowledgeFlowRunner.getKeyColumnNames() ) + "");
			
			String versioning = (String)env.get(VERSIONING);
			if(versioning != null && versioning.equalsIgnoreCase("true")){
				knowledgeFlowRunner.setVersioning(true);
				logger.debug("Versioning is enabled");
				
				String versionColumnNam;
				if( (versionColumnNam = (String)env.get(VERSION_COLUMN_NAME)) != null) {
					knowledgeFlowRunner.setVersionColumnName( versionColumnNam );
					logger.debug("Version column name is equal to [" + knowledgeFlowRunner.getVersionColumnName() + "]");
				}
				
				String version;
				if( (version = (String)env.get(VERSION)) != null) {
					knowledgeFlowRunner.setVersion(version);
					logger.debug("Version is equal to [" + knowledgeFlowRunner.getVersion() + "]");
				}
			} else {
				logger.debug("Versioning is not enabled");
			}
			
			knowledgeFlowRunner.setupSavers();
			logger.debug("Savers successfully initializated");
			knowledgeFlowRunner.setupLoaders();
			logger.debug("Loaders successfully initializated");
			
			logger.debug( "\n" + Utils.getLoderDesc(knowledgeFlowRunner.getLoaders()) );
			logger.debug( "\n" + Utils.getSaverDesc(knowledgeFlowRunner.getSavers()) );
			
		} catch(Throwable t) {
			throw new WekaEngineRuntimeException("Impossible to instatiate the Knowledge-Flow Runner]", t);
		} finally {
			logger.debug("OUT");
		}
		
		return knowledgeFlowRunner;
			
	}
	
	
	Connection getConnection() {
		Connection conn = null;
		try {
			conn = getDataSource().getConnection();
		} catch (Throwable t) {
			throw new SpagoBIRuntimeException("Impossible to get connection", t);
		} 
		return conn;  
	}
	
	public IDataSource getDataSource() {
		return (IDataSource)this.getEnv().get(EngineConstants.ENV_DATASOURCE);
	}	
}
