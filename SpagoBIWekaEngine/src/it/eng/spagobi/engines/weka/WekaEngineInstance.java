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

import java.sql.Connection;
import java.util.Map;

import org.apache.log4j.Logger;

import it.eng.spagobi.tools.datasource.bo.IDataSource;
import it.eng.spagobi.utilities.engines.EngineAnalysisMetadata;
import it.eng.spagobi.utilities.engines.EngineConstants;
import it.eng.spagobi.utilities.engines.IEngineAnalysisState;
import it.eng.spagobi.utilities.engines.IEngineInstance;
import it.eng.spagobi.utilities.engines.SpagoBIEngineException;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public class WekaEngineInstance implements IEngineInstance {
	
	Map env;
	String template;
	
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
		this.template = template;	
	}
	
	// -----------------------------------------------------------------------
	// UTILITY METHODS
	// -----------------------------------------------------------------------
	
	public Connection getOutConnection() {
		return getConnection();
	}
	
	public Connection getInConnection() {
		return getConnection();
	}
	
	public Connection getConnection() {
		Connection conn = null;
		try {
			if( getDataSource() != null) {
				conn = getDataSource().getConnection();
			}
		} catch (Throwable t) {
			throw new SpagoBIRuntimeException("Impossible to get connection", t);
		} 
		return conn;  
	}
	
	public IDataSource getDataSource() {
		return (IDataSource)getEnv().get(EngineConstants.ENV_DATASOURCE);
	}	
	
	public String getWriteMode() {
		return (String)getEnv().get(WRITE_MODE);
	}
	
	public void setWriteMode(String writeModel) {
		getEnv().put(WRITE_MODE, writeModel);
	}
	
	public String[] getKeyColumnNames() {
		String keysValue = (String)getEnv().get(KEYS);
		String[] keys = (keysValue == null)? null: keysValue.split(",");
		return keys;
	}
	
	public void setKeys(String writeModel) {
		getEnv().put(KEYS, writeModel);
	}
	
	public boolean isVersioningEnabled() {
		return getVerioning() != null && getVerioning().equalsIgnoreCase("true");
	}
	
	public String getVerioning() {
		return (String)getEnv().get(VERSIONING);
	}
	
	public void setVerioning(String versioning) {
		getEnv().put(VERSIONING, versioning);
	}
	
	public String getVersionColumnName() {
		return (String)getEnv().get(VERSION_COLUMN_NAME);
	}
	
	public void setgetVerionColumnName(String columnName) {
		getEnv().put(VERSION_COLUMN_NAME, columnName);
	}
	
	public String getVersion() {
		return (String)getEnv().get(VERSION);
	}
	
	public void setgetVerion(String version) {
		getEnv().put(VERSION, version);
	}
	
	// -----------------------------------------------------------------------
	// ACCESSOR METHODS
	// -----------------------------------------------------------------------
	
	public EngineAnalysisMetadata getAnalysisMetadata() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getId() {
		return null;
	}
	
	public IEngineAnalysisState getAnalysisState() {
		return null;
	}

	public String getTemplate() {
		return template;
	}
	
	public Map getEnv() {
		return env;
	}

	

	public void setAnalysisMetadata(EngineAnalysisMetadata analysisMetadata) {
		// TODO Auto-generated method stub
	}

	public void setAnalysisState(IEngineAnalysisState analysisState) {
		// TODO Auto-generated method stub	
	}

	public void setTemplate(String template) {
		this.template = template;
	}
	
	public void setEnv(Map env) {
		this.env = env;
		
	}

	public void validate() throws SpagoBIEngineException {
		// TODO Auto-generated method stub
	}
}
