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
package it.eng.spagobi.engines.worksheet.services.designer;

import it.eng.spago.base.SourceBean;
import it.eng.spagobi.engines.worksheet.services.initializers.WorksheetEngineStartAction;
import it.eng.spagobi.tools.dataset.bo.IDataSet;
import it.eng.spagobi.tools.datasource.bo.IDataSource;
import it.eng.spagobi.utilities.assertion.Assert;

import org.apache.log4j.Logger;

/**
 * @authors Davide Zerbetto (davide.zerbetto@eng.it), Alberto Ghedin (alberto.ghedin@eng.it)
 */
public class WorksheetWithDatasetStartEditAction extends WorksheetEngineStartAction{ //WorksheetEngineStartAction {	

	private static final long serialVersionUID = 6272194014941617286L;

	// INPUT PARAMETERS
	public static final String DATASET_LABEL = "dataset_label";
	public static final String DATASOURCE_LABEL = "datasource_label";
	
	/** Logger component. */
	private static transient Logger logger = Logger.getLogger(WorksheetWithDatasetStartEditAction.class);


//	public void service(SourceBean serviceRequest, SourceBean serviceResponse) {
//		logger.debug("IN");
//		super.service(serviceRequest, serviceResponse);
//
//		// publisher for the qbe edit
//		String publisherName = "WORKSHEET_START_EDIT_ACTION_DATASET_PUBLISHER";
//
//		try {
//			serviceResponse.setAttribute(DynamicPublisher.PUBLISHER_NAME,
//					publisherName);
//		} catch (SourceBeanException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//
//		logger.debug("OUT");
//	}

//	public Map getEnv() {
//		Map env = new HashMap();
//
//		copyRequestParametersIntoEnv(env, getSpagoBIRequestContainer());
//		//env.put(EngineConstants.ENV_DATASOURCE, getDataSource());
//		// document id can be null (when using QbE for dataset definition)
//		//		   if (getDocumentId() != null) {
//		//			   env.put(EngineConstants.ENV_DOCUMENT_ID, getDocumentId());
//		//		   }
//		env.put(EngineConstants.ENV_USER_PROFILE, getUserProfile());
//		// env.put(EngineConstants.ENV_CONTENT_SERVICE_PROXY, getContentServiceProxy());
//		env.put(EngineConstants.ENV_AUDIT_SERVICE_PROXY, getAuditServiceProxy() );
//		env.put(EngineConstants.ENV_DATASET_PROXY, getDataSetServiceProxy()); 
//		env.put(EngineConstants.ENV_LOCALE, getLocale()); 
//
//		return env;
//	}

	@Override
    protected boolean goToWorksheetPreentation() {
		return true;
	}
	
	@Override
	public IDataSet getDataSet() {
		// dataset information is coming with the request
		String datasetLabel = this.getAttributeAsString( DATASET_LABEL );
		logger.debug("Parameter [" + DATASET_LABEL + "]  is equal to [" + datasetLabel + "]");
		Assert.assertNotNull(datasetLabel, "Dataset not specified");
		IDataSet dataSet = getDataSetServiceProxy().getDataSetByLabel(datasetLabel);  	
		return dataSet;
	}

	@Override
	public IDataSource getDataSource() {
		// datasource information is coming with the request
		String datasourceLabel = this.getAttributeAsString( DATASOURCE_LABEL );
		logger.debug("Parameter [" + DATASOURCE_LABEL + "]  is equal to [" + datasourceLabel + "]");
		Assert.assertNotNull(datasourceLabel, "Dataset not specified");
		IDataSource dataSource = getDataSourceServiceProxy().getDataSourceByLabel(datasourceLabel);
		return dataSource;
	}

	@Override
	public SourceBean getTemplateAsSourceBean() {
		// we must start with an empty template
		return null;
	}

	@Override
	public String getDocumentId() {
		// there is no document at the time
		return null;
	}

}