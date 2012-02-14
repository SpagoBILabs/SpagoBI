package it.eng.spagobi.engine.mobile.composed.service;

import it.eng.spago.base.SourceBean;
import it.eng.spagobi.analiticalmodel.document.bo.BIObject;
import it.eng.spagobi.analiticalmodel.document.bo.ObjTemplate;
import it.eng.spagobi.commons.constants.ObjectsTreeConstants;
import it.eng.spagobi.commons.services.AbstractSpagoBIAction;
import it.eng.spagobi.engine.mobile.table.serializer.MobileDatasetTableSerializer;
import it.eng.spagobi.engine.mobile.template.IMobileTemplateInstance;
import it.eng.spagobi.engine.mobile.template.TableTemplateInstance;
import it.eng.spagobi.tools.dataset.bo.IDataSet;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;
import it.eng.spagobi.utilities.exceptions.SpagoBIServiceException;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

public class ExecuteMobileComposedAction extends AbstractSpagoBIAction{
	
	private static Logger logger = Logger.getLogger(ExecuteMobileComposedAction.class);
	
	@Override
	public void doService() {
		// TODO Auto-generated method stub
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
			TableTemplateInstance templInst = new TableTemplateInstance(template);
			logger.debug("Created template instance");

			//this engine doesn't need dataset, cause it just encapsulates other mobile docs

			JSONArray fieldsJSON= null;

			JSONArray conditionsJSON = null;
			try {
				MobileDatasetTableSerializer writer = new MobileDatasetTableSerializer();
/*				JSONObject features = templInst.getFeatures();
				//JSONArray conditions = (JSONArray)features.get("conditions");
				dataSetJSON = (JSONObject)writer.write(features);*/
				logger.debug("Serialized response");
				
			} catch (Throwable e) {
				throw new SpagoBIServiceException("Impossible to serialize composed informations", e);
			}
			
			//try {
				logger.debug("OUT");
				//writeBackToClient( new JSONSuccess( dataSetJSON) );
/*			} catch (IOException e) {
				throw new SpagoBIServiceException("Impossible to write back the responce to the client", e);
			}*/
			
		}catch (Exception e) {
			logger.error("Unable to execute table document",e);
		}
	}

}
