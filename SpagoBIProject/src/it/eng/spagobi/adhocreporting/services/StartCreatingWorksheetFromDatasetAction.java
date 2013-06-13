/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.adhocreporting.services;

import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.analiticalmodel.execution.service.CreateDatasetForWorksheetAction;
import it.eng.spagobi.analiticalmodel.execution.service.ExecuteAdHocUtility;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.commons.utilities.GeneralUtilities;
import it.eng.spagobi.engines.config.bo.Engine;
import it.eng.spagobi.tools.datasource.bo.DataSource;
import it.eng.spagobi.utilities.exceptions.SpagoBIServiceException;

import java.util.Map;

import org.apache.log4j.LogMF;
import org.apache.log4j.Logger;

/**
 * @author Zerbetto Davide (davide.zerbetto@eng.it)
 */
public class StartCreatingWorksheetFromDatasetAction extends CreateDatasetForWorksheetAction {

	// logger component
	private static Logger logger = Logger.getLogger(StartCreatingWorksheetFromDatasetAction.class);

	public void doService() {
		logger.debug("IN");
		try {
				
			// create the input parameters to pass to the WorkSheet Edit Service
			Map worksheetEditActionParameters = buildWorksheetEditServiceBaseParametersMap();
			
			String executionId = ExecuteAdHocUtility.createNewExecutionId();
			worksheetEditActionParameters.put("SBI_EXECUTION_ID" , executionId);
						
			Engine worksheetEngine = getWorksheetEngine();
			LogMF.debug(logger, "Engine label is equal to [{0}]", worksheetEngine.getLabel());
			
			if (worksheetEngine.getDataSourceId() == null) {
				throw new SpagoBIServiceException(
						SERVICE_NAME,
						"The Wrosksheet engine has no default datasource, that is required for using this functionality. Please contact the administrator");
			}
			
			int engineDatasource = worksheetEngine.getDataSourceId();
			DataSource datasource;
			try {
				datasource = DAOFactory.getDataSourceDAO().loadDataSourceByID(engineDatasource);
			} catch (EMFUserError e) {
				throw new SpagoBIServiceException(SERVICE_NAME, "Error getting datasource with id " + engineDatasource, e);	
			}
			worksheetEditActionParameters.put("ENGINE_DATASOURCE_LABEL", datasource.getLabel());
			
			// create the WorkSheet Edit Service's URL
			String worksheetEditActionUrl = GeneralUtilities.getUrl(worksheetEngine.getUrl(), worksheetEditActionParameters);
			LogMF.debug(logger, "Worksheet edit service invocation url is equal to [{}]", worksheetEditActionUrl);
			
			logger.trace("Copying output parameters to response...");
			try {
				getServiceResponse().setAttribute(OUTPUT_PARAMETER_EXECUTION_ID, executionId);
				getServiceResponse().setAttribute(OUTPUT_PARAMETER_WORKSHEET_EDIT_SERVICE_URL, worksheetEditActionUrl);
			} catch (Throwable t) {
				throw new SpagoBIServiceException(SERVICE_NAME, "An error occurred while creating service response", t);				
			}
			logger.trace("Output parameter succesfully copied to response");
			
		} finally {
			logger.debug("OUT");
		}
	}

}
