/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.engines.chart.services;

import it.eng.spago.base.SourceBean;
import it.eng.spagobi.commons.bo.UserProfile;
import it.eng.spagobi.commons.utilities.StringUtilities;
import it.eng.spagobi.engines.chart.ChartEngineInstance;
import it.eng.spagobi.services.proxy.DataSetServiceProxy;
import it.eng.spagobi.tools.dataset.bo.IDataSet;
import it.eng.spagobi.tools.dataset.common.behaviour.UserProfileUtils;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;
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
public class GetChartDataAction extends AbstractChartEngineAction {
	
	public static final String SERVICE_NAME = "EXECUTE_DATASET";
	
	// request parameters
	public static String DATASET_LABEL = "ds_label";
	public static String USER_ID = "userId";
	public static String CALLBACK = "callback";
	public static String LOCALE = "LOCALE";
	public static String START = "start";
	public static String LIMIT = "limit";
	public static String ROWS_LIMIT = "ds_rowsLimit";
	
	// logger component
	private static Logger logger = Logger.getLogger(GetChartDataAction.class);
		
	public void service(SourceBean request, SourceBean response) {
		
		String dataSetLabel;
		String user;
		String callback;
		String locale;
		Integer start;
		Integer limit;
		Integer rowsLimit;	
	
		IDataSet dataSet;
		IDataStore dataStore;
		
		logger.debug("IN");
		Monitor monitor =MonitorFactory.start("SpagoBI_Chart.GetChartDataAction.service");	
		
		try {
			super.service(request,response);
			ChartEngineInstance chartEngineInstance = getChartEngineInstance();
			
			dataSetLabel = getAttributeAsString( DATASET_LABEL );
			logger.debug("Parameter [" + DATASET_LABEL + "] is equals to [" + dataSetLabel + "]");			
			Assert.assertTrue(!StringUtilities.isEmpty( dataSetLabel ), "Parameter [" + DATASET_LABEL + "] cannot be null or empty");
			
			callback = getAttributeAsString( CALLBACK );
			logger.debug("Parameter [" + CALLBACK + "] is equals to [" + callback + "]");
			
			locale = getAttributeAsString( LOCALE );
			logger.debug("Parameter [" + LOCALE + "] is equals to [" + locale + "]");
			
			rowsLimit = (getAttributeAsInteger( ROWS_LIMIT ) == null) ? -1 :  getAttributeAsInteger( ROWS_LIMIT );
			logger.debug("Parameter [" + ROWS_LIMIT + "] is equals to [" + rowsLimit + "]");
			
			start = (getAttributeAsInteger( START ) == null) ? 0 : getAttributeAsInteger( START );
			logger.debug("Parameter [" + START + "] is equals to [" + start + "]");
			
			limit = (getAttributeAsInteger( LIMIT ) == null) ? -1 :  getAttributeAsInteger( LIMIT );
			logger.debug("Parameter [" + LIMIT + "] is equals to [" + limit + "]");
			

			dataSet = null;
			try {
				dataSet = chartEngineInstance.getDataSet();
			} catch(Throwable t) {
				throw new SpagoBIServiceException("Impossible to find a dataset whose label is [" + dataSetLabel + "]", t);
			}
			Assert.assertNotNull(dataSet, "Impossible to find a dataset whose label is [" + dataSetLabel + "]");
			Map params = chartEngineInstance.getAnalyticalDrivers();
			params.put(LOCALE, locale);
			dataSet.setParamsMap(params);
			dataSet.setUserProfileAttributes(UserProfileUtils.getProfileAttributes( (UserProfile) this.getEnv().get(EngineConstants.ENV_USER_PROFILE)));
			Monitor monitorLD = MonitorFactory.start("SpagoBI_Chart.GetChartDataAction.service.LoadData");
		
			dataSet.loadData(start, limit, rowsLimit);
			
			monitorLD.stop();
			dataStore = dataSet.getDataStore();
			Assert.assertNotNull(dataStore, "The dataStore returned by loadData method of the class [" + dataSet.getClass().getName()+ "] cannot be null");
			
			JSONObject results = new JSONObject();
			try {
				JSONDataWriter writer = new JSONDataWriter();
				
				Object resultNumber = dataStore.getMetaData().getProperty("resultNumber");
				if(resultNumber == null) dataStore.getMetaData().setProperty("resultNumber", new Integer((int)dataStore.getRecordsCount()));
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

}
