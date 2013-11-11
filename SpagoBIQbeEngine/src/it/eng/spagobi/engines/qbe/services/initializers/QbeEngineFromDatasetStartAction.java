/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.engines.qbe.services.initializers;

import it.eng.qbe.dataset.QbeDataSet;
import it.eng.spago.base.SourceBean;
import it.eng.spagobi.tools.dataset.bo.IDataSet;
import it.eng.spagobi.tools.dataset.common.metadata.IFieldMetaData;
import it.eng.spagobi.tools.dataset.common.metadata.IMetaData;
import it.eng.spagobi.tools.dataset.persist.IDataSetTableDescriptor;
import it.eng.spagobi.tools.datasource.bo.IDataSource;
import it.eng.spagobi.utilities.assertion.Assert;
import it.eng.spagobi.utilities.engines.EngineConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;


/**
 * The Class QbeEngineFromDatasetStartAction.
 * Called when opening QBE engine by passing a datase, not a document.
 * 
 * @author Giulio Gavardi
 */
public class QbeEngineFromDatasetStartAction extends QbeEngineStartAction {	
	
	// INPUT PARAMETERS
	
	// OUTPUT PARAMETERS
	public static final String LANGUAGE = "LANGUAGE";
	public static final String COUNTRY = "COUNTRY";
	
	// SESSION PARAMETRES	
	public static final String ENGINE_INSTANCE = EngineConstants.ENGINE_INSTANCE;
	public static final String REGISTRY_CONFIGURATION = "REGISTRY_CONFIGURATION";
	
	// INPUT PARAMETERS
	
	// The passed dataset label 
	public static final String DATASET_LABEL = "dataset_label";
	// label of default datasource associated to Qbe Engine
	public static final String DATASOURCE_LABEL = "datasource_label";
	
	
	
	/** Logger component. */
    private static transient Logger logger = Logger.getLogger(QbeEngineFromDatasetStartAction.class);
    
    public static final String ENGINE_NAME = "SpagoBIQbeEngine";
				
    private IDataSet dataSet;
	
	
	@Override
	public IDataSet getDataSet() {
		logger.debug("IN");
		if (dataSet == null) {
			// dataset information is coming with the request
			String datasetLabel = this.getAttributeAsString( DATASET_LABEL );
			logger.debug("Parameter [" + DATASET_LABEL + "]  is equal to [" + datasetLabel + "]");
			Assert.assertNotNull(datasetLabel, "Dataset not specified");
			dataSet = getDataSetServiceProxy().getDataSetByLabel(datasetLabel);  	
		}
		logger.debug("OUT");
		return dataSet;
	}

	@Override
	public IDataSource getDataSource() {
		logger.debug("IN");
		IDataSet dataset = this.getDataSet();
		IDataSource datasource = dataset.getDataSource();
		logger.debug("OUT : returning [" + datasource + "]");
		return datasource;
	}


	@Override
	public String getDocumentId() {
		// there is no document at the time
		return null;
	}   

// no template in this use case
	 public SourceBean getTemplateAsSourceBean() {
		 SourceBean templateSB = null;
		 return templateSB;
	 }
	 
	 
	 
	public Map addDatasetsToEnv() {
		Map env = super.getEnv();
		env.put(EngineConstants.ENV_LOCALE, getLocale());
		String datasetLabel = this.getAttributeAsString(DATASET_LABEL);
		env.put(EngineConstants.ENV_DATASET_LABEL, datasetLabel);

		IDataSet dataset = this.getDataSet();
		
		// substitute default engine's datasource with dataset one
		IDataSource dataSource = dataset.getDataSource();
		if (dataSource == null) {
			logger.debug("Dataset has no datasource.");
		} else {
			env.put(EngineConstants.ENV_DATASOURCE, dataSource);
		}
		
		boolean wasPersistedBefore = dataset.isPersisted() || dataset.isFlatDataset();
		IDataSetTableDescriptor descriptor = this.checkPersistence(dataset, env);
		if (!wasPersistedBefore && dataset instanceof QbeDataSet) {
			adjustMetadataForQbeDataset((QbeDataSet) dataset, descriptor);
		}

		List<IDataSet> dataSets = new ArrayList<IDataSet>();
		dataSets.add(dataset);
		env.put(EngineConstants.ENV_DATASETS, dataSets);
		return env;
	}
	
	private void adjustMetadataForQbeDataset(QbeDataSet dataset,
			IDataSetTableDescriptor descriptor) {
		IMetaData metadata = dataset.getMetadata();
		int columns = metadata.getFieldCount();
		for (int i = 0; i < columns; i++) {
			IFieldMetaData fieldMetadata = metadata.getFieldMeta(i);
			String newName = descriptor.getColumnName(fieldMetadata
					.getName());
			fieldMetadata.setName(newName);
			fieldMetadata.setProperty("uniqueName", newName);
		}
		dataset.setMetadata(metadata);
	}

	protected boolean tolerateMissingDatasource() {
		return true;
	}

}
