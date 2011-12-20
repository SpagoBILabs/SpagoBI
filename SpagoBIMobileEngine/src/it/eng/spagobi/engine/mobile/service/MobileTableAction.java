/**
SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2004 - 2011 Engineering Ingegneria Informatica S.p.A.

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
package it.eng.spagobi.engine.mobile.service;

import it.eng.spago.base.SourceBean;
import it.eng.spagobi.engine.mobile.MobileEngine;
import it.eng.spagobi.engine.mobile.MobileEngineInstance;
import it.eng.spagobi.engine.mobile.util.MobileDatasetTableSerializer;
import it.eng.spagobi.services.proxy.DataSetServiceProxy;
import it.eng.spagobi.tools.dataset.bo.IDataSet;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;
import it.eng.spagobi.utilities.engines.AbstractEngineAction;
import it.eng.spagobi.utilities.engines.EngineConstants;
import it.eng.spagobi.utilities.engines.SpagoBIEngineServiceExceptionHandler;
import it.eng.spagobi.utilities.exceptions.SpagoBIServiceException;
import it.eng.spagobi.utilities.service.JSONSuccess;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.json.JSONObject;

public class MobileTableAction extends AbstractEngineAction  {
	
	private static transient Logger logger = Logger.getLogger(MobileTableAction.class);
//http://localhost:8080/SpagoBIMobileEngine/servlet/AdapterHTTP?ACTION_NAME=MOBILE_ENGINE_START_ACTION&SBI_EXECUTION_ROLE=%2Fspagobi%2Fadmin&SBI_COUNTRY=US&document=94&NEW_SESSION=TRUE&SBI_LANGUAGE=en&user_id=biadmin&SBI_EXECUTION_ID=1bc219f0b78a11e09a24e7901a48313b
	public static final String DOCUMENT_ID = "document";
	// request parameters
	private MobileEngineInstance engineInstance; 
	private final String MESSAGE_DET = "MESSAGE_DET";
	
	private final String GET_DATA = "GET_DATA";
	public void service(SourceBean request, SourceBean response) {
	
		IDataSet dataSet;
		IDataStore dataStore;
		JSONObject dataSetJSON;
		
		logger.debug("IN");
		
		try {
			super.service(request,response);
			String serviceType = this.getAttributeAsString(MESSAGE_DET);
			if(serviceType != null && serviceType.equalsIgnoreCase(GET_DATA)){

				engineInstance = (MobileEngineInstance)getEngineInstance();
				if(engineInstance == null){
					engineInstance = (MobileEngineInstance)getAttributeFromSession(EngineConstants.ENGINE_INSTANCE);
					engineInstance = (MobileEngineInstance)getSpagoBIHttpSessionContainer().get(EngineConstants.ENGINE_INSTANCE);
					
				}
				dataSet = null;
	
				try {
					String docId = (String)request.getAttribute(DOCUMENT_ID);
					dataSet = getDataSet(docId);
				} catch(Throwable t) {
					throw new SpagoBIServiceException("Impossible to get dataset]", t);
				}
				dataSet.loadData();

				dataStore = dataSet.getDataStore();

				dataSetJSON = null;
				
				try {
					MobileDatasetTableSerializer writer = new MobileDatasetTableSerializer();
					dataSetJSON = (JSONObject)writer.write(dataStore, engineInstance.getConditions());
				} catch (Throwable e) {
					throw new SpagoBIServiceException("Impossible to serialize datastore", e);
				}
				
				try {
					writeBackToClient( new JSONSuccess( dataSetJSON) );
				} catch (IOException e) {
					throw new SpagoBIServiceException("Impossible to write back the responce to the client", e);
				}
			}else if(serviceType == null){
				System.out.println("SERVICE TYPE MISSING");
			}
			logger.debug("OUT");	

		} catch (Throwable t) {
			throw SpagoBIEngineServiceExceptionHandler.getInstance().getWrappedException(getActionName(), getEngineInstance(), t);
		} finally {

			logger.debug("OUT");
		}
	}
	private IDataSet getDataSet(String docId) {
		IDataSet dataSet;
		DataSetServiceProxy datasetProxy;
		
		datasetProxy = engineInstance.getDataSetServiceProxy();
		dataSet = datasetProxy.getDataSet(docId);
		engineInstance.setDataSet(dataSet.getLabel(), dataSet);

		
		return dataSet;
	}

}
