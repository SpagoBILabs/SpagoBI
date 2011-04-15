/**
 * SpagoBI - The Business Intelligence Free Platform
 *
 * Copyright (C) 2004 - 2008 Engineering Ingegneria Informatica S.p.A.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 **/
package it.eng.qbe.datasource.naming;

import it.eng.qbe.datasource.ConnectionDescriptor;
import it.eng.qbe.datasource.configuration.CompositeDataSourceConfiguration;
import it.eng.qbe.datasource.configuration.IDataSourceConfiguration;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * The Class QbeNamingStrategy.
 * 
 * @author Andrea Gioia
 */
public class SimpleDataSourceNamingStrategy implements IDataSourceNamingStrategy {
	
	public static final String DATASOURCE_NAME_SUFFIX = "DS";	
	public static final String STRING_SEPARETOR = "_";
	
    public static transient Logger logger = Logger.getLogger(SimpleDataSourceNamingStrategy.class);
	
    /* (non-Javadoc)
	 * @see it.eng.qbe.naming.NamingStrategy#getDatasourceName(java.util.List, it.eng.qbe.datasource.DBConnection)
	 */
	public String getDataSourceName(IDataSourceConfiguration configuration) {
		ConnectionDescriptor connection = (ConnectionDescriptor)configuration.loadDataSourceProperties().get("connection");
		List<String> modelNames = new ArrayList<String>();
		if(configuration instanceof CompositeDataSourceConfiguration){
			CompositeDataSourceConfiguration cc = (CompositeDataSourceConfiguration)configuration;
			Iterator<IDataSourceConfiguration> it = cc.getSubConfigurations().iterator();
			while(it.hasNext()) modelNames.add(it.next().getModelName());
		} else {
			modelNames.add(configuration.getModelName());
		}
		String datasourceName = getDatasourceUnqualifiedName(modelNames, connection);
		return datasourceName + STRING_SEPARETOR + DATASOURCE_NAME_SUFFIX;
	}	
	
	
	private String getDatamartName(List datamartNames) {
		String datamartName = getDatamartUnqualifiedName(datamartNames);
		return datamartName;
	}	
	
	private String getDatamartUnqualifiedName(List datamartNames) {
		String name = null;
				
		name = "";
		for(int i = 0; i < datamartNames.size(); i++) {
			name += (i==0?"":"_") + (String)datamartNames.get(i);
		}
		
		if(datamartNames.size()>1){
			name = "_" + name;		
		}
		
		return name;
	}
	
	private String getDatasourceUnqualifiedName(List datamartNames, ConnectionDescriptor connection) {
		String datasourceName = getDatamartName(datamartNames);
		if (connection.isJndiConncetion()) {
			datasourceName += "@" + connection.getJndiName();
		} else {
			datasourceName += "@" + connection.getUsername() + "@" + connection.getUrl();
		}
		logger.info("Using " + datasourceName + " as datasource unqualified name");
		return datasourceName;
	}
}
