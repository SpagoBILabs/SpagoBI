package it.eng.spagobi.engine.mobile.composed.service;

import it.eng.spago.base.SourceBean;
import it.eng.spagobi.analiticalmodel.document.bo.BIObject;
import it.eng.spagobi.analiticalmodel.document.bo.ObjTemplate;
import it.eng.spagobi.engine.mobile.service.AbstractExecuteMobileAction;
import it.eng.spagobi.engine.mobile.template.ComposedTemplateInstance;
import it.eng.spagobi.utilities.exceptions.SpagoBIServiceException;
import it.eng.spagobi.utilities.service.JSONSuccess;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONObject;

public class ExecuteMobileComposedAction extends AbstractExecuteMobileAction{
	
	private static Logger logger = Logger.getLogger(ExecuteMobileComposedAction.class);
	
	@Override
	public void doService() {
		// TODO Auto-generated method stub
		logger.debug("IN");

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
			
			
			ComposedTemplateInstance templInst = new ComposedTemplateInstance(template);
			templInst.loadTemplateFeatures();
			logger.debug("Created template instance");
			JSONObject features = templInst.getFeatures();
			//this engine doesn't need dataset, cause it just encapsulates other mobile docs

			try {
				logger.debug("OUT");
				writeBackToClient( new JSONSuccess( features) );
			} catch (IOException e) {
				throw new SpagoBIServiceException("Impossible to write back the responce to the client", e);
			}
			
		}catch (Exception e) {
			logger.error("Unable to execute composed document",e);
		}
	}

}
