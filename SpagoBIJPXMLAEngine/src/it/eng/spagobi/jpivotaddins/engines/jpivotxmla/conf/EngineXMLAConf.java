/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 *
 * This file is part of SpagoBI. SpagoBI is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 2.1 of the License, or any later version. 
 * SpagoBI is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received
 * a copy of the GNU Lesser General Public License along with SpagoBI. If not, see: http://www.gnu.org/licenses/.
 * The complete text of SpagoBI license is included in the COPYING.LESSER file. 
 */
package it.eng.spagobi.jpivotaddins.engines.jpivotxmla.conf;

import it.eng.spago.base.SourceBean;
import it.eng.spago.base.SourceBeanException;
import it.eng.spagobi.jpivotaddins.engines.jpivotxmla.connection.IConnection;
import it.eng.spagobi.jpivotaddins.engines.jpivotxmla.connection.JDBCConnection;
import it.eng.spagobi.jpivotaddins.engines.jpivotxmla.connection.JNDIConnection;
import it.eng.spagobi.jpivotaddins.engines.jpivotxmla.connection.XMLAConnection;

import org.apache.log4j.Logger;
import org.xml.sax.InputSource;

public class EngineXMLAConf {
	private SourceBean config = null;
	
	private static transient Logger logger = Logger.getLogger(EngineXMLAConf.class);

	private static EngineXMLAConf instance = null;
	
	public static EngineXMLAConf getInstance(){
		if(instance==null) instance = new EngineXMLAConf();
		return instance;
	}
	
	private EngineXMLAConf() {
		try {
			logger.debug("Resource: " + getClass().getResource("/engine-config.xml"));
			config = SourceBean.fromXMLStream(
					new InputSource(getClass().getResourceAsStream("/engine-config.xml")));
		} catch (SourceBeanException e) {
			logger.error("Impossible to load configuration for jasper report engine", e);
		}
	}
	
	public SourceBean getConfig() {
		return config;
	}
	
	public IConnection getConnection(String connectionName) {
		IConnection connection = null;
		SourceBean connSb;
		String connectionType;
		
		if(connectionName == null) return null;
		
		try {
			connSb = (SourceBean)config.getFilteredSourceBeanAttribute("CONNECTIONS-CONFIGURATION.CONNECTION", "name", connectionName);
			connectionType = (String)connSb.getAttribute("type");
			
			if(connectionType == null) {
				logger.error("Attribute 'type' of connection " + connectionName + " is not setted");
			}else if(connectionType.equalsIgnoreCase("jndi")) {
				connection = new JNDIConnection(connSb);
			} else if (connectionType.equalsIgnoreCase("jdbc")) {
				connection = new JDBCConnection(connSb);
			} else if (connectionType.equalsIgnoreCase("xmla")) {
				connection = new XMLAConnection(connSb);
			} else {
				logger.error("Value '"+  connectionType +"' in not a valid value for attribute 'type' in connection " + connectionName);
			}
		} catch (Exception e) {
			logger.error("Problems occurred while reading configuration file", e);
		}	
		
		return connection;
	}
	
	public String getDefaultConnectionName() {
		String defaultConnectionName = null;
		SourceBean connSb;
		
		connSb = (SourceBean)config.getAttribute("CONNECTIONS-CONFIGURATION");
		defaultConnectionName = (String)connSb.getAttribute("defaultConnectionName");
		
		return defaultConnectionName;
	}
	
	public IConnection getDefaultConnection() {
		IConnection connection = null;
		String defaultConnectionName;
		
		defaultConnectionName = getDefaultConnectionName();
		if(defaultConnectionName == null){
			logger.warn("Default connection name is not specified");
		} else  {
			connection = getConnection(defaultConnectionName);
		}	
		
		return connection;
	}
}
