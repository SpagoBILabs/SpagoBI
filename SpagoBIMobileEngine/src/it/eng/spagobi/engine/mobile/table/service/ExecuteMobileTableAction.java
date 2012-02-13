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

import it.eng.spago.base.SourceBean;
import it.eng.spagobi.analiticalmodel.document.bo.BIObject;
import it.eng.spagobi.analiticalmodel.document.bo.DataSetExecutorForBIObject;
import it.eng.spagobi.analiticalmodel.document.bo.ObjTemplate;
import it.eng.spagobi.commons.constants.ObjectsTreeConstants;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.commons.services.AbstractSpagoBIAction;
import it.eng.spagobi.engine.mobile.table.serializer.MobileDatasetTableSerializer;
import it.eng.spagobi.engine.mobile.template.IMobileTemplateInstance;
import it.eng.spagobi.engine.mobile.template.MobileTemplateFactory;
import it.eng.spagobi.tools.dataset.bo.IDataSet;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;
import it.eng.spagobi.utilities.exceptions.SpagoBIServiceException;
import it.eng.spagobi.utilities.service.JSONSuccess;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @authors Alberto Ghedin (alberto.ghedin@eng.it)
 * Monica Franceschini
 *
 */
public class ExecuteMobileTableAction extends AbstractSpagoBIAction {	

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
			BIObject documentBIObject = (BIObject)getAttributeFromSession(ObjectsTreeConstants.OBJECT_ID);
			logger.debug("Got BIObject from session");
			//Load the template of the document
			ObjTemplate objTemp = documentBIObject.getActiveTemplate();	
			logger.debug("Got ObjTemplate ");
			//CREATE TEMPLATE INSTANCE
			byte [] templateContent = objTemp.getContent();
			String templContString = new String(templateContent);
			SourceBean template = SourceBean.fromXMLString( templContString );
			logger.debug("Created template source bean");
			IMobileTemplateInstance templInst = MobileTemplateFactory.createMobileTemplateInstance(template);
			logger.debug("Created template instance");
			//Load the dataset
			Integer id = documentBIObject.getDataSetId();
			dataSet = DAOFactory.getDataSetDAO().loadActiveIDataSetByID(id);
			logger.debug("Got document dataset");
			//LOAD DATA
			
			DataSetExecutorForBIObject dataSetExecutorForBIObject = new DataSetExecutorForBIObject(dataSet, documentBIObject, this.getUserProfile());
			dataStore = dataSetExecutorForBIObject.executeDataSet();
			logger.debug("Execute the data set");
			
/*			dataSet.loadData();
			dataStore = dataSet.getDataStore();*/
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
				throw new SpagoBIServiceException("Impossible to serialize datastore", e);
			}
			
			try {
				logger.debug("OUT");
				writeBackToClient( new JSONSuccess( dataSetJSON) );
			} catch (IOException e) {
				throw new SpagoBIServiceException("Impossible to write back the responce to the client", e);
			}
			
		}catch (Exception e) {
			logger.error("Unable to execute table document",e);
		}
	}
	
}