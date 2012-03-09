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
package it.eng.spagobi.engine.mobile.chart.service;

import it.eng.spago.base.SourceBean;
import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.analiticalmodel.document.bo.BIObject;
import it.eng.spagobi.analiticalmodel.document.bo.DataSetExecutorForBIObject;
import it.eng.spagobi.analiticalmodel.document.bo.ObjTemplate;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.engine.mobile.MobileConstants;
import it.eng.spagobi.engine.mobile.service.AbstractExecuteMobileAction;
import it.eng.spagobi.engine.mobile.template.ChartTemplateInstance;
import it.eng.spagobi.engine.mobile.template.IMobileTemplateInstance;
import it.eng.spagobi.tools.dataset.bo.IDataSet;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;
import it.eng.spagobi.tools.dataset.common.datawriter.JSONDataWriter;
import it.eng.spagobi.utilities.exceptions.SpagoBIServiceException;
import it.eng.spagobi.utilities.service.JSONSuccess;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONArray;
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
		JSONObject toReturn = new JSONObject();
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
			JSONObject chartConfigFromTemplate = templateInstance.getFeatures();
			logger.debug("Finished to get the chart config from the template. ");
			
			logger.debug("Getting the document dataset...");
			Integer id = documentBIObject.getDataSetId();
			dataSet = DAOFactory.getDataSetDAO().loadActiveIDataSetByID(id);
			logger.debug("Got document dataset");
			//LOAD DATA
			DataSetExecutorForBIObject dataSetExecutorForBIObject = new DataSetExecutorForBIObject(dataSet, documentBIObject, this.getUserProfile());
			dataSetExecutorForBIObject.executeDataSet();
			logger.debug("Execute the data set");
			
			logger.debug("Building the data store..");
			dataStore = dataSet.getDataStore();
			
			Map<String,Object> parametersForWriter = new HashMap<String,Object>();
			parametersForWriter.put(JSONDataWriter.PROPERTY_ADJUST, true);
			
			JSONDataWriter dataSetWriter = new JSONDataWriter(parametersForWriter);
			JSONObject dataStroreJSON =  (JSONObject) dataSetWriter.write(dataStore);
			JSONObject dataStroreJSONMetdaData = dataStroreJSON.getJSONObject(JSONDataWriter.METADATA);

			JSONObject extDataStore = new JSONObject();
			String dataPosition = dataStroreJSONMetdaData.getString("root");
			JSONArray data = dataStroreJSON.getJSONArray(dataPosition);
			extDataStore.put("fields", dataStroreJSONMetdaData.getJSONArray("fields"));
			extDataStore.put("data", data);
			extDataStore.put("xtype", "jsonstore");
			logger.debug("Data store builded");

			chartConfigFromTemplate.put("store", extDataStore);
			toReturn.put("config", chartConfigFromTemplate);
			toReturn.put("store", extDataStore);
			
			writeBackToClient( new JSONSuccess( toReturn ) );
			
		} catch (EMFUserError emf) {
			logger.error("Error loading the data set from the biobject", emf);
			throw new SpagoBIServiceException("Error loading the data set from the biobject", emf);
		} catch (JSONException je) {
			logger.error("Error managing the json object", je);
			throw new SpagoBIServiceException("Error managing the json object", je);
		} catch (IOException ioe) {
			logger.error("Impossible to write back the responce to the client", ioe);
			throw new SpagoBIServiceException("Impossible to write back the responce to the client", ioe);
		} catch (Exception e) {
			logger.error("Generic error execiting the Chart Action", e);
			throw new SpagoBIServiceException("Generic error execiting the Chart Action", e);
		}finally {
			logger.debug("OUT");
		}
		
	}
	

}