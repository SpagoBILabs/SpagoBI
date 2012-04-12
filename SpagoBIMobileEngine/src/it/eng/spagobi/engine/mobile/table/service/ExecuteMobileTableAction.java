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
package it.eng.spagobi.engine.mobile.table.service;

import it.eng.qbe.query.WhereField;
import it.eng.spago.base.SourceBean;
import it.eng.spagobi.analiticalmodel.document.bo.BIObject;
import it.eng.spagobi.analiticalmodel.document.bo.DataSetExecutorForBIObject;
import it.eng.spagobi.analiticalmodel.document.bo.ObjTemplate;
import it.eng.spagobi.commons.constants.ObjectsTreeConstants;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.commons.services.AbstractSpagoBIAction;
import it.eng.spagobi.engine.mobile.service.AbstractExecuteMobileAction;
import it.eng.spagobi.engine.mobile.table.serializer.MobileDatasetTableSerializer;
import it.eng.spagobi.engine.mobile.template.TableTemplateInstance;
import it.eng.spagobi.tools.dataset.bo.IDataSet;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;
import it.eng.spagobi.tools.dataset.common.datawriter.JSONDataWriter;
import it.eng.spagobi.utilities.engines.SpagoBIEngineServiceException;
import it.eng.spagobi.utilities.exceptions.SpagoBIServiceException;
import it.eng.spagobi.utilities.service.JSONFailure;
import it.eng.spagobi.utilities.service.JSONSuccess;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @authors Alberto Ghedin (alberto.ghedin@eng.it)
 * Monica Franceschini
 *
 */
public class ExecuteMobileTableAction extends AbstractExecuteMobileAction {	

	private static final long serialVersionUID = -349776903181827582L;
	// logger component
	private static Logger logger = Logger.getLogger(ExecuteMobileTableAction.class);

	public void doService() {
		logger.debug("IN");
		IDataStore dataStore;
		JSONObject dataSetJSON;
		IDataSet dataSet;
		try{
			
			
			//Load the BIObject
			BIObject documentBIObject = getAndValidateBIObject();
			List parametersError = getParamErrors();
			
			logger.debug("Got BIObject from session");
			//Load the template of the document
			ObjTemplate objTemp = documentBIObject.getActiveTemplate();	
			logger.debug("Got ObjTemplate ");
			//CREATE TEMPLATE INSTANCE
			byte [] templateContent = objTemp.getContent();
			String templContString = new String(templateContent);
			SourceBean template = SourceBean.fromXMLString( templContString );
			logger.debug("Created template source bean");
			//GETS PARAMETERS VALUES
			HashMap paramMap = getParametersList(getAttributeAsJSONObject("PARAMETERS"));

			TableTemplateInstance templInst = new TableTemplateInstance(template, paramMap);
			templInst.loadTemplateFeatures();
			logger.debug("Created template instance");
			//Load the dataset
			Integer id = documentBIObject.getDataSetId();
			dataSet = DAOFactory.getDataSetDAO().loadActiveIDataSetByID(id);
			logger.debug("Got document dataset");
			//LOAD DATA
			
			DataSetExecutorForBIObject dataSetExecutorForBIObject = new DataSetExecutorForBIObject(dataSet, documentBIObject, this.getUserProfile());
			dataStore = dataSetExecutorForBIObject.executeDataSet();
			logger.debug("Execute the data set");
			

			logger.debug("Loaded datastore from dataset");
			dataSetJSON = null;
			JSONArray fieldsJSON= null;

			JSONArray conditionsJSON = null;
			try {
				MobileDatasetTableSerializer writer = new MobileDatasetTableSerializer();
				JSONObject features = templInst.getFeatures();
				//JSONArray conditions = (JSONArray)features.get("conditions");
				dataSetJSON = (JSONObject)writer.write(dataStore, features);
				logger.debug("Serialized response");
				
			} catch (Throwable e) {
				SpagoBIEngineServiceException serviceError = new SpagoBIEngineServiceException("Execution", "Error serializing result set");
				try {
					writeBackToClient(new JSONFailure(serviceError));
				} catch (Exception ex) {
					logger.error("Exception occurred writing back to client", ex);
					throw new SpagoBIServiceException("Exception occurred writing back to client", ex);
				}
				throw new SpagoBIServiceException("Impossible to serialize datastore", e);
			}
			
			try {
				logger.debug("OUT");
				writeBackToClient( new JSONSuccess( dataSetJSON) );
			} catch (IOException e) {

				throw new SpagoBIServiceException("Impossible to write back the responce to the client", e);
			}
			
		}catch (Exception e) {
			SpagoBIEngineServiceException serviceError = new SpagoBIEngineServiceException("Execution", "Error executing table");
			try {
				writeBackToClient(new JSONFailure(serviceError));
			} catch (Exception ex) {
				logger.error("Exception occurred writing back to client", ex);
				throw new SpagoBIServiceException("Exception occurred writing back to client", ex);
			}
			logger.error("Unable to execute table document",e);
		}
	}
	
}