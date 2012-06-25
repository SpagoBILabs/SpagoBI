/* SpagoBI, the Open Source Business Intelligence suite

* © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this file,
* You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.engines.console.services;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

import it.eng.spago.base.SourceBean;
import it.eng.spagobi.commons.utilities.StringUtilities;
import it.eng.spagobi.services.proxy.DataSetServiceProxy;
import it.eng.spagobi.tools.dataset.bo.IDataSet;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;
import it.eng.spagobi.tools.dataset.common.datawriter.JSONDataWriter;
import it.eng.spagobi.utilities.assertion.Assert;
import it.eng.spagobi.utilities.engines.SpagoBIEngineServiceExceptionHandler;
import it.eng.spagobi.utilities.exceptions.SpagoBIServiceException;
import it.eng.spagobi.utilities.service.JSONSuccess;




/**
 * @author Antonella Giachino (antonella.giachino@eng.it)
 */
public class ExecuteDataSetAction extends AbstractConsoleEngineAction {
	
	public static final String SERVICE_NAME = "EXECUTE_DATASET";
	
	// request parameters
	public static String DATASET_LABEL = "ds_label";
	public static String USER_ID = "userId";
	public static String CALLBACK = "callback";
	
	// logger component
	private static Logger logger = Logger.getLogger(ExecuteDataSetAction.class);
	
	
	public void service(SourceBean request, SourceBean response) {
		
		String dataSetLabel;
		String user;
		String callback;
	
		IDataSet dataSet;
		IDataStore dataStore;
		JSONObject dataSetJSON;
		
		
		logger.debug("IN");
		Monitor monitor =MonitorFactory.start("SpagoBI_Console.ExecuteDataSetAction.service");
		
		try {
			super.service(request,response);
			
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
			Monitor monitorLD =MonitorFactory.start("SpagoBI_Console.ExecuteDataSetAction.service.LoadData");	
			dataSet.loadData();
			monitorLD.stop();
			dataStore = dataSet.getDataStore();
			Assert.assertNotNull(dataStore, "The dataStore returned by loadData method of the class [" + dataSet.getClass().getName()+ "] cannot be null");
					
			
			dataSetJSON = null;
			
			try {
				JSONDataWriter writer = new JSONDataWriter();
				dataSetJSON = (JSONObject)writer.write(dataStore);
			} catch (Throwable e) {
				throw new SpagoBIServiceException("Impossible to serialize datastore", e);
			}
			
			try {
				writeBackToClient( new JSONSuccess( dataSetJSON, callback ) );
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
		
		datasetProxy = getConsoleEngineInstance().getDataSetServiceProxy();
		dataSet =  datasetProxy.getDataSetByLabel(label);
		
		return dataSet;
	}

}
