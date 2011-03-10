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
package it.eng.qbe.datasource;

import it.eng.qbe.dao.DAOFactory;
import it.eng.qbe.datasource.configuration.FileDataSourceConfiguration;
import it.eng.qbe.datasource.configuration.IDataSourceConfiguration;
import it.eng.qbe.datasource.hibernate.HibernateDataSource;
import it.eng.qbe.datasource.jpa.JPADataSource;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;

import java.io.File;
import java.util.List;
import java.util.Map;


/**
 * The Class DataSourceFactory.
 * 
 * @author Andrea Gioia
 */
public class DataSourceFactory {
	


	public static IDataSource buildDataSource(String driverName, String dataSourceName, List<IDataSourceConfiguration> configurations) {
		
		AbstractDataSource dataSource = null;
		
		
		/*
        boolean isJPA = false;
		try {
			isJPA = DAOFactory.getDatamartJarFileDAO().isAJPADatamartJarFile(configurations.get(0).getFile());
		} catch (Exception e) {
			throw new SpagoBIRuntimeException("Error loading mapping file associated to datamart [" + configurations.get(0) + "]", e);
		}
		if(configurations.size() > 1) {
			for(int i = 1; i < configurations.size(); i++) {
				boolean b;
				try {
					b = DAOFactory.getDatamartJarFileDAO().isAJPADatamartJarFile(configurations.get(0).getFile());
				} catch (Exception e) {
					throw new SpagoBIRuntimeException("Error loading mapping file associated to datamart [" + configurations.get(i) + "]", e);
				}
				if(isJPA != b) {
					throw new SpagoBIRuntimeException("Impossible to create a composite datasource from different datasource type");
				}
			}
		}
		*/
		
		
		if(driverName.equalsIgnoreCase("jpa")){
			dataSource = new JPADataSource(dataSourceName, configurations);
		} else {
			dataSource = new HibernateDataSource(dataSourceName, configurations);
		}
		
		
		//initDataSource(dataSource, dblinkMap, connection);
		return dataSource;
	}

	/*
	private static void initDataSource(AbstractDataSource dataSource,
			Map dblinkMap, 
			DBConnection connection) {
		
		dataSource.setConnection(connection);
		dataSource.setDblinkMap(dblinkMap);		
	}
	*/
	
}
