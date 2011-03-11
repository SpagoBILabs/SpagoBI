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
package it.eng.spagobi.engines.qbe.namingstrategy;

import it.eng.qbe.datasource.DBConnection;
import it.eng.qbe.datasource.naming.DataSourceNamingStrategy;

import java.util.List;

import org.apache.log4j.Logger;

// TODO: Auto-generated Javadoc
/**
 * The Class QbeNamingStrategy.
 * 
 * @author Andrea Gioia
 */
public class QbeNamingStrategy implements DataSourceNamingStrategy {
	
	/** The Constant DATAMART_NAME_SUFFIX. */
	public static final String DATAMART_NAME_SUFFIX = "DM";
	
	/** The Constant DATASOURCE_NAME_SUFFIX. */
	public static final String DATASOURCE_NAME_SUFFIX = "DS";
	
	/** The Constant STRING_SEPARETOR. */
	public static final String STRING_SEPARETOR = "_";
	
	/** Logger component. */
    public static transient Logger logger = Logger.getLogger(QbeNamingStrategy.class);
	
	/* (non-Javadoc)
	 * @see it.eng.qbe.naming.NamingStrategy#getDatamartName(java.util.List)
	 */
	private String getDatamartName(List datamartNames) {
		String datamartName = getDatamartUnqualifiedName(datamartNames);
		return datamartName;
	}	
	
	/**
	 * Gets the datamart unqualified name.
	 * 
	 * @param datamartNames the datamart names
	 * 
	 * @return the datamart unqualified name
	 */
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
	
	/**
	 * Gets the datasource unqualified name.
	 * 
	 * @param datamartNames the datamart names
	 * @param connection the connection
	 * 
	 * @return the datasource unqualified name
	 */
	private String getDatasourceUnqualifiedName(List datamartNames, DBConnection connection) {
		String datasourceName = getDatamartName(datamartNames);
		if (connection.isJndiConncetion()) {
			datasourceName += "@" + connection.getJndiName();
		} else {
			datasourceName += "@" + connection.getUsername() + "@" + connection.getUrl();
		}
		logger.info("Using " + datasourceName + " as datasource unqualified name");
		return datasourceName;
	}
	
	/* (non-Javadoc)
	 * @see it.eng.qbe.naming.NamingStrategy#getDatasourceName(java.util.List, it.eng.qbe.datasource.DBConnection)
	 */
	public String getDataSourceName(List datamartNames, DBConnection connection) {
		String datasourceName = getDatasourceUnqualifiedName(datamartNames, connection);
		return datasourceName + STRING_SEPARETOR + DATASOURCE_NAME_SUFFIX;
	}	
}
