/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.tools.dataset.service;

import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.analiticalmodel.execution.service.ExecuteAdHocUtility;
import it.eng.spagobi.commons.bo.UserProfile;
import it.eng.spagobi.commons.constants.SpagoBIConstants;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.commons.utilities.GeneralUtilities;
import it.eng.spagobi.engines.config.bo.Engine;
import it.eng.spagobi.services.common.SsoServiceInterface;
import it.eng.spagobi.tools.datasource.bo.IDataSource;
import it.eng.spagobi.utilities.engines.EngineConstants;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;
import it.eng.spagobi.utilities.exceptions.SpagoBIServiceException;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.log4j.LogMF;
import org.apache.log4j.Logger;

/**
 * @author Antonella Giachino (antonella.giachino@eng.it)
 */
public class SelfServiceDatasetStartAction extends ManageDatasets  {

	private static final long serialVersionUID = 1L;
	
	public static final String SERVICE_NAME = "SELF_SERVICE_DATASET_ACTION";
	public static final String LANGUAGE = "LANGUAGE";
	public static final String COUNTRY = "COUNTRY";
	public static final String OUTPUT_PARAMETER_EXECUTION_ID = "executionId";
	
	public static final String OUTPUT_PARAMETER_WORKSHEET_EDIT_SERVICE_URL = "worksheetServiceUrl";
	public static final String WORKSHEET_EDIT_ACTION = "WORKSHEET_WITH_DATASET_START_EDIT_ACTION";	
	
	public static final String OUTPUT_PARAMETER_QBE_EDIT_FROM_BM_SERVICE_URL = "qbeFromBMServiceUrl";
	public static final String OUTPUT_PARAMETER_QBE_EDIT_FROM_DATA_SET_SERVICE_URL = "qbeFromDataSetServiceUrl";
	public static final String QBE_EDIT_FROM_BM_ACTION = "QBE_ENGINE_START_ACTION_FROM_BM";
	public static final String QBE_EDIT_FROM_DATA_SET_ACTION = "QBE_ENGINE_FROM_DATASET_START_ACTION";
	
	public static final String OUTPUT_PARAMETER_GEOREPORT_EDIT_SERVICE_URL = "georeportServiceUrl";
	public static final String IS_FROM_MYDATA = "MYDATA";
	//public static final String GEOREPORT_EDIT_ACTION = "GEOREPORT_ENGINE_START_EDIT_ACTION";
	

	// logger component
	private static Logger logger = Logger.getLogger(SelfServiceDatasetStartAction.class);

	public void doService() {
		logger.debug("IN");
		try {
			
			String executionId = ExecuteAdHocUtility.createNewExecutionId();
			
			String qbeEditFromBMActionUrl = buildQbeEditFromBMServiceUrl(executionId);
			String qbeEditFromDataSetActionUrl = buildQbeEditFromDataSetServiceUrl(executionId);
			String worksheetEditActionUrl = buildWorksheetEditServiceUrl(executionId);
			String geoereportEditActionUrl = buildGeoreportEditServiceUrl(executionId);
			String isFromMyData = (getAttributeAsString("MYDATA")==null)?"FALSE":getAttributeAsString("MYDATA");
			logger.trace("Copying output parameters to response...");
			try {
				Locale locale = getLocale();
				setAttribute(LANGUAGE, locale.getLanguage());
				setAttribute(COUNTRY, locale.getCountry());
				setAttribute(OUTPUT_PARAMETER_EXECUTION_ID, executionId);
				setAttribute(OUTPUT_PARAMETER_WORKSHEET_EDIT_SERVICE_URL, worksheetEditActionUrl);
				setAttribute(OUTPUT_PARAMETER_QBE_EDIT_FROM_BM_SERVICE_URL, qbeEditFromBMActionUrl);
				setAttribute(OUTPUT_PARAMETER_QBE_EDIT_FROM_DATA_SET_SERVICE_URL, qbeEditFromDataSetActionUrl);
				setAttribute(OUTPUT_PARAMETER_GEOREPORT_EDIT_SERVICE_URL, geoereportEditActionUrl);
				setAttribute(IS_FROM_MYDATA, isFromMyData);
			} catch (Throwable t) {
				throw new SpagoBIServiceException(SERVICE_NAME, "An error occurred while creating service response", t);				
			}
			logger.trace("Output parameter succesfully copied to response");



		} finally {
			logger.debug("OUT");
		}
	}
	
	
	// GEO
	protected String buildGeoreportEditServiceUrl(String executionId) {
		Map<String, String> parametersMap = buildGeoreportEditServiceBaseParametersMap();
		parametersMap.put("SBI_EXECUTION_ID" , executionId);
		
		Engine georeportEngine = ExecuteAdHocUtility.getGeoreportEngine();
		// GeoReportEngineStartEditAction
		
		String baseEditUrl = georeportEngine.getUrl().replace("GeoReportEngineStartAction", "GeoReportEngineStartEditAction");
		String georeportEditActionUrl = GeneralUtilities.getUrl(baseEditUrl, parametersMap);
		LogMF.debug(logger, "Georeport edit service invocation url is equal to [{}]", georeportEditActionUrl);
		
		return georeportEditActionUrl;
	}
	
