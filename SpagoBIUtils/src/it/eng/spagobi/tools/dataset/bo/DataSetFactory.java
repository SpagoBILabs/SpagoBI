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
package it.eng.spagobi.tools.dataset.bo;

import it.eng.spagobi.services.dataset.bo.SpagoBiDataSet;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.xml.sax.InputSource;

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

		InputStream source = DataSetFactory.class.getResourceAsStream("/datasetTypes.properties");
		Properties p = new Properties();
		try {
			p.load(source);
		} catch (IOException e) {
			throw new SpagoBIRuntimeException("Cannot load configuration from datasetTypes.properties file", e);
		}
		String dsType = dataSetConfig.getType();
		logger.debug("Dataset type: " + dsType);
		String className = p.getProperty(dsType);
		logger.debug("Dataset class: " + className);
		if (className == null) {
			throw new SpagoBIRuntimeException("No dataset class found for dataset type [" + dsType + "]");
		}
		Constructor c = null;
		Object object = null;;
		try {
			c = Class.forName(className).getConstructor(SpagoBiDataSet.class);
			object = c.newInstance(dataSetConfig);
			dataSet = (IDataSet) object;
		} catch (Exception e) {
			throw new SpagoBIRuntimeException("Error while instantiating dataset type [" + dsType 
					+ "], class [" + className + "]", e);
		}

		// if custom data set type try instanziate the refeerred class
		IDataSet customDataset = dataSet;
		if(CustomDataSet.DS_TYPE.equals(( dataSetConfig.getType() ) )
				&& customDataset instanceof CustomDataSet){
			try {
				dataSet = ((CustomDataSet)customDataset).instantiate();			
			} catch (Exception e) {
				logger.error("Cannot instantiate class "+((CustomDataSet)customDataset).getJavaClassName()+ ": go on with CustomDatasetClass");
			}			
		}


		//		if ( ScriptDataSet.DS_TYPE.equals( dataSetConfig.getType() ) ) {
		//			dataSet = new ScriptDataSet( dataSetConfig );	
		//		} else if (  JDBCDataSet.DS_TYPE.equals( dataSetConfig.getType() ) ) {
		//			dataSet = new JDBCDataSet( dataSetConfig );
		//		} else if ( JavaClassDataSet.DS_TYPE.equals( dataSetConfig.getType() ) ) {
		//			dataSet = new JavaClassDataSet( dataSetConfig );
		//		} else if ( WebServiceDataSet.DS_TYPE.equals( dataSetConfig.getType() ) ) {
		//			dataSet = new WebServiceDataSet( dataSetConfig );
		//		} else if ( FileDataSet.DS_TYPE.equals( dataSetConfig.getType() ) ) {
		//			dataSet = new FileDataSet( dataSetConfig );
		//		} else {
		//			logger.error("Invalid dataset type [" + dataSetConfig.getType() + "]");
		//			throw new IllegalArgumentException("dataset type in dataset-config cannot be equal to [" + dataSetConfig.getType() + "]");
		//		}

		return dataSet;
	}
}
