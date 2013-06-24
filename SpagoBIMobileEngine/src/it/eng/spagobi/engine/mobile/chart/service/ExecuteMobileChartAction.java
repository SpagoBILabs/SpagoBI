/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.engine.mobile.chart.service;

import it.eng.spago.base.SourceBean;
import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.analiticalmodel.document.bo.BIObject;
import it.eng.spagobi.analiticalmodel.document.bo.DataSetExecutorForBIObject;
import it.eng.spagobi.analiticalmodel.document.bo.ObjTemplate;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.engine.mobile.MobileConstants;
import it.eng.spagobi.engine.mobile.chart.serializer.MobileChartJSONSerializer;
import it.eng.spagobi.engine.mobile.service.AbstractExecuteMobileAction;
import it.eng.spagobi.engine.mobile.template.ChartTemplateInstance;
import it.eng.spagobi.engine.mobile.template.IMobileTemplateInstance;
import it.eng.spagobi.tools.dataset.bo.IDataSet;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;
import it.eng.spagobi.utilities.engines.SpagoBIEngineServiceException;
import it.eng.spagobi.utilities.exceptions.SpagoBIServiceException;
import it.eng.spagobi.utilities.service.JSONFailure;
import it.eng.spagobi.utilities.service.JSONSuccess;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @authors Alberto Ghedin (alberto.ghedin@eng.it)
 *
 */
public class ExecuteMobileChartAction extends AbstractExecuteMobileAction {	

	private static final long serialVersionUID = -1068103976812551203L;

	private static Logger logger = Logger.getLogger(ExecuteMobileChartAction.class);

	public void doService() {
		logger.debug("IN");
		IDataStore dataStore;
		IDataSet dataSet;
		
		JSONObject parameterJSON;
		BIObject documentBIObject;
		
		try{
			
			documentBIObject = getAndValidateBIObject();
			List parametersError = getParamErrors();
				
			JSONObject parameters = this.getAttributeAsJSONObject( MobileConstants.PARAMETERS );

			if(parameters!=null){
				String[] fields =  JSONObject.getNames(parameters);
				if (fields != null) {
					for (String field : fields) {
						parameterJSON = new JSONObject();
						parameterJSON.put("name",field);
						parameterJSON.put("value",parameters.getString(field));
					}
				}
			}

			Integer id = documentBIObject.getDataSetId();
			dataSet = DAOFactory.getDataSetDAO().loadActiveIDataSetByID(id);
			//LOAD DATA
			DataSetExecutorForBIObject dataSetExecutorForBIObject = new DataSetExecutorForBIObject(dataSet, documentBIObject, this.getUserProfile());
			dataSetExecutorForBIObject.executeDataSet();
			logger.debug("Execute the data set");
			
			logger.debug("Building the data store..");
			dataStore = dataSet.getDataStore();
			
			logger.debug("Got BIObject from session");
			//Load the template of the document
			ObjTemplate objTemp = documentBIObject.getActiveTemplate();	
			logger.debug("Got ObjTemplate ");

			logger.debug("Elaboranting the template... ");
			byte [] templateContent = objTemp.getContent();
			String templContString = new String(templateContent);
			SourceBean template = SourceBean.fromXMLString( templContString );
			
			HashMap paramMap = getParametersList(getAttributeAsJSONObject("PARAMETERS"));
			IMobileTemplateInstance templateInstance = new ChartTemplateInstance(template, paramMap);
			templateInstance.loadTemplateFeatures();
			
			MobileChartJSONSerializer writer = new MobileChartJSONSerializer();	
			JSONObject toReturn =  (JSONObject)writer.write(dataStore, templateInstance);
			writeBackToClient( new JSONSuccess( toReturn ) );
			
		} catch (EMFUserError emf) {
			SpagoBIEngineServiceException serviceError = new SpagoBIEngineServiceException("Execution", "Error loading the data set from the biobject");
			try {
				writeBackToClient(new JSONFailure(serviceError));
			} catch (Exception e) {
				logger.error("Exception occurred writing back to client", e);
				throw new SpagoBIServiceException("Exception occurred writing back to client", e);
			} 
			logger.error("Error loading the data set from the biobject", emf);
			throw new SpagoBIServiceException("Error loading the data set from the biobject", emf);
		} catch (JSONException je) {
			SpagoBIEngineServiceException serviceError = new SpagoBIEngineServiceException("Execution", "Error managing the json object");
			try {
				writeBackToClient(new JSONFailure(serviceError));
			} catch (Exception e) {
				logger.error("Exception occurred writing back to client", e);
				throw new SpagoBIServiceException("Exception occurred writing back to client", e);
			} 
			logger.error("Error managing the json object", je);
			throw new SpagoBIServiceException("Error managing the json object", je);
		} catch (IOException ioe) {
			SpagoBIEngineServiceException serviceError = new SpagoBIEngineServiceException("Execution", "Impossible to write back the responce to the client");
			try {
				writeBackToClient(new JSONFailure(serviceError));
			} catch (Exception e) {
				logger.error("Exception occurred writing back to client", e);
				throw new SpagoBIServiceException("Exception occurred writing back to client", e);
			} 
			logger.error("Impossible to write back the responce to the client", ioe);
			throw new SpagoBIServiceException("Impossible to write back the responce to the client", ioe);
		} catch (Exception e) {
			SpagoBIEngineServiceException serviceError = new SpagoBIEngineServiceException("Execution", "Generic error executing the Chart Action");
			try {
				writeBackToClient(new JSONFailure(serviceError));
			} catch (Exception ex) {
				logger.error("Exception occurred writing back to client", ex);
				throw new SpagoBIServiceException("Exception occurred writing back to client", ex);
			} 
			logger.error("Generic error execiting the Chart Action", e);
			throw new SpagoBIServiceException("Generic error execiting the Chart Action", e);
		}finally {
			logger.debug("OUT");
		}
		
	}
	

}