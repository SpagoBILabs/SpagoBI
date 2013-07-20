/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package it.eng.spagobi.engines.georeport.services;

import it.eng.spagobi.engines.georeport.GeoReportEngineInstance;
import it.eng.spagobi.services.proxy.DataSetServiceProxy;
import it.eng.spagobi.tools.dataset.bo.IDataSet;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;
import it.eng.spagobi.tools.dataset.common.datawriter.JSONDataWriter;
import it.eng.spagobi.tools.dataset.common.metadata.IFieldMetaData;
import it.eng.spagobi.tools.dataset.common.metadata.IMetaData;
import it.eng.spagobi.utilities.engines.BaseServletIOManager;
import it.eng.spagobi.utilities.engines.EngineConstants;
import it.eng.spagobi.utilities.engines.SpagoBIEngineException;
import it.eng.spagobi.utilities.service.AbstractBaseServlet;

import org.apache.log4j.Logger;
import org.json.JSONObject;


/**
 * @authors Andrea Gioia (andrea.gioia@eng.it)
 */
public class GetTargetDatasetAction extends AbstractBaseServlet {
	
	private static final long serialVersionUID = 1L;
	
	/** Logger component. */
    private static transient Logger logger = Logger.getLogger(GeoReportEngineStartAction.class);
    
    
	public void doService( BaseServletIOManager servletIOManager ) throws SpagoBIEngineException {

		GeoReportEngineInstance engineInstance;
		
		IDataSet dataSet;
		IDataStore dataStore;
		IMetaData dataStoreMeta;
		
		logger.debug("IN");
		
		try {
			engineInstance = (GeoReportEngineInstance)servletIOManager.getHttpSession().getAttribute(EngineConstants.ENGINE_INSTANCE);
			
			//DataSet
			dataSet = engineInstance.getDataSet();
			dataSet.setParamsMap(engineInstance.getEnv());
			dataSet.loadData();
			
			//Datastore 
			dataStore = dataSet.getDataStore();
			
			for(int i = 0; i < dataStore.getMetaData().getFieldCount(); i++) {
				IFieldMetaData fieldMeta = dataStore.getMetaData().getFieldMeta(i);
				fieldMeta.setName(fieldMeta.getName().toUpperCase());
				if(fieldMeta.getAlias() != null) {
					fieldMeta.setAlias(fieldMeta.getAlias().toUpperCase());
				}
			}
			
			
			JSONDataWriter dataWriter = new JSONDataWriter();
			JSONObject result = (JSONObject)dataWriter.write(dataStore);
			servletIOManager.tryToWriteBackToClient( result.toString() );
			
		} catch(Throwable t) {
			t.printStackTrace();
		} finally {
			logger.debug("OUT");
		}
	}

	public void handleException(BaseServletIOManager servletIOManager,
			Throwable t) {
		t.printStackTrace();		
	}
	
	
	
	private IDataSet getDataSet(BaseServletIOManager servletIOManager) {
		IDataSet dataSet;
		DataSetServiceProxy datasetProxy;
		String user;
		String label;
		
		user = servletIOManager.getParameterAsString("userId");
		label = servletIOManager.getParameterAsString("label");
		
		datasetProxy = new DataSetServiceProxy(user, servletIOManager.getHttpSession());
		dataSet =  datasetProxy.getDataSetByLabel(label);
		
		return dataSet;
	}
	
	
}

