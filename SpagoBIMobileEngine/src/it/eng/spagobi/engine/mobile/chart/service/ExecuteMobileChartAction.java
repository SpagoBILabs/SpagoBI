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

import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.analiticalmodel.document.bo.BIObject;
import it.eng.spagobi.analiticalmodel.document.bo.DataSetExecutorForBIObject;
import it.eng.spagobi.analiticalmodel.document.bo.ObjTemplate;
import it.eng.spagobi.commons.constants.ObjectsTreeConstants;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.commons.services.AbstractSpagoBIAction;
import it.eng.spagobi.tools.dataset.bo.IDataSet;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;
import it.eng.spagobi.tools.dataset.common.datawriter.JSONDataWriter;
import it.eng.spagobi.utilities.exceptions.SpagoBIServiceException;
import it.eng.spagobi.utilities.service.JSONSuccess;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @authors Alberto Ghedin (alberto.ghedin@eng.it)
 *
 */
public class ExecuteMobileChartAction extends AbstractSpagoBIAction {	

	private static final long serialVersionUID = -1068103976812551203L;

	private static Logger logger = Logger.getLogger(ExecuteMobileChartAction.class);

	public void doService() {
		logger.debug("IN");
		IDataStore dataStore;
		JSONObject dataSetJSON;
		IDataSet dataSet;
		JSONObject toReturn = new JSONObject();
		
		try{
			//Load the BIObject
			BIObject documentBIObject = (BIObject)getAttributeFromSession(ObjectsTreeConstants.OBJECT_ID);
			logger.debug("Got BIObject from session");
			//Load the template of the document
			ObjTemplate objTemp = documentBIObject.getActiveTemplate();	
			logger.debug("Got ObjTemplate ");
//			
//			byte [] templateContent = objTemp.getContent();
//			String templContString = new String(templateContent);
//			SourceBean template = SourceBean.fromXMLString( templContString );
//			logger.debug("Created template source bean");
//			IMobileTemplateInstance templInst = MobileTemplateFactory.createMobileTemplateInstance(template);
//			logger.debug("Created template instance");
//			//Load the dataset
			
			Integer id = documentBIObject.getDataSetId();
			dataSet = DAOFactory.getDataSetDAO().loadActiveIDataSetByID(id);
			logger.debug("Got document dataset");
			//LOAD DATA

			DataSetExecutorForBIObject dataSetExecutorForBIObject = new DataSetExecutorForBIObject(dataSet, documentBIObject, this.getUserProfile());
			dataSetExecutorForBIObject.executeDataSet();
			logger.debug("Execute the data set");
			
			logger.debug("Building the data store..");
			dataStore = dataSet.getDataStore();
			JSONDataWriter dataSetWriter = new JSONDataWriter();
			JSONObject dataStroreJSON =  (JSONObject) dataSetWriter.write(dataStore);
			JSONObject dataStroreJSONMetdaData = dataStroreJSON.getJSONObject(JSONDataWriter.METADATA);
			JSONObject extDataStore = new JSONObject();
			String dataPosition = dataStroreJSONMetdaData.getString("root");
			JSONArray data = dataStroreJSON.getJSONArray(dataPosition);
			extDataStore.put("fields", dataStroreJSONMetdaData.getJSONArray("fields"));
			extDataStore.put("data", data);
			toReturn.put("store", extDataStore);
			logger.debug("Data store builded");
			
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
		} finally {
			logger.debug("OUT");
		}
		
	}
	

}