	protected Map<String, String> buildGeoreportEditServiceBaseParametersMap() {
		Map<String, String> parametersMap = buildServiceBaseParametersMap();
		
		return parametersMap;
	}
	
	
	// WORKSHEET
	protected String buildWorksheetEditServiceUrl(String executionId) {
		Map<String, String> parametersMap = buildWorksheetEditServiceBaseParametersMap();
		parametersMap.put("SBI_EXECUTION_ID" , executionId);
		
		Engine worksheetEngine = ExecuteAdHocUtility.getWorksheetEngine();
		LogMF.debug(logger, "Engine label is equal to [{0}]", worksheetEngine.getLabel());
		Integer defEngineDataSourceWork = worksheetEngine.getDataSourceId();
		if(defEngineDataSourceWork!=null){
			try {
				IDataSource ds = DAOFactory.getDataSourceDAO().loadDataSourceByID(defEngineDataSourceWork);
				parametersMap.put(EngineConstants.ENGINE_DATASOURCE_LABEL, ds.getLabel());
			} catch (EMFUserError e) {
				logger.error("Error loading the datasource of the worksheet engine", e);
				throw new SpagoBIRuntimeException("Error loading the datasource of the worksheet engine", e);
			}
		} else{
			logger.error("No default data source defined for the worksheet engine");
			throw new SpagoBIRuntimeException("No default data source defined for the worksheet engine");
		}

		// create the WorkSheet Edit Service's URL
		String worksheetEditActionUrl = GeneralUtilities.getUrl(worksheetEngine.getUrl(), parametersMap);
		LogMF.debug(logger, "Worksheet edit service invocation url is equal to [{}]", worksheetEditActionUrl);
		
		return worksheetEditActionUrl;
	}
	
	protected Map<String, String> buildWorksheetEditServiceBaseParametersMap() {
		Map<String, String> parametersMap = buildServiceBaseParametersMap();
		parametersMap.put("ACTION_NAME", WORKSHEET_EDIT_ACTION);
		return parametersMap;
	}

	
	// QBE from BM
	protected String buildQbeEditFromBMServiceUrl(String executionId) {
		Map<String, String> parametersMap = buildQbeEditFromBMServiceBaseParametersMap();
		parametersMap.put("SBI_EXECUTION_ID" , executionId);
		
		Engine qbeEngine = ExecuteAdHocUtility.getQbeEngine();

		Engine worksheetEngine = ExecuteAdHocUtility.getWorksheetEngine();
		Integer defEngineDataSourceQbe = worksheetEngine.getDataSourceId();
		if(defEngineDataSourceQbe!=null){
			try {
				IDataSource ds = DAOFactory.getDataSourceDAO().loadDataSourceByID(defEngineDataSourceQbe);
				parametersMap.put(EngineConstants.ENGINE_DATASOURCE_LABEL,ds.getLabel());
			} catch (EMFUserError e) {
				logger.error("Error loading the datasource of the engine qbe", e);
				throw new SpagoBIRuntimeException("Error loading the datasource of the engine qbe", e);
			}
		}else{
			logger.error("No default data source defined for the qbe engine");
			throw new SpagoBIRuntimeException("No default data source defined for the qbe engine");
		}


		LogMF.debug(logger, "Engine label is equal to [{0}]", qbeEngine.getLabel());

		// create the qbe Edit Service's URL
		String qbeEditActionUrl = GeneralUtilities.getUrl(qbeEngine.getUrl(), parametersMap);
		LogMF.debug(logger, "Qbe edit service invocation url is equal to [{}]", qbeEditActionUrl);
		
		return qbeEditActionUrl;
	}
	
