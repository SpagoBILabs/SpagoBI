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
package it.eng.spagobi.engines.weka.knowledgeflow;

import java.io.File;
import java.sql.Connection;
import java.util.Map;

import it.eng.spagobi.tools.datasource.bo.IDataSource;
import it.eng.spagobi.utilities.engines.EngineConstants;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public class WekaKnowledgeFlowEnv {
	Map env;
	
	public static final String WRITE_MODE = "writeMode"; 
	public static final String KEYS = "keys";
	public static final String VERSIONING = "versioning";
	public static final String VERSION_COLUMN_NAME = "versionColumnName";
	public static final String VERSION = "version";
	public static final String OUTPUT_FILE = "ouptutFile";
	
	public WekaKnowledgeFlowEnv(Map env) {
		this.env = env;
	}
	
	public boolean containsParameter(String parameterName) {
		return getEnv().containsKey(parameterName);
	}
	public Object getParameter(String parameterName, Object defaultValue) {
		return containsParameter(parameterName)? getEnv().get(parameterName): defaultValue;
	}
	
	public Object getParameter(String parameterName) {
		return getParameter(parameterName, null);
	}
	
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
	
	private IDataSource getDataSource() {
		return (IDataSource)getParameter(EngineConstants.ENV_DATASOURCE);
	}	
	
	public String getWriteMode() {
		return (String)getParameter(WRITE_MODE);
	}
	
	public void setWriteMode(String writeModel) {
		getEnv().put(WRITE_MODE, writeModel);
	}
	
	public String[] getKeyColumnNames() {
		String keysValue = (String)getParameter(KEYS);
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
		return (String)getParameter(VERSIONING);
	}
	
	public void setVerioning(String versioning) {
		getEnv().put(VERSIONING, versioning);
	}
	
	public String getVersionColumnName() {
		return (String)getParameter(VERSION_COLUMN_NAME);
	}
	
	public void setgetVerionColumnName(String columnName) {
		getEnv().put(VERSION_COLUMN_NAME, columnName);
	}
	
	public String getVersion() {
		return (String)getParameter(VERSION);
	}
	
	public void setVerion(String version) {
		getEnv().put(VERSION, version);
	}
	
	public File getOutputFile() {
		return (File)getParameter(OUTPUT_FILE);
	}
	
	public void setOutputFile(File file) {
		getEnv().put(OUTPUT_FILE, file);
	}
	
	private Map getEnv() {
		return env;
	}
}
