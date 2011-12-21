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
package it.eng.spagobi.sdk.datasources.impl;

import it.eng.spagobi.commons.constants.SpagoBIConstants;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.sdk.AbstractSDKService;
import it.eng.spagobi.sdk.datasets.bo.SDKDataSet;
import it.eng.spagobi.sdk.datasets.bo.SDKDataSetParameter;
import it.eng.spagobi.sdk.datasets.bo.SDKDataStoreMetadata;
import it.eng.spagobi.sdk.datasources.bo.SDKDataSource;
import it.eng.spagobi.sdk.exceptions.InvalidParameterValue;
import it.eng.spagobi.sdk.exceptions.MissingParameterValue;
import it.eng.spagobi.sdk.exceptions.NotAllowedOperationException;
import it.eng.spagobi.sdk.utilities.SDKObjectsConverter;
import it.eng.spagobi.tools.dataset.bo.DataSetParameterItem;
import it.eng.spagobi.tools.dataset.bo.IDataSet;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;
import it.eng.spagobi.tools.dataset.common.metadata.MetaData;
import it.eng.spagobi.tools.datasource.bo.IDataSource;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.validator.GenericValidator;
import org.apache.log4j.Logger;

public class DataSourcesSDKServiceImpl extends AbstractSDKService implements it.eng.spagobi.sdk.datasources.DataSourcesSDKService {

	static private Logger logger = Logger.getLogger(DataSourcesSDKServiceImpl.class);






	public SDKDataSource getDataSource(Integer dataSourceId)
	throws NotAllowedOperationException {
		SDKDataSource toReturn = null;
		logger.debug("IN: dataSourceId in input = " + dataSourceId);
		try {
			super.checkUserPermissionForFunctionality(SpagoBIConstants.DATASOURCE_MANAGEMENT, "User cannot see datasource congifuration.");
			if (dataSourceId == null) {
				logger.warn("DataSource identifier in input is null!");
				return null;
			}
			IDataSource dataSource = DAOFactory.getDataSourceDAO().loadDataSourceByID(dataSourceId);
			if (dataSource == null) {
				logger.warn("DataSource with identifier [" + dataSourceId + "] not existing.");
				return null;
			}
			toReturn = new SDKObjectsConverter().fromSpagoBiDataSourceToSDKDataSource(dataSource.toSpagoBiDataSource());
		} catch(NotAllowedOperationException e) {
			throw e;
		} catch(Exception e) {
			logger.error("Error while retrieving SDKDataSources list", e);
			logger.debug("Returning null");
			return null;
		} finally {
			logger.debug("OUT");
		}
		return toReturn;
	}

	public SDKDataSource[] getDataSources() throws NotAllowedOperationException {
		SDKDataSource[] toReturn = null;
		logger.debug("IN");
		try {
			super.checkUserPermissionForFunctionality(SpagoBIConstants.DATASOURCE_MANAGEMENT, "User cannot see datasources congifuration.");
			List dataSourceList = DAOFactory.getDataSourceDAO().loadAllDataSources();
			toReturn = new SDKDataSource[dataSourceList.size()];
			for (int i = 0; i < dataSourceList.size(); i++) {
				IDataSource dataSource = (IDataSource) dataSourceList.get(i);
				SDKDataSource sdkDataSource = new SDKObjectsConverter().fromSpagoBiDataSourceToSDKDataSource(dataSource.toSpagoBiDataSource());
				toReturn[i] = sdkDataSource;
			}
		} catch(NotAllowedOperationException e) {
			throw e;
		} catch(Exception e) {
			logger.error("Error while retrieving SDKSource list", e);
			logger.debug("Returning null");
			return null;
		} finally {
			logger.debug("OUT");
		}
		return toReturn;
	}	

}