	// QBE from dataset
	protected String buildQbeEditFromDataSetServiceUrl(String executionId) {
		Map<String, String> parametersMap = buildQbeEditFromDataSetServiceBaseParametersMap();
		parametersMap.put("SBI_EXECUTION_ID" , executionId);
		
		Engine qbeEngine = ExecuteAdHocUtility.getQbeEngine();

		Engine worksheetEngine = ExecuteAdHocUtility.getWorksheetEngine();
		Integer defEngineDataSourceQbe = worksheetEngine.getDataSourceId();
		if(defEngineDataSourceQbe!=null){
			try {
				IDataSource ds = DAOFactory.getDataSourceDAO().loadDataSourceByID(defEngineDataSourceQbe);
				parametersMap.put(EngineConstants.ENGINE_DATASOURCE_LABEL,ds.getLabel());
			} catch (EMFUserError e) {
				logger.error("Error loading the datasource of the engine qbe", e);
				throw new SpagoBIRuntimeException("Error loading the datasource of the engine qbe", e);
			}
		}else{
			logger.error("No default data source defined for the qbe engine");
			throw new SpagoBIRuntimeException("No default data source defined for the qbe engine");
		}


		LogMF.debug(logger, "Engine label is equal to [{0}]", qbeEngine.getLabel());

		// create the qbe Edit Service's URL
		String qbeEditActionUrl = GeneralUtilities.getUrl(qbeEngine.getUrl(), parametersMap);
		LogMF.debug(logger, "Qbe edit service invocation url is equal to [{}]", qbeEditActionUrl);
		
		return qbeEditActionUrl;
	}

	protected Map<String, String> buildQbeEditFromBMServiceBaseParametersMap() {
		Map<String, String> parametersMap = buildServiceBaseParametersMap();
		parametersMap.put("ACTION_NAME", QBE_EDIT_FROM_BM_ACTION);
		return parametersMap;
	}
	
	protected Map<String, String> buildQbeEditFromDataSetServiceBaseParametersMap() {
		Map<String, String> parametersMap = buildServiceBaseParametersMap();
		parametersMap.put("ACTION_NAME", QBE_EDIT_FROM_DATA_SET_ACTION);
		return parametersMap;
	}
	
	
	
	protected Map<String, String> buildServiceBaseParametersMap() {
		HashMap<String, String> parametersMap = new HashMap<String, String>();
		
		parametersMap.put("NEW_SESSION", "TRUE");

		parametersMap.put(SpagoBIConstants.SBI_CONTEXT, GeneralUtilities.getSpagoBiContext());
		parametersMap.put(SpagoBIConstants.SBI_HOST, GeneralUtilities.getSpagoBiHost());

		parametersMap.put(SpagoBIConstants.SBI_LANGUAGE, getLocale().getLanguage());
		parametersMap.put(SpagoBIConstants.SBI_COUNTRY, getLocale().getCountry());

		if (!GeneralUtilities.isSSOEnabled()) {
			UserProfile userProfile = (UserProfile)getUserProfile();
			parametersMap.put(SsoServiceInterface.USER_ID, (String)userProfile.getUserId());
		}

		return parametersMap;
	}


}
