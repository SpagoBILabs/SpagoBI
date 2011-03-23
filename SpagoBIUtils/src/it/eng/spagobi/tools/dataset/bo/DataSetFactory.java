/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005 Engineering Ingegneria Informatica S.p.A.

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
package it.eng.spagobi.tools.dataset.bo;

import it.eng.spagobi.services.dataset.bo.SpagoBiDataSet;

import org.apache.log4j.Logger;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 *
 */
public class DataSetFactory {
	
	private static transient Logger logger = Logger.getLogger(DataSetFactory.class);
	
	public static IDataSet getDataSet( SpagoBiDataSet dataSetConfig ) {
		IDataSet dataSet = null;
				
		if (dataSetConfig == null) {
			throw new IllegalArgumentException("dataset-config parameter cannot be null");
		}
		
		if ( ScriptDataSet.DS_TYPE.equals( dataSetConfig.getType() ) ) {
			dataSet = new ScriptDataSet( dataSetConfig );	
		} else if (  JDBCDataSet.DS_TYPE.equals( dataSetConfig.getType() ) ) {
			//dataSet = new JDBCDataSet( dataSetConfig );
			dataSet = new JDBCStandardDataSet( dataSetConfig );
		} else if ( JavaClassDataSet.DS_TYPE.equals( dataSetConfig.getType() ) ) {
			dataSet = new JavaClassDataSet( dataSetConfig );
		} else if ( WebServiceDataSet.DS_TYPE.equals( dataSetConfig.getType() ) ) {
			dataSet = new WebServiceDataSet( dataSetConfig );
		} else if ( FileDataSet.DS_TYPE.equals( dataSetConfig.getType() ) ) {
			dataSet = new FileDataSet( dataSetConfig );
		} else {
			logger.error("Invalid dataset type [" + dataSetConfig.getType() + "]");
			throw new IllegalArgumentException("dataset type in dataset-config cannot be equal to [" + dataSetConfig.getType() + "]");
		}
		
		return dataSet;
	}
}
