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
package it.eng.spagobi.sdk.services;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import it.eng.spagobi.commons.serializer.SerializationException;
import it.eng.spagobi.commons.serializer.SerializerFactory;
import it.eng.spagobi.commons.services.AbstractSpagoBIAction;
import it.eng.spagobi.commons.utilities.SpagoBIServiceExceptionHandler;
import it.eng.spagobi.commons.utilities.StringUtilities;
import it.eng.spagobi.services.dataset.bo.SpagoBiDataSet;
import it.eng.spagobi.services.dataset.service.DataSetSupplier;
import it.eng.spagobi.tools.dataset.bo.DataSetFactory;
import it.eng.spagobi.tools.dataset.bo.IDataSet;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;
import it.eng.spagobi.utilities.assertion.Assert;
import it.eng.spagobi.utilities.exceptions.SpagoBIServiceException;
import it.eng.spagobi.utilities.service.JSONSuccess;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public class ExecuteDataSetAction extends AbstractSpagoBIAction {
	
	public static final String SERVICE_NAME = "EXECUTE_DATASET";
	
	// request parameters
	public static String DATASET_LABEL = "label";
	public static String CALLBACK = "callback";
	
	// logger component
	private static Logger logger = Logger.getLogger(ExecuteDataSetAction.class);
	
	
	public void doService() {
		
		String dataSetLabel;
		String callback;
		DataSetSupplier dataSetSupplier;
		SpagoBiDataSet dataSetConfig;
		IDataSet dataSet;
		IDataStore dataStore;
		JSONObject dataSetJSON;
		
		
		logger.debug("IN");
		
		try {
		
			dataSetLabel = getAttributeAsString( DATASET_LABEL );
			logger.debug("Parameter [" + DATASET_LABEL + "] is equals to [" + dataSetLabel + "]");			
			Assert.assertTrue(!StringUtilities.isEmpty( dataSetLabel ), "Parameter [" + DATASET_LABEL + "] cannot be null or empty");
			
			callback = getAttributeAsString( CALLBACK );
			logger.debug("Parameter [" + CALLBACK + "] is equals to [" + callback + "]");
			
			dataSetConfig = null;
			try {
				dataSetSupplier = new DataSetSupplier();						
				dataSetConfig = dataSetSupplier.getDataSetByLabel(dataSetLabel);
			} catch(Throwable t) {
				throw new SpagoBIServiceException("Impossible to find a dataset whose label is [" + dataSetLabel + "]", t);
			}
			Assert.assertNotNull(dataSetConfig, "Impossible to find a dataset whose label is [" + dataSetLabel + "]");
			
			dataSet = DataSetFactory.getDataSet( dataSetConfig );
			dataSet.loadData();
			dataStore = dataSet.getDataStore();
			Assert.assertNotNull(dataStore, "The dataStore returned by loadData method of the class [" + dataSet.getClass().getName()+ "] cannot be null");
					
			
			dataSetJSON = null;
			try {
				dataSetJSON = (JSONObject)SerializerFactory.getSerializer("application/json").serialize( dataStore, null );
			} catch (SerializationException e) {
				throw new SpagoBIServiceException("Impossible to serialize datastore", e);
			}
			
			try {
				writeBackToClient( new JSONSuccess( dataSetJSON, callback ) );
			} catch (IOException e) {
				throw new SpagoBIServiceException("Impossible to write back the responce to the client", e);
			}
		} catch (Throwable t) {
			SpagoBIServiceExceptionHandler.getInstance().getWrappedException(SERVICE_NAME, t);
		} finally {
			logger.debug("OUT");
		}
	}

}
