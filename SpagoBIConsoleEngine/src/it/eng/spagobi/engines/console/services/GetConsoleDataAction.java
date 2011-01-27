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
package it.eng.spagobi.engines.console.services;

import it.eng.spago.base.SourceBean;
import it.eng.spagobi.commons.bo.UserProfile;
import it.eng.spagobi.commons.utilities.StringUtilities;
import it.eng.spagobi.engines.console.ConsoleEngineConfig;
import it.eng.spagobi.engines.console.ConsoleEngineInstance;
import it.eng.spagobi.services.proxy.DataSetServiceProxy;
import it.eng.spagobi.tools.dataset.bo.IDataSet;
import it.eng.spagobi.tools.dataset.common.datastore.DataStore;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;
import it.eng.spagobi.tools.dataset.common.datastore.IRecord;
import it.eng.spagobi.tools.dataset.common.datawriter.JSONDataWriter;
import it.eng.spagobi.utilities.assertion.Assert;
import it.eng.spagobi.utilities.engines.EngineConstants;
import it.eng.spagobi.utilities.engines.SpagoBIEngineServiceExceptionHandler;
import it.eng.spagobi.utilities.exceptions.SpagoBIServiceException;
import it.eng.spagobi.utilities.service.JSONSuccess;

import java.io.IOException;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;




/**
 * @author Antonella Giachino (antonella.giachino@eng.it)
 */
public class GetConsoleDataAction extends AbstractConsoleEngineAction {
	
	public static final String SERVICE_NAME = "EXECUTE_DATASET";
	
	// request parameters
	public static String DATASET_LABEL = "ds_label";
	public static String USER_ID = "userId";
	public static String CALLBACK = "callback";
	
	// logger component
	private static Logger logger = Logger.getLogger(GetConsoleDataAction.class);
		
	public void service(SourceBean request, SourceBean response) {
		
		String dataSetLabel;
		String user;
		String callback;
	
		IDataSet dataSet;
		IDataStore dataStore;
		
		logger.debug("IN");
		Monitor monitor =MonitorFactory.start("SpagoBI_Console.GetConsoleDataAction.service");	
		
		try {
			super.service(request,response);
			ConsoleEngineInstance consoleEngineInstance = getConsoleEngineInstance();
			
			dataSetLabel = getAttributeAsString( DATASET_LABEL );
			logger.debug("Parameter [" + DATASET_LABEL + "] is equals to [" + dataSetLabel + "]");			
			Assert.assertTrue(!StringUtilities.isEmpty( dataSetLabel ), "Parameter [" + DATASET_LABEL + "] cannot be null or empty");
			
			callback = getAttributeAsString( CALLBACK );
			logger.debug("Parameter [" + CALLBACK + "] is equals to [" + callback + "]");
			
			dataSet = null;
			try {
				dataSet = getDataSet(dataSetLabel);
			} catch(Throwable t) {
				throw new SpagoBIServiceException("Impossible to find a dataset whose label is [" + dataSetLabel + "]", t);
			}
			Assert.assertNotNull(dataSet, "Impossible to find a dataset whose label is [" + dataSetLabel + "]");
			Map params = consoleEngineInstance.getAnalyticalDrivers();
			dataSet.setParamsMap(params);
			dataSet.setUserProfile((UserProfile)consoleEngineInstance.getEnv().get(EngineConstants.ENV_USER_PROFILE));
			//dataSet.setParamsMap(getEnv());
			//gets the max number of rows for the table
			String strRowLimit = ConsoleEngineConfig.getInstance().getProperty("CONSOLE-TABLE-ROWS-LIMIT");
			int rowLimit = (strRowLimit == null)? 0 : Integer.parseInt(strRowLimit);
			Monitor monitorLD =MonitorFactory.start("SpagoBI_Console.GetConsoleDataAction.service.LoadData");
			if (rowLimit > 0){
				dataSet.loadData(-1, -1, rowLimit);
			}else{
				dataSet.loadData();
			}
			monitorLD.stop();
			dataStore = dataSet.getDataStore();
			Assert.assertNotNull(dataStore, "The dataStore returned by loadData method of the class [" + dataSet.getClass().getName()+ "] cannot be null");
			
			JSONObject results = new JSONObject();
			try {
				JSONDataWriter writer = new JSONDataWriter();
				
				Object resultNumber = dataStore.getMetaData().getProperty("resultNumber");
				if(resultNumber == null) dataStore.getMetaData().setProperty("resultNumber", new Integer((int)dataStore.getRecordsCount()));
				/*
				//gets the max number of rows for the table
				String strRowLimit = ConsoleEngineConfig.getInstance().getProperty("CONSOLE-TABLE-ROWS-LIMIT");
				int rowLimit = (strRowLimit == null)? 0 : Integer.parseInt(strRowLimit);
				
	
				if (rowLimit > 0){
					IDataStore tmpDS = new DataStore();
					for(int index = 0; index<dataStore.getRecordsCount() && index<rowLimit; index++){
						IRecord record = dataStore.getRecordAt(index);
						tmpDS.appendRecord(record);
					}
					//set the original datastore with the new one
					dataStore = tmpDS;
				}
*/
				JSONObject dataSetJSON = (JSONObject)writer.write(dataStore);				
				results = dataSetJSON;
			} catch (Throwable e) {
				throw new SpagoBIServiceException("Impossible to serialize datastore", e);
			}
			
			try {
				writeBackToClient( new JSONSuccess( results, callback ) );
			} catch (IOException e) {
				throw new SpagoBIServiceException("Impossible to write back the responce to the client", e);
			}
		} catch (Throwable t) {
			throw SpagoBIEngineServiceExceptionHandler.getInstance().getWrappedException(getActionName(), getEngineInstance(), t);
		} finally {
			monitor.stop();
			logger.debug("OUT");
		}
	}
	
	private IDataSet getDataSet(String label) {
		IDataSet dataSet;
		DataSetServiceProxy datasetProxy;
		ConsoleEngineInstance engineInstance;
		
		engineInstance = getConsoleEngineInstance();
		dataSet = engineInstance.getDataSet(label);
		if (dataSet == null || dataSet.hasDataStoreTransformer()) {
			logger.debug("Dataset with label " + label + " was not already loaded. Invoking DataSetService....");
			datasetProxy = engineInstance.getDataSetServiceProxy();
			dataSet = datasetProxy.getDataSetByLabel(label);
			engineInstance.setDataSet(label, dataSet);
		} else {
			logger.debug("Dataset with label " + label + " was already loaded. Returning it without calling DataSetService.");
		}
		
		return dataSet;
	}

}